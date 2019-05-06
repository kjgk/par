package com.unicorn.par.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
        prefix = "sms.miaodi"
)
@Getter
@Setter
public class SmsTemplateConfigurationProperties {

    private String template0;
}
