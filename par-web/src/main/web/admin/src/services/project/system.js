import { createCrudService } from '../common'
import {config, request} from "../../utils"

const path = '/system'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  getSystemList () {
    return request(`${api}/list?self=1`)
  },
}
