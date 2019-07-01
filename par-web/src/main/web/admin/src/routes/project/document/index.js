import React from 'react'
import PropTypes from 'prop-types'
import {routerRedux} from 'dva/router'
import {connect} from 'dva'
import {Button, Col, Popconfirm, Row} from 'antd'
import {Formatter, Page} from '../../../components'
import queryString from 'query-string'
import List from './List'
import Filter from './Filter'
import Modal from './Modal'

const namespace = 'document'
const categories = {
  'month': '月报',
  'season': '季报',
  'year': '年报',
  'emergencyPlan': '应急预案',
  'emergencyDrill': '应急演练记录',
}

const Component = ({
                     location, dispatch, model, loading,
                   }) => {
  location.query = queryString.parse(location.search)
  const {query, pathname} = location
  const {
    list, pagination, currentItem, modalVisible, modalType, selectedRowKeys, fileList,
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

  const category = pathname.split('/')[2]

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
    item: modalType === 'create' ? {category,} : currentItem,
    visible: modalVisible,
    maskClosable: false,
    confirmLoading: loading.effects[`${namespace}/${modalType}`],
    title: `上传${categories[category]}`,
    fileList,
    onUploadChange(data) {
      dispatch({
        type: `${namespace}/uploadChange`,
        payload: data,
      })
    },
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
          fileList: [],
        },
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
