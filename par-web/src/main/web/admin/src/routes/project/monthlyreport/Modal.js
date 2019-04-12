import React from 'react'
import PropTypes from 'prop-types'
import {Col, Divider, Form, Input, Modal, Row} from 'antd'
import styles from "./index.module.less"

const {TextArea} = Input

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
                 viewMode,
                 form: {
                   getFieldDecorator,
                   validateFields,
                   getFieldsValue,
                 },
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    title: item.system.name + ' - 月报',
    onOk: () => {
      validateFields((errors) => {
        if (errors) {
          return
        }
        const data = {
          ...getFieldsValue(),
          objectId: item.objectId,
          system: item.system,
        }
        onOk(data)
      })
    },
  }

  return (
    <Modal {...modalOpts}>
      <Form className={styles.modal_form}>
        <Row gutter={0}>
          <Col span={11}>
            <Form.Item label={<span>组织或参加各类项目会议</span>}>
              {getFieldDecorator('meeting', {
                initialValue: item.meeting,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>系统日常巡检</span>}>
              {getFieldDecorator('daily', {
                initialValue: item.daily,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>咨询类服务</span>}>
              {getFieldDecorator('consultation', {
                initialValue: item.consultation,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>网络协助类</span>}>
              {getFieldDecorator('networkAssistance', {
                initialValue: item.networkAssistance,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>上门技术支持</span>}>
              {getFieldDecorator('doorToDoor', {
                initialValue: item.doorToDoor,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>数据处理及功能完善</span>}>
              {getFieldDecorator('dataAndFunction', {
                initialValue: item.dataAndFunction,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>各类文档</span>}>
              {getFieldDecorator('documents', {
                initialValue: item.documents,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>各类系统培训</span>}>
              {getFieldDecorator('train', {
                initialValue: item.train,
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
        </Row>
        <Divider>【重点工作】</Divider>
        {getFieldDecorator('keyWork', {
          initialValue: item.keyWork,
        })(
          <TextArea rows={3} placeholder="请输入重点工作"/>
        )}
        <Divider>【运行维护】</Divider>
        {getFieldDecorator('maintenance', {
          initialValue: item.maintenance,
        })(
          <TextArea rows={3} placeholder="请输入运行维护"/>
        )}
        <Divider>【功能完善】</Divider>
        {getFieldDecorator('perfection', {
          initialValue: item.perfection,
        })(
          <TextArea rows={3} placeholder="请输入功能完善"/>
        )}
        <Divider>【故障及故障分析】</Divider>
        {getFieldDecorator('fault', {
          initialValue: item.fault,
        })(
          <TextArea rows={3} placeholder="请输入故障及故障分析"/>
        )}
        <Divider>【存在问题】</Divider>
        {getFieldDecorator('problem', {
          initialValue: item.problem,
        })(
          <TextArea rows={3} placeholder="请输入存在问题"/>
        )}
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
