package com.unicorn.par.domain.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyReportAudit {

    private Long monthlyReportId;

    private Integer result;

    private String message;
}
