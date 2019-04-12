import { createCrudService } from '../common'
import {config, request} from "../../utils"
import {stringify} from "qs"

const path = '/monthlyReport'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
}
