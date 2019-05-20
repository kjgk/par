package com.unicorn.par.domain.po;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicorn.core.domain.DefaultPersistent;
import com.unicorn.core.domain.vo.FileUploadInfo;
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
@Table(name = "sed_tickethandle")
@EntityListeners({AuditingEntityListener.class})
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketHandle extends DefaultPersistent {

    @OneToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @OneToOne
    @JoinColumn(name = "accendant_id")
    private Accendant accendant;

    private Date acceptTime;

    private Date finishTime;

    // 1=已解决
    // 2=未解决
    private Integer result;

    private String remark;

    @Transient
    List<FileUploadInfo> attachments;
}