package com.unicorn.par.ins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class Runner {

    @Value("${chrome-driver-path}")
    private String chromeDriverPath;

    @Bean
    CommandLineRunner initChromeDriverPath() {
        return args -> System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    }
}
