import service from '../../services/project/supervisor'
import { createCrudModel } from '../common'

const namespace = 'supervisor'
const pathname = '/project/supervisor'

export default createCrudModel(namespace, pathname, service)
