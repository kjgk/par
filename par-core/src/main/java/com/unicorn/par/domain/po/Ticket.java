package com.unicorn.par.domain.po;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicorn.core.domain.DefaultPersistent;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.domain.po.User;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sed_ticket")
@EntityListeners({AuditingEntityListener.class})
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket extends DefaultPersistent {

    // 优先级
    // 1：重要，2：一般
    private Integer priority;

    // 问题描述
    private String content;

    @OneToOne
    @JoinColumn(name = "system_id")
    private System system;

    // 报修人
    private String contacts;

    // 报修人手机号
    private String phoneNo;

    @OneToOne
    @JoinColumn(name = "submitter")
    private User submitter;

    private Date submitTime;

    private Integer status;

    // 来源
    // 1：线上流转，2：线下录入，巡检记录
    private Integer source;

    @Transient
    List<FileUploadInfo> attachments;
}