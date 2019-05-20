import React from 'react'
import {connect} from 'dva'
import styles from './index.module.less'
import {Page} from "../../../../components"
import {Divider, Radio, Select} from "antd"
import {routerRedux} from "dva/router"
import queryString from "query-string"
import ReportChart from "./Chart"

const namespace = 'inspectionReport'
const Component = ({
                     location, dispatch, model, loading,
                   }) => {

  const {pathname} = location
  const query = queryString.parse(location.search)
  const {year, month} = query
  const {yearMonths, inspectionResults} = model

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

  return (
    <Page inner>
      <div className={styles.main}>
        <div className={styles.filter}>
          <Select defaultValue="2019" style={{ width: 100 }}>
            <Select.Option value="2019">2019</Select.Option>
          </Select>
          &nbsp;
          <Radio.Group defaultValue={parseInt(month, 10)} onChange={handleChange}>
            {yearMonths.map(month => <Radio.Button key={month.value} disabled={!month.enabled} value={month.value}>{`${month.value}月`}</Radio.Button>)}
          </Radio.Group>
        </div>
        <div>
          <div className={styles.legend}>
            图例：
            <ul>
              <li style={{backgroundColor: '#87d068'}}>正常</li>
              <li style={{backgroundColor: '#2db7f5'}}>延时</li>
              <li style={{backgroundColor: '#ffc229'}}>异常</li>
              <li style={{backgroundColor: '#ff7700'}}>延时+异常</li>
              <li style={{backgroundColor: '#f51e3a'}}>未巡检</li>
            </ul>
          </div>
          <ReportChart {...inspectionResults}/>
        </div>
      </div>
    </Page>
  )
}

export default connect((models) => {
  return {
    model: models[namespace],
    loading: models.loading,
  }
})(Component)
