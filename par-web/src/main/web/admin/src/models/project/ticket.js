import service from '../../services/project/ticket'
import { createCrudModel } from '../common'

const namespace = 'ticket'
const pathname = '/ticket'

export default createCrudModel(namespace, pathname, service)
