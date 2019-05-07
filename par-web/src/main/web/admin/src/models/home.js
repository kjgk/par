import modelExtend from 'dva-model-extend'
import {model} from './common'
import {routerRedux} from "dva/router"

export default modelExtend(model, {

  namespace: 'home',
  state: {},
  subscriptions: {

    setupHistory({dispatch, history}) {
      history.listen((location) => {
        if (location.pathname === '/home') {
          dispatch({
            type: 'home',
            payload: {},
          })
        } else {
        }
      })
    },
  },
  effects: {

    * home({payload,}, {call, put, select}) {

      // 根据角色跳转
      const {user} = yield select(_ => _.app)
      if (user.role === 'Admin') {
        yield put(routerRedux.push({
          pathname: '/system',
        }))
      } else if (user.role === 'Supervisor') {
        yield put(routerRedux.push({
          pathname: '/inspection/report',
        }))
      } else if (user.role === 'Accendant') {
        yield put(routerRedux.push({
          pathname: '/inspection',
        }))
      }
    },

  },
  reducers: {},
})
