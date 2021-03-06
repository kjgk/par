package com.unicorn.par.domain.po;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicorn.core.domain.DefaultNomenclator;
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
@Table(name = "sed_inspection")
@EntityListeners({AuditingEntityListener.class})
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Inspection extends DefaultNomenclator {

    @OneToOne
    @JoinColumn(name = "system_id")
    private System system;

    @OneToOne
    @JoinColumn(name = "accendant_id")
    private Accendant accendant;

    @OneToOne
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    @OneToMany(mappedBy = "inspection")
    @OrderBy(value = "function_id")
    private List<InspectionDetail> detailList;

    private Date inspectionTime;

    /**
     * 巡检时间段
     */
    private Integer segment;

    // 是否延时上传
    private Integer delay;

    private Integer auto;

    // 异常描述
    @Column(columnDefinition = "text")
    private String message;

    private Integer externalCauses;
}
