import modelExtend from 'dva-model-extend'
import {model} from '../common'
import {modifyPassword} from "../../services/app";
import {message} from "antd"

export default modelExtend(model, {
  namespace: 'modifyPassword',
  state: {
    visible: false,
  },
  subscriptions: {},
  effects: {
    * submit({
               payload: {
                 newPassword,
                 confirmNewPassword,
                 originPassword,
               }
             }, {call, put, select}) {
      if (newPassword !== confirmNewPassword) {
        message.error("两次密码输入不一致！")
        return
      }
      yield call(modifyPassword, {
        newPassword,
        originPassword,
      })
      message.success("密码修改成功！")
      yield put({
        type: 'updateState',
        payload: {
          visible: false,
        }
      })
    },
  },

  reducers: {
    showModal(state) {
      return {
        ...state,
        visible: true,
      }
    },
    hideModal(state) {
      return {
        ...state,
        visible: false,
      }
    }
  },
})
