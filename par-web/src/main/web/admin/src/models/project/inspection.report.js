import service from '../../services/project/inspection'
import {model} from '../common'
import modelExtend from "dva-model-extend"
import queryString from "query-string"
import moment from "moment"

const namespace = 'inspectionReport'

export default modelExtend(model, {
  namespace,
  state: {
    weekDays: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    reportList: [],
  },
  subscriptions: {

    setupHistory({dispatch, history}) {
      history.listen((location) => {
        if (location.pathname === '/inspection/report') {
          let now = moment()
          dispatch({
            type: 'query',
            payload: {
              viewMode: 'week',
              date: now.year() + '' + now.week(),
              ...queryString.parse(location.search),
            },
          })
        } else {
          dispatch({
            type: 'updateState',
            payload: {
              reportList: [],
            }
          })
        }
      })
    },
  },
  effects: {

    * query({payload = {}}, {call, put, select}) {
      let reportList = yield call(service.getInspectionReport, payload)
      yield put({
        type: 'updateState',
        payload: {
          reportList,
        }
      })
    },
  },

  reducers: {},
})
