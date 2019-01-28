import React from 'react'
import PropTypes from 'prop-types'
import { Button, Form, Input, Radio } from 'antd'

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 4 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 20 },
  },
}

const tailFormItemLayout = {
  wrapperCol: {
    xs: {
      span: 24,
      offset: 0,
    },
    sm: {
      span: 24,
      offset: 4,
    },
  },
}

const FormItem = Form.Item

const form = ({
                item = {},
                onOk,
                form: {
                  getFieldDecorator,
                  validateFields,
                  getFieldsValue,
                },
              }) => {

  const handleSubmit = function (e) {
    e.preventDefault()
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
  }

  return (
    <Form onSubmit={handleSubmit}>
      <FormItem label="名称" hasFeedback {...formItemLayout}>
        {getFieldDecorator('name', {
          initialValue: item.name,
          rules: [{
            required: true,
            message: '请输入名称',
          }],
        })(<Input placeholder="请输入名称"/>)}
      </FormItem>
      <FormItem label="标识" hasFeedback {...formItemLayout}>
        {getFieldDecorator('tag', {
          initialValue: item.tag,
        })(<Input placeholder="请输入标识"/>)}
      </FormItem>
      <FormItem label="描述" hasFeedback {...formItemLayout}>
        {getFieldDecorator('description', {
          initialValue: item.description,
        })(<Input.TextArea rows={3} placeholder="请输入描述"/>)}
      </FormItem>
      <FormItem {...tailFormItemLayout}>
        <Button type="primary" htmlType="submit">保存</Button>
      </FormItem>
    </Form>
  )
}

form.propTypes = {
  form: PropTypes.object.isRequired,
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default Form.create({
  mapPropsToFields: props => {
  },
})(form)
