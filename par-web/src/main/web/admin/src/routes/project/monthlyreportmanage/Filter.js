import React from 'react'
import PropTypes from 'prop-types'
import {Button, Col, DatePicker, Form, Input, Row, Select} from 'antd'
import SystemSelect from "../../../sections/system/SystemSelect"
import moment from "moment"

const {Search} = Input

const {MonthPicker} = DatePicker

const ColProps = {
  xs: 24,
  sm: 12,
  style: {
    marginBottom: 16,
  },
}

const TwoColProps = {
  ...ColProps,
  xl: 96,
}

const Option = Select.Option

const Filter = ({
                  onAdd,
                  onFilterChange,
                  filter,
                  monthlyReportStatus,
                  form: {
                    getFieldDecorator,
                    getFieldsValue,
                    setFieldsValue,
                  },
                }) => {

  const handleSubmit = () => {
    let fields = getFieldsValue()
    onFilterChange({
      ...fields,
      month: fields.month && fields.month.format('YYYY-MM')
    })
  }

  const handleReset = () => {
    const fields = getFieldsValue()
    for (let item in fields) {
      if ({}.hasOwnProperty.call(fields, item)) {
        if (fields[item] instanceof Array) {
          fields[item] = []
        } else {
          fields[item] = undefined
        }
      }
    }
    setFieldsValue(fields)
    handleSubmit()
  }

  const {status, month, systemId} = filter

  return (
    <Row gutter={24}>
      <Col {...ColProps} xl={{span: 6}} md={{span: 8}}>
        {getFieldDecorator('systemId', {initialValue: systemId})(
          <SystemSelect allowClear
                        placeholder="请选择所属系统"
                        onChange={() => setTimeout(handleSubmit)}/>)}
      </Col>
      <Col {...ColProps} xl={{span: 3}} md={{span: 8}}>
        {getFieldDecorator('month', {initialValue: month && moment(month)})(
          <MonthPicker style={{width: '100%'}} onChange={() => setTimeout(handleSubmit)} placeholder="请选择月份"
          />)}
      </Col>
      <Col {...ColProps} xl={{span: 3}} md={{span: 8}}>
        {getFieldDecorator('status', {initialValue: status})(
          <Select optionFilterProp="children"
                  allowClear
                  placeholder="请选择状态"
                  style={{width: '100%'}}
                  onChange={() => setTimeout(handleSubmit)}>
            {Object.keys(monthlyReportStatus).map(key => <Option value={key} key={key}>{monthlyReportStatus[key][0]}</Option>)}
          </Select>
        )}
      </Col>
      <Col {...TwoColProps} xl={{span: 8}} md={{span: 8}}>
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
        }}>
          <div>
            <Button type="primary" className="margin-right" onClick={handleSubmit}>查询</Button>
            <Button onClick={handleReset}>重置</Button>
          </div>
        </div>
      </Col>
    </Row>
  )
}

Filter.propTypes = {
  onAdd: PropTypes.func,
  form: PropTypes.object,
  filter: PropTypes.object,
  onFilterChange: PropTypes.func,
}

export default Form.create()(Filter)
