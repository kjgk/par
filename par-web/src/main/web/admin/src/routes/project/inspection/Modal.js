import React from 'react'
import PropTypes from 'prop-types'
import {Button, Checkbox, Divider, Empty, Form, Icon, List, Modal, Steps, Tabs, Upload, message, Row, Col} from 'antd'

import styles from './inspection.module.less'
import {api, contextPath} from "../../../utils/config"
import {Formatter} from "../../../components"

const {Step} = Steps
const {TabPane} = Tabs

const modal = ({
                 item = {},
                 systemList = [],
                 currentStep,
                 onOk,
                 onSelectSystem,
                 currentSystem,
                 onNextStep,
                 allowNextStep,
                 onUploadChange,
                 onFormPaste,
                 functionScreenshots,
                 functionResults,
                 toggleResult,
                 user,
                 form: {
                   getFieldDecorator,
                   validateFields,
                   getFieldsValue,
                 },
                 confirmLoading,
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    footer: null,
  }

  const handleSystemItemClick = (item) => {
    onSelectSystem(item)
  }

  const handleNextStep = () => {
    onNextStep()
  }

  const handleSubmit = () => {
    let data = {
      detailList: [],
      system: {objectId: currentSystem.objectId},
    }
    for (const fun of currentSystem.functionList) {
      let screenshots = []
      for (const file of functionScreenshots[fun.objectId].fileList) {
        screenshots.push(file.response)
      }
      data.detailList.push({
        result: functionResults[fun.objectId] ? 1 : 0,
        screenshots,
        'function': {
          objectId: fun.objectId,
        },
      })
    }
    onOk(data)
  }

  const uploaderProps = {
    action: `${contextPath}${api.fileUpload}`,
    multiple: true,
    accept: '.jpg,.jpeg,.png,.bmp',
    listType: 'picture-card',
    name: 'attachment',
  }

  const functionResultValues = Object.values(functionResults)
  const validCount = functionResultValues.filter(value => value).length
  const invalidCount = functionResultValues.filter(value => !value).length

  let screenshotsCount = 0
  for (let screenshots of Object.values(functionScreenshots)) {
    screenshotsCount += screenshots.fileList.length
  }

  const handlePaste = (event, functionId) => {
    if (!(event.clipboardData && event.clipboardData.items)) {
      return
    }
    for (let file of event.clipboardData.files) {
      onFormPaste(file, functionId)
    }
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <Steps current={currentStep}>
          <Step title="选择运维系统" description=""/>
          <Step title="检查系统功能点" description=""/>
          <Step title="完成" description=""/>
        </Steps>
        <div className={styles['inspection-form']}>
          {currentStep === 0 && <div className={styles['step-1']}>
            <List
              bordered
              dataSource={systemList}
              renderItem={item => (<List.Item onClick={() => {
                handleSystemItemClick(item)
              }}>
                <List.Item.Meta
                  title={item.name}
                  description={item.url}
                />
                {currentSystem && currentSystem.objectId === item.objectId &&
                <Icon type="check-circle" theme="twoTone" twoToneColor="#52c41a" style={{fontSize: 20}}/>}
              </List.Item>)}
            />
          </div>}
          {currentStep === 1 && <div className={styles['step-2']}>
            <Divider>
              <div>{currentSystem.name}</div>
              <small>
                <a href={currentSystem.url} target="_blank">{currentSystem.url}</a>
              </small>
            </Divider>

            {(!currentSystem.functionList || currentSystem.functionList.length === 0) && <Empty description={
              <span>该系统未设置功能点</span>
            }/>}

            {currentSystem.functionList && currentSystem.functionList.length > 0 && <Tabs tabPosition="left">
              {currentSystem.functionList.map((item) =>
                <TabPane tab={item.name} key={item.objectId}>
                  <div className={styles['detail-form']} onPaste={(event) => handlePaste(event, item.objectId)}>
                    <div style={{margin: '0 0 10px 0'}}>
                      <Checkbox checked={functionResults[item.objectId]} onChange={() => {
                        toggleResult(item.objectId)
                      }}>此功能运行正常</Checkbox></div>
                    <Upload {...uploaderProps} fileList={functionScreenshots[item.objectId] && functionScreenshots[item.objectId].fileList}
                            onChange={(data) => {
                              onUploadChange(item.objectId, data)
                            }}>
                      <div>
                        <Icon type="plus"/>
                        <div className="ant-upload-text">上传截图</div>
                      </div>
                    </Upload>
                  </div>
                </TabPane>
              )}
            </Tabs>}
          </div>}
          {currentStep === 2 && <div className={styles['step-3']}>

            <Divider>
              <div>{currentSystem.name}</div>
              <small>
                <a href={currentSystem.url} target="_blank">{currentSystem.url}</a>
              </small>
            </Divider>

            <div className={styles['inspection-result']}>
              <Row gutter={16}>
                <Col span={12}>
                  巡检人： {user.username}
                </Col>
                <Col span={12}>
                  巡检时间： <Formatter.Date value={new Date().getTime()}/>
                </Col>
                <Col span={12}>
                  功能点： 正常：{validCount}个，异常：{invalidCount}个
                </Col>
                <Col span={12}>
                  系统截图：{screenshotsCount}张
                </Col>
              </Row>
            </div>
          </div>}

          <div className={styles['step-button']}>
            {currentStep < 2 && <Button htmlType="button" type="primary" disabled={!allowNextStep} onClick={handleNextStep}>下一步</Button>}
            {currentStep >= 2 && <Button loading={confirmLoading} htmlType="button" type="primary" onClick={handleSubmit}>提交</Button>}
          </div>
        </div>
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  form: PropTypes.object.isRequired,
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
  systemList: PropTypes.array,
}

export default Form.create()(modal)
