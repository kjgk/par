package com.unicorn.par.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unicorn.core.exception.ServiceException;
import com.unicorn.par.domain.po.Holiday;
import com.unicorn.par.domain.po.QHoliday;
import com.unicorn.par.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Transactional
@Slf4j
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * 获取api接口获取节假日信息
     */
    public void initHoliday(Integer year) {

        DateTime dateTime = new DateTime().withYear(year).withDayOfYear(1).withTimeAtStartOfDay();
        while (dateTime.getYear() == year) {
            Integer value = checkDate(dateTime.toDate());
            Holiday holiday = new Holiday();
            holiday.setDate(dateTime.toDate());
            holiday.setValue(value);
            holidayRepository.save(holiday);
            dateTime = dateTime.plusDays(1);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重新获取失败的数据
     */
    public void initHoliday2() {

        holidayRepository.findAll(QHoliday.holiday.value.isNull())
                .forEach(holiday -> {
                    holiday.setValue(checkDate(holiday.getDate()));
                });
    }


    public Integer checkDate(Date date) {

        log.info("开始获取{}", date);
        OkHttpClient okHttpClient = new OkHttpClient();
        String dateString = new SimpleDateFormat("yyyyMMdd").format(date);
        Request request = new Request.Builder()
                .url("http://api.goseek.cn/Tools/holiday?date=" + dateString)
                .get()
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            JSONObject result = JSON.parseObject(response.body().string());
            return result.getInteger("data");
        } catch (Exception e) {
            String message = "获取节假日信息失败：" + e.getMessage();
            log.error(message);
        }
        return null;
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

    public boolean isWorkday(Date day) {

        Holiday holiday = holidayRepository.findOne(QHoliday.holiday.date.eq(new DateTime(day).withTimeAtStartOfDay().toDate())).orElse(null);
        if (holiday == null) {
            throw new ServiceException("date " + day + " not found in configs");
        }
        return holiday.getValue() == 0 || holiday.getValue() == 2;
    }
}
