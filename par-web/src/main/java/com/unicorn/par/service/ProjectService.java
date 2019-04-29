package com.unicorn.par.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class ProjectService {

    @Value("${project.public_url}")
    private String publicUrl;

    @Value("${project.requires_secure}")
    private Boolean requiresSecure;

    @Value("${project.online_date}")
    private Date onlineDate;

    public String getPublicUrl() {
        return publicUrl;
    }

    public Boolean getRequiresSecure() {
        return requiresSecure;
    }

    public Date getOnlineDate() {

        return onlineDate;
    }
}
