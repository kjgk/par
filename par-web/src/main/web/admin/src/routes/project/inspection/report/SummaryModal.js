import React from 'react'
import {Button, Modal, Table} from 'antd'

const modal = ({
                 inspectionSummary = {},
                 exportLink,
                 loading,
                 onOk,
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    width: 960,
    footer: [
      <Button type="primary" style={{marginRight: 5}} href={exportLink} target="_blank">导出</Button>,
      <Button type="default" onClick={modalProps.onCancel}>关闭</Button>,
    ],
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
      width: '10%',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#2db7f5'}}>延时</span>,
      width: '10%',
      dataIndex: 'goodAndDelay',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#f6d059'}}>异常</span>,
      width: '10%',
      dataIndex: 'bad',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#9527ff'}}>延时+异常</span>,
      width: '10%',
      dataIndex: 'badAndDelay',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#8e4e2f'}}>外部原因</span>,
      width: '10%',
      dataIndex: 'externalCauses',
      render: (value) => value === 0 ? '-' : value,
    },
    {
      title: <span style={{fontWeight: 'bold', color: '#f51e3a'}}>未巡检</span>,
      width: '10%',
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
