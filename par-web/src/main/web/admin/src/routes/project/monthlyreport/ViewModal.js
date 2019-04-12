import React from 'react'
import PropTypes from 'prop-types'
import {Modal} from 'antd'

const modal = ({
                 item = {},
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    title: item.system.name + ' - 月报',
  }

  return (
    <Modal {...modalOpts}>
      todo
    </Modal>
  )
}

modal.propTypes = {
  type: PropTypes.string,
  item: PropTypes.object,
}

export default modal
