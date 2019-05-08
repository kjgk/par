import React from 'react'
import {Select} from 'antd'
import service from '../../services/project/system'

const Option = Select.Option

export default class Component extends React.Component {

  constructor(props) {
    super(props)
    this.state = {
      list: [],
      value: props.value,
    }
  }

  componentDidMount() {
    this.initStatus()
  }

  async initStatus() {
    const list = await service.getSystemList(this.props.self !== true)
    this.setState({
      list,
    })
  }

  render() {
    return (
      <Select {...this.props}
              optionFilterProp="children"
              style={{width: '100%'}}
      >
        {this.state.list.map((item) => <Option value={item.objectId} key={item.objectId}>{item.name}</Option>)}
      </Select>
    )
  }
}
