import modelExtend from 'dva-model-extend'
import {model} from '../common'
import ticketService from '../../services/project/ticket'
import {message} from "antd"
import {routerRedux} from "dva/router"

const namespace = 'ticketCreate'

export default modelExtend(model, {

  namespace,
  state: {
    fileList: [],
    fileLimit: 3,
    showSuccess: false,
  },
  subscriptions: {

    setupHistory({dispatch, history}) {
      history.listen((location) => {
        if (location.pathname === '/ticket/create') {
          dispatch({
            type: 'resetForm',
          })
        } else {
        }
      })
    },

  },
  effects: {

    * save({payload,}, {call, put, select}) {

      const {fileList} = yield select(_ => _[namespace])
      const attachments = []
      for (const file of fileList) {
        attachments.push(file.response)
      }
      yield call(ticketService.create, {
        ...payload,
        attachments,
      })
      yield put({
        type: 'updateState',
        payload: {
          showSuccess: true
        },
      })
    },
  },
  reducers: {
    uploadChange(state, {payload}) {
      if (payload.fileList.length > state.fileLimit) {
        return state
      }
      return {
        ...state,
        fileList: payload.fileList,
      }
    },
    resetForm(state, {payload}) {
      return {
        ...state,
        fileList: [],
        showSuccess: false,
      }
    },
  },
})
