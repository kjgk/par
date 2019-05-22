import React, {Fragment} from 'react'
import PropTypes from 'prop-types'
import {Button, Col, Divider, Form, Input, Modal, Row} from 'antd'
import moment from "moment"
import styles from "./index.module.less"
import {contextPath} from "../../../utils/config"
import {ImageList} from "../../../components"

const modal = ({
                 item = {},
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    footer: <Button type="default" onClick={modalProps.onCancel}>关闭</Button>,
    title: `${item.system.name} - ${moment(item.month).format('YYYY年M月')} - 月报`,
  }

  return (
    <Modal {...modalOpts}>
      <Form className={styles.modal_form}>
        <Row gutter={0}>
          <Col span={11}>
            <Form.Item label={<span>组织或参加各类项目会议（次）</span>}>
              {item.meeting}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>系统日常巡检（次）</span>}>
              {item.daily}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>咨询类服务（次）</span>}>
              {item.consultation}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>网络协助类（次）</span>}>
              {item.networkAssistance}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>上门技术支持（次）</span>}>
              {item.doorToDoor}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>数据处理及功能完善（次）</span>}>
              {item.dataAndFunction}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>各类文档（个）</span>}>
              {item.documents}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>各类系统培训（次）</span>}>
              {item.train}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>本月系统用户数（个）</span>}>
              {item.userCount}
            </Form.Item>
          </Col>
          <Col span={11}>
            <Form.Item label={<span>本月系统用户登录次数（次）</span>}>
              {item.userLoginCount}
            </Form.Item>
          </Col>
        </Row>
        <Divider>【重点工作】</Divider>
        <div className={styles.display_field}>{item.keyWork || '无'}</div>
        <Divider>【运行维护】</Divider>
        <div className={styles.display_field}>{item.maintenance || '无'}</div>
        <Divider>【功能完善】</Divider>
        <div className={styles.display_field}>{item.perfection || '无'}</div>
        <Divider>【故障及故障分析】</Divider>
        <div className={styles.display_field}>{item.fault || '无'}</div>
        <Divider>【存在问题】</Divider>
        <div className={styles.display_field}>{item.problem || '无'}</div>
        {
          item.attachments.length > 0 &&
          <Fragment>
            <Divider>【附件】</Divider>
            <div className={styles.display_field}>
              <ImageList list={item.attachments
                .filter(attachment => attachment.url && attachment.imageUrl)
                .map((attachment) => ({url: contextPath + attachment.imageUrl}))}/>
              {item.attachments
                .filter(attachment => attachment.url && !attachment.imageUrl)
                .map((attachment) => <div>
                    <a href={`${contextPath}${attachment.url}`} target="_blank">{attachment.filename}</a>
                  </div>
                )}
            </div>
          </Fragment>
        }
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  type: PropTypes.string,
  item: PropTypes.object,
}

export default modal
