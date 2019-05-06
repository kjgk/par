import service from '../../services/project/monthly.report'
import {pageModel} from '../common'
import modelExtend from "dva-model-extend"
import queryString from "query-string"

const namespace = 'monthlyReportManage'
const pathname = '/monthly/report/manage'

export default modelExtend(pageModel, {
  namespace,
  state: {
  },
  subscriptions: {

    setup({dispatch, history}) {
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
              list: [],
            }
          })
        }
      })
    },
  },
  effects: {

    * query({payload = {}}, {call, put}) {
      const {content, totalElements} = yield call(service.getAllMonthReport, payload)
      if (content) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: content,
            pagination: {
              current: Number(payload.page) || 1,
              pageSize: Number(payload.pageSize) || 10,
              total: typeof totalElements === 'string' ? parseInt(totalElements, 10) : totalElements,
            },
          },
        })
      }
    },
  },
})
