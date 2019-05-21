import service from '../../services/project/monthly.report'
import {pageModel} from '../common'
import modelExtend from "dva-model-extend"
import queryString from "query-string"
import {message} from "antd"

const namespace = 'monthlyReportManage'
const pathname = '/monthly/report/manage'

export default modelExtend(pageModel, {
  namespace,
  state: {
    summaryStatus: [],
    selectedSystems: [],
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

    * audit({payload: {objectId, ...params}}, {call, put}) {
      yield call(service.auditMonthReport, objectId, params)
      message.success('审核成功！')
      yield put({
        type: 'hideModal',
      })
    },

    * summary({payload}, {call, put}) {
      yield put({
        type: 'updateState',
        payload: {
          summaryStatus: [],
          selectedSystems: [],
        },
      })
      const summaryStatus = yield call(service.getMonthReportStatus)
      const selectedSystems = summaryStatus.filter(status => status.status !== undefined).map(status => status.systemId)
      yield put({
        type: 'updateState',
        payload: {
          modalVisible: true,
          modalType: 'summary',
          summaryStatus,
          selectedSystems,
        },
      })
    },
  },
  reducers: {
    showModal(state, {payload}) {
      return {
        ...state, ...payload,
        modalVisible: true,
      }
    },
    hideModal(state) {
      return {
        ...state,
        modalVisible: false,
      }
    },

    selectSystem(state, {payload}) {
      return {
        ...state,
        selectedSystems: payload,
      }
    },
  },
})
