import React from 'react'
import PropTypes from 'prop-types'
import {connect} from 'dva'
import {Alert, Button, Form, Input, Row} from 'antd'
import styles from './index.module.less'
import {title, footerText} from '../../utils/config'

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

  function handleOk() {
    validateFieldsAndScroll((errors, {username, password}) => {
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

  function renderMessage(content) {
    return <Alert style={{marginBottom: 18}} message={content} type="error" showIcon/>
  }

  return (
    <div className={styles.login}>
      <div className={styles.main}>
        <h3>{title}</h3>
        <div className={styles.form}>
          <div className={styles.logo}>
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
              })(<Input onPressEnter={handleOk} autoFocus placeholder="用户名"/>)}
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
            <div>
              <Button type="primary" onClick={handleOk} loading={loading.effects.login}>
                登录
              </Button>
            </div>
            <div style={{textAlign: 'right', marginTop: '15px'}}>
              {/*<a>忘记密码</a>*/}
            </div>
          </form>
        </div>
      </div>

      <footer>{footerText}</footer>
    </div>
  )
}

Login.propTypes = {
  form: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({login, loading}) => ({
  loading,
  login,
}))(Form.create()(Login))
