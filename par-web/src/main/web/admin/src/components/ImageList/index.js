import React, {Fragment} from 'react'
import styles from './ImageList.module.less'

const ImageList = ({
                     size = [180, 120],
                     list = [],
                   }) => {

  if (!list || list.length === 0) {
    return <Fragment />
  }

  return (
    <div className={styles.imageList}>
      <ul>
        {list.map(item => <li key={item.url}>
          <a href={`${item.url}`} target="_blank">
            <img src={`${item.url}!${size[0]}_${size[1]}`}/>
          </a>
        </li>)}
      </ul>
    </div>
  )
}

export default ImageList