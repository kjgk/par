import React from 'react'
import PropTypes from 'prop-types'
import { Modal, Spin, Tree } from 'antd'

const TreeNode = Tree.TreeNode

const renderTreeNodes = function (treeData) {
  return treeData.map((item) => {
    return <TreeNode title={item.name} key={item.objectId} isLeaf={item.leaf === 1}>
      {item.children && item.children.length && renderTreeNodes(item.children)}
    </TreeNode>
  })
}

const modal = ({
                 data: { checkedMenus, menuData },
                 onCheck,
                 modalLoading,
                 ...modalProps
               }) => {


  const treeProps = {
    defaultExpandAll: true,
    checkable: true,
    checkedKeys: checkedMenus,
    onCheck,
  }

  return (
    <Modal {...modalProps}>
      <Spin spinning={modalLoading}>
        {menuData.length && <Tree {...treeProps}>
          {renderTreeNodes(menuData)}
        </Tree>}</Spin>
    </Modal>
  )
}

modal.propTypes = {
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default modal
