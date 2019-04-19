import React, {Fragment} from 'react'
import PropTypes from 'prop-types'
import {routerRedux} from 'dva/router'
import {connect} from 'dva'
import {Button, Calendar, Card, Col, Icon, message, Popconfirm, Row, Spin, Tabs} from 'antd'
import {Page} from '../../../components'
import queryString from 'query-string'
import Modal from './Modal'
import ViewModal from "./ViewModal"
import styles from "./inspection.module.less"
import moment from "moment"
import classnames from "classnames"

const {TabPane} = Tabs
const namespace = 'inspection'
const name = '系统巡检'

const Component = ({
                     location, dispatch, model, loading, app,
                   }) => {
  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {
    list, pagination, currentItem, modalVisible, modalType, selectedRowKeys,
    systemList, currentStep, currentSystem, allowNextStep, functionScreenshots, functionResults,
    inspectionResults: {holidayInfo = {}, calendarStartDate, inspectionInfo = {}, segmentResult1, segmentResult2, now,},
  } = model

  const {user} = app

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
      type: `${namespace}/firstStep`,
      payload: system,
    })
  }
  const handleView = (inspectionId) => {
    dispatch({
      type: `${namespace}/viewInspection`,
      payload: inspectionId,
    })
  }


  const modalProps = {
    item: modalType === 'create' ? {} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    width: 840,
    confirmLoading: loading.effects[`${namespace}/${modalType}`],
    title: name,
    systemList,
    currentStep,
    currentSystem,
    allowNextStep,
    functionScreenshots,
    functionResults,
    user,
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
    onSelectSystem(item) {
      dispatch({
        type: `${namespace}/selectSystem`,
        payload: item,
      })
    },
    onNextStep(item) {
      dispatch({
        type: `${namespace}/nextStep`,
        payload: item,
      })
    },
    onUploadChange(functionId, data) {
      let payload = {}
      payload[functionId] = data
      dispatch({
        type: `${namespace}/uploadChange`,
        payload,
      })
    },
    toggleResult(functionId) {
      dispatch({
        type: `${namespace}/toggleResult`,
        payload: functionId,
      })
    },
    onFormPaste(file, functionId) {
      dispatch({
        type: `${namespace}/uploadFileFromClipboard`,
        payload: {
          file,
          functionId,
        },
      })
    }
  }

  const viewModalProps = {
    item: currentItem,
    visible: modalVisible,
    maskClosable: false,
    title: `巡检详情`,
    onCancel() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
    },
  }

  let {systemId, month} = query
  systemId = systemId || (systemList[0] && systemList[0].objectId)
  let currentMonth = moment(now).startOf('M')
  currentMonth = month ? currentMonth.year(parseInt(month.substring(0, 4), 10)).month(parseInt(month.substring(4), 10) - 1) : currentMonth
  let todayMills = moment(now).startOf('D').toDate().getTime()
  return (
    <Page inner>
      <Spin spinning={loading.effects[`${namespace}/query`]}>
        <Tabs activeKey={systemId} onChange={(systemId) => handleRefresh({systemId})}>
          {systemList
            .map(system => <TabPane tab={system.name} key={system.objectId}>
              {
                system.objectId === systemId && <Fragment>
                  <div className={styles["inspection-alert"]}>请在每天【8:30-9:30】和【12:30-13:30】提交巡检记录！</div>
                  <Calendar
                    className={styles['inspection-calendar']}
                    validRange={[calendarStartDate, moment(now)]}
                    onChange={(d) => {
                      let _month = d.format('YYYYMM')
                      month !== _month && handleRefresh({month: _month})
                    }}
                    value={currentMonth}
                    dateFullCellRender={(date) => {
                      let time = date.startOf('D').toDate().getTime()
                      let ins1 = inspectionInfo[time + '-1']
                      let ins2 = inspectionInfo[time + '-3']
                      let today = todayMills === time
                      return <div
                        className={classnames({'inspection-calendar-cell': true, 'holiday': holidayInfo[time]})}>
                        <div className="inspection-calendar-cell-value">{date.format('D')}</div>
                        {
                          (!today || segmentResult1 === 1 || segmentResult1 === 2) &&
                          <div className={classnames({'inspection-calendar-cell-item': true, 'item-checked': !!ins1, 'item-unchecked': !ins1})}
                               onClick={() => !!ins1 && handleView(ins1.inspectionId)}
                          >
                            {ins1 ? <Icon type="check-square"/> : <Icon type="close-square"/>}
                          </div>
                        }
                        {
                          (!today || segmentResult2 === 1 || segmentResult2 === 2) &&
                          <div className={classnames({'inspection-calendar-cell-item': true, 'item-checked': !!ins2, 'item-unchecked': !ins2})}
                               onClick={() => !!ins2 && handleView(ins2.inspectionId)}
                          >
                            {ins2 ? <Icon type="check-square"/> : <Icon type="close-square"/>}
                          </div>
                        }
                        {
                          today && (segmentResult1 === 0 || segmentResult2 === 0) &&
                          <div className="inspection-calendar-cell-item item-input"
                               onClick={() => handleAdd(system)}
                          >
                            <Icon type="edit"/>
                          </div>
                        }
                      </div>
                    }}/>
                </Fragment>}
            </TabPane>)}
        </Tabs>
      </Spin>
      {modalVisible && modalType === 'create' && <Modal {...modalProps} />}
      {modalVisible && modalType === 'view' && <ViewModal  {...viewModalProps}/>}
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
