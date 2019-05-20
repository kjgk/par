import React from 'react'
import PropTypes from 'prop-types'
import {routerRedux} from 'dva/router'
import {connect} from 'dva'
import {Button, Col, Popconfirm, Row} from 'antd'
import {Formatter, Page} from '../../../../components'
import queryString from 'query-string'
import List from '../List'
import Filter from '../Filter'
import ViewModal from '../ViewModal'
import ProcessModal from './Modal'

const namespace = 'ticketHandle'

const Component = ({
                     location, dispatch, model, loading,
                   }) => {
  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {
    list, pagination, currentItem, modalVisible, modalType, selectedRowKeys, fileList, fileLimit,
  } = model

  const handleRefresh = (newQuery) => {
    dispatch(routerRedux.push({
      pathname,
      search: queryString.stringify({
        ...query,
        ...newQuery,
      }),
    }))
  }

  const listProps = {
    dataSource: list,
    loading: loading.effects[`${namespace}/query`],
    pagination,
    location,
    onChange(page) {
      handleRefresh({
        page: page.current,
        pageSize: page.pageSize,
      })
    },
    onViewItem(item) {
      dispatch({
        type: `${namespace}/showModal`,
        payload: {
          modalType: 'view',
          currentItem: item,
        },
      })
    },
    onAcceptItem(value) {
      dispatch({
        type: `${namespace}/accept`,
        payload: value,
      })
        .then(() => handleRefresh({
          page: (list.length === 1 && pagination.current > 1) ? pagination.current - 1 : pagination.current,
        }))
    },
    onProcessItem(item) {
      dispatch({
        type: `${namespace}/showModal`,
        payload: {
          modalType: 'process',
          currentItem: item,
        },
      })
    },
  }

  const filterProps = {
    filter: {
      ...query,
    },
    onRecord() {
      dispatch(routerRedux.push({
        pathname: '/ticket/record',
      }))
    },
    onFilterChange(value) {
      handleRefresh({
        ...value,
        page: 1,
      })
    },
  }

  const viewModalProps = {
    item: currentItem,
    visible: modalVisible,
    maskClosable: false,
    title: `工单详情`,
    onCancel() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
    },
  }

  const processModalProps = {
    item: currentItem,
    visible: modalVisible,
    maskClosable: false,
    title: `工单结单`,
    fileList,
    fileLimit,
    onUploadChange(data) {
      dispatch({
        type: `${namespace}/uploadChange`,
        payload: data,
      })
    },
    onOk(data) {
      dispatch({
        type: `${namespace}/process`,
        payload: data,
      })
        .then(() => handleRefresh())
    },
    onCancel() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
    },
  }

  return (
    <Page inner>
      <Filter {...filterProps} />
      <List {...listProps} />
      {modalVisible && modalType === 'view' && <ViewModal  {...viewModalProps}/>}
      {modalVisible && modalType === 'process' && <ProcessModal  {...processModalProps}/>}
    </Page>
  )
}

Component.propTypes = {
  model: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}
export default connect((models) => {
  return {
    model: models[namespace],
    loading: models.loading,
  }
})(Component)
