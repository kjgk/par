import React from 'react'
import PropTypes from 'prop-types'
import {Divider, Modal, Table} from 'antd'
import {Formatter} from '../../../components'
import queryString from 'query-string'
import TicketStatus from '../../../sections/ticket/TicketStatus'

const List = ({onDeleteItem, onViewItem, onAcceptItem, onProcessItem, location, ...tableProps}) => {
  location.query = queryString.parse(location.search)

  const handleView = (record) => {
    onViewItem(record)
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

  const handleAccept = (record) => {
    Modal.confirm({
      title: '确定要接单吗?',
      onOk() {
        onAcceptItem(record.objectId)
      },
    })
  }

  const handleProcess = (record) => {
    onProcessItem(record)
  }

  const columns = [
    {
      title: '所属系统',
      dataIndex: 'systemName',
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
      title: '提交时间',
      dataIndex: 'submitTime',
      width: 180,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '操作',
      width: 180,
      render: (text, record) => {
        return <div>
          <a onClick={() => {
            handleView(record)
          }}>查看详情</a>
          {onAcceptItem && record.status === 0 && <React.Fragment><Divider type="vertical"/>
            <a style={{color: '#F50'}} onClick={() => {
              handleAccept(record)
            }}>接单</a></React.Fragment>}
          {onProcessItem && record.status === 1 && <React.Fragment><Divider type="vertical"/>
            <a style={{color: '#6dbb91'}} onClick={() => {
              handleProcess(record)
            }}>结单</a></React.Fragment>}
          {onDeleteItem && <React.Fragment><Divider type="vertical"/>
            <a onClick={() => {
              handleDelete(record)
            }}>删除</a></React.Fragment>}
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
