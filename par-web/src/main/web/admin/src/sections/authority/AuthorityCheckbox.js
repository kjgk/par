import React from 'react'
import PropTypes from 'prop-types'
import { Checkbox  } from 'antd'
import service from '../../services/system/authority'

const CheckboxGroup = Checkbox.Group

class AuthorityCheckbox extends React.Component {

  constructor (props) {
    super(props)
    this.state = {
      list: [],
      value: props.value
    }
    this.fetchList()
  }

  async fetchList () {
    const list = []
    const response = await service.list()
    for(let item of response) {
      list.push({
        value: item.objectId,
        label: item.name,
      })
    }
    this.setState({list})
  }

  render () {
    return (
      <CheckboxGroup
        options={this.state.list}
        value={this.props.value}
        onChange={this.props.onChange}/>
    )
  }
}

AuthorityCheckbox.propTypes = {
  value: PropTypes.arrayOf(PropTypes.string),
  onChange: PropTypes.func,
}

export default AuthorityCheckbox
