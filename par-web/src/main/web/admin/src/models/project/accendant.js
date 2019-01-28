import service from '../../services/project/accendant'
import { createCrudModel } from '../common'

const namespace = 'accendant'
const pathname = '/project/accendant'

export default createCrudModel(namespace, pathname, service)
