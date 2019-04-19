package com.unicorn.par.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class InspectionMonthResult implements Serializable {

    private Date onlineDate;

    private Map<Long, Boolean> dateInfo = new HashMap();

    private Map<String, Detail> detailList = new HashMap();

    /**
     * 今天上午的巡检状态
     * null=未到时间
     * 0=已到时间，待巡检
     * 1=已巡检
     * 2=超过时间，未巡检
     */
    private Integer segmentResult1;

    private Integer segmentResult2;

    private Date now = new Date();

    @Getter
    @Setter
    public static class Detail implements Serializable {

        private Long inspectionId;

        private Date date;

        private Integer segment;

        private Integer result;

        @Override
        public String toString() {
            return date.getTime() + "-" + segment;
        }
    }
}


