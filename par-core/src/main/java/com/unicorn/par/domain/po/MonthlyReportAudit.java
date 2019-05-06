package com.unicorn.par.domain.po;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicorn.core.domain.DefaultIdentifiable;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "sed_monthlyreportaudit")
@EntityListeners({AuditingEntityListener.class})
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthlyReportAudit extends DefaultIdentifiable {

    @OneToOne
    @JoinColumn(name = "monthly_report_id")
    private MonthlyReport monthlyReport;

    private Integer result;

    @Column(columnDefinition = "text")
    private String message;

    private Date auditTime;
}
