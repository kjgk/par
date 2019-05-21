import React from 'react'
import {Checkbox, Col, Form, Modal, Row} from 'antd'
import {apiPrefix, contextPath} from "../../../utils/config"
import {stringify} from "qs"

const CheckboxGroup = Checkbox.Group

const modal = ({
                 summaryStatus = [],
                 selectedSystems = [],
                 item = {},
                 onSelectSystem,
                 form: {
                   getFieldDecorator,
                 },
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    title: `月报汇总`,
    footer: null,
  }

  const handleChange = (v) => {
    onSelectSystem(v)
  }

  return (
    <Modal {...modalOpts}>
      <Form>
        {
          getFieldDecorator('systemList', {
            initialValue: selectedSystems
          })(
            <CheckboxGroup onChange={handleChange}>
              <Row>
                {summaryStatus.map(status =>
                  <Col key={status.systemId} span={12}>
                    <Checkbox value={status.systemId}
                              disabled={status.status !== 1}
                    >{status.systemName}</Checkbox>
                  </Col>)}
              </Row>
            </CheckboxGroup>
          )
        }
        <div style={{fontSize: 16, textAlign: 'center'}}>
          <a style={{fontSize: 16, border: ''}} target="_blank"
             href={`${contextPath}${apiPrefix}/monthlyReport/summary/export?${stringify({system: selectedSystems}, {indices: false})}`}>
            下载
          </a>
        </div>
      </Form>
    </Modal>
  )
}

export default Form.create()(modal)
