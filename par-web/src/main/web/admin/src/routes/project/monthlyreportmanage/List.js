import React, {Fragment} from 'react'
import {Divider, Table, Tag} from 'antd'
import {Formatter} from '../../../components'
import queryString from 'query-string'
import {apiPrefix, contextPath} from "../../../utils/config"

const List = ({onViewItem, onAuditItem, location, monthlyReportStatus, ...tableProps}) => {
  location.query = queryString.parse(location.search)

  const handleAudit = (record) => {
    onAuditItem(record)
  }
  const handleView = (record) => {
    onViewItem(record)
  }

  const columns = [
    {
      title: '月报  ',
      dataIndex: 'system.name',
      align: 'left',
      width: 360,
      render: (value, record) => <span><Formatter.Date value={record.month} pattern="YYYY年M月"/> - {value}</span>,
    },
    {
      title: '重点工作',
      align: 'left',
      dataIndex: 'keyWork',
      render: (value) => value && value.substring(0, 40),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (value) => <Tag color={monthlyReportStatus[value][1]}>{monthlyReportStatus[value][0]}</Tag>,
    },
    {
      title: '提交时间',
      dataIndex: 'submitTime',
      width: 160,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '提交人',
      dataIndex: 'accendant.username',
      width: 100,
    },
    {
      title: '操作',
      width: 110,
      dataIndex: 'objectId',
      render: (text, record) => {
        return <div>
          <a onClick={() => {
            handleView(record)
          }}>查看</a>
          {
            record.status === 0 && <Fragment>
              <Divider type="vertical"/>
              <a onClick={() => {
                handleAudit(record)
              }}>审核</a>
            </Fragment>
          }
          {
            (record.status === 1 || record.status === 5) && <Fragment>
              <Divider type="vertical"/>
              <a target="_blank" href={`${contextPath}${apiPrefix}/monthlyReport/${record.objectId}/export`}>下载</a>
            </Fragment>
          }
        </div>
      },
    },
  ]

  return (
    <Table
      {...tableProps}
      bordered
      scroll={{x: 800}}
      columns={columns}
      rowKey='objectId'
    />
  )
}

export default List
