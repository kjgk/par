package com.unicorn.par.ins.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(
        prefix = "inspection-config"
)
@Getter
@Setter
public class InspectionConfigurationProperties {

    private String chromeDriverPath;

    private String chromeBinaryPath;

    private String postUrl;

    private Map<String, SystemConfig> systemConfig;


    @Getter
    @Setter
    public static class SystemConfig {

        private Long systemId;

        private String url;
    }
}
