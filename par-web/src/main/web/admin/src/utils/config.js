const api = '/api/v1'

module.exports = {
  name: '巡检平台',
  prefix: 'par',
  title: '绿化市容信息化系统巡检平台',
  footerText: '上海市绿化和市容管理信息中心 © 2020',
  openPages: ['/login', '/'],
  apiPrefix: api,
  api: {
    userLogin: `/login/account`,
    userLogout: `/logout`,
    userModifyPassword: `${api}/system/user/modifyPassword`,
    currentInfo: `${api}/current`,
    menus: `${api}/menus`,
    fileUpload: `${api}/system/file/upload`,
  },
  contextPath: '/par'
}
