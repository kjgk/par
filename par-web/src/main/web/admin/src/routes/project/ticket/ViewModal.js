import React, {Fragment} from 'react'
import PropTypes from 'prop-types'
import {Button, Col, Divider, Form, Input, Modal, Row} from 'antd'
import TicketStatus from "../../../sections/ticket/TicketStatus"
import {Formatter} from "../../../components"
import {contextPath} from '../../../utils/config'

const modal = ({
                 item = {},
                 onOk,
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    width: 800,
    footer: <Button type="primary" onClick={modalProps.onCancel}>确定</Button>,
  }

  return (
    <Modal {...modalOpts}>
      <Form className="ant-advanced-form">
        <Row gutter={24}>
          <Col span={22} offset={2}>
            <Form.Item label="所属系统">
              {item.systemName}
            </Form.Item>
          </Col>
          <Col span={22} offset={2}>
            <Form.Item label="工单描述">
              <div style={{whiteSpace: 'pre-wrap'}}>{item.content}</div>
            </Form.Item>
          </Col>
          <Col span={10} offset={2}>
            <Form.Item label="报修人">
              {item.contacts || '-'}
            </Form.Item>
          </Col>
          <Col span={10} offset={2}>
            <Form.Item label="报修人手机号">
              {item.phoneNo || '-'}
            </Form.Item>
          </Col>
          <Col span={10} offset={2}>
            <Form.Item label="提交时间">
              <Formatter.Date value={item.submitTime}/>
            </Form.Item>
          </Col>
          <Col span={10} offset={2}>
            <Form.Item label="状态">
              <div><TicketStatus value={item.status}/></div>
            </Form.Item>
          </Col>
          {item && item.attachments.length > 0 && <Col span={22} offset={2}>
            <Form.Item label="附件">
              {item.attachments.map((attachment) =>
                <div key={attachment.url}>
                  <a href={`${contextPath}${attachment.url}`} target="_blank">{attachment.filename}</a>
                </div>
              )}
            </Form.Item>
          </Col>}
          {
            item.status === 2 && item.handleInfo && <Fragment>
              <Divider>处理结果</Divider>
              <Col span={22} offset={2}>
                <Form.Item label="是否解决">
                  {item.handleInfo.result === 1 ? '已解决' : '未解决'}
                </Form.Item>
              </Col>

              <Col span={22} offset={2}>
                <Form.Item label="备注">
                  <div style={{whiteSpace: 'pre-wrap'}}>{item.handleInfo.remark}</div>
                </Form.Item>
              </Col>

              <Col span={10} offset={2}>
                <Form.Item label="接单时间">
                  <Formatter.Date value={item.handleInfo.acceptTime} emptyText="-"/>
                </Form.Item>
              </Col>

              {item.handleInfo.finishTime && <Col span={10} offset={2}>
                <Form.Item label="完成时间">
                  <Formatter.Date value={item.handleInfo.finishTime}/>
                </Form.Item>
              </Col>}

              {item && item.handleInfo.attachments.length > 0 && <Col span={22} offset={2}>
                <Form.Item label="附件">
                  {item.handleInfo.attachments.map((attachment) =>
                    <div key={attachment.url}>
                      <a href={`${contextPath}${attachment.url}`} target="_blank">{attachment.filename}</a>
                    </div>
                  )}
                </Form.Item>
              </Col>}
            </Fragment>
          }
        </Row>
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default modal
