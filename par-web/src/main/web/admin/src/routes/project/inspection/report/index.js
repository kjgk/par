import React from 'react'
import {connect} from 'dva'
import styles from './index.module.less'
import {Page} from "../../../../components"
import {Icon, Table, DatePicker} from "antd"
import {routerRedux} from "dva/router"
import queryString from "query-string"
import moment from "moment"

const {WeekPicker} = DatePicker
const namespace = 'inspectionReport'
const Component = ({
                     location, dispatch, model, loading,
                   }) => {

  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {weekDays, reportList} = model

  const dataSource = reportList.map((item) => {
    let data = {
      systemName: item.systemName,
    }
    item.values.forEach((value, i) => {
      data[`d${i}`] = value
    })
    return data
  })

  const columns = [
    {
      title: '系统名称',
      dataIndex: 'systemName',
      key: 'systemName',
      width: 320,
    },
    ...weekDays.map((week, i) => ({
      title: week,
      dataIndex: `d${i}`,
      key: `d${i}`,
      width: '11%',
      render(value) {
        return {
          null: undefined,
          0: <Icon type="exclamation-circle" theme="twoTone" twoToneColor="#f80"/>,
          1: <Icon type="check-circle" theme="twoTone" twoToneColor="#6b7"/>,
          2: <Icon type="close-circle" theme="twoTone" twoToneColor="#e22"/>,
        }[value]
      }
    })),
  ]

  const handleRefresh = (newQuery) => {
    dispatch(routerRedux.push({
      pathname,
      search: queryString.stringify({
        ...query,
        ...newQuery,
      }),
    }))
  }

  let date = moment()
  if (query.date) {
    date.year(parseInt(query.date.substring(0, 4), 10))
    date.week(parseInt(query.date.substring(4), 10))
  }

  return (
    <Page inner>
      <div className={styles.main}>
        <div className={styles.filter}>
          <WeekPicker allowClear={false} value={date} disabledDate={(d) => d.isAfter(moment())}
                      onChange={(v) => handleRefresh({date: v.year() + '' + v.week()})}/>
        </div>
        <Table rowKey='systemName' dataSource={dataSource} columns={columns} pagination={false} size="small" scroll={{y: 640}} bordered/>
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
