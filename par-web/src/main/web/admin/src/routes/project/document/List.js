import React, {Fragment} from 'react'
import PropTypes from 'prop-types'
import {Divider, Modal, Table} from 'antd'
import {Formatter} from '../../../components'
import queryString from 'query-string'
import {contextPath} from "../../../utils/config"

const List = ({onDeleteItem, onEditItem, location, ...tableProps}) => {
  location.query = queryString.parse(location.search)

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
      title: '名称',
      dataIndex: 'name',
    },
    {
      title: '所属系统',
      dataIndex: 'system.name',
    },
    {
      title: '上传者',
      width: 120,
      dataIndex: 'uploader.name',
    },
    {
      title: '上传时间',
      dataIndex: 'createdDate',
      width: 180,
      render: (value) => <Formatter.Date value={value}/>,
    },
    {
      title: '操作',
      width: 120,
      render: (text, record) => {
        return <div>
          {
            record.attachment &&
            <Fragment>
              <a href={`${contextPath}${record.attachment.url}`} target="_blank">下载</a>
              <Divider type="vertical"/>
            </Fragment>
          }
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
