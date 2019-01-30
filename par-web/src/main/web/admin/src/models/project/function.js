import service from '../../services/project/function'
import { createCrudModel } from '../common'

const namespace = 'function'
const pathname = '/project/function'

export default createCrudModel(namespace, pathname, service)
