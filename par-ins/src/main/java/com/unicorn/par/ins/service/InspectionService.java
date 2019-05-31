package com.unicorn.par.ins.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unicorn.par.ins.model.AutoInspection;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InspectionService {

    @Value("${auto-inspection-url}")
    private String autoInspectionUrl;

    public void postAutoInspection(AutoInspection autoInspection) throws Exception {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(autoInspection));
        Request request = new Request.Builder()
                .url(autoInspectionUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        JSONObject result = JSON.parseObject(response.body().string());
        if (!result.getBoolean("success")) {
            throw new Exception(result.getString("message"));
        }
    }
}
