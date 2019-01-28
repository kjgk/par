import service from '../../services/system/menu'
import { createTreeModel } from '../common'

const namespace = 'menu'
const pathname = '/system/menu'

export default createTreeModel(namespace, pathname, service)
