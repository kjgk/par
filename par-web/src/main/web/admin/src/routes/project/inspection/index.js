import React from 'react'
import PropTypes from 'prop-types'
import {routerRedux} from 'dva/router'
import {connect} from 'dva'
import {Button, Col, Popconfirm, Row} from 'antd'
import {Page} from '../../../components'
import queryString from 'query-string'
import List from './List'
import Filter from './Filter'
import Modal from './Modal'

const namespace = 'inspection'
const name = '系统巡检'

const Component = ({
                     location, dispatch, model, loading, app,
                   }) => {
  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {
    list, pagination, currentItem, modalVisible, modalType, selectedRowKeys,
    systemList, currentStep, currentSystem, allowNextStep, functionScreenshots, functionResults
  } = model

  const {user} = app

  const handleRefresh = (newQuery) => {
    dispatch(routerRedux.push({
      pathname,
      search: queryString.stringify({
        ...query,
        ...newQuery,
      }),
    }))
  }

  const handleDeleteItems = () => {
    dispatch({
      type: `${namespace}/multiDelete`,
      payload: selectedRowKeys,
    })
      .then(() => {
        handleRefresh({
          page: (list.length === selectedRowKeys.length && pagination.current > 1) ? pagination.current - 1 : pagination.current,
        })
      })
  }

  const modalProps = {
    item: modalType === 'create' ? {} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    width: 840,
    confirmLoading: loading.effects[`${namespace}/${modalType}`],
    title: name,
    systemList,
    currentStep,
    currentSystem,
    allowNextStep,
    functionScreenshots,
    functionResults,
    user,
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
    onSelectSystem(item) {
      dispatch({
        type: `${namespace}/selectSystem`,
        payload: item,
      })
    },
    onNextStep(item) {
      dispatch({
        type: `${namespace}/nextStep`,
        payload: item,
      })
    },
    onUploadChange(functionId, data) {
      let payload = {}
      payload[functionId] = data
      dispatch({
        type: `${namespace}/uploadChange`,
        payload,
      })
    },
    toggleResult(functionId) {
      dispatch({
        type: `${namespace}/toggleResult`,
        payload: functionId,
      })
    },
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
    onDeleteItem(id) {
      dispatch({
        type: `${namespace}/delete`,
        payload: id,
      })
        .then(() => handleRefresh({
          page: (list.length === 1 && pagination.current > 1) ? pagination.current - 1 : pagination.current,
        }))
    },
    onEditItem(item) {
      dispatch({
        type: `${namespace}/showModal`,
        payload: {
          modalType: 'update',
          currentItem: item,
        },
      })
    },
    rowSelection: {
      selectedRowKeys,
      onChange: (keys) => {
        dispatch({
          type: `${namespace}/updateState`,
          payload: {
            selectedRowKeys: keys,
          },
        })
      },
    },
  }

  const filterProps = {
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
      dispatch({
        type: `${namespace}/firstStep`,
      })
    },
  }

  return (
    <Page inner>
      <Filter {...filterProps} />
      {
        selectedRowKeys.length > 0 &&
        <Row style={{
          marginBottom: 24,
          textAlign: 'right',
        }}>
          <Col>
            {`已选择 ${selectedRowKeys.length} 条数据 `}
            <Popconfirm title="确定删除?" placement="left" onConfirm={handleDeleteItems}>
              <Button type="primary" style={{marginLeft: 8}} size="small">删除</Button>
            </Popconfirm>
          </Col>
        </Row>
      }
      <List {...listProps} />
      {modalVisible && <Modal {...modalProps} />}
    </Page>
  )
}

Component.propTypes = {
  model: PropTypes.object,
  app: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}
export default connect((models) => {
  return {
    model: models[namespace],
    app: models.app,
    loading: models.loading,
  }
})(Component)
