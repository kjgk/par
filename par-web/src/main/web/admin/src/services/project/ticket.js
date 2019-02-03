import {createCrudService} from '../common'
import {config, request} from "../../utils"

const path = '/ticket'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  acceptTicket(ticketId) {
    return request(`${api}/${ticketId}/accept`, {
      method: 'POST',
      body: {},
    })
  },
  processTicket(ticketId) {

  },
}
