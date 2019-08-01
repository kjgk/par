import React from 'react'
import {Icon} from 'antd'
import classnames from "classnames"
import styles from './inspection.module.less'

const Component = ({
                     inspectionId,
                     value,
                     onClick,
                   }) => {

  const classes = [
    'inspection-calendar-cell-item',
    !inspectionId && styles['item-status-0'],
    styles['item-status-' + value],
  ]

  return (
    <div className={classnames(...classes)} onClick={onClick}>
      {!inspectionId && <Icon type="close"/>}
      {inspectionId && [1, 3].includes(value) && <Icon type="check"/>}
      {inspectionId && [2, 4, 5].includes(value) && <Icon type="exclamation"/>}
    </div>
  )
}


export default Component
