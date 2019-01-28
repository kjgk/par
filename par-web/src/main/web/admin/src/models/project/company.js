import service from '../../services/project/company'
import { createCrudModel } from '../common'

const namespace = 'company'
const pathname = '/project/company'

export default createCrudModel(namespace, pathname, service)
