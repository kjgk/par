package com.unicorn.par.service;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.MonthlyReport;
import com.unicorn.par.repository.MonthlyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class MonthlyReportService {

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @Autowired
    private AccendantService accendantService;

    public Page<MonthlyReport> getMonthlyReport(QueryInfo queryInfo) {

        return monthlyReportRepository.findAll(queryInfo);
    }

    public List<BasicInfo> getMonthlyReport() {

        return monthlyReportRepository.list();
    }

    public MonthlyReport getMonthlyReport(Long objectId) {

        return monthlyReportRepository.get(objectId);
    }

    public void saveMonthlyReport(MonthlyReport monthlyReport) {

        MonthlyReport current;
        if (StringUtils.isEmpty(monthlyReport.getObjectId())) {
            current = monthlyReportRepository.save(monthlyReport);
        } else {
            current = monthlyReportRepository.getOne(monthlyReport.getObjectId());
            current.setMeeting(monthlyReport.getMeeting());
            current.setDaily(monthlyReport.getDaily());
            current.setConsultation(monthlyReport.getConsultation());
            current.setDoorToDoor(monthlyReport.getDoorToDoor());
            current.setNetworkAssistance(monthlyReport.getNetworkAssistance());
            current.setDataAndFunction(monthlyReport.getDataAndFunction());
            current.setTrain(monthlyReport.getTrain());
            current.setDocuments(monthlyReport.getDocuments());
            current.setKeyWork(monthlyReport.getKeyWork());
            current.setMaintenance(monthlyReport.getMaintenance());
            current.setPerfection(monthlyReport.getPerfection());
            current.setFault(monthlyReport.getFault());
            current.setProblem(monthlyReport.getProblem());
        }

        current.setSubmitTime(new Date());
        current.setAccendant(accendantService.getCurrentAccendant());
    }

    public void deleteMonthlyReport(Long objectId) {

        monthlyReportRepository.deleteById(objectId);
    }

    public void deleteMonthlyReport(List<Long> objectIds) {

        objectIds.forEach(this::deleteMonthlyReport);
    }
}
