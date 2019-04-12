import React from 'react'
import PropTypes from 'prop-types'
import {routerRedux} from 'dva/router'
import {connect} from 'dva'
import {Page} from '../../../components'
import queryString from 'query-string'
import {Button, Card, Col, Icon, Row, Tabs} from "antd"
import Modal from "./Modal"
import ViewModal from "./ViewModal"

const {TabPane} = Tabs
const namespace = 'monthlyReport'
const name = '月报'

const Component = ({
                     location, dispatch, model, loading, app,
                   }) => {
  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {
    list, systemList, currentItem, modalVisible, modalType,
  } = model


  const handleRefresh = (newQuery) => {
    dispatch(routerRedux.push({
      pathname,
      search: queryString.stringify({
        ...query,
        ...newQuery,
      }),
    }))
  }

  const handleAdd = (system) => {
    dispatch({
      type: `${namespace}/showModal`,
      payload: {
        modalType: 'create',
        currentItem: {
          system: {
            objectId: system.objectId,
            name: system.name,
          },
        },
      },
    })
  }

  const handleEdit = (item) => {
    dispatch({
      type: `${namespace}/showModal`,
      payload: {
        modalType: 'update',
        currentItem: item,
      },
    })
  }

  const handleView = (item) => {
    dispatch({
      type: `${namespace}/showModal`,
      payload: {
        modalType: 'view',
        currentItem: item,
      },
    })
  }

  const modalProps = {
    item: currentItem,
    visible: modalVisible,
    maskClosable: false,
    width: 840,
    confirmLoading: loading.effects[`${namespace}/${modalType}`],
    onOk(data) {
      dispatch({
        type: `${namespace}/${modalType}`,
        payload: data,
      })
        .then(() => handleRefresh())
    },
    onCancel() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
    },
  }

  const viewModalProps = {
    item: currentItem,
    visible: modalVisible,
    maskClosable: false,
    width: 840,
    onOk() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
    },
    onCancel() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
    },
  }

  return (
    <Page inner>
      <Tabs activeKey={query.systemId || (systemList[0] && systemList[0].objectId)} onChange={(systemId) => handleRefresh({systemId})}>
        {systemList.map(system => <TabPane tab={system.name} key={system.objectId}>
          <Button htmlType="button" type="primary" onClick={() => handleAdd(system)}>
            <Icon type="plus"/> 提交月报
          </Button>
          <Row gutter={16} style={{marginTop: 15}}>
            {
              list.map(item => <Col key={item.objectId} md={12} lg={6} xxl={4}>
                <Card title={item.submitTime} hoverable
                      actions={[<Icon type="eye" onClick={() => handleView(item)}/>, <Icon type="edit" onClick={() => handleEdit(item)}/>,
                        <Icon type="file-word"/>]}>
                  <a href="#">More</a>
                </Card>
              </Col>)
            }
          </Row>
        </TabPane>)}
      </Tabs>,
      {modalVisible && modalType !== 'view' && <Modal {...modalProps} />}
      {modalVisible && modalType === 'view' && <ViewModal {...viewModalProps} />}
    </Page>
  )
}

Component.propTypes = {
  model: PropTypes.object,
  app: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}
export default connect((models) => {
  return {
    model: models[namespace],
    app: models.app,
    loading: models.loading,
  }
})(Component)
