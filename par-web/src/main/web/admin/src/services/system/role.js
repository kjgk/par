import { config, request } from '../../utils'
import { createCrudService } from '../common'

const path = '/system/role'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  loadMenu (objectId) {
    return request(`${api}/${objectId}/menu`)
  },
  saveMenu (objectId, params) {
    return request(`${api}/${objectId}/menu`, {
      method: 'POST',
      body: params,
    })
  },
}
