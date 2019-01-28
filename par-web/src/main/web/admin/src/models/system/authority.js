import service from '../../services/system/authority'
import { createCrudModel } from '../common'

const namespace = 'authority'
const pathname = '/system/authority'

export default createCrudModel(namespace, pathname, service)
