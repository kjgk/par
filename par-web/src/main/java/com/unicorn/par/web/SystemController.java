package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.QSystem;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.domain.vo.SystemInfo;
import com.unicorn.par.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/system")
public class SystemController {

    @Autowired
    private SystemService systemService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<System> list(PageInfo pageInfo, String keyword, Long company) {

        QSystem system = QSystem.system;

        BooleanExpression expression = system.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(system.name.containsIgnoreCase(s)
                        .or(system.url.containsIgnoreCase(s))
                        .or(system.company.name.containsIgnoreCase(s))
                );
            }
        }
        if (company != null) {
            expression = expression.and(system.company.objectId.eq(company));
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.ASC, "objectId")
        );
        return systemService.getSystem(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<SystemInfo> list(Integer self) {

        return systemService.getSystemList(self);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createSystem(@RequestBody System system) {

        systemService.saveSystem(system);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateSystem(@PathVariable("objectId") Long objectId, @RequestBody System system) {

        system.setObjectId(objectId);
        systemService.saveSystem(system);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteSystem(@PathVariable("objectId") Long objectId) {

        systemService.deleteSystem(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        systemService.deleteSystem(objectIds);
    }
}
