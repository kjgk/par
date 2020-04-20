import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Modal } from 'antd'
import SystemSelect from "../../../sections/system/SystemSelect"

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
}

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

  const modalOpts = {
    ...modalProps,
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
      <Form layout="horizontal">
        <Form.Item label="所属系统" {...formItemLayout}>
          {getFieldDecorator('system.objectId', {
            initialValue: item.system && item.system.objectId,
            rules: [{
              required: true,
              message: '请选择所属系统',
            }],
          })(<SystemSelect placeholder="请选择所属系统"/>)}
        </Form.Item>
        <Form.Item label="功能点" {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.name,
            rules: [{
              required: true,
              message: '请输入功能点名称',
            }],
          })(<Input placeholder="请输入名称"/>)}
        </Form.Item>
        <Form.Item label="排序号" {...formItemLayout}>
          {getFieldDecorator('orderNo', {
            initialValue: item.orderNo,
            rules: [{
              required: true,
              message: '请输入排序号',
            }],
          })(<Input placeholder="请输入排序号"/>)}
        </Form.Item>
        <Form.Item label="描述" {...formItemLayout}>
          {getFieldDecorator('description', {
            initialValue: item.description,
          })(<Input.TextArea rows={3} placeholder="请输入描述"/>)}
        </Form.Item>
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  form: PropTypes.object.isRequired,
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default Form.create()(modal)
