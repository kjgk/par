import React from 'react'
import {Form, Input, Modal} from 'antd'

const formItemLayout = {
  labelCol: {
    span: 4,
  },
  wrapperCol: {
    span: 18,
  },
}

const ModifyPassword = ({
                          onOk,
                          dispatch,
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
        onOk(getFieldsValue())
      })
    },
  }


  return (
    <Modal  {...modalOpts}>
      <Form layout="horizontal">
        <Form.Item label="原始密码" hasFeedback {...formItemLayout}>
          {getFieldDecorator('originPassword', {
            rules: [{
              required: true,
              message: '请输入原始密码',
            }],
          })(<Input type="password" placeholder="请输入原始密码"/>)}
        </Form.Item>
        <Form.Item label="新密码" hasFeedback {...formItemLayout} >
          {getFieldDecorator('newPassword', {
            rules: [{
              required: true,
              message: '请输入新密码',
            }],
          })(<Input type="password" placeholder="请输入新密码"/>)}
        </Form.Item>

        <Form.Item label="确认密码" hasFeedback {...formItemLayout} >
          {getFieldDecorator('confirmNewPassword', {
            rules: [{
              required: true,
              message: '请输入确认密码',
            }],
          })(<Input type="password" placeholder="请输入确认密码"/>)}
        </Form.Item>
      </Form>

    </Modal>
  )
}

export default Form.create()(ModifyPassword)
