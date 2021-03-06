import React from 'react'
import PropTypes from 'prop-types'
import {Redirect, Route, routerRedux, Switch} from 'dva/router'
import dynamic from 'dva/dynamic'
import App from './routes/app'
import {LocaleProvider} from 'antd'
import zh_CN from 'antd/es/locale-provider/zh_CN'
import moment from 'moment'
import 'moment/locale/zh-cn'

moment.locale('zh-cn')

const {ConnectedRouter} = routerRedux

const Routers = function ({history, app}) {
  const error = dynamic({
    app,
    component: () => import('./routes/error'),
  })
  const routes = [
    {
      path: '/login',
      models: () => [import('./models/login')],
      component: () => import('./routes/login/'),
    },
    {
      path: '/home',
      models: () => [import('./models/home')],
      component: () => import('./routes/home/'),
    },
    {
      path: '/system/role',
      models: () => [import('./models/system/role')],
      component: () => import('./routes/system/role/'),
    },
    {
      path: '/system/authority',
      models: () => [import('./models/system/authority')],
      component: () => import('./routes/system/authority/'),
    },
    {
      path: '/system/code',
      models: () => [import('./models/system/code')],
      component: () => import('./routes/system/code/'),
    },
    {
      path: '/system/menu',
      models: () => [import('./models/system/menu')],
      component: () => import('./routes/system/menu/'),
    },
    {
      path: '/system/user',
      models: () => [import('./models/system/user')],
      component: () => import('./routes/system/user/'),
    },
    {
      path: '/project/region',
      models: () => [import('./models/project/region')],
      component: () => import('./routes/project/region/'),
    },
    {
      path: '/project/company',
      models: () => [import('./models/project/company')],
      component: () => import('./routes/project/company/'),
    },
    {
      path: '/project/accendant',
      models: () => [import('./models/project/accendant')],
      component: () => import('./routes/project/accendant/'),
    },
    {
      path: '/project/supervisor',
      models: () => [import('./models/project/supervisor')],
      component: () => import('./routes/project/supervisor/'),
    },
    {
      path: '/project/system',
      models: () => [import('./models/project/system')],
      component: () => import('./routes/project/system/'),
    },
    {
      path: '/project/function',
      models: () => [import('./models/project/function')],
      component: () => import('./routes/project/function/'),
    },
    {
      path: '/inspection',
      models: () => [import('./models/project/inspection')],
      component: () => import('./routes/project/inspection/'),
    },
    {
      path: '/inspection/report',
      models: () => [import('./models/project/inspection.report')],
      component: () => import('./routes/project/inspection/report'),
    },
    {
      path: '/ticket',
      models: () => [import('./models/project/ticket')],
      component: () => import('./routes/project/ticket/'),
    },
    {
      path: '/ticket/create',
      models: () => [import('./models/project/ticket.create')],
      component: () => import('./routes/project/ticket/create'),
    },
    {
      path: '/ticket/handle',
      models: () => [import('./models/project/ticket.handle')],
      component: () => import('./routes/project/ticket/handle'),
    },
    {
      path: '/ticket/record',
      models: () => [import('./models/project/ticket.record')],
      component: () => import('./routes/project/ticket/record'),
    },
    {
      path: '/monthly/report',
      models: () => [import('./models/project/monthly.report')],
      component: () => import('./routes/project/monthlyreport'),
    },
    {
      path: '/monthly/report/manage',
      models: () => [import('./models/project/monthly.report.manage')],
      component: () => import('./routes/project/monthlyreportmanage'),
    },
    {
      path: '/document/*',
      models: () => [import('./models/project/document')],
      component: () => import('./routes/project/document'),
    },
  ]

  return (
    <ConnectedRouter history={history}>
      <LocaleProvider locale={zh_CN}>
        <App>
          <Switch>
            <Route exact path="/" render={() => <Redirect to="/home"/>}/>
            <Route exact path="/project" render={() => <Redirect to="/project/accendant"/>}/>
            <Route exact path="/system" render={() => <Redirect to="/system/user"/>}/>
            {
              routes.map(({path, ...dynamics}, key) => (
                <Route key={key}
                       exact
                       path={path}
                       component={dynamic({
                         app,
                         ...dynamics,
                       })}
                />
              ))
            }
            <Route component={error}/>
          </Switch>
        </App>
      </LocaleProvider>
    </ConnectedRouter>
  )
}

Routers.propTypes = {
  history: PropTypes.object,
  app: PropTypes.object,
}

export default Routers
