import service from '../../services/project/monthly.report'
import systemService from '../../services/project/system'
import {createCrudModel} from '../common'
import modelExtend from "dva-model-extend"
import queryString from "query-string"

const namespace = 'monthlyReport'
const pathname = '/monthly/report'

export default modelExtend(createCrudModel(namespace, pathname, service), {
  namespace,
  state: {
    list: [],
    systemList: [],
    currentMonth: undefined,
    currentMonthlyReport: undefined,
    fileList: [],
    fileLimit: 3,
  },
  subscriptions: {

    setup({dispatch, history}) {
      history.listen((location) => {
        if (location.pathname === '/monthly/report') {
          dispatch({
            type: 'query',
            payload: {
              ...queryString.parse(location.search),
            },
          })
        } else {
          dispatch({
            type: 'updateState',
            payload: {
              list: [],
              systemList: [],
              currentMonthlyReport: undefined,
            }
          })
        }
      })
    },
  },
  effects: {

    * query({payload = {}}, {call, put, select}) {
      let {systemList} = yield select(_ => _[namespace])
      if (systemList.length === 0) {
        systemList = yield call(systemService.getSystemList)
      }
      let systemId = payload.systemId || (systemList.length > 0 && systemList[0].objectId)
      let result = yield call(service.query, {
        systemId,
        ...payload
      })
      const {currentMonth, currentMonthlyReport} = yield call(service.getCurrentMonthReport, systemId)
      yield put({
        type: 'updateState',
        payload: {
          currentMonth: currentMonth || null,
          currentMonthlyReport,
        }
      })
      yield put({
        type: 'updateState',
        payload: {
          list: result.content,
          systemList,
        }
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
