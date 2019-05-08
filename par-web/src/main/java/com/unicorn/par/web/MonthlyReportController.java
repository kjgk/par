package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.base.web.BaseController;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.MonthlyReport;
import com.unicorn.par.domain.po.QMonthlyReport;
import com.unicorn.par.domain.po.MonthlyReportAudit;
import com.unicorn.par.domain.po.Supervisor;
import com.unicorn.par.service.MonthlyReportService;
import com.unicorn.par.service.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/monthlyReport")
public class MonthlyReportController extends BaseController {

    @Autowired
    private MonthlyReportService monthlyReportService;

    @Autowired
    private SupervisorService supervisorService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<MonthlyReport> list(PageInfo pageInfo, Long systemId) {

        QMonthlyReport monthlyReport = QMonthlyReport.monthlyReport;

        BooleanExpression expression = monthlyReport.isNotNull();
        if (systemId != null) {
            expression = expression.and(monthlyReport.system.objectId.eq(systemId));
        } else {
            expression = expression.and(monthlyReport.isNull());
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.DESC, "month").and(new Sort(Sort.Direction.ASC, "system.objectId"))
        );
        return monthlyReportService.getMonthlyReport(queryInfo);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createMonthlyReport(@RequestBody MonthlyReport monthlyReport) {

        monthlyReportService.saveMonthlyReport(monthlyReport);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateMonthlyReport(@PathVariable("objectId") Long objectId, @RequestBody MonthlyReport monthlyReport) {

        monthlyReport.setObjectId(objectId);
        monthlyReportService.saveMonthlyReport(monthlyReport);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteMonthlyReport(@PathVariable("objectId") Long objectId) {

        monthlyReportService.deleteMonthlyReport(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        monthlyReportService.deleteMonthlyReport(objectIds);
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Map getCurrentMonthlyReport(Long systemId) {

        Map result = new HashMap();
        Date currentMonth = monthlyReportService.getCurrentMonth();
        if (currentMonth != null) {
            MonthlyReport currentMonthlyReport = monthlyReportService.getMonthlyReport(systemId, currentMonth);
            if (currentMonthlyReport != null) {
                result.put("currentMonthlyReport", currentMonthlyReport.getObjectId());
            }
        }
        result.put("currentMonth", currentMonth);
        return result;
    }

    // 月报管理列表
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Page<MonthlyReport> all(PageInfo pageInfo, Long systemId, Date month, Integer status) {

        QMonthlyReport monthlyReport = QMonthlyReport.monthlyReport;

        BooleanExpression expression = monthlyReport.isNotNull();
        if (systemId != null) {
            expression = expression.and(monthlyReport.system.objectId.eq(systemId));
        } else {
            Supervisor currentSupervisor = supervisorService.getCurrentSupervisor();
            if (currentSupervisor != null) {
                expression = expression.and(monthlyReport.system.supervisor.objectId.eq(currentSupervisor.getObjectId()));
            }
        }
        if (month != null) {
            expression = expression.and(monthlyReport.month.eq(month));
        }
        if (status != null) {
            expression = expression.and(monthlyReport.status.eq(status));
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.DESC, "month").and(new Sort(Sort.Direction.ASC, "system.objectId"))
        );
        return monthlyReportService.getMonthlyReport(queryInfo);
    }

    // 月报审核
    @Secured(value = {"ROLE_REPORT_AUDIT"})
    @RequestMapping(value = "/{objectId}/audit", method = RequestMethod.POST)
    public void auditMonthlyReport(@PathVariable("objectId") Long objectId, @RequestBody MonthlyReportAudit audit) {

        audit.setMonthlyReport(new MonthlyReport());
        audit.getMonthlyReport().setObjectId(objectId);
        monthlyReportService.auditMonthlyReport(audit);
    }
}
