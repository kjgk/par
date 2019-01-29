const api = '/api/v1'

module.exports = {
  name: '巡检平台',
  prefix: 'par',
  footerText: '巡检平台-后台管理  © 2019',
  openPages: ['/login'],
  apiPrefix: api,
  api: {
    userLogin: `/login/account`,
    userLogout: `/logout`,
    currentInfo: `${api}/current`,
    menus: `${api}/menus`,
  },
  contentPath: '/par'
}
