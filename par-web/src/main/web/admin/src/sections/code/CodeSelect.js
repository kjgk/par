import React from 'react'
import PropTypes from 'prop-types'
import { Select } from 'antd'
import service from '../../services/system/code'

const Option = Select.Option

class CodeSelect extends React.Component {

  constructor (props) {
    super(props)
    this.state = {
      list: [],
      value: props.value,
    }
    this.fetchList()
  }

  async fetchList () {
    this.setState({
      list: await service.query({tag: this.props.tag}),
    })
  }

  render () {
    return (
      <Select value={this.props.value}
              onChange={this.props.onChange}
              placeholder={this.props.placeholder}
              allowClear={this.props.allowClear}
              showSearch={this.props.showSearch}
              optionFilterProp="children"
              style={{ width: '100%' }}
      >
        {this.state.list.map((item) => <Option value={item.objectId} key={item.objectId}>{item.name}</Option>)}
      </Select>
    )
  }
}

CodeSelect.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func,
  allowClear: PropTypes.bool,
  placeholder: PropTypes.string,
  tag: PropTypes.string,
}

export default CodeSelect
