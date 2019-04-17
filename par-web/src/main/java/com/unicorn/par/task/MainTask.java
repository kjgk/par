package com.unicorn.par.task;

import com.unicorn.par.service.MonthlyReportService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MainTask {

    private MonthlyReportService monthlyReportService;


    @Scheduled(cron = "0 0 0/1 * * ?")
    public void invalidCurrentMonthReport() {

        monthlyReportService.invalidCurrentMonth();
    }
}
