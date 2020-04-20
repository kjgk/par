import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Modal, Select } from 'antd'
import RoleSelect from '../../../sections/role/RoleSelect'

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
}

const Option = Select.Option

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
        if (data.role) {
          data.userRoleList = [{
            role: { objectId: data.role },
          }]
          delete data.role
        }
        onOk(data)
      })
    },
  }

  const roleId = item.userRoleList && item.userRoleList.length ?
    item.userRoleList[0].role.objectId : undefined

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <Form.Item label="昵称" {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: item.name,
            rules: [{
              required: true,
              message: '请输入昵称',
            }],
          })(<Input placeholder="请输入昵称"/>)}
        </Form.Item>
        <Form.Item label="角色" {...formItemLayout}>
          {getFieldDecorator('role', {
            initialValue: roleId,
            rules: [{
              required: true,
              message: '请选择角色',
            }],
          })(<RoleSelect placeholder="请选择角色"/>)}
        </Form.Item>
        <Form.Item label="简介" {...formItemLayout}>
          {getFieldDecorator('description', {
            initialValue: item.description,
          })(<Input.TextArea rows={3} placeholder="请输入简介"/>)}
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
