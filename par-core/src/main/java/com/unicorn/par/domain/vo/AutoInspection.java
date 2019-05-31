package com.unicorn.par.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AutoInspection implements Serializable {

    private Long systemId;

    private List<Detail> detailList;

    @Getter
    @Setter
    public static class Detail implements Serializable {

        /**
         * 0=异常
         * 1=正常
         */
        private Integer result;

        private List<String> screenshots;
    }
}


