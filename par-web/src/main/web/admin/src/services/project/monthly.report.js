import {createCrudService} from '../common'
import {config, request} from "../../utils"

const path = '/monthlyReport'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  getCurrentMonthReport(systemId) {
    return request(`${api}/current?systemId=${systemId}`)
  },
}
