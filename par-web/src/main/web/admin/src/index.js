/* global window */
/* global document */
/* global location */
/* eslint no-restricted-globals: ["error", "event"] */

import dva from 'dva'
import createLoading from 'dva-loading'
import createHistory from 'history/createBrowserHistory'
import { contextPath } from './utils/config'

const development = process.env.NODE_ENV === 'development'

// 1. Initialize
const app = dva({
  ...createLoading({
    effects: true,
  }),
  history: createHistory({ basename: development ? '' : `${contextPath}/admin` }),
  onError (error) {
    console.error(error.message)
  },
})

// 2. Model
app.model(require('./models/app').default)

// 3. Router
app.router(require('./router').default)

// 4. Start
app.start('#root')

export default app._store // eslint-disable-line
