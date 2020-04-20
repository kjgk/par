import React from 'react'
import PropTypes from 'prop-types'
import {connect} from 'dva'
import {Button, Col, Divider, Form, Icon, Input, Radio, Row, Upload} from "antd"
import {Page} from "../../../../components"
import SystemSelect from "../../../../sections/system/SystemSelect"
import {api, contextPath} from "../../../../utils/config"
import styles from "../index.module.less"
import {Link} from "react-router-dom"

const RadioGroup = Radio.Group

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 12,
  },
}

const tailFormItemLayout = {
  wrapperCol: {
    xs: {
      span: 24,
      offset: 0,
    },
    sm: {
      span: 18,
      offset: 6,
    },
  },
};

const namespace = 'ticketRecord'

const Component = ({
                     loading,
                     dispatch,
                     ticketRecord,
                     form: {
                       getFieldDecorator,
                       validateFields,
                       getFieldsValue,
                     },
                   }) => {

  const {fileList1, fileList2, fileLimit, showSuccess} = ticketRecord

  const confirmLoading = loading.effects[`${namespace}/save`]

  const uploaderProps1 = {
    action: `${contextPath}${api.fileUpload}`,
    multiple: true,
    name: 'attachment',
    fileList1,
    beforeUpload() {
      if (fileList1.length >= fileLimit) {
        return false
      }
    },
    onChange(data) {
      dispatch({
        type: `${namespace}/uploadChange1`,
        payload: data,
      })
    }
  }

  const uploaderProps2 = {
    action: `${contextPath}${api.fileUpload}`,
    multiple: true,
    name: 'attachment',
    fileList2,
    beforeUpload() {
      if (fileList2.length >= fileLimit) {
        return false
      }
    },
    onChange(data) {
      dispatch({
        type: `${namespace}/uploadChange2`,
        payload: data,
      })
    }
  }

  const onSubmit = (e) => {
    e.preventDefault()
    validateFields((errors, values) => {
      if (errors) {
        return
      }
      dispatch({
        type: `${namespace}/save`,
        payload: values,
      })
    })
  }

  const resetForm = () => {
    dispatch({
      type: `${namespace}/resetForm`,
    })
  }

  return (
    <Page inner>
      {showSuccess && <div className={styles.success} onClick={resetForm}>
        <h1>
          <Icon type="check-circle"/> 工单保存成功！</h1>
        <div>
          您可以&nbsp;
          <a onClick={resetForm}>继续添加工单</a><Divider type="vertical"/>
          <Link to="/ticket/handle">返回工单列表</Link>
        </div>
      </div>}
      {!showSuccess && <Row>
        <Col md={24} xl={20} xxl={16}>
          <Form layout="horizontal" onSubmit={onSubmit}>
            <Divider>工单内容</Divider>
            <Form.Item label="优先级" {...formItemLayout}>
              {getFieldDecorator('ticket.priority', {
                initialValue: 2,
              })(<RadioGroup>
                <Radio value={1}>重要</Radio>
                <Radio value={2}>一般</Radio>
              </RadioGroup>)}
            </Form.Item>
            <Form.Item label="问题描述" {...formItemLayout}>
              {getFieldDecorator('ticket.content', {
                initialValue: '',
                rules: [{
                  required: true,
                  message: '请输入问题描述',
                }],
              })(<Input.TextArea autoFocus rows={3} placeholder="请输入问题描述"/>)}
            </Form.Item>
            <Form.Item label="所属系统" {...formItemLayout}>
              {getFieldDecorator('ticket.system.objectId', {
                initialValue: undefined,
                rules: [{
                  required: true,
                  message: '请选择所属系统',
                }],
              })(<SystemSelect self={true} placeholder="请选择所属系统"/>)}
            </Form.Item>
            <Form.Item label="报修人" {...formItemLayout}>
              {getFieldDecorator('ticket.contacts', {
                initialValue: '',
              })(<Input placeholder="请输入报修人"/>)}
            </Form.Item>
            <Form.Item label="报修人手机号" {...formItemLayout}>
              {getFieldDecorator('ticket.phoneNo', {
                initialValue: '',
              })(<Input placeholder="请输入报修人手机号"/>)}
            </Form.Item>
            <Form.Item label="附件" {...formItemLayout}>
              <Upload {...uploaderProps1}>
                <Button disabled={fileList1.length >= fileLimit}>
                  <Icon type="upload"/> 上传附件
                </Button>
                <span style={{color: '#666'}}> 您可以上传{fileLimit}个附件</span>
              </Upload>
            </Form.Item>

            <Divider>处理结果</Divider>
            <Form.Item label="处理结果" {...formItemLayout}>
              {getFieldDecorator('result', {
                initialValue: 1,
              })(<RadioGroup>
                <Radio value={1}>已解决</Radio>
                <Radio value={2}>未解决</Radio>
              </RadioGroup>)}
            </Form.Item>
            <Form.Item label="备注" {...formItemLayout}>
              {getFieldDecorator('remark', {
                initialValue: '',
              })(<Input.TextArea rows={3} placeholder="请输入备注"/>)}
            </Form.Item>
            <Form.Item label="附件" {...formItemLayout}>
              <Upload {...uploaderProps2}>
                <Button disabled={fileList2.length >= fileLimit}>
                  <Icon type="upload"/> 上传附件
                </Button>
                <span style={{color: '#666'}}> 您可以上传{fileLimit}个附件</span>
              </Upload>
            </Form.Item>

            <Form.Item {...tailFormItemLayout}>
              <Button loading={confirmLoading} htmlType="submit" type="primary">保存工单</Button>
            </Form.Item>
          </Form>
        </Col>
      </Row>}
    </Page>
  )
}

Component.propTypes = {
  form: PropTypes.object.isRequired,
  dispatch: PropTypes.func,
  ticketRecord: PropTypes.object,
  loading: PropTypes.object,
}

export default connect(({ticketRecord, loading}) => ({
  ticketRecord,
  loading,
}))(Form.create()(Component))
