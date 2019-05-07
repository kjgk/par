package com.unicorn.par.task;

import com.unicorn.par.service.AchievementService;
import com.unicorn.par.service.HolidayService;
import com.unicorn.par.service.MonthlyReportService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class MainTask {

    private MonthlyReportService monthlyReportService;

    private HolidayService holidayService;

    private AchievementService achievementService;

    // 清空当前月报月份
    // 每小时执行
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void invalidCurrentMonthReport() {

        monthlyReportService.invalidCurrentMonth();
    }

    // 计算绩效得分
    // 每日凌晨4点执行
//    @Scheduled(cron = "0 0 4 1/1 * ?")
//    @Scheduled(fixedDelay = 1000)
    public void generateAchievement() {

        int workdayOfMonth = holidayService.workdayOfMonth(new Date());
        if (workdayOfMonth == 3) {
            achievementService.generateAchievement();
        }
    }
}
