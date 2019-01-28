import React from 'react'
import PropTypes from 'prop-types'
import {connect} from 'dva'

const Home = ({
                home,
                loading,
                dispatch,
              }) => {

  const {statistics} = home

  return (
    <div>Home {statistics.count}</div>
  )
}

Home.propTypes = {
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({home, loading}) => ({
  loading,
  home,
}))(Home)
