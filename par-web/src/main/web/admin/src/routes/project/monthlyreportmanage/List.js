import React from 'react'
import {Divider, Table} from 'antd'
import {Formatter} from '../../../components'
import queryString from 'query-string'

const List = ({onDeleteItem, onEditItem, location, monthlyReportStatus, ...tableProps}) => {
  location.query = queryString.parse(location.search)

  const handleEdit = (record) => {
    onEditItem(record)
  }
  const handleView = (record) => {
    onEditItem(record)
  }

  const columns = [
    {
      title: '所属系统',
      dataIndex: 'system.name',
      align: 'left',
      render: (value, record) => <span><Formatter.Date value={record.month} pattern="YYYY年M月"/> - {value}</span>,
    },
    {
      title: '主要内容',
      align: 'left',
      dataIndex: 'maintenance',
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (value) => monthlyReportStatus[value],
    },
    {
      title: '提交时间',
      dataIndex: 'createdDate',
      width: 180,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '操作',
      width: 120,
      render: (text, record) => {
        return <div>
          <a onClick={() => {
            handleEdit(record)
          }}>审核</a>
          <Divider type="vertical"/>
          <a onClick={() => {
            handleView(record)
          }}>查看</a>
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
