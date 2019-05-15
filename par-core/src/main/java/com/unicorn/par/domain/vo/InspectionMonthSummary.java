package com.unicorn.par.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class InspectionMonthSummary implements Serializable {

    // ['1号', '2号', ..., '31号']
    private List<String> dateList = new ArrayList();

    // ['系统-1', '系统-2', ...]
    private List<String> systemList = new ArrayList();

    // [
    //  [0, 0, 1, 10] -> [日期索引，系统索引，1=上午|3=下午，值 -> （参考：InspectionResult.java）
    //  ]
    // ]
    private List<Integer[]> values = new ArrayList();

}


