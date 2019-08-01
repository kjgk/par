import React from 'react'
import {connect} from 'dva'
import styles from './index.module.less'
import {Page} from "../../../../components"
import {Button, Radio, Select} from "antd"
import {routerRedux} from "dva/router"
import queryString from "query-string"
import ReportChart from "./Chart"
import SummaryModal from "./SummaryModal"
import ViewModal from "../ViewModal"
import {contextPath} from "../../../../utils/config"

const namespace = 'inspectionReport'
const Component = ({
                     location, dispatch, model, loading,
                   }) => {

  const {pathname} = location
  const query = queryString.parse(location.search)
  const {year, month} = query
  const {yearMonths, inspectionResults, inspectionSummary, currentItem, modalType, modalVisible,} = model

  const handleRefresh = (newQuery) => {
    dispatch(routerRedux.push({
      pathname,
      search: queryString.stringify({
        ...query,
        ...newQuery,
      }),
    }))
  }

  const handleChange = (e) => {
    handleRefresh({
      month: e.target.value,
    })
  }

  const handleSummary = () => {
    dispatch({
      type: `${namespace}/showSummary`,
      payload: query,
    })
  }

  const handleChartClick = (inspectionId) => {
    dispatch({
      type: `${namespace}/viewInspection`,
      payload: inspectionId,
    })
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

  const summaryModalProps = {
    visible: modalVisible,
    inspectionSummary,
    exportLink: `${contextPath}/api/v1/inspection/summary/export?${queryString.stringify(query)}`,
    loading: loading.effects[`${namespace}/showSummary`],
    maskClosable: false,
    title: `${year}年${month}月各运维公司巡检统计表`,
    onCancel() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
    },
  }

  return (
    <Page inner>
      <div className={styles.main}>
        <div className={styles.filter}>
          <Select defaultValue="2019" style={{width: 100}}>
            <Select.Option value="2019">2019</Select.Option>
          </Select>
          &nbsp;
          <Radio.Group defaultValue={parseInt(month, 10)} onChange={handleChange}>
            {yearMonths.map(month => <Radio.Button key={month.value} disabled={!month.enabled} value={month.value}>{`${month.value}月`}</Radio.Button>)}
          </Radio.Group>
        </div>
        <div className={styles.header}>
          <div className={styles.buttons}>
            <Button onClick={handleSummary}>查看当月汇总</Button>
          </div>
          <div className={styles.legend}>
            图例：
            <ul>
              <li style={{backgroundColor: '#87d068'}}>正常</li>
              <li style={{backgroundColor: '#2db7f5'}}>延时</li>
              <li style={{backgroundColor: '#f6d059'}}>异常</li>
              <li style={{backgroundColor: '#9527ff'}}>延时+异常</li>
              <li style={{backgroundColor: '#f51e3a'}}>未巡检</li>
              <li style={{backgroundColor: '#8e4e2f'}}>外部原因</li>
            </ul>
          </div>
        </div>
        <ReportChart {...inspectionResults} onChartClick={handleChartClick}/>
      </div>
      {modalVisible && modalType === 'view' && <ViewModal  {...viewModalProps}/>}
      {modalVisible && modalType === 'summary' && <SummaryModal {...summaryModalProps}/>}
    </Page>
  )
}

export default connect((models) => {
  return {
    model: models[namespace],
    loading: models.loading,
  }
})(Component)
