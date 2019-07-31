import service from '../../services/project/document'
import {createCrudModel} from '../common'
import modelExtend from "dva-model-extend"
import queryString from "query-string"
import {message} from "antd"
import moment from "moment"

const namespace = 'document'
const pathname = '/document'


let crudModel = createCrudModel(namespace, pathname, service)
crudModel.reducers.uploadChange = (state, {payload}) => {
  return {
    ...state,
    fileList: payload.fileList,
  }
}
crudModel.effects.create = function* ({payload}, {call, put, select}) {

  const {fileList: [file]} = yield select(_ => _[namespace])
  yield call(service.create, {
    ...payload,
    attachment: file && file.response,
  })
  yield put({type: 'hideModal'})
  message.success('保存成功！')
}

crudModel.effects.showSummary = function* ({payload}, {call, put, select}) {

  const {defaultDateRange: [startDate, endDate]} = yield select(_ => _[namespace])
  yield put({
    type: 'showModal',
    payload: {
      modalType: 'summary',
    },
  })
  yield put({
    type: 'querySummary',
    payload: {
      ...payload,
      startDate,
      endDate,
    },
  })
}

crudModel.effects.querySummary = function* ({payload}, {call, put, select}) {

  const {
    category,
    startDate,
    endDate
  } = payload

  const summaryList = yield call(service.querySummary, {
    category,
    startDate: startDate.startOf("d").format("YYYY-MM-DD HH:mm:ss"),
    endDate: endDate.endOf("d").format("YYYY-MM-DD HH:mm:ss"),
  })
  yield put({
    type: 'updateState',
    payload: {
      summaryList,
    },
  })
}


export default modelExtend(
  crudModel,
  {
    namespace: namespace,
    state: {
      defaultDateRange: [moment().startOf('M').add(-1, 'M'), moment()]
    },
    subscriptions: {
      setup({dispatch, history}) {
        history.listen((location) => {
          if (location.pathname.startsWith(pathname)) {
            const category = location.pathname.split("/")[2]
            const payload = {
              page: 1,
              pageSize: 10,
              ...{
                ...queryString.parse(location.search),
                category,
              },
            }
            dispatch({
              type: 'query',
              payload,
            })
          } else {
            dispatch({
              type: 'updateState',
              payload: {
                currentItem: {},
                modalVisible: false,
                modalType: 'create',
                selectedRowKeys: [],
                fileList: [],
                summaryList: [],
              },
            })
          }
        })
      },
    },
  }
)
