import React from 'react'
import {routerRedux} from 'dva/router'
import {connect} from 'dva'
import {Page} from '../../../components'
import queryString from 'query-string'
import List from './List'
import Filter from './Filter'
import {monthlyReportStatus} from '../../../models/dict'
import ViewModal from "../monthlyreport/ViewModal"
import AuditModal from "./AuditModal"

const namespace = 'monthlyReportManage'
const name = '月报管理'

const Component = ({
                     location, dispatch, model, loading,
                   }) => {
  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {
    list, pagination, currentItem, modalVisible, modalType, selectedRowKeys,
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

  const modalProps = {
    item: modalType === 'create' ? {} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    confirmLoading: loading.effects[`${namespace}/${modalType}`],
    title: modalType === 'create' ? `添加${name}` : `编辑${name}`,
    onOk(data) {
      dispatch({
        type: `${namespace}/${modalType}`,
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

  const listProps = {
    dataSource: list,
    loading: loading.effects[`${namespace}/query`],
    pagination,
    location,
    monthlyReportStatus,
    onChange(page) {
      handleRefresh({
        page: page.current,
        pageSize: page.pageSize,
      })
    },
    onAuditItem(item) {
      dispatch({
        type: `${namespace}/showModal`,
        payload: {
          modalType: 'audit',
          currentItem: item,
        },
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
  }

  const filterProps = {
    monthlyReportStatus,
    filter: {
      ...query,
    },
    onFilterChange(value) {
      handleRefresh({
        ...value,
        page: 1,
      })
    },
    onAdd() {
      dispatch({
        type: `${namespace}/showModal`,
        payload: {
          modalType: 'create',
        },
      })
    },
  }

  const auditModalProps = {
    item: currentItem,
    visible: modalVisible,
    maskClosable: false,
    width: 640,
    onOk(data) {
      dispatch({
        type: `${namespace}/audit`,
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

  const viewModalProps = {
    item: currentItem,
    visible: modalVisible,
    maskClosable: false,
    width: 840,
    onOk() {
      dispatch({
        type: `${namespace}/hideModal`,
      })
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
      {modalVisible && modalType === 'audit' && <AuditModal {...auditModalProps} />}
      {modalVisible && modalType === 'view' && <ViewModal {...viewModalProps} />}
    </Page>
  )
}

export default connect((models) => {
  return {
    model: models[namespace],
    loading: models.loading,
  }
})(Component)
