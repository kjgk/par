import service from '../../services/project/region'
import { createCrudModel } from '../common'

const namespace = 'region'
const pathname = '/project/region'

export default createCrudModel(namespace, pathname, service)
