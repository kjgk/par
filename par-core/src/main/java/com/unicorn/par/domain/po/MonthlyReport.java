package com.unicorn.par.domain.po;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicorn.core.domain.DefaultPersistent;
import com.unicorn.core.domain.vo.AttachmentInfo;
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
@Table(name = "sed_monthlyreport")
@EntityListeners({AuditingEntityListener.class})
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthlyReport extends DefaultPersistent {

    @OneToOne
    @JoinColumn(name = "system_id")
    private System system;

    @OneToOne
    @JoinColumn(name = "accendant_id")
    private Accendant accendant;

    private Date month;

    private Date submitTime;

    private Integer daily;

    private Integer meeting;

    private Integer doorToDoor;

    private Integer consultation;

    private Integer networkAssistance;

    private Integer dataAndFunction;

    private Integer train;

    private Integer documents;

    // 本月系统用户数
    private Integer userCount;

    // 本月系统用户登录次数
    private Integer userLoginCount;

    @Column(columnDefinition = "text")
    private String keyWork;

    @Column(columnDefinition = "text")
    private String maintenance;

    @Column(columnDefinition = "text")
    private String perfection;

    @Column(columnDefinition = "text")
    private String fault;

    @Column(columnDefinition = "text")
    private String problem;

    private Integer status;

    private String auditMessage;

    // 是否延时上传
    private Integer delay;

    @Transient
    List<AttachmentInfo> attachments;

    @Transient
    private Boolean draft;
}
