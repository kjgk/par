import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Alert, Button, Form, Input, Row } from 'antd'
import styles from './index.module.less'
const FormItem = Form.Item

const Login = ({
                 login,
                 loading,
                 dispatch,
                 form: {
                   getFieldDecorator,
                   validateFieldsAndScroll,
                 },
               }) => {

  function handleOk () {
    validateFieldsAndScroll((errors, { username, password }) => {
      if (errors) {
        return
      }
      dispatch({
        type: 'login/login',
        payload: {
          username: username.trim(),
          password,
        },
      })
    })
  }

  function renderMessage (content) {
    return <Alert style={{ marginBottom: 24 }} message={content} type="error" showIcon/>
  }

  return (
    <div className={styles.form}>
      <div className={styles.logo}>
        <img alt="logo" src={require('../../assets/logo.svg')}/>
        <span>用户登录</span>
      </div>
      <form>
        {login.errorMessage && !loading.effects.login && renderMessage(login.errorMessage)}
        <FormItem hasFeedback>
          {getFieldDecorator('username', {
            rules: [
              {
                required: true,
                message: '请输入帐号',
              },
            ],
          })(<Input onPressEnter={handleOk} placeholder="用户名"/>)}
        </FormItem>
        <FormItem hasFeedback>
          {getFieldDecorator('password', {
            rules: [
              {
                required: true,
                message: '请输入密码',
              },
            ],
          })(<Input type="password" onPressEnter={handleOk} placeholder="密码"/>)}
        </FormItem>
        <Row>
          <Button type="primary" onClick={handleOk} loading={loading.effects.login}>
            登录
          </Button>
        </Row>
      </form>
    </div>
  )
}

Login.propTypes = {
  form: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ login, loading }) => ({
  loading,
  login,
}))(Form.create()(Login))
