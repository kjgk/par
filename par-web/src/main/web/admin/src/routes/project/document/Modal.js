import React from 'react'
import PropTypes from 'prop-types'
import {Button, Form, Icon, Input, Modal, Upload} from 'antd'
import SystemSelect from "../../../sections/system/SystemSelect"
import {api, contextPath} from "../../../utils/config"

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
                 fileList = [],
                 onOk,
                 onUploadChange,
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
          category: {
            tag: item.category,
          },
        }
        onOk(data)
      })
    },
  }

  const uploaderProps = {
    action: `${contextPath}${api.fileUpload}`,
    multiple: false,
    name: 'attachment',
    fileList,
    beforeUpload() {
      if (fileList.length >= 1) {
        return false
      }
    },
    onChange(data) {
      onUploadChange(data)
    }
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <Form.Item label="所属系统" hasFeedback {...formItemLayout}>
          {getFieldDecorator('system.objectId', {
            initialValue: item.system && item.system.objectId,
            rules: [{
              required: true,
              message: '请选择所属系统',
            }],
          })(<SystemSelect self placeholder="请选择所属系统"/>)}
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
        <Form.Item label="描述" hasFeedback {...formItemLayout}>
          {getFieldDecorator('description', {
            initialValue: item.description,
          })(<Input.TextArea rows={3} placeholder="请输入描述"/>)}
        </Form.Item>
        <Form.Item label="附件"  {...formItemLayout}>
          {getFieldDecorator('attachment', {
            rules: [{
              required: true,
              message: '请上传附件',
            }],
          })(<Upload {...uploaderProps}>
            {
              fileList.length === 0 && <Button>
                <Icon type="upload"/> 上传附件
              </Button>
            }
          </Upload>)}
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
