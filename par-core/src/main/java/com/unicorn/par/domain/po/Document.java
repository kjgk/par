package com.unicorn.par.domain.po;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicorn.core.domain.DefaultNomenclator;
import com.unicorn.core.domain.po.Code;
import com.unicorn.core.domain.po.User;
import com.unicorn.core.domain.vo.AttachmentInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "sed_document")
@EntityListeners({AuditingEntityListener.class})
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document extends DefaultNomenclator {

    @OneToOne
    @JoinColumn(name = "category_id")
    private Code category;

    @OneToOne
    @JoinColumn(name = "system_id")
    private System system;

    @OneToOne
    @JoinColumn(name = "uploader")
    private User uploader;

    @Transient
    AttachmentInfo attachment;
}
