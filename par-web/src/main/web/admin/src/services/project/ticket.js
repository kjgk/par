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
  processTicket({objectId, ...data}) {
    return request(`${api}/${objectId}/process`, {
      method: 'POST',
      body: data,
    })
  },
  recordTicket(params) {
    return request(`${api}/record`, {
      method: 'POST',
      body: params,
    })
  },
}
