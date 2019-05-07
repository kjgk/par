import React from 'react'
import PropTypes from 'prop-types'
import { Divider, Modal, Table } from 'antd'
import { Formatter } from '../../../components'
import queryString from 'query-string'

const List = ({ onDeleteItem, onEditItem, onEditFunction, location, ...tableProps }) => {
  location.query = queryString.parse(location.search)

  const handleEdit = (record) => {
    onEditItem(record)
  }

  const handleEditFunction = (record) => {
    onEditFunction(record)
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
      title: '所属单位',
      dataIndex: 'company.name',
    },
    {
      title: '系统名称',
      dataIndex: 'name',
    },
    {
      title: '系统网址',
      dataIndex: 'url',
    },
    {
      title: '项目负责人',
      dataIndex: 'supervisor.username',
    },
    {
      title: '创建时间',
      dataIndex: 'createdDate',
      width: 180,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '操作',
      width: 200,
      render: (text, record) => {
        return <div>
          <a onClick={() => {
            handleEdit(record)
          }}>编辑</a>
          {/*<Divider type="vertical"/>*/}
          {/*<a onClick={() => {*/}
            {/*handleEditFunction(record)*/}
          {/*}}>设置功能点</a>*/}
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
