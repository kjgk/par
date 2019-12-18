package com.unicorn;

import com.unicorn.core.service.EnvironmentService;
import com.unicorn.par.service.HolidayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;


@Configuration
@Slf4j
public class Runner {

    @Bean
    CommandLineRunner initDirectory(
            EnvironmentService environmentService
    ) {
        return args -> {

            try {
                if (!StringUtils.isEmpty(environmentService.getTempPath())) {
                    FileUtils.forceMkdir(new File(environmentService.getTempPath()));
                }
                if (!StringUtils.isEmpty(environmentService.getUploadPath())) {
                    FileUtils.forceMkdir(new File(environmentService.getUploadPath()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

//        @Bean
    CommandLineRunner initHoliday(
            HolidayService holidayService
    ) {
        return args -> {
            holidayService.initHoliday(2020);
        };
    }
}
