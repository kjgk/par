import service from '../../services/project/ticket'
import {createCrudModel} from '../common'
import modelExtend from "dva-model-extend"
import {message} from "antd"

const namespace = 'ticketHandle'
const pathname = '/ticket/handle'

export default modelExtend(createCrudModel(namespace, pathname, service), {
  namespace,
  state: {},
  effects: {
    * accept({payload = {}}, {call, put, select}) {
      yield call(service.acceptTicket, payload)
      message.success('接单成功！')
    },
    * process({payload = {}}, {call, put, select}) {
      yield call(service.processTicket, payload)
      yield put({type: 'hideModal'})
      message.success('处理成功！')
    },
  },
})
