import service from '../../services/project/inspection'
import {uploadFile} from '../../services/common'
import systemService from '../../services/project/system'
import {createCrudModel} from '../common'
import modelExtend from "dva-model-extend"

const namespace = 'inspection'
const pathname = '/inspection'

export default modelExtend(createCrudModel(namespace, pathname, service), {
  namespace,
  state: {
    systemList: [],
    currentStep: 0,
    currentSystem: undefined,
    allowNextStep: false,
    functionScreenshots: {},
    functionResults: {},
  },

  effects: {

    * firstStep({payload = {}}, {call, put, select}) {
      yield put({
        type: 'updateState',
        payload: {
          systemList: [],
          currentStep: 0,
          currentSystem: undefined,
          allowNextStep: false,
          functionScreenshots: {},
          functionResults: {},
        },
      })
      const systemList = yield call(systemService.getSystemList, payload)
      yield put({
        type: 'updateState',
        payload: {
          systemList
        },
      })
    },

    * nextStep({payload = {}}, {call, put, select}) {
      const {currentStep, currentSystem} = yield select(_ => _[namespace])
      if (currentStep === 0) {
        // 默认选中正常
        const functionResults = {}
        for (let fun of currentSystem.functionList || []) {
          functionResults[fun.objectId] = true
        }
        yield put({
          type: 'updateState',
          payload: {
            currentStep: currentStep + 1,
            allowNextStep: false,
            functionResults,
          },
        })
      } else if (currentStep === 1) {
        yield put({
          type: 'updateState',
          payload: {
            currentStep: currentStep + 1,
          },
        })
      }
    },

    * uploadFileFromClipboard({payload = {}}, {call, put, select}) {

      const {functionScreenshots, currentSystem} = yield select(_ => _[namespace])
      const {file, functionId} = payload
      let allowNextStep = false
      const fileItem = {
        uid: 'rc-upload-' + new Date().getTime(),
        name: file.name,
        size: file.size,
        type: file.type,
        percent: 0,
        status: "uploading",
      }
      functionScreenshots[functionId] = functionScreenshots[functionId] || {fileList: []}
      functionScreenshots[functionId].fileList.push(fileItem)

      yield put({
        type: 'updateState',
        payload: {
          functionScreenshots,
          allowNextStep,
        },
      })

      const response = yield call(uploadFile, file)

      fileItem.percent = 100
      fileItem.status = 'done'
      fileItem.thumbUrl = response.link
      fileItem.response = response

      allowNextStep = true
      for (let fun of currentSystem.functionList) {
        let screenshots = functionScreenshots[fun.objectId]
        if (screenshots && screenshots.fileList.length > 0) {
        } else {
          allowNextStep = false
        }
      }

      yield put({
        type: 'updateState',
        payload: {
          functionScreenshots,
          allowNextStep,
        },
      })
    }
  },

  reducers: {
    selectSystem(state, {payload}) {

      return {
        ...state,
        currentSystem: payload,
        allowNextStep: true,
      }
    },
    uploadChange(state, {payload}) {
      const {functionScreenshots, currentSystem} = state
      let allowNextStep = true
      let currentScreenshots = null
      for (let fun of currentSystem.functionList) {
        let screenshots = payload[fun.objectId] || functionScreenshots[fun.objectId]
        currentScreenshots = currentScreenshots || payload[fun.objectId]
        if (screenshots && screenshots.fileList.length > 0) {
        } else {
          allowNextStep = false
        }
      }
      allowNextStep = allowNextStep && currentScreenshots.file.percent === 100

      delete currentScreenshots.file
      for (let file of currentScreenshots.fileList) {
        if (file.status === 'done') {
          file.thumbUrl = file.response.link
        } else {
          file.thumbUrl = null
        }
      }

      return {
        ...state,
        functionScreenshots: {
          ...functionScreenshots,
          ...payload,
        },
        allowNextStep,
      }
    },
    toggleResult(state, {payload}) {
      const {functionResults} = state
      functionResults[payload] = !functionResults[payload]
      return {
        ...state,
        functionResults,
      }
    },
  },
})
