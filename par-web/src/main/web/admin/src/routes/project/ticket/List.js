import React from 'react'
import PropTypes from 'prop-types'
import {Divider, Modal, Table} from 'antd'
import {Formatter} from '../../../components'
import queryString from 'query-string'
import TicketStatus from '../../../sections/ticket/TicketStatus'

const List = ({onDeleteItem, onEditItem, location, ...tableProps}) => {
  location.query = queryString.parse(location.search)

  const handleEdit = (record) => {
    onEditItem(record)
  }

  const handleDelete = (record) => {
    Modal.confirm({
      title: '确定要删除该数据吗?',
      okType: 'danger',
      onOk() {
        onDeleteItem(record.objectId)
      },
    })
  }

  const columns = [
    {
      title: '所属系统',
      dataIndex: 'system.name',
    },
    {
      title: '工单描述',
      dataIndex: 'content',
      render: (value) => value && (value.length > 40 ? value.substring(0, 40) + '...' : value)
    },
    {
      title: '联系人',
      dataIndex: 'contacts',
      width: 100,
    },
    {
      title: '联系方式',
      dataIndex: 'phoneNo',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 72,
      render: (value) => <TicketStatus value={value}/>
    },
    {
      title: '创建时间',
      dataIndex: 'submitTIme',
      width: 180,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '操作',
      width: 180,
      render: (text, record) => {
        return <div>
          <a onClick={() => {
            handleEdit(record)
          }}>查看详情</a>
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
      scroll={{x: 800}}
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
