package com.unicorn.par.domain.vo;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.par.domain.po.Function;
import com.unicorn.par.domain.po.System;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class SystemInfo extends BasicInfo {

    private String url;

    private List<BasicInfo> functionList;

    public SystemInfo(Long objectId, String name, String url) {
        super(objectId, name);
        this.url = url;
    }

    public SystemInfo(Long objectId, String name, String url, List<Function> functionList) {
        super(objectId, name);
        this.url = url;

        if (!CollectionUtils.isEmpty(functionList)) {
            this.functionList = BasicInfo.valueOf(functionList);
        }
    }

    public static SystemInfo valueOf(System system) {
        return new SystemInfo(system.getObjectId(), system.getName(), system.getUrl());
    }

    public static SystemInfo valueOf(System system, List<Function> functionList) {
        return new SystemInfo(system.getObjectId(), system.getName(), system.getUrl(), functionList);
    }
}
