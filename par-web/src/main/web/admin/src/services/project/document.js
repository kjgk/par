import { createCrudService } from '../common'
import {config, request} from "../../utils"
import {stringify} from "qs"

const path = '/document'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  querySummary (params) {
    return request(`${api}/summary?${stringify(params)}`)
  },
}
