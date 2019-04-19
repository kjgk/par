package com.unicorn.par.service;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
    private String _onlineDate;

    private Date onlineDate;

    public String getPublicUrl() {
        return publicUrl;
    }

    public Boolean getRequiresSecure() {
        return requiresSecure;
    }

    public Date getOnlineDate() {

        if (onlineDate == null) {
            DateTime dateTime = new DateTime()
                    .withTimeAtStartOfDay()
                    .withDayOfMonth(1)
                    .withMonthOfYear(Integer.valueOf(_onlineDate.substring(4)))
                    .withYear(Integer.valueOf(_onlineDate.substring(0, 4)));
            onlineDate = dateTime.toDate();
        }
        return onlineDate;
    }
}
