package com.unicorn.par.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InspectionInfo implements Serializable {

    private Long objectId;

    private String systemName;

    private Long systemId;

    private String accendantName;

    private Long accendantId;

    private Date inspectionTime;

    private List<Detail> detailList;

    @Getter
    @Setter
    public static class Detail implements Serializable {

        private Long objectId;

        private String name;

        private Integer result;

        private List<String> screenshots;
    }
}


