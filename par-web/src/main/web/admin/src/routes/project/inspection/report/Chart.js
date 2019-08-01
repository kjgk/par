import React from 'react'
import {Axis, Chart, Geom, Shape, Tooltip, Legend} from 'bizcharts'

Shape.registerShape('polygon', 'doublePoint', {
  draw(cfg, container) {
    const {segment, value} = cfg.origin._origin
    const points = this.parsePoints(cfg.points) // 将0-1空间的坐标转换为画布坐标
    let color = '#DDDDDD'
    let width = points[2].x - points[0].x
    if (value === -1) {
      color = '#ffdddd'
    }
    if (value === 0) {
      color = '#f51e3a'
    }
    if (value === 1) {
      color = '#87d068'
      // color = '#20AA73'
    }
    if (value === 2) {
      color = '#f6d059'
    }
    if (value === 3) {
      color = '#2db7f5'
    }
    if (value === 4) {
      color = '#9527ff'
    }
    if (value === 5) {
      color = '#8e4e2f'
    }
    return container.addShape('polygon', {
      attrs: {
        cursor: value > 0 ? 'pointer' : 'not-allowed',
        points: [
          [points[0].x + (segment === 3 ? width / 2 : 4), points[0].y - 4],
          [points[1].x + (segment === 3 ? width / 2 : 4), points[1].y + 4],
          [points[2].x - (segment === 1 ? width / 2 : 4), points[2].y + 4],
          [points[3].x - (segment === 1 ? width / 2 : 4), points[3].y - 4],
        ],
        fill: color,
      }
    })
  }
})

const segmentNames = {1: '上午', 3: '下午',}
const stateNames = {0: '未巡检', 1: '已巡检', 2: '已巡检', 3: '已巡检', 4: '已巡检',}

const ReportChart = ({
                       systemList = [],
                       dateList = [],
                       values = [],
                       onChartClick,
                     }) => {

  const dataList = []
  for (let i = 0; i < values.length; i++) {
    const item = values[i]
    dataList.push({
      day: item[0],
      name: item[1],
      segment: item[2],
      value: item[3],
      inspectionId: item[4],
    })
  }

  const cols = {
    name: {
      type: 'cat',
      values: systemList,
    },
    day: {
      type: 'cat',
      values: dateList,
    },
  }

  const handleClick = ({data} = {}) => {
    if (data) {
      const {_origin: {inspectionId, value}} = data
      if (value > 0 && inspectionId) {
        onChartClick(inspectionId)
      }
    }
  }

  return <Chart
    height={22 * systemList.length + 60}
    data={dataList}
    scale={cols}
    padding={[40, 20, 20, 250]}
    forceFit
    animate={false}
    onClick={handleClick}
  >
    <Axis
      name="name"
      grid={{
        align: 'center',
        lineStyle: {
          lineWidth: 1,
          lineDash: null,
          stroke: '#fff',
        },
        showFirstLine: true,
      }}
    />
    <Axis
      name="day"
      position="top"
      grid={{
        align: 'center',
        lineStyle: {
          lineWidth: 1,
          lineDash: null,
          stroke: '#fff',
        },
      }}
    />
    <Tooltip custom={true} showTitle={false} itemTpl='<tr><td>{content}</td></tr>'/>
    <Geom
      type="polygon"
      position="day*name"
      shape="doublePoint"
      tooltip={['day*segment*name*value', (day, segment, name, value) => {
        if (value === null) {
          return {
            content: '待巡检',
          }
        }
        if (value === -1) {
          return {
            content: '休息日',
          }
        }
        return {
          content: systemList[name] + ' / ' + (day + 1) + '号' + segmentNames[segment] + ' / ' + stateNames[value],
        }
      }]}
    >
    </Geom>
  </Chart>
}

export default ReportChart