import React, {Fragment} from 'react'
import PropTypes from 'prop-types'
import {routerRedux} from 'dva/router'
import moment from 'moment'
import {connect} from 'dva'
import {Page} from '../../../components'
import queryString from 'query-string'
import {Button, Card, Col, Icon, Row, Tabs, message} from "antd"
import Modal from "./Modal"
import ViewModal from "./ViewModal"
import styles from "./index.module.less"

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
    currentMonth, currentMonthlyReport,
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
    if (item.month) {
      dispatch({
        type: `${namespace}/showModal`,
        payload: {
          modalType: 'view',
          currentItem: item,
        },
      })
    }
  }

  const modalProps = {
    item: currentItem,
    currentMonth,
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

  const getReportTitle = (item) => {

    if (item.month) {
      return moment(item.month).format("YYYY年 M月")
    }
    return '未知'
  }

  let {systemId, month} = query
  systemId = systemId || (systemList[0] && systemList[0].objectId)

  return (
    <Page inner>
      <Tabs activeKey={systemId} onChange={(systemId) => handleRefresh({systemId})}>
        {systemList.map(system => <TabPane tab={system.name} key={system.objectId}>
          {
            system.objectId === systemId && <Fragment>
              <div className={styles["report-alert"]}>请在每月最后3个工作日提交月报！</div>
              <div style={{textAlign: 'center'}}>
                <Button disabled={!currentMonth || currentMonthlyReport} htmlType="button" type="primary" onClick={() => handleAdd(system)}>
                  <Icon type="plus"/> 提交月报
                </Button>
              </div>
              <Row gutter={16} style={{marginTop: 15}}>
                {
                  list.map(item => <Col key={item.objectId} md={12} lg={6} xxl={4}>
                    <Card className={styles.report_item} title={getReportTitle(item)} hoverable
                          onClick={() => handleView(item)}
                          actions={[
                            <Icon type="eye"/>,
                            item.objectId === currentMonthlyReport ? <Icon type="edit" onClick={(event) => {
                              event.stopPropagation()
                              event.preventDefault()
                              handleEdit(item)
                            }}/> : null,
                            <Icon type="file-word" onClick={(event) => {
                              event.stopPropagation()
                              event.preventDefault()
                              message.info('月报暂不支持导出！')
                            }}/>,
                          ].filter(action => action !== null)}>
                      {item.keyWork || item.maintenance || item.perfection}
                    </Card>
                  </Col>)
                }
              </Row>
            </Fragment>
          }
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