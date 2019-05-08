import React from 'react'
import PropTypes from 'prop-types'
import {Button, Card, Divider, Form, Icon, Input, List, Modal, Skeleton} from 'antd'
import {Formatter} from "../../../components"
import {contextPath} from '../../../utils/config'

const formItemLayout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 16,
  },
}

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
      <Form layout="horizontal">
        <Divider>
          <div>{item.systemName}</div>
          <div>
            <small style={{color: '#666'}}>
              巡检人：{item.username} &nbsp;&nbsp;
              巡检时间：<Formatter.Date value={item.inspectionTime}/>
            </small>
          </div>
        </Divider>
        <List
          grid={{
            gutter: 16, xs: 3,
          }}
          dataSource={item.detailList}
          renderItem={item => (
            <List.Item>
              <Card title={item.name} extra={
                item.result === 1 ? <Icon type="check-circle" theme="twoTone" twoToneColor="#52c41a"/> :
                  <Icon type="close-circle" theme="twoTone" twoToneColor="#c5241a"/>
              }>
                {item.screenshots.map((screenshots) => <a
                  style={{display: 'block', marginBottom: 5}}
                  href={`${contextPath}${screenshots}`}
                  target="_blank"
                  key={screenshots}>
                  <img src={`${contextPath}${screenshots}!190_143`}/>
                </a>)}
              </Card>
            </List.Item>
          )}
        />,
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default modal
