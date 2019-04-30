import {stringify} from 'qs'
import {config, request} from '../utils'

const { api } = config
const { userLogout, userLogin, currentInfo, userModifyPassword } = api

export function login (params) {
  return request(userLogin, {
    method: 'POST',
    body: stringify(params),
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
    },
  })
}

export function logout () {
  return request(userLogout, {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
    },
  })
}

export function query (params) {
  return request(currentInfo, {
    method: 'GET',
    body: params,
  })
}

export function modifyPassword (params) {
  return request (userModifyPassword, {
    method: 'PUT',
    body: params,
    headers: {
      'Content-Type': 'application/json',
    },
  })
}
