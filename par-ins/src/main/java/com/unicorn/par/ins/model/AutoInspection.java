package com.unicorn.par.ins.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AutoInspection implements Serializable {

    private Long systemId;

    private List<Detail> detailList = new ArrayList();

    @Getter
    @Setter
    public static class Detail implements Serializable {

        /**
         * 0=异常
         * 1=正常
         */
        private Integer result = 1;

        private List<String> screenshots = new ArrayList();
    }
}


