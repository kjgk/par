import service from '../../services/project/system'
import {createCrudModel} from '../common'
import modelExtend from "dva-model-extend"

const namespace = 'system'
const pathname = '/project/system'

export default modelExtend(createCrudModel(namespace, pathname, service), {
  namespace,
  state: {
    functionModalVisible: false,
  },

  reducers: {
    hideFunctionModal(state) {
      return {
        ...state,
        functionModalVisible: false,
      }
    },
    showFunctionModal(state) {
      return {
        ...state,
        functionModalVisible: true,
      }
    },
  },
})
