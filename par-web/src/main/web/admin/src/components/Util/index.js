import React from 'react'
import { Icon, Tree } from 'antd'

const TreeNode = Tree.TreeNode

const createTreeNodeTitle = function ({ title, handleDelete }) {
  return (
    <div>{title} {handleDelete &&
    <Icon onClick={() => handleDelete()} style={{ color: '#AAAAAA' }} type="close-circle-o"/>} </div>
  )
}

const renderTreeNodes = function ({ treeData, selectedKeys }, { onDelete }) {
  return treeData.map((item) => {
    return <TreeNode title={createTreeNodeTitle({
      title: item.name || '-新节点-',
      handleDelete: selectedKeys.includes(item.objectId) && (() => {
        onDelete(item.objectId)
      }),
    })} key={item.objectId} isLeaf={item.leaf === 1}>
      {item.children && item.children.length && renderTreeNodes({
        treeData: item.children,
        selectedKeys,
      }, { onDelete })}
    </TreeNode>
  })
}

export {
  renderTreeNodes,
}
