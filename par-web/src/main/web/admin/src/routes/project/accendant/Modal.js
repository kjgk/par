import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Modal } from 'antd'
import CompanySelect from '../../../sections/company/CompanySelect'

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
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
        <Form.Item label="单位" hasFeedback {...formItemLayout}>
          {getFieldDecorator('company.objectId', {
            initialValue: item.company && item.company.objectId,
            rules: [{
              required: true,
              message: '请选择单位',
            }],
          })(<CompanySelect placeholder="请选择单位"/>)}
        </Form.Item>
        <Form.Item label="姓名" hasFeedback {...formItemLayout}>
          {getFieldDecorator('username', {
            initialValue: item.username,
            rules: [{
              required: true,
              message: '请输入姓名',
            }],
          })(<Input placeholder="请输入姓名"/>)}
        </Form.Item>
        <Form.Item label="手机号" hasFeedback {...formItemLayout}>
          {getFieldDecorator('phoneNo', {
            initialValue: item.phoneNo,
            rules: [{
              required: true,
              pattern: /^1[34578][0-9]{9}$/,
              message: '请输入手机号',
            }],
          })(<Input placeholder="请输入手机号"/>)}
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
