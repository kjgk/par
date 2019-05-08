import {createCrudService} from '../common'
import {config, request} from "../../utils"

const path = '/system'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  getSystemList(self) {
    return request(`${api}/list?self=${self ? 0 : 1}`)
  },
}
