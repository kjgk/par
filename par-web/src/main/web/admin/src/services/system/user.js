import { config, request } from '../../utils'
import { createCrudService } from '../common'

const path = '/system/user'
const api = `${config.apiPrefix}${path}`

export default {
  ...createCrudService(path),
  saveAccount (objectId, params) {
    return request(`${api}/${objectId}/account`, {
      method: 'PUT',
      body: params,
    })
  },
}
