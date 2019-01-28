import React from 'react'
import PropTypes from 'prop-types'
import { Divider, Modal, Table } from 'antd'
import { Formatter } from '../../../components'
import queryString from 'query-string'

const List = ({ onDeleteItem, onEditItem, onEditAccount, location, ...tableProps }) => {
  location.query = queryString.parse(location.search)

  const handleEdit = (record) => {
    onEditItem(record)
  }

  const handleAccount = (record) => {
    onEditAccount(record)
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
      title: '昵称',
      dataIndex: 'name',
    },
    {
      title: '帐号',
      dataIndex: 'account.name',
    },
    {
      title: '角色',
      dataIndex: 'userRoleList',
      render: (value) => value.map((userRole) => userRole.role.name)
        .join(),
    },
    {
      title: '简介',
      dataIndex: 'description',
    },
    {
      title: '创建时间',
      dataIndex: 'createdDate',
      width: 180,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '操作',
      width: 180,
      render: (text, record) => {
        return <div>
          <a onClick={() => {
            handleAccount(record)
          }}>帐号</a>
          <Divider type="vertical"/>
          <a onClick={() => {
            handleEdit(record)
          }}>编辑</a>
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
