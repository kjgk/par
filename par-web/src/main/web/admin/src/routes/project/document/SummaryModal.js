import React from 'react'
import {Button, Form, Icon, Input, Modal, DatePicker, Table} from 'antd'
import moment from "moment"

function disabledDate(current) {
  return current && current > moment().endOf('day')
}

const {RangePicker,} = DatePicker

const modal = ({
                 onSummary,
                 summaryList,
                 loading,
                 defaultDateRange,
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    width: 800,
    footer: <Button type="primary" onClick={modalProps.onCancel}>确定</Button>,
  }

  const handleSummary = ([startDate, endDate]) => {
    if (startDate && endDate) {
      onSummary({
        startDate,
        endDate,
      })
    }
  }

  const columns = [
    {
      title: '序号',
      dataIndex: 'systemId',
      width: 60,
      render: (value, record, index) => index + 1,
    },
    {
      key: 'systemName',
      title: '系统名称',
      width: 500,
      dataIndex: 'systemName',
    },
    {
      key: 'count',
      title: '已上传文件个数',
      dataIndex: 'count',
    }
  ]

  return (
    <Modal {...modalOpts}>
      <div style={{textAlign: "center"}}>
        查询时间段：
        <RangePicker
          defaultValue={defaultDateRange}
          onChange={handleSummary} disabledDate={disabledDate}/> &nbsp;
      </div>
      <Table style={{marginTop: 5}} loading={loading} columns={columns} dataSource={summaryList} bordered
             rowKey={'systemName'} size={"small"} pagination={false} scroll={{y: 580}}/>
    </Modal>
  )
}

export default modal
