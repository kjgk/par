import React from 'react'
import { Button, Icon, Layout, Tree } from 'antd'
import PropTypes from 'prop-types'
import { connect } from 'dva/index'
import queryString from 'query-string'
import { routerRedux } from 'dva/router'
import { Page, Util } from '../../../components'
import Form from './Form'
import { Modal } from 'antd'
import styles from '../../../components/Page/Page.module.less'

const namespace = 'code'

const defaultValues = {
  enabled: 1,
  hidden: 0,
}

const { Content, Sider } = Layout

const Component = ({ location, dispatch, model, loading }) => {

  const { pathname } = location
  const { selectedKeys, expandedKeys, currentItem } = model
  const isCreate = currentItem && currentItem.$new

  const onAdd = () => {
    if (!currentItem) {
      return
    }
    dispatch({
      type: `${namespace}/add`,
      payload: defaultValues,
    })
  }

  const onDelete = (objectId) => {
    Modal.confirm({
      title: '确定要删除该数据吗?',
      okType: 'danger',
      onOk () {
        dispatch({
          type: `${namespace}/delete`,
          payload: objectId,
        })
          .then(() => {
            dispatch(routerRedux.push({
              pathname,
            }))
          })
      },
    })
  }

  const treeProps = {
    draggable: true,
    selectedKeys,
    expandedKeys,
    loadData: (treeNode) => {
      return dispatch({
        type: `${namespace}/query`,
        payload: { id: treeNode.props.eventKey },
      })
    },
    onSelect: (id) => {
      if (id && id.length > 0) {
        if (isCreate) {
          Modal.warning({ title: '请先保存当前节点！' })
          return
        }
        dispatch(routerRedux.push({
          pathname,
          search: queryString.stringify({ id }),
        }))
      }
    },
    onExpand: (keys) => {
      dispatch({
        type: `${namespace}/updateState`,
        payload: { expandedKeys: keys },
      })
    },
    onDrop: (info) => {
      dispatch({
        type: `${namespace}/move`,
        payload: info,
      })
    },
  }

  const formProps = {
    onOk (data) {
      dispatch({
        type: `${namespace}/${isCreate ? 'create' : 'update'}`,
        payload: data,
      })
    },
    item: currentItem,
  }

  return (
    <Page loading={loading} inner >
      <Layout>
        <Sider>
          <Button type="dashed" size="small" onClick={onAdd}> <Icon type="plus" />添加子节点</Button>
          <Tree {...treeProps} className={styles.siderTree}>
            {Util.renderTreeNodes(model, { onDelete })}
          </Tree>
        </Sider>
        <Content>
          {currentItem && <Form {...formProps} />}
        </Content>
      </Layout>
    </Page>
  )
}

Component.propTypes = {
  model: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.bool,
}

export default connect((models) => {
  return {
    model: models[namespace],
    loading: models.loading.effects[namespace + '/create'] || models.loading.effects[namespace + '/update'],
  }
})(Component)
