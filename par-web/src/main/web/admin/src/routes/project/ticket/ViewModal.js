import React, {Fragment} from 'react'
import PropTypes from 'prop-types'
import {Button, Col, Divider, Form, Modal, Row} from 'antd'
import TicketStatus from "../../../sections/ticket/TicketStatus"
import {Formatter, ImageList} from "../../../components"
import {contextPath} from '../../../utils/config'
import styles from './index.module.less'

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
      <Form className={styles.modal_form}>
        <Row gutter={24}>
          <Col span={22}>
            <Form.Item label={<span>所属系统</span>}>
              {item.systemName}
            </Form.Item>
          </Col>
          <Col span={22}>
            <Form.Item label={<span>工单描述</span>}>
              <div style={{whiteSpace: 'pre-wrap'}}>{item.content}</div>
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item label={<span>报修人</span>}>
              {item.contacts || '-'}
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item label={<span>报修人手机号</span>}>
              {item.phoneNo || '-'}
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item label={<span>提交时间</span>}>
              <Formatter.Date value={item.submitTime}/>
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item label={<span>状态</span>}>
              <div><TicketStatus value={item.status}/></div>
            </Form.Item>
          </Col>
          {item && item.attachments.length > 0 && <Col span={22}>
            <Form.Item label={<span>附件</span>}>
              <ImageList list={item.attachments
                .filter(attachment => attachment.url && attachment.imageUrl)
                .map((attachment) => ({url: contextPath + attachment.imageUrl}))}/>
              {item.attachments
                .filter(attachment => attachment.url && !attachment.imageUrl)
                .map((attachment) => <div>
                  <a href={`${contextPath}${attachment.url}`} target="_blank">{attachment.filename}</a>
                </div>
              )}
            </Form.Item>
          </Col>}
          {
            item.status === 2 && item.handleInfo && <Fragment>
              <Divider>处理结果</Divider>
              <Col span={22}>
                <Form.Item label={<span>是否解决</span>}>
                  {item.handleInfo.result === 1 ? <span style={{color: '#7bbd5f', fontWeight: "bold"}}>已解决</span> : <span style={{color: '#f51e3a', fontWeight: "bold"}}>未解决</span>}
                </Form.Item>
              </Col>

              <Col span={22}>
                <Form.Item label={<span>附件</span>}>
                  <div style={{whiteSpace: 'pre-wrap'}}>{item.handleInfo.remark}</div>
                </Form.Item>
              </Col>

              <Col span={10}>
                <Form.Item label={<span>接单时间</span>}>
                  <Formatter.Date value={item.handleInfo.acceptTime} emptyText="-"/>
                </Form.Item>
              </Col>

              {item.handleInfo.finishTime && <Col span={10}>
                <Form.Item label={<span>完成时间</span>}>
                  <Formatter.Date value={item.handleInfo.finishTime}/>
                </Form.Item>
              </Col>}

              {item && item.handleInfo.attachments.length > 0 && <Col span={22}>
                <Form.Item label={<span>附件</span>}>
                  <ImageList list={item.handleInfo.attachments
                    .filter(attachment => attachment.url && attachment.imageUrl)
                    .map((attachment) => ({url: contextPath + attachment.imageUrl}))}/>
                  {item.handleInfo.attachments
                    .map((attachment) => <div>
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
