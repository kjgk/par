package com.unicorn.par.service;

import com.unicorn.core.domain.vo.AttachmentInfo;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.exception.ServiceException;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.MonthlyReport;
import com.unicorn.par.domain.po.QMonthlyReport;
import com.unicorn.par.repository.MonthlyReportRepository;
import com.unicorn.std.domain.po.Attachment;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.service.ContentAttachmentService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class MonthlyReportService {

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @Autowired
    private ContentAttachmentService contentAttachmentService;

    @Autowired
    private AccendantService accendantService;

    @Autowired
    private HolidayService holidayService;

    public Page<MonthlyReport> getMonthlyReport(QueryInfo queryInfo) {

        return monthlyReportRepository.findAll(queryInfo).map(monthlyReport -> {

            // 加载附件
            monthlyReport.setAttachments(new ArrayList());
            List<ContentAttachment> attachmentList = contentAttachmentService.getAttachmentList(monthlyReport.getObjectId());
            for (ContentAttachment contentAttachment : attachmentList) {
                AttachmentInfo attachmentInfo = new AttachmentInfo();
                attachmentInfo.setFilename(contentAttachment.getAttachment().getOriginalFilename());
                attachmentInfo.setAttachmentId(contentAttachment.getAttachment().getObjectId());
                monthlyReport.getAttachments().add(attachmentInfo);
            }
            return monthlyReport;
        });
    }

    public List<BasicInfo> getMonthlyReport() {

        return monthlyReportRepository.list();
    }

    public MonthlyReport getMonthlyReport(Long objectId) {

        return monthlyReportRepository.get(objectId);
    }

    public MonthlyReport getMonthlyReport(Long systemId, Date month) {

        QMonthlyReport monthlyReport = QMonthlyReport.monthlyReport;
        return monthlyReportRepository.findOne(monthlyReport.month.eq(month).and(monthlyReport.system.objectId.eq(systemId))).orElse(null);
    }

    public void saveMonthlyReport(MonthlyReport monthlyReport) {

        Date currentMonth = getCurrentMonth();
        if (currentMonth == null) {
            throw new ServiceException("请在每月最后3个工作日提交月报！");
        }
        MonthlyReport current;
        if (StringUtils.isEmpty(monthlyReport.getObjectId())) {
            current = monthlyReportRepository.save(monthlyReport);
            current.setMonth(currentMonth);
            current.setSubmitTime(new Date());
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

        List<ContentAttachment> contentAttachments = new ArrayList();
        if (!CollectionUtils.isEmpty(monthlyReport.getAttachments())) {
            for (AttachmentInfo attachmentInfo : monthlyReport.getAttachments()) {
                ContentAttachment contentAttachment = new ContentAttachment();
                if (attachmentInfo.getAttachmentId() != null) {
                    contentAttachment.setAttachment(new Attachment());
                    contentAttachment.getAttachment().setObjectId(attachmentInfo.getAttachmentId());
                } else {
                    contentAttachment.setFileInfo(FileUploadInfo.valueOf(attachmentInfo.getTempFilename(), attachmentInfo.getFilename()));
                }
                contentAttachment.setRelatedType(MonthlyReport.class.getSimpleName());
                contentAttachment.setRelatedId(current.getObjectId());
                contentAttachments.add(contentAttachment);
            }
        }
        contentAttachmentService.save(MonthlyReport.class.getSimpleName(), current.getObjectId(), null, contentAttachments);

        current.setAccendant(accendantService.getCurrentAccendant());
    }

    public void deleteMonthlyReport(Long objectId) {

        monthlyReportRepository.deleteById(objectId);
    }

    public void deleteMonthlyReport(List<Long> objectIds) {

        objectIds.forEach(this::deleteMonthlyReport);
    }

    /**
     * 获取当前月报的月份（为每月最后3个工作日至下月前2个工作日，其它时间返回空）
     */
    @Cacheable(value = "currentMonthReport")
    public Date getCurrentMonth() {

        final int[] rules = new int[]{3, 2};
        Date now = new Date();
        DateTime dateTime = new DateTime(now).withTimeAtStartOfDay();
        int workdayOfMonth = holidayService.workdayOfMonth(now);
        if (dateTime.getDayOfMonth() < 15) {
            // 每月的前2个工作日允许填写上个月月报
            if (workdayOfMonth < rules[1] || (workdayOfMonth == rules[1] && holidayService.isWorkday(now))) {
                return dateTime.minusMonths(1).withDayOfMonth(1).toDate();
            }
        } else {
            int workdays = holidayService.workdaysOfMonth(dateTime.getYear(), dateTime.getMonthOfYear());
            // 每月的最后3个工作日允许填写本月月报
            if (workdays - workdayOfMonth < rules[0]) {
                return dateTime.withDayOfMonth(1).toDate();
            }
        }
        return null;
    }

    @CacheEvict(value = "currentMonthReport")
    public void invalidCurrentMonth() {
    }
}
