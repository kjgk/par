import React from 'react'
import PropTypes from 'prop-types'
import { Select } from 'antd'
import service from '../../services/project/region'

const Option = Select.Option

class RegionSelect extends React.Component {

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
      list: await service.list(),
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

RegionSelect.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func,
  allowClear: PropTypes.bool,
  placeholder: PropTypes.string,
}

export default RegionSelect
