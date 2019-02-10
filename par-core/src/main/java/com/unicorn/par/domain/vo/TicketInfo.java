package com.unicorn.par.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TicketInfo implements Serializable {

    private Long objectId;

    private Integer priority;

    private String content;

    private String systemName;

    private Long systemId;

    private String contacts;

    private String phoneNo;

    private String submitter;

    private Date submitTime;

    private Integer status;

    private List<String> attachments;
}
