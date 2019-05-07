package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Supervisor;
import com.unicorn.par.domain.po.QSupervisor;
import com.unicorn.par.service.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/supervisor")
public class SupervisorController {

    @Autowired
    private SupervisorService supervisorService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<Supervisor> list(PageInfo pageInfo, String keyword) {

        QSupervisor supervisor = QSupervisor.supervisor;

        BooleanExpression expression = supervisor.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(supervisor.user.name.containsIgnoreCase(s));
            }
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.ASC, "objectId")
        );
        return supervisorService.getSupervisor(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BasicInfo> list() {

        return supervisorService.getSupervisor();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createSupervisor(@RequestBody Supervisor supervisor) {

        supervisorService.saveSupervisor(supervisor);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateSupervisor(@PathVariable("objectId") Long objectId, @RequestBody Supervisor supervisor) {

        supervisor.setObjectId(objectId);
        supervisorService.saveSupervisor(supervisor);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteSupervisor(@PathVariable("objectId") Long objectId) {

        supervisorService.deleteSupervisor(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        supervisorService.deleteSupervisor(objectIds);
    }
}
