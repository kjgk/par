import service from '../../services/project/system'
import { createCrudModel } from '../common'

const namespace = 'system'
const pathname = '/project/system'

export default createCrudModel(namespace, pathname, service)
