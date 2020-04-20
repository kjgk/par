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
                   getFieldValue,
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

  let confirmDirty = false

  const handleConfirmBlur = (e) => {
    confirmDirty = confirmDirty || !!e.target.value
  }

  const compareToFirstPassword = (rule, value, callback) => {
    if (value && value !== getFieldValue('password')) {
      callback('两次密码输入不一致!')
    } else {
      callback()
    }
  }

  const validateToNextPassword = (rule, value, callback) => {
    if (value && confirmDirty) {
      validateFields(['confirm'], { force: true })
    }
    callback()
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <Form.Item label="帐号" {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.account ? item.account.name : '',
            rules: [{
              required: true,
              message: '请输入帐号',
            }],
          })(<Input placeholder="请输入帐号"/>)}
        </Form.Item>
        <Form.Item label="密码" {...formItemLayout}>
          {getFieldDecorator('password', {
            rules: [{
              required: true,
              message: '请输入密码',
            }, {
              validator: validateToNextPassword,
            }],
          })(<Input type="password" placeholder="请输入密码"/>)}
        </Form.Item>
        <Form.Item label="确认密码" {...formItemLayout}>
          {getFieldDecorator('confirm', {
            rules: [{
              required: true,
              message: '请再次输入密码',
            }, {
              validator: compareToFirstPassword,
            }],
          })(<Input type="password" placeholder="请再次输入密码" onBlur={handleConfirmBlur}/>)}
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
