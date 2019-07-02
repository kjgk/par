package com.unicorn.par.ins;

import com.unicorn.par.ins.config.InspectionConfigurationProperties;
import com.unicorn.par.ins.task.MainTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class Runner {

    @Bean
    CommandLineRunner initChromeDriverPath(
            InspectionConfigurationProperties inspectionConfigurationProperties
    ) {
        return args -> System.setProperty("webdriver.chrome.driver", inspectionConfigurationProperties.getChromeDriverPath());
    }

//    @Bean
    CommandLineRunner autoInspection(MainTask mainTask) {
        return args -> mainTask.autoInspection();
    }
}