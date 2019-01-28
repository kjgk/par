import { routerRedux } from 'dva/router'
import { login } from '../services/app'
import { model } from './common'
import modelExtend from 'dva-model-extend'

export default modelExtend(model, {
  namespace: 'login',

  state: {
    errorMessage: undefined,
  },

  effects: {
    * login ({ payload }, { put, call, select }) {
      yield put({
        type: 'updateState',
        payload: { errorMessage: undefined },
      })
      const {success, message} = yield call(login, payload)
      const { locationQuery } = yield select(_ => _.app)
      if (success) {
        const { from } = locationQuery
        yield put({ type: 'app/query' })
        if (from && from !== '/login') {
          yield put(routerRedux.push(from))
        } else {
          yield put(routerRedux.push('/project'))
        }
      } else {
        yield put({
          type: 'updateState',
          payload: { errorMessage: message },
        })
      }
    },
  },
})
