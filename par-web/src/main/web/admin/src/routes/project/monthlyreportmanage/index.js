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
import SummaryModal from "./SummaryModal"

const namespace = 'monthlyReportManage'

const Component = ({
                     location, dispatch, model, loading,
                   }) => {
  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {
    list, pagination, currentItem, modalVisible, modalType, summaryStatus, selectedSystems,
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
    onSummary() {
      dispatch({
        type: `${namespace}/summary`,
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

  const summaryModalProps = {
    visible: modalVisible,
    maskClosable: false,
    width: 800,
    summaryStatus,
    selectedSystems,
    onSelectSystem(data) {
      dispatch({
        type: `${namespace}/selectSystem`,
        payload: data,
      })
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
      {modalVisible && modalType === 'summary' && <SummaryModal {...summaryModalProps} />}
    </Page>
  )
}

export default connect((models) => {
  return {
    model: models[namespace],
    loading: models.loading,
  }
})(Component)
