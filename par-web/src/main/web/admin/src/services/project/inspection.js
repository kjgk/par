import { createCrudService } from '../common'
import {config, request} from "../../utils"
import {stringify} from "qs"

const path = '/inspection'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  getInspectionReport (params) {
    return request(`${api}/report?${stringify(params)}`)
  },
  getInspectionSummary (params) {
    return request(`${api}/summary?${stringify(params)}`)
  },
}
