import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Modal } from 'antd'
import CompanySelect from "../../../sections/company/CompanySelect"

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
        <Form.Item label="所属单位" hasFeedback {...formItemLayout}>
          {getFieldDecorator('company.objectId', {
            initialValue: item.company && item.company.objectId,
            rules: [{
              required: true,
              message: '请选择所属单位',
            }],
          })(<CompanySelect placeholder="请选择所属单位"/>)}
        </Form.Item>
        <Form.Item label="名称" hasFeedback {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.name,
            rules: [{
              required: true,
              message: '请输入名称',
            }],
          })(<Input placeholder="请输入名称"/>)}
        </Form.Item>
        <Form.Item label="系统网址" hasFeedback {...formItemLayout}>
          {getFieldDecorator('url', {
            initialValue: item.url,
          })(<Input.TextArea rows={3} placeholder="请输入系统网址"/>)}
        </Form.Item>
        <Form.Item label="描述" hasFeedback {...formItemLayout}>
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
