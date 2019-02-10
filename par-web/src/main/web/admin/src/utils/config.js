const api = '/api/v1'

module.exports = {
  name: '巡检平台',
  prefix: 'par',
  footerText: '绿化市容系统巡检平台 © 2019',
  openPages: ['/login', '/'],
  apiPrefix: api,
  api: {
    userLogin: `/login/account`,
    userLogout: `/logout`,
    currentInfo: `${api}/current`,
    menus: `${api}/menus`,
  },
  contextPath: '/par'
}
