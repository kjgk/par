import React from 'react'
import {Select} from 'antd'
import service from '../../services/project/system'

const Option = Select.Option

let list

export default class Component extends React.Component {

  constructor(props) {
    super(props)
    this.state = {
      list: list || [],
      value: props.value,
    }
  }

  componentDidMount() {
    if (list === undefined || list.length === 0) {
      this.initStatus()
    }
  }

  async initStatus() {
    list = await service.list()
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
