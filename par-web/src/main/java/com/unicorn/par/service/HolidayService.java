package com.unicorn.par.service;

import com.unicorn.core.exception.ServiceException;
import com.unicorn.par.domain.po.Holiday;
import com.unicorn.par.domain.po.QHoliday;
import com.unicorn.par.repository.HolidayRepository;
import com.unicorn.utils.DateUtils;
import com.unicorn.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;

@Service
@Transactional
@Slf4j
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * 初始化节假日信息
     */
    public void initHoliday(Integer year) {

        // 2020年放假安排
        HashMap dateConfigs = new HashMap() {{
            // 元旦
            put("2020-01-01", 1);
            // 春节
            put("2020-01-19", 2);
            put("2020-01-24", 1);
            put("2020-01-25", 1);
            put("2020-01-26", 1);
            put("2020-01-27", 1);
            put("2020-01-28", 1);
            put("2020-01-29", 1);
            put("2020-01-30", 1);
            put("2020-02-01", 2);
            // 清明节
            put("2020-04-04", 1);
            put("2020-04-05", 1);
            put("2020-04-06", 1);
            // 劳动节
            put("2020-04-26", 2);
            put("2020-05-01", 1);
            put("2020-05-02", 1);
            put("2020-05-03", 1);
            put("2020-05-04", 1);
            put("2020-05-05", 1);
            put("2020-05-09", 2);
            // 端午节
            put("2020-06-25", 1);
            put("2020-06-26", 1);
            put("2020-06-27", 1);
            put("2020-06-28", 2);
            // 中秋节/国庆节
            put("2020-09-27", 2);
            put("2020-10-01", 1);
            put("2020-10-02", 1);
            put("2020-10-03", 1);
            put("2020-10-04", 1);
            put("2020-10-05", 1);
            put("2020-10-06", 1);
            put("2020-10-07", 1);
            put("2020-10-08", 1);
            put("2020-10-10", 2);
        }};

        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

        DateTime dateTime = new DateTime().withYear(year).withDayOfYear(1).withTimeAtStartOfDay();
        while (dateTime.getYear() == year) {

//            log.info("{} => 周{}", dateTime.toDate(), dateTime.dayOfWeek().get());

            String date = DateUtils.format(dateTime.toDate(), DateUtils.FORMAT_SHORT);
            int dayOfWeek = dateTime.dayOfWeek().get();
            // 0=工作日，1=法定节假日（五一、十一、春节等），2=节假日调休（上班），3=休息日（周末）
            int value = 0;
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                value = 3;
            }
            value = (int) dateConfigs.getOrDefault(date, value);
            System.out.println("insert into sed_holiday values (" + idWorker.nextId() + ", '" + date + "', " + value + ");");

            dateTime = dateTime.plusDays(1);

        }
    }

    /**
     * 获取指定的日期为每月的第几个工作日
     */
    public int workdayOfMonth(Date day) {

        QHoliday holiday = QHoliday.holiday;
        DateTime dateTime = new DateTime(day).withTimeAtStartOfDay();
        DateTime beginDate = dateTime.withDayOfMonth(1);
        return Long.valueOf(holidayRepository.count(
                holiday.date.goe(beginDate.toDate())
                        .and(holiday.date.loe(dateTime.toDate()))
                        .and(holiday.value.in(0, 2))
        )).intValue();
    }

    /**
     * 获取指定的月份有多少个工作日
     */
    public int workdaysOfMonth(int year, int month) {

        QHoliday holiday = QHoliday.holiday;
        DateTime beginDate = new DateTime().withTimeAtStartOfDay().withDayOfMonth(1).withMonthOfYear(month).withYear(year);
        DateTime endDate = beginDate.plusMonths(1);
        return Long.valueOf(holidayRepository.count(
                holiday.date.goe(beginDate.toDate())
                        .and(holiday.date.lt(endDate.toDate()))
                        .and(holiday.value.in(0, 2))
        )).intValue();
    }

    @Cacheable(value = "workday")
    public boolean isWorkday(Date day) {

        Holiday holiday = holidayRepository.findOne(QHoliday.holiday.date.eq(new DateTime(day).withTimeAtStartOfDay().toDate())).orElse(null);
        if (holiday == null) {
            throw new ServiceException("date " + day + " not found in configs");
        }
        return holiday.getValue() == 0 || holiday.getValue() == 2;
    }
}
