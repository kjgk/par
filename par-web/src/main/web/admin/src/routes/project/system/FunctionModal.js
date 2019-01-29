import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Modal } from 'antd'
import CompanySelect from "../../../sections/company/CompanySelect"

const modal = ({
                 item = {},
                 onOk,
                 ...modalProps
               }) => {

  const modalOpts = {
    ...modalProps,
    onOk: () => {

    },
  }

  return (
    <Modal {...modalOpts}>
abc
      {/*todo*/}
    </Modal>
  )
}

modal.propTypes = {
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default modal
