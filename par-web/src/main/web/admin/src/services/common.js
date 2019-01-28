import {stringify} from 'qs'
import {config, request} from '../utils'

const {apiPrefix} = config

const createCrudService = function (path) {

  const api = `${apiPrefix}${path}`

  return {
    query(params) {
      return request(`${api}?${stringify(params)}`)
    },

    list() {
      return request(`${api}/list`)
    },

    get(objectId) {
      return request(`${api}/${objectId}`)
    },

    create(params) {
      return request(api, {
        method: 'POST',
        body: params,
      })
    },

    update(params) {
      return request(`${api}/${params.objectId}`, {
        method: 'PATCH',
        body: params,
      })
    },

    remove(objectId) {
      return request(`${api}/${objectId}`, {
        method: 'DELETE',
      })
    },

    multiRemove(params) {
      return request(`${api}`, {
        method: 'DELETE',
        body: params,
      })
    },
  }
}

const createTreeService = function (path) {

  const api = `${apiPrefix}${path}`

  return {
    ...createCrudService(path),
    query(params) {
      return request(`${api}/tree?${stringify(params)}`)
    },
    getAll() {
      return request(`${api}/tree`)
    },
    move(params) {
      return request(`${api}/${params.sourceId}/move`, {
        method: 'POST',
        body: params,
      })
    },
  }
}

export {createCrudService, createTreeService}
