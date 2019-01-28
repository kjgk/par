import { request, config } from '../utils'

const { api } = config
const { menus } = api

export function query (params) {
  return request(menus, {
    method: 'GET',
    data: params,
  })
}
