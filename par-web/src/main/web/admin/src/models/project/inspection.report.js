import service from '../../services/project/inspection'
import {model} from '../common'
import modelExtend from "dva-model-extend"
import queryString from "query-string"
import moment from "moment"
import {routerRedux} from "dva/router"

const namespace = 'inspectionReport'
const pathname = '/inspection/report'
export default modelExtend(model, {
  namespace,
  state: {
    yearMonths: [],
    inspectionResults: [],
    inspectionSummary: {},
    modalVisible: false,
    currentItem: {},
  },
  subscriptions: {

    setupHistory({dispatch, history}) {
      history.listen((location) => {
        if (location.pathname === pathname) {
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
              yearMonths: [],
              inspectionResults: [],
              inspectionSummary: {},
              modalVisible: false,
              currentItem: {},
            }
          })
        }
      })
    },
  },
  effects: {

    * query({payload = {}}, {call, put, select}) {

      const now = moment(), yearMonths = []
      let {year, month} = payload
      let dateValid = true
      year = parseInt(year, 10)
      month = parseInt(month, 10)
      if (isNaN(month) || month < 1 || month > 12) {
        month = now.month() + 1
        dateValid = false
      }
      if (isNaN(year) || year > now.year()) {
        year = now.year()
        dateValid = false
      }
      if (moment().year(year).month(month - 1).startOf('M').isAfter(now)) {
        dateValid = false
        month = now.month() + 1
      }
      if (!dateValid) {
        yield put(routerRedux.push({
          pathname,
          search: queryString.stringify({
            month,
            year,
          }),
        }))
      }

      for (let i = 1; i <= 12; i++) {
        yearMonths.push({
          value: i,
          enabled: now.isAfter(moment().year(year).month(i - 1).startOf('d')),
        })
      }

      let inspectionResults = yield call(service.getInspectionReport, {month, year,})
      yield put({
        type: 'updateState',
        payload: {
          inspectionResults,
          yearMonths,
        }
      })
    },

    * showSummary({payload = {}}, {call, put, select}) {

      yield put({
        type: 'updateState',
        payload: {
          modalVisible: true,
          modalType: 'summary',
          inspectionSummary: {},
        }
      })
      let inspectionSummary = yield call(service.getInspectionSummary, payload)
      yield put({
        type: 'updateState',
        payload: {
          inspectionSummary,
        }
      })
    },

    * viewInspection({payload = {}}, {call, put, select}) {

      const currentItem = yield call(service.get, payload)
      yield put({
        type: 'updateState',
        payload: {
          modalVisible: true,
          modalType: 'view',
          currentItem,
        },
      })
    }
  },

  reducers: {
    hideModal(state) {
      return {
        ...state,
        modalVisible: false,
      }
    },
  },
})
