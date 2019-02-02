import React from 'react'
import PropTypes from 'prop-types'
import {connect} from 'dva'

const Home = ({
                home,
                loading,
                dispatch,
              }) => {

  return (
    <div>Home</div>
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
