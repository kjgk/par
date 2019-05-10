import React from 'react'
import PropTypes from 'prop-types'
import moment from 'moment'
import {Button, Col, Divider, Form, Icon, Input, Modal, Row, Tabs, Upload} from 'antd'
import styles from "./index.module.less"
import {api, contextPath} from "../../../utils/config"

const {TextArea} = Input

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
}

const {TabPane} = Tabs

const integerRegexp = /^[+]{0,1}(\d+)$/

const modal = ({
                 item = {},
                 currentMonth,
                 onOk,
                 viewMode,
                 fileList = [],
                 fileLimit = 0,
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
    title: `${item.system.name} - ${moment(item.month || currentMonth).format('YYYY年M月')} - 月报`,
    onOk: () => {
      validateFields((errors) => {
        if (errors) {
          return
        }
        const attachments = []
        for (const file of fileList) {
          attachments.push(
            file.attachmentId ? {attachmentId: file.attachmentId} : file.response
          )
        }
        const data = {
          ...getFieldsValue(),
          objectId: item.objectId,
          system: item.system,
          attachments,
        }
        onOk(data)
      })
    },
  }

  const uploaderProps = {
    action: `${contextPath}${api.fileUpload}`,
    multiple: true,
    name: 'attachment',
    fileList,
    beforeUpload() {
      if (fileList.length >= fileLimit) {
        return false
      }
    },
    onChange(data) {
      onUploadChange(data)
    }
  }

  return (
    <Modal {...modalOpts}>
      <Form className={styles.modal_form}>
        <Row gutter={0}>
          <Col span={11}>
            <Form.Item label={<span>组织或参加各类项目会议（次）</span>}>
              {getFieldDecorator('meeting', {
                initialValue: item.meeting,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>系统日常巡检（次）</span>}>
              {getFieldDecorator('daily', {
                initialValue: item.daily,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>咨询类服务（次）</span>}>
              {getFieldDecorator('consultation', {
                initialValue: item.consultation,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>网络协助类（次）</span>}>
              {getFieldDecorator('networkAssistance', {
                initialValue: item.networkAssistance,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>上门技术支持（次）</span>}>
              {getFieldDecorator('doorToDoor', {
                initialValue: item.doorToDoor,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>数据处理及功能完善（次）</span>}>
              {getFieldDecorator('dataAndFunction', {
                initialValue: item.dataAndFunction,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>各类文档（个）</span>}>
              {getFieldDecorator('documents', {
                initialValue: item.documents,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入个数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>各类系统培训（次）</span>}>
              {getFieldDecorator('train', {
                initialValue: item.train,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>

          <Col span={11}>
            <Form.Item label={<span>本月系统用户数（个）</span>}>
              {getFieldDecorator('userCount', {
                initialValue: item.userCount,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入个数"/>
              )}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>本月系统用户登录次数（次）</span>}>
              {getFieldDecorator('userLoginCount', {
                initialValue: item.userLoginCount,
                rules: [{
                  pattern: integerRegexp,
                  message: '请输入整数',
                }]
              })(
                <Input placeholder="请输入次数"/>
              )}
            </Form.Item>
          </Col>
        </Row>
        <Tabs>
          <TabPane tab="重点工作" key={1}>
            {getFieldDecorator('keyWork', {
              initialValue: item.keyWork,
            })(
              <TextArea rows={10} placeholder="请输入重点工作"/>
            )}
          </TabPane>
          <TabPane tab="运行维护" key={2}>
            {getFieldDecorator('maintenance', {
              initialValue: item.maintenance,
            })(
              <TextArea rows={10} placeholder="请输入运行维护"/>
            )}
          </TabPane>
          <TabPane tab="功能完善" key={3}>
            {getFieldDecorator('perfection', {
              initialValue: item.perfection,
            })(
              <TextArea rows={10} placeholder="请输入功能完善"/>
            )}
          </TabPane>
          <TabPane tab="故障及故障分析" key={4}>
            {getFieldDecorator('fault', {
              initialValue: item.fault,
            })(
              <TextArea rows={10} placeholder="请输入故障及故障分析"/>
            )}
          </TabPane>
          <TabPane tab="存在问题" key={5}>
            {getFieldDecorator('problem', {
              initialValue: item.problem,
            })(
              <TextArea rows={10} placeholder="请输入存在问题"/>
            )}
          </TabPane>
          <TabPane tab="附件" key={10}>
            <Upload {...uploaderProps}>
              <Button disabled={fileList.length >= fileLimit}>
                <Icon type="upload"/> 上传附件
              </Button>
            </Upload>
            <span style={{color: '#666'}}> 您可以上传{fileLimit}个附件</span>
          </TabPane>
        </Tabs>
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
