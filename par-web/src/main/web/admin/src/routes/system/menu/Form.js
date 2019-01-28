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

const RadioGroup = Radio.Group

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
            message: '请输入菜单名称',
          }],
        })(<Input placeholder="请输入名称"/>)}
      </FormItem>
      <FormItem label="标识" hasFeedback {...formItemLayout}>
        {getFieldDecorator('tag', {
          initialValue: item.tag,
        })(<Input placeholder="请输入标识"/>)}
      </FormItem>
      <FormItem label="图标" hasFeedback {...formItemLayout}>
        {getFieldDecorator('icon', {
          initialValue: item.icon,
        })(<Input placeholder="请输入图标"/>)}
      </FormItem>
      <FormItem label="URL" hasFeedback {...formItemLayout}>
        {getFieldDecorator('url', {
          initialValue: item.url,
        })(<Input placeholder="请输入URL"/>)}
      </FormItem>
      <FormItem label="是否启用" {...formItemLayout}>
        {getFieldDecorator('enabled', {
          initialValue: item.enabled,
        })(<RadioGroup>
          <Radio value={1}>是</Radio>
          <Radio value={0}>否</Radio>
        </RadioGroup>)}
      </FormItem>
      <FormItem label="是否隐藏" {...formItemLayout}>
        {getFieldDecorator('hidden', {
          initialValue: item.hidden,
        })(<RadioGroup>
          <Radio value={1}>是</Radio>
          <Radio value={0}>否</Radio>
        </RadioGroup>)}
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
