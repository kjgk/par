package com.unicorn.par.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectService {

    @Value("${project.public_url}")
    private String publicUrl;

    @Value("${project.requires_secure}")
    private Boolean requiresSecure;

    public String getPublicUrl() {
        return publicUrl;
    }

    public Boolean getRequiresSecure() {
        return requiresSecure;
    }
}
