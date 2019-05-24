import service from '../../services/project/document'
import {createCrudModel} from '../common'
import modelExtend from "dva-model-extend"
import queryString from "query-string"
import {message} from "antd"

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

export default modelExtend(
  crudModel,
  {
    namespace: namespace,
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
              },
            })
          }
        })
      },
    },
  }
)
