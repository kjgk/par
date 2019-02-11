import React from 'react'
import PropTypes from 'prop-types'
import {Button, Form, Input, Modal} from 'antd'
import TicketStatus from "../../../sections/ticket/TicketStatus"
import {Formatter} from "../../../components"
import {contextPath} from '../../../utils/config'

const formItemLayout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 16,
  },
}

const modal = ({
                 item = {},
                 onOk,
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    width: 800,
    footer: <Button type="primary" onClick={modalProps.onCancel}>确定</Button>,
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <Form.Item label="所属系统" {...formItemLayout}>
          <div>{item.systemName}</div>
        </Form.Item>
        <Form.Item label="工单描述" {...formItemLayout}>
          <div>{item.content}</div>
        </Form.Item>
        <Form.Item label="联系人" {...formItemLayout}>
          <div>{item.contacts}</div>
        </Form.Item>
        <Form.Item label="联系方式" {...formItemLayout}>
          <div>{item.phoneNo}</div>
        </Form.Item>
        <Form.Item label="提交时间" {...formItemLayout}>
          <div><Formatter.Date value={item.submitTime}/></div>
        </Form.Item>
        <Form.Item label="状态" {...formItemLayout}>
          <div><TicketStatus value={item.status}/></div>
        </Form.Item>
        <Form.Item label="附件" {...formItemLayout}>
          {item.attachments.map((attachment) =>
            <div key={attachment.url}>
              <a href={`${contextPath}${attachment.url}`} target="_blank">{attachment.filename}</a>
            </div>
          )}
        </Form.Item>
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default modal
