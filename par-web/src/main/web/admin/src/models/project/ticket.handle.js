import service from '../../services/project/ticket'
import {createCrudModel} from '../common'
import modelExtend from "dva-model-extend"
import {message} from "antd"

const namespace = 'ticketHandle'
const pathname = '/ticket/handle'

export default modelExtend(createCrudModel(namespace, pathname, service), {
  namespace,
  state: {
    fileList: [],
    fileLimit: 3,
  },
  effects: {
    * accept({payload = {}}, {call, put, select}) {
      yield call(service.acceptTicket, payload)
      message.success('接单成功！')
    },
    * process({payload = {}}, {call, put, select}) {
      const {fileList} = yield select(_ => _[namespace])
      const attachments = []
      for (const file of fileList) {
        attachments.push(file.response)
      }
      yield call(service.processTicket, {
        ...payload,
        attachments,
      })
      message.success('处理成功！')
      yield put({
        type: 'updateState',
        payload: {
          modalVisible: false,
          fileList: []
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
  },
})
