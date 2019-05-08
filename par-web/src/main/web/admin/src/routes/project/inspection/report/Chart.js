import React from 'react'
import {Axis, Chart, Geom, Shape, Tooltip} from 'bizcharts'

Shape.registerShape('polygon', 'doublePoint', {
  draw(cfg, container) {
    const {segment, value} = cfg.origin._origin
    const points = this.parsePoints(cfg.points) // 将0-1空间的坐标转换为画布坐标
    let color = '#DDDDDD'
    let width = points[2].x - points[0].x
    if (value === 1) {
      color = '#20AA73'
    }
    if (value === 0) {
      color = '#f51e3a'
    }
    if (value === -1) {
      color = '#ffdddd'
    }
    return container.addShape('polygon', {
      attrs: {
        points: [
          [points[0].x + (segment === 3 ? width / 2 : 4), points[0].y - 4],
          [points[1].x + (segment === 3 ? width / 2 : 4), points[1].y + 4],
          [points[2].x - (segment === 1 ? width / 2 : 4), points[2].y + 4],
          [points[3].x - (segment === 1 ? width / 2 : 4), points[3].y - 4],
        ],
        fill: color
      }
    })
  }
})

const segmentNames = {1: '上午', 3: '下午'}
const stateNames = {1: '已巡检', 0: '未巡检'}

const ReportChart = ({
                       systemList = [],
                       dateList = [],
                       values = [],
                     }) => {

  const dataList = []
  for (let i = 0; i < values.length; i++) {
    const item = values[i]
    dataList.push({
      day: item[0],
      name: item[1],
      segment: item[2],
      value: item[3],
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

  return <Chart
    height={22 * systemList.length + 60}
    data={dataList}
    scale={cols}
    padding={[40, 20, 20, 250]}
    forceFit
    animate={false}
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
      // color={['value', '#FFCCCC-#20AA73']}
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
          content: systemList[name] + '：' + (day + 1) + '号' + segmentNames[segment] + '：' + stateNames[value],
        }
      }]}
    >
    </Geom>
  </Chart>
}

export default ReportChart