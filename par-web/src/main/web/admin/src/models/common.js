import modelExtend from 'dva-model-extend'
import queryString from 'query-string'
import uuid from 'uuid/v4'
import {message} from 'antd'

const usageMessage = '数据已被使用，无法删除！'

const model = {
  reducers: {
    updateState(state, {payload}) {
      return {
        ...state,
        ...payload,
      }
    },
  },
}

const pageModel = modelExtend(model, {

  state: {
    list: [],
    pagination: {
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: total => `共 ${total} 条`,
      current: 1,
      total: 0,
      pageSize: 10,
    },
  },

  reducers: {
    querySuccess(state, {payload}) {
      const {list, pagination} = payload
      return {
        ...state,
        list,
        pagination: {
          ...state.pagination,
          ...pagination,
        },
      }
    },
  },

})

const createCrudModel = function (namespace, pathname, {query, get, create, update, remove, multiRemove}) {

  return modelExtend(pageModel, {
    namespace: namespace,
    state: {
      currentItem: {},
      modalVisible: false,
      modalType: 'create',
      selectedRowKeys: [],
    },

    subscriptions: {
      setup({dispatch, history}) {
        history.listen((location) => {
          if (location.pathname === pathname) {
            const payload = {
              page: 1,
              pageSize: 10,
              ...queryString.parse(location.search),
            }
            dispatch({
              type: 'query',
              payload,
            })
          } else {
            dispatch({
              type: 'updateState',
              payload: {
                currentItem: {},
                modalVisible: false,
                modalType: 'create',
                selectedRowKeys: [],
              },
            })
          }
        })
      },
    },

    effects: {

      * query({payload = {}}, {call, put}) {
        const {content, totalElements} = yield call(query, payload)
        if (content) {
          yield put({
            type: 'querySuccess',
            payload: {
              list: content,
              pagination: {
                current: Number(payload.page) || 1,
                pageSize: Number(payload.pageSize) || 10,
                total: typeof totalElements === 'string' ? parseInt(totalElements, 10) : totalElements,
              },
            },
          })
        }
      },

      * create({payload}, {call, put}) {
        yield call(create, payload)
        yield put({type: 'hideModal'})
        message.success('保存成功！')
      },

      * update({payload}, {call, put}) {
        yield call(update, payload)
        yield put({type: 'hideModal'})
        message.success('更新成功！')
      },

      * delete({payload}, {call, put, select}) {
        try {
          yield call(remove, payload)
        } catch (e) {
          if (e.name === 417) {
            message.error(usageMessage)
          }
          throw e
        }
        const {selectedRowKeys} = yield select(_ => _[namespace])
        yield put({
          type: 'updateState',
          payload: {selectedRowKeys: selectedRowKeys.filter(_ => _ !== payload)},
        })
        message.success('删除成功！')
      },

      * multiDelete({payload}, {call, put}) {
        try {
          yield call(multiRemove, payload)
        } catch (e) {
          if (e.name === 417) {
            message.error(usageMessage)
          }
          throw e
        }
        yield put({
          type: 'updateState',
          payload: {selectedRowKeys: []},
        })
        message.success('删除成功！')
      },
    },

    reducers: {
      showModal(state, {payload}) {
        return {
          ...state, ...payload,
          modalVisible: true,
        }
      },
      hideModal(state) {
        return {
          ...state,
          modalVisible: false,
        }
      },
    },
  })
}

const createTreeModel = function (namespace, pathname, {query, get, create, update, remove, move}) {

  const findTreeNode = function (treeData, id) {
    for (let treeNode of treeData) {
      if (treeNode.objectId === id) {
        return treeNode
      }
      if (treeNode.children) {
        treeNode = findTreeNode(treeNode.children, id)
        if (treeNode) {
          return treeNode
        }
      }
    }
  }

  const findExpandedPath = function (items, path = []) {
    let newPath = [...path]
    for (let item of items) {
      if (item.children && item.children.length) {
        return newPath.push(item.objectId) && findExpandedPath(item.children, newPath)
      }
    }
    return newPath
  }

  const deleteTreeNode = function (treeData, id) {
    for (let treeNode of treeData) {
      if (treeNode.objectId === id) {
        return treeData.splice(treeData.indexOf(treeNode), 1)
      }
      if (treeNode.children) {
        deleteTreeNode(treeNode.children, id)
      }
    }
  }

  return modelExtend(model, {
    namespace: namespace,
    state: {
      currentItem: undefined,
      treeData: [],
      selectedKeys: [],
      expandedKeys: [],
    },

    subscriptions: {
      setup({dispatch, history}) {
        history.listen((location) => {
          if (location.pathname === pathname) {
            dispatch({
              type: 'locationChange',
              payload: queryString.parse(location.search),
            })
          } else {
            dispatch({
              type: 'updateState',
              payload: {
                expandedKeys: [],
                selectedKeys: [],
                treeData: [],
                currentItem: undefined,
              },
            })
          }
        })
      },
    },

    effects: {

      * locationChange({payload}, {call, put, select}) {
        const id = payload.id
        const {treeData, selectedKeys} = yield select(_ => _[namespace])
        if (id === undefined) {
          if (treeData.length === 0) {
            yield put({
              type: 'query',
              payload: {
                id: -1,
              },
            })
          } else {
            deleteTreeNode(treeData, selectedKeys[0])
            yield put({
              type: 'updateState',
              payload: {
                selectedKeys: [],
                currentItem: undefined,
                treeData,
              },
            })
          }
        } else {
          const treeNode = findTreeNode(treeData, id)
          const currentItem = yield call(get, id)
          yield put({
            type: 'updateState',
            payload: {
              selectedKeys: [id],
              currentItem: {
                ...currentItem,
                objectId: id,
              },
            },
          })
          // 如果当前节点不存在，则从服务器获取
          if (!treeNode) {
            yield put({
              type: 'query',
              payload: {
                ...payload,
                backward: true,
              },
            })
          }
        }
      },

      * query({payload}, {call, put, select}) {
        const {treeData} = yield select(_ => _[namespace])
        const treeNode = findTreeNode(treeData, payload.id)
        if (treeNode && treeNode.children) {
          return
        }
        const newData = yield call(query, payload)
        yield put({
          type: 'querySuccess',
          payload: {
            treeNode,
            newData,
          },
        })
      },

      * create({payload}, {call, put, select}) {
        const {treeData, currentItem} = yield select(_ => _[namespace])
        const treeNode = findTreeNode(treeData, currentItem.objectId)
        delete payload.objectId
        const {objectId} = yield call(create, {
          ...payload,
          parentId: currentItem.parentId,
        })
        delete currentItem.$new
        treeNode.name = payload.name
        treeNode.objectId = objectId
        yield put({
          type: 'updateState',
          payload: {
            treeData,
            currentItem: {
              ...currentItem,
              ...payload,
              objectId,
            },
            selectedKeys: [objectId],
          },
        })
        message.success('保存成功！')
      },

      * update({payload}, {call, put, select}) {
        const {treeData, currentItem} = yield select(_ => _[namespace])
        const treeNode = findTreeNode(treeData, currentItem.objectId)
        yield call(update, payload)
        treeNode.name = payload.name
        yield put({
          type: 'updateState',
          payload: {
            treeData,
            currentItem: {
              ...currentItem,
              ...payload,
            },
          },
        })
        message.success('更新成功！')
      },

      * delete({payload}, {call, put, select}) {
        const {currentItem} = yield select(_ => _[namespace])
        if (!currentItem.$new) {
          try {
            yield call(remove, payload)
          } catch (e) {
            if (e.name === 417) {
              message.error(usageMessage)
            }
            throw e
          }
        }
        message.success('删除成功！')
      },

      * add({payload}, {call, put, select}) {
        const {treeData, currentItem, expandedKeys} = yield select(_ => _[namespace])
        const parentId = currentItem.objectId
        const treeNode = findTreeNode(treeData, parentId)
        const newTreeNode = {
          objectId: uuid(),
          name: '',
          leaf: 1,
        }

        // 当前节点包含子节点，但未获取
        if (treeNode.leaf === 0) {
          if (treeNode.children) {
            treeNode.children.push(newTreeNode)
          } else {
            yield put({
              type: 'query',
              payload: {id: parentId},
            })
            // todo 重新调用add
          }
        } else {
          treeNode.children = [newTreeNode]
          treeNode.leaf = 0
        }

        if (treeNode.children) {
          yield put({
            type: 'updateState',
            payload: {
              expandedKeys: [...expandedKeys, parentId],
              selectedKeys: [newTreeNode.objectId],
              currentItem: {
                $new: true,
                objectId: newTreeNode.objectId,
                parentId,
                name: newTreeNode.name,
                ...payload,
              },
            },
          })
        }
      },

      * move({payload: info}, {call, put, select}) {
        const {treeData} = yield select(_ => _[namespace])
        const {
          dragNodesKeys,
          dragNode: {props: {pos: pos1}},
          dropPosition,
          dropToGap,
          node: {props: {eventKey: targetId, pos: pos2}}
        } = info

        const sourceId = dragNodesKeys[dragNodesKeys.length - 1]

        if (pos2.indexOf(pos1) === 0) {
          message.warn('不能移动到此处！')
          return
        }
        let position = 0
        if (dropToGap !== undefined) {
          let p = pos2.split('-')
          position = dropPosition - parseInt(p[p.length - 1], 10)
        }
        let payload = {
          sourceId,
          targetId,
          position
        }
        // 执行移动请求
        let {targetParentId, targetOrderNo} = yield call(move, payload)

        if (targetParentId) {
          let sourceNode = findTreeNode(treeData, sourceId)
          let targetParentNode = findTreeNode(treeData, targetParentId)

          // 移除原节点
          deleteTreeNode(treeData, sourceId)

          // 插入新节点
          targetParentNode.leaf = 0
          targetParentNode.children = targetParentNode.children || []
          targetParentNode.children.splice(targetOrderNo - 1, 0, sourceNode)
        }
      },
    },

    reducers: {
      querySuccess(state, {payload}) {
        const {treeNode, newData} = payload
        const {selectedKeys, expandedKeys} = state
        if (treeNode === undefined) {
          if (selectedKeys[0]) {
            expandedKeys.push(...findExpandedPath(newData))
          }
          return {
            ...state,
            treeData: newData,
            expandedKeys: [...expandedKeys],  // https://github.com/react-component/tree/issues/203
          }
        }
        treeNode.children = newData
        return {
          ...state,
        }
      },

    },
  })
}

export {
  model,
  pageModel,
  createCrudModel,
  createTreeModel,
}
