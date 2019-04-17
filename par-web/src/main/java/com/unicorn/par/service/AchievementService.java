package com.unicorn.par.service;

import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.domain.po.*;
import com.unicorn.par.repository.AchievementRepository;
import com.unicorn.par.repository.InspectionRepository;
import com.unicorn.par.repository.MonthlyReportRepository;
import com.unicorn.par.repository.SystemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AchievementService {

    private AchievementRepository achievementRepository;

    private SystemRepository systemRepository;

    private HolidayService holidayService;

    private MonthlyReportRepository monthlyReportRepository;

    private InspectionRepository inspectionRepository;

    public Page<Achievement> getAchievement(QueryInfo queryInfo) {

        return achievementRepository.findAll(queryInfo);
    }

    public void generateAchievement() {

        log.info("开始计算绩效");
        DateTime month = new DateTime().minusMonths(1).withDayOfMonth(1).withTimeAtStartOfDay();
        List<System> systemList = systemRepository.findAll();
        for (System system : systemList) {
            Achievement achievement = new Achievement();
            achievement.setMonth(month.toDate());
            achievement.setSystem(system);

            // 计算巡检得分
            long count = inspectionRepository.count(
                    QInspection.inspection.system.objectId.eq(system.getObjectId())
                            .and(QInspection.inspection.inspectionTime.gt(month.toDate()))
                            .and(QInspection.inspection.inspectionTime.lt(month.plusMonths(1).toDate()))
            );
            achievement.setInspectionScore(
                    Double.valueOf(
                            Math.sqrt(1.0 * count / holidayService.workdaysOfMonth(month.getYear(), month.getMonthOfYear()))
                                    * 50
                    ).intValue()
            );

            // 计算月报得分
            MonthlyReport monthlyReport = monthlyReportRepository.findOne(
                    QMonthlyReport.monthlyReport.system.objectId.eq(system.getObjectId())
                            .and(QMonthlyReport.monthlyReport.month.eq(month.toDate()))
            ).orElse(null);
            if (monthlyReport == null) {
                achievement.setMonthReportScore(0);
            } else {
                if (new DateTime(monthlyReport.getSubmitTime()).withDayOfMonth(1).withTimeAtStartOfDay().getMillis()
                        == month.getMillis()) {
                    achievement.setMonthReportScore(50);
                } else {
                    achievement.setMonthReportScore(40);
                }
            }

            achievement.setScore(achievement.getInspectionScore() + achievement.getMonthReportScore());
            achievementRepository.save(achievement);

            log.info("【{}】得分={}", system.getName(), achievement.getScore());
        }
        log.info("绩效计算完成");
    }
}
