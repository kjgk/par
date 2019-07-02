package com.unicorn.par.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InspectionMonthSummary implements Serializable {

    private List<Detail> detailList = new ArrayList();

    private Integer total;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Detail implements Serializable {

        private String systemName;

        private Integer good;

        private Integer bad;

        private Integer goodAndDelay;

        private Integer badAndDelay;

        private Integer no;

    }
}


