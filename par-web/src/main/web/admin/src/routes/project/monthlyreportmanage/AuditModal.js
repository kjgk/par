import React from 'react'
import {Button, Form, Input, Modal, Radio} from 'antd'
import moment from "moment"

const RadioGroup = Radio.Group

const modal = ({
                 item = {},
                 onOk,
                 form: {
                   getFieldDecorator,
                   validateFields,
                   getFieldsValue,
                 },
                 ...modalProps
               }) => {

  const formItemLayout = {
    labelCol: {
      span: 4,
    },
    wrapperCol: {
      span: 18,
    },
  }

  const modalOpts = {
    ...modalProps,
    title: `${item.system.name} - ${moment(item.month).format('YYYY年M月')} - 月报`,
    onOk: () => {
      validateFields((errors) => {
        if (errors) {
          return
        }
        const data = {
          ...getFieldsValue(),
          objectId: item.objectId,
        }
        onOk(data)
      })
    },
  }

  return (
    <Modal {...modalOpts}>
      <Form>
        <Form.Item label="审核结果" {...formItemLayout}>
          {getFieldDecorator('result', {
            initialValue: 1,
            rules: [{
              required: true,
            }],
          })(<RadioGroup>
            <Radio value={1}>通过</Radio>
            <Radio value={0}>退回</Radio>
          </RadioGroup>)}
        </Form.Item>
        <Form.Item label="退回意见" {...formItemLayout}>
          {getFieldDecorator('message', {
            initialValue: '',
          })(<Input placeholder="请输入退回意见"/>)}
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default Form.create()(modal)
