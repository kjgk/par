import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Modal } from 'antd'
import AuthorityCheckbox from '../../../sections/authority/AuthorityCheckbox'

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
        if (data.authority) {
          data.roleAuthorityList = data.authority.map((objectId) => ({
            authority: { objectId },
          }))
          delete data.authority
        }
        onOk(data)
      })
    },
  }

  const authorities = item.roleAuthorityList && item.roleAuthorityList.map((item) => {
    return item.authority.objectId
  })

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <Form.Item label="名称" hasFeedback {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.name,
            rules: [{
              required: true,
              message: '请输入角色名称',
            }],
          })(<Input placeholder="请输入名称"/>)}
        </Form.Item>
        <Form.Item label="标识" hasFeedback {...formItemLayout}>
          {getFieldDecorator('tag', {
            initialValue: item.tag,
          })(<Input placeholder="请输入标识"/>)}
        </Form.Item>
        <Form.Item label="权限" {...formItemLayout}>
          {getFieldDecorator('authority', {
            initialValue: authorities,
          })(<AuthorityCheckbox/>)}
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
