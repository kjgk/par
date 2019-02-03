import React from 'react'
import {Tag} from "antd"

const statusConfig = {
  0: ['#f50', '待处理'],
  1: ['#2db7f5', '处理中'],
  2: ['#87d068', '已完成'],
}

export default ({value}) => <Tag color={statusConfig[value][0]}>{statusConfig[value][1]}</Tag>
