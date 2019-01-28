import roleService from '../../services/system/role'
import menuService from '../../services/system/menu'
import modelExtend from 'dva-model-extend'
import { createCrudModel } from '../common'
import { message } from 'antd'

const namespace = 'role'
const pathname = '/system/role'

const getChildMenus = function (node, result) {
  result = result || {}
  let value = [node.objectId]
  if (node.children && node.children.length) {
    for (let child of node.children) {
      value.push(...(getChildMenus(child, result)).value)
    }
  }
  result[node.objectId] = value
  return {
    value,
    result,
  }
}

const model = modelExtend(createCrudModel(namespace, pathname, roleService), {
  namespace,
  state: {
    menuModalVisible: false,
    checkedMenus: [],
    halfCheckedMenus: [],
    menuData: [],
  },
  effects: {
    * saveMenu ({ payload }, { call, put, select }) {
      const { currentItem, checkedMenus, halfCheckedMenus } = yield select(_ => _[namespace])
      yield call(roleService.saveMenu, currentItem.objectId, [...checkedMenus, ...halfCheckedMenus])
      yield put({
        type: 'hideMenuModal',
      })
      message.success('保存成功！')
    },
    * showMenuModal ({ payload }, { call, put, select }) {
      const { menuData } = yield select(_ => _[namespace])
      yield put({
        type: 'updateState',
        payload: {
          menuModalVisible: true,
          currentItem: payload,
          checkedMenus: [],
        },
      })
      const [menus, _menuData] = yield [
        call(roleService.loadMenu, payload.objectId),
        menuData.length ? menuData : call(menuService.getAll),
      ]

      const { result: childMenuData } = getChildMenus({
          objectId: 0,
          children: _menuData,
        }),
        halfCheckedMenus = [],
        checkedMenus = []

      for (let menuId of menus) {
        const childMenus = childMenuData[menuId]
        if (!childMenus || childMenus.length === 0) {
          continue
        }
        let includes = true
        if (childMenus.length > 1) {
          for (let child of childMenus) {
            if (!menus.includes(child)) {
              includes = false
              break
            }
          }
        }
        (includes ? checkedMenus : halfCheckedMenus).push(menuId)
      }

      yield put({
        type: 'updateState',
        payload: {
          checkedMenus,
          halfCheckedMenus,
          menuData: _menuData,
        },
      })
    },
  },
  reducers: {
    hideMenuModal (state) {
      return {
        ...state,
        menuModalVisible: false,
      }
    },
    checkedMenu (state, { payload }) {
      return {
        ...state,
        ...payload,
      }
    },
  },
})

export default model
