import React from 'react'
import {Alert, Button, Card, Divider, Form, Icon, List, Modal, Table} from 'antd'

const modal = ({
                 inspectionSummary = {},
                 loading,
                 onOk,
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    width: 900,
    footer: <Button type="primary" onClick={modalProps.onCancel}>确定</Button>,
  }

  const columns = [
    {
      title: '序号',
      dataIndex: 'systemId',
      width: '6%',
      render: (value, record, index) => index + 1,
    },
    {
      title: '系统名称',
      dataIndex: 'systemName',
      width: '34%',
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#87d068'}}>正常</span>,
      dataIndex: 'good',
      width: '12%',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#2db7f5'}}>延时</span>,
      width: '12%',
      dataIndex: 'goodAndDelay',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#f6d059'}}>异常</span>,
      width: '12%',
      dataIndex: 'bad',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#9527ff'}}>延时+异常</span>,
      width: '12%',
      dataIndex: 'badAndDelay',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#f51e3a'}}>未巡检</span>,
      width: '12%',
      dataIndex: 'no',
      render: (value) => value === 0 ? '-' : value,
    },
  ]

  return (
    <Modal {...modalOpts}>
      <Table loading={loading} columns={columns} dataSource={inspectionSummary.detailList} bordered
             rowKey={'systemName'} size={"small"} pagination={false} scroll={{y: 580}}/>
    </Modal>
  )
}

export default modal
