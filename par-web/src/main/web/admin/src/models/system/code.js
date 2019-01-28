import service from '../../services/system/code'
import { createTreeModel } from '../common'

const namespace = 'code'
const pathname = `/system/${namespace}`

export default createTreeModel(namespace, pathname, service)
