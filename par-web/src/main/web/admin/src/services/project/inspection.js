import { createCrudService } from '../common'
import {config, request} from "../../utils"

const path = '/inspection'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
}
