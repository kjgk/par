import React from 'react'
import PropTypes from 'prop-types'
import { Divider, Modal, Table } from 'antd'
import { Formatter } from '../../../components'
import queryString from 'query-string'

const List = ({ onDeleteItem, onViewItem, location, ...tableProps }) => {
  location.query = queryString.parse(location.search)

  const handleView = (record) => {
    onViewItem(record)
  }

  const handleDelete = (record) => {
    Modal.confirm({
      title: '确定要删除该数据吗?',
      okType: 'danger',
      onOk () {
        onDeleteItem(record.objectId)
      },
    })
  }

  const columns = [
    {
      title: '系统',
      dataIndex: 'systemName',
    },
    {
      title: '巡检人',
      dataIndex: 'accendantName',
      width: 180,
    },
    {
      title: '巡检时间',
      dataIndex: 'inspectionTime',
      width: 180,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '操作',
      width: 120,
      render: (text, record) => {
        return <div>
          <a onClick={() => {
            handleView(record)
          }}>查看</a>
          <Divider type="vertical"/>
          <a onClick={() => {
            handleDelete(record)
          }}>删除</a>
        </div>
      },
    },
  ]

  return (
    <Table
      {...tableProps}
      bordered
      scroll={{ x: 800 }}
      columns={columns}
      rowKey='objectId'
    />
  )
}

List.propTypes = {
  onDeleteItem: PropTypes.func,
  onEditItem: PropTypes.func,
  location: PropTypes.object,
}

export default List
