package com.unicorn.par.domain.po;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unicorn.core.domain.DefaultIdentifiable;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sed_inspectiondetail")
@EntityListeners({AuditingEntityListener.class})
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InspectionDetail extends DefaultIdentifiable {

    @ManyToOne
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    @OneToOne
    @JoinColumn(name = "function_id")
    private Function function;

    private Integer result;

    @Transient
    List<FileUploadInfo> screenshots;
}
