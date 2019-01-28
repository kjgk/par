import modelExtend from 'dva-model-extend'
import {model} from './common'

export default modelExtend(model, {

  namespace: 'home',
  state: {
    statistics: {},
  },
  subscriptions: {

    setupHistory({dispatch, history}) {
      history.listen((location) => {
        if (location.pathname === '/') {
          dispatch({
            type: 'updateState',
            payload: {
              statistics: {
                count: 99
              },
            },
          })
        } else {
          dispatch({
            type: 'updateState',
            payload: {
              statistics: {},
            },
          })
        }
      })
    },


  },
  effects: {

    * fetch({payload,}, {call, put}) {
    },

  },
  reducers: {},
})
