import React from 'react'
import PropTypes from 'prop-types'
import {Button, Form, Icon, Input, Modal, Radio, Upload} from 'antd'
import {api, contextPath} from "../../../../utils/config"

const RadioGroup = Radio.Group

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

  const uploaderProps = {
    action: `${contextPath}${api.fileUpload}`,
    multiple: true,
    name: 'attachment',
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <Form.Item label="处理结果" {...formItemLayout}>
          {getFieldDecorator('result', {
            initialValue: 1,
          })(<RadioGroup>
            <Radio value={1}>已解决</Radio>
            <Radio value={2}>未解决</Radio>
          </RadioGroup>)}
        </Form.Item>
        <Form.Item label="备注" hasFeedback {...formItemLayout}>
          {getFieldDecorator('remark', {
            initialValue: item.remark,
          })(<Input.TextArea rows={3} placeholder="请输入备注"/>)}
        </Form.Item>
        <Form.Item label="附件" hasFeedback {...formItemLayout}>
          <Upload {...uploaderProps}>
            <Button >
              <Icon type="upload"/> 上传附件
            </Button>
          </Upload>
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
