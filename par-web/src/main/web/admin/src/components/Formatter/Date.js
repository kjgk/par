import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import moment from 'moment'
export default class Date extends PureComponent {
  render () {
    const { value, pattern = 'Y-MM-DD HH:mm'} = this.props
    if(value === undefined || value === null) {
      return <span style={this.props.style}> {this.props.emptyText || ''} </span>
    }
    let text = moment(value)
      .format(pattern)
    return (
      <span style={this.props.style}>{text}</span>
    )
  }
}

Date.propTypes = {
  value: PropTypes.oneOfType([
    PropTypes.number,
    PropTypes.string,
  ]),
  pattern: PropTypes.string,
}
