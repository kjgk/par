package com.unicorn.par.domain.vo;

import com.unicorn.core.domain.vo.BasicInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class RegistrationInfo implements Serializable {

    private String objectId;

    private String name;

    private String address;

    private BasicInfo region;

    private BasicInfo area;

    private Double coordinateX;

    private Double coordinateY;

    private Date registrationTime;

}
