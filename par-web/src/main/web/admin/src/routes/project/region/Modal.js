import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Modal } from 'antd'

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
        <Form.Item label="名称" {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.name,
            rules: [{
              required: true,
              message: '请输入名称',
            }],
          })(<Input placeholder="请输入名称"/>)}
        </Form.Item>
        <Form.Item label="标识" {...formItemLayout}>
          {getFieldDecorator('tag', {
            initialValue: item.tag,
          })(<Input placeholder="请输入标识"/>)}
        </Form.Item>
        <Form.Item label="简称" {...formItemLayout}>
          {getFieldDecorator('shortName', {
            initialValue: item.shortName,
          })(<Input placeholder="请输入简称"/>)}
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
