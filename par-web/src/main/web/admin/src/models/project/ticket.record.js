import modelExtend from 'dva-model-extend'
import {model} from '../common'
import ticketService from '../../services/project/ticket'
import {message} from "antd"
import {routerRedux} from "dva/router"

const namespace = 'ticketRecord'

export default modelExtend(model, {

  namespace,
  state: {
    fileList1: [],
    fileList2: [],
    fileLimit: 3,
    showSuccess: false,
  },
  subscriptions: {

    setupHistory({dispatch, history}) {
      history.listen((location) => {
        if (location.pathname === '/ticket/record') {
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

      const {fileList1, fileList2} = yield select(_ => _[namespace])
      const attachments1 = [], attachments2 = []
      for (const file of fileList1) {
        attachments1.push(file.response)
      }
      for (const file of fileList2) {
        attachments2.push(file.response)
      }
      const data = payload
      data.ticket.attachments = attachments1
      data.attachments = attachments2
      yield call(ticketService.recordTicket, data)
      yield put({
        type: 'updateState',
        payload: {
          showSuccess: true
        },
      })
    },
  },
  reducers: {
    uploadChange1(state, {payload}) {
      if (payload.fileList.length > state.fileLimit) {
        return state
      }
      return {
        ...state,
        fileList1: payload.fileList,
      }
    },
    uploadChange2(state, {payload}) {
      if (payload.fileList.length > state.fileLimit) {
        return state
      }
      return {
        ...state,
        fileList2: payload.fileList,
      }
    },
    resetForm(state, {payload}) {
      return {
        ...state,
        fileList1: [],
        fileList2: [],
        showSuccess: false,
      }
    },
  },
})
