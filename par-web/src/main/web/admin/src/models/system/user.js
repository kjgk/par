import service from '../../services/system/user'
import { createCrudModel } from '../common'
import modelExtend from 'dva-model-extend'
import { message } from 'antd'

const namespace = 'user'
const pathname = '/system/user'

const model = modelExtend(createCrudModel(namespace, pathname, service), {
  namespace,
  state: {
    accountModalVisible: false,
  },
  effects: {
    * saveAccount ({ payload: { name, password, objectId } }, { call, put, select }) {
      yield call(service.saveAccount, objectId, {
        name,
        password,
      })
      yield put({
        type: 'hideAccountModal'
      })
      message.success('帐号设置成功！')
    },
    * showAccountModal ({ payload }, { call, put, select }) {
      yield put({
        type: 'updateState',
        payload: {
          accountModalVisible: true,
          currentItem: payload,
        },
      })
    },
  },
  reducers: {
    hideAccountModal (state) {
      return {
        ...state,
        accountModalVisible: false,
      }
    },
  },
})

export default model
