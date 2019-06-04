package com.unicorn.par.ins.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unicorn.par.ins.config.InspectionConfigurationProperties;
import com.unicorn.par.ins.model.AutoInspection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class InspectionService {

    private InspectionConfigurationProperties inspectionConfigurationProperties;

    public void postAutoInspection(AutoInspection autoInspection) throws Exception {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(autoInspection));
        Request request = new Request.Builder()
                .url(inspectionConfigurationProperties.getPostUrl())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        JSONObject result = JSON.parseObject(response.body().string());
        if (result == null) {
            throw new Exception("服务器未返回任何信息！");
        }
        if (!result.getBoolean("success")) {
            throw new Exception(result.getString("message"));
        }
    }
}
