package com.unicorn.par.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unicorn.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class ProjectService {

    @Value("${project.public_url}")
    private String publicUrl;

    @Value("${project.requires_secure}")
    private Boolean requiresSecure;

    public String getPublicUrl() {
        return publicUrl;
    }

    public Boolean getRequiresSecure() {
        return requiresSecure;
    }


    public boolean isHoliday(Date date) {

        OkHttpClient okHttpClient = new OkHttpClient();
        String dateString = new SimpleDateFormat("yyyyMMdd").format(date);
        Request request = new Request.Builder()
                .url("http://api.goseek.cn/Tools/holiday?date=" + dateString)
                .get()
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            JSONObject result = JSON.parseObject(response.body().string());
            Integer data = result.getInteger("data");
            return data == 1 || data == 3;
        } catch (Exception e) {
            String message = "获取节假日信息失败：" + e.getMessage();
            log.error(message);
            throw new ServiceException(message);
        }
    }
}
