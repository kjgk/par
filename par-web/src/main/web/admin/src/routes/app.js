/* global window */
/* global document */
import React, {Fragment} from 'react'
import NProgress from 'nprogress'
import PropTypes from 'prop-types'
import pathToRegexp from 'path-to-regexp'
import {connect} from 'dva'
import {Loader, MyLayout} from '../components'
import {BackTop, Layout} from 'antd'
import {classnames, config} from '../utils'
import {Helmet} from 'react-helmet'
import {withRouter} from 'dva/router'
import Error from './error'
import ModifyPassword from '../routes/system/ModifyPassword'
import '../themes/index.less'
import './app.less'

const {Content, Footer, Sider} = Layout
const {Header, Bread, styles} = MyLayout
const {prefix, openPages} = config

let lastHref

const App = ({
               children, dispatch, app, loading, location, modifyPassword,
             }) => {
  const {
    user, siderFold, darkTheme, isNavbar, menuPopoverVisible, navOpenKeys, menu, permissions,
  } = app
  const {visible} = modifyPassword
  let {pathname} = location
  pathname = pathname.startsWith('/') ? pathname : `/${pathname}`
  const current = menu.filter(item => pathToRegexp(item.route || '').exec(pathname))
  const hasPermission = current.length ? permissions.visit.includes(current[0].id) : false
  const {href} = window.location

  if (lastHref !== href) {
    NProgress.start()
    if (!loading.global) {
      NProgress.done()
      lastHref = href
    }
  }

  const headerProps = {
    menu,
    user,
    location,
    siderFold,
    isNavbar,
    menuPopoverVisible,
    navOpenKeys,
    switchMenuPopover() {
      dispatch({type: 'app/switchMenuPopver'})
    },
    logout() {
      dispatch({type: 'app/logout'})
    },
    modifyPassword() {
      dispatch({type: 'modifyPassword/showModal'})
    },
    switchSider() {
      dispatch({type: 'app/switchSider'})
    },
    changeOpenKeys(openKeys) {
      dispatch({type: 'app/handleNavOpenKeys', payload: {navOpenKeys: openKeys}})
    },
  }

  const siderProps = {
    menu,
    location,
    siderFold,
    darkTheme,
    navOpenKeys,
    changeTheme() {
      dispatch({type: 'app/switchTheme'})
    },
    changeOpenKeys(openKeys) {
      window.localStorage.setItem(`${prefix}navOpenKeys`, JSON.stringify(openKeys))
      dispatch({type: 'app/handleNavOpenKeys', payload: {navOpenKeys: openKeys}})
    },
  }

  const breadProps = {
    menu,
    location,
  }

  const modifyPasswordModalProps = {
    title: '修改密码',
    visible,
    maskClosable: false,
    onOk(data) {
      dispatch({
        type: 'modifyPassword/submit',
        payload: data
      })
    },
    onCancel() {
      dispatch({
        type: 'modifyPassword/hideModal',
      });
    }
  }

  if (openPages && openPages.includes(pathname)) {
    return (<Fragment>
      <Loader fullScreen spinning={loading.effects['app/query']}/>
      {children}
    </Fragment>)
  }

  return (
    <Fragment>
      <Loader fullScreen spinning={loading.effects['app/query']}/>
      <Helmet>
        <title>绿化市容信息化系统巡检平台</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
      </Helmet>

      <Layout className={classnames({[styles.dark]: darkTheme, [styles.light]: !darkTheme})}>
        {!isNavbar && <Sider
          trigger={null}
          collapsible
          collapsed={siderFold}
        >
          {siderProps.menu.length === 0 ? null : <MyLayout.Sider {...siderProps} />}
        </Sider>}
        <Layout style={{height: '100vh', overflow: 'scroll'}} id="mainContainer">
          <BackTop target={() => document.getElementById('mainContainer')}/>
          <Header {...headerProps} />
          <Content>
            <Bread {...breadProps} />
            {hasPermission ? children : <Error/>}
          </Content>
          <Footer>
            {config.footerText}
          </Footer>
        </Layout>
      </Layout>
      {visible && <ModifyPassword {...modifyPasswordModalProps}/>}
    </Fragment>
  )
}

App.propTypes = {
  children: PropTypes.element.isRequired,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  app: PropTypes.object,
  modifyPassword: PropTypes.object,
  loading: PropTypes.object,
}

export default withRouter(connect(({app, loading, modifyPassword}) => ({app, loading, modifyPassword}))(App))
