package com.unicorn.par.service;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.util.BytePictureUtils;
import com.unicorn.core.domain.vo.AttachmentInfo;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.exception.ServiceException;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.core.service.EnvironmentService;
import com.unicorn.par.domain.enumeration.MonthlyReportStatus;
import com.unicorn.par.domain.po.MonthlyReport;
import com.unicorn.par.domain.po.MonthlyReportAudit;
import com.unicorn.par.domain.po.QMonthlyReport;
import com.unicorn.par.repository.MonthlyReportAuditRepository;
import com.unicorn.par.repository.MonthlyReportRepository;
import com.unicorn.std.domain.po.Attachment;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.service.ContentAttachmentService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MonthlyReportService {

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @Autowired
    private MonthlyReportAuditRepository monthlyReportAuditRepository;

    @Autowired
    private ContentAttachmentService contentAttachmentService;

    @Autowired
    private AccendantService accendantService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EnvironmentService environmentService;

    private final int[] DATE_RULES = new int[]{1, 2};

    public Page<MonthlyReport> getMonthlyReport(QueryInfo queryInfo) {

        return monthlyReportRepository.findAll(queryInfo).map(monthlyReport -> {
            monthlyReport.setAttachments(contentAttachmentService.getAttachmentInfos(monthlyReport.getObjectId()));
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

        MonthlyReport current;
        if (StringUtils.isEmpty(monthlyReport.getObjectId())) {
            Date currentMonth = getCurrentMonth();
            if (currentMonth == null) {
                throw new ServiceException("现在不允许提交月报！");
            }
            current = monthlyReportRepository.save(monthlyReport);
            current.setMonth(currentMonth);
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
            current.setUserCount(monthlyReport.getUserCount());
            current.setUserLoginCount(monthlyReport.getUserLoginCount());
            current.setKeyWork(monthlyReport.getKeyWork());
            current.setMaintenance(monthlyReport.getMaintenance());
            current.setPerfection(monthlyReport.getPerfection());
            current.setFault(monthlyReport.getFault());
            current.setProblem(monthlyReport.getProblem());
        }

        if (monthlyReport.getDraft()) {
            current.setStatus(MonthlyReportStatus.Draft);
        } else {
            // 判断是否延时
            int workdayOfMonth = holidayService.workdayOfMonth(new Date());
            current.setDelay(workdayOfMonth > DATE_RULES[1] ? 1 : 0);

            current.setSubmitTime(new Date());
            current.setStatus(MonthlyReportStatus.Submit);
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
     * 获取当前月报的月份（为每月最后1个工作日为当月，其他时间为上月）
     */
    @Cacheable(value = "currentMonthReport")
    public Date getCurrentMonth() {

        final int lastDates = DATE_RULES[0]; // 每月最后x天（工作日）提交月报
        Date now = new Date();
        DateTime dateTime = new DateTime(now).withTimeAtStartOfDay();
        int workdays = holidayService.workdaysOfMonth(dateTime.getYear(), dateTime.getMonthOfYear());
        int workdayOfMonth = holidayService.workdayOfMonth(now);
        if (workdays - workdayOfMonth < lastDates) {
            return dateTime.withDayOfMonth(1).toDate();
        } else {
            return dateTime.minusMonths(1).withDayOfMonth(1).toDate();
        }
    }

    @CacheEvict(value = "currentMonthReport")
    public void invalidCurrentMonth() {
    }

    // 月报审核
    public void auditMonthlyReport(MonthlyReportAudit audit) {

        MonthlyReport current = monthlyReportRepository.get(audit.getMonthlyReport().getObjectId());
        current.setStatus(audit.getResult() == 1 ? MonthlyReportStatus.Resolve : MonthlyReportStatus.Reject);
        current.setAuditMessage(audit.getMessage());

        audit.setMonthlyReport(current);
        audit.setAuditTime(new Date());
        monthlyReportAuditRepository.save(audit);
    }

    // 月报导出word
    public XWPFTemplate getMonthlyReportTemplate(Long objectId) {

        MonthlyReport monthlyReport = monthlyReportRepository.get(objectId);

        String templateFileName = this.getClass().getResource("/").getPath() + "templates/MonthlyReport.docx";
        XWPFTemplate template = XWPFTemplate.compile(templateFileName)
                .render(new HashMap() {{
                    put("system", monthlyReport.getSystem().getName());
                    put("daily", monthlyReport.getDaily());
                    put("meeting", monthlyReport.getMeeting());
                    put("doorToDoor", monthlyReport.getDoorToDoor());
                    put("consultation", monthlyReport.getConsultation());
                    put("networkAssistance", monthlyReport.getNetworkAssistance());
                    put("dataAndFunction", monthlyReport.getDataAndFunction());
                    put("train", monthlyReport.getTrain());
                    put("documents", monthlyReport.getDocuments());
                    put("keyWork", monthlyReport.getKeyWork());
                    put("maintenance", monthlyReport.getMaintenance());
                    put("perfection", monthlyReport.getPerfection());
                    put("fault", monthlyReport.getFault());
                    put("problem", monthlyReport.getProblem());

                    // 图片附件
                    List<ContentAttachment> attachmentList = contentAttachmentService.getAttachmentList(objectId);
                    int index = 0;
                    for (ContentAttachment contentAttachment : attachmentList) {
                        String fileType = contentAttachment.getAttachment().getFileType();
                        if ("jpg".equals(fileType) || "png".equals(fileType)) {
                            byte[] localByteArray = BytePictureUtils.getLocalByteArray(new File(environmentService.getUploadPath() + contentAttachment.getAttachment().getFilename()));
                            put("picture" + ++index, new PictureRenderData(480, 320, "." + fileType, localByteArray));
                        }
                    }
                }});
        return template;
    }

    // 月报汇总导出word
    public XWPFTemplate getMonthlyReportSummaryTemplate(List<Long> systemList) {

        Date currentMonth = getCurrentMonth();
        DateTime dateTime = new DateTime(currentMonth);
        List<MonthlyReport> monthlyReportList = monthlyReportRepository.findAll(QMonthlyReport.monthlyReport.system.objectId.in(systemList)
                .and(QMonthlyReport.monthlyReport.month.eq(currentMonth))
                .and(QMonthlyReport.monthlyReport.status.in(MonthlyReportStatus.Resolve, MonthlyReportStatus.Archive))
        );

        List<Map> detailList = new ArrayList();
        String path = this.getClass().getResource("/").getPath();
        Style style = new Style();
        style.setFontFamily("微软雅黑");
        style.setFontSize(12);
        int no = 1;
        int daily = 0, meeting = 0, doorToDoor = 0, consultation = 0, networkAssistance = 0, dataAndFunction = 0, train = 0, documents = 0;
        for (MonthlyReport monthlyReport : monthlyReportList) {
            HashMap data = new HashMap();
            List<String[]> items = new ArrayList() {{
                add(new String[]{"【重点工作】", monthlyReport.getKeyWork()});
                add(new String[]{"【运行维护】", monthlyReport.getMaintenance()});
                add(new String[]{"【功能完善】", monthlyReport.getPerfection()});
                add(new String[]{"【故障及故障分析】", monthlyReport.getFault()});
                add(new String[]{"【存在问题】", monthlyReport.getProblem()});
            }};
            data.put("no", no++);
            data.put("systemName", monthlyReport.getSystem().getName());
            data.put("dataAndFunction", monthlyReport.getDataAndFunction());
            List<HashMap> dataList = items.stream().map(item -> new HashMap() {{
                if (!StringUtils.isEmpty(item[1])) {
                    put("title", item[0]);
                    put("content", item[1]);

                }
            }}).filter(item -> !item.isEmpty()).collect(Collectors.toList());
            if (dataList.size() == 0) {
                dataList.add(new HashMap() {{
                    put("title", "无");
                    put("content", "");
                }});
            }
            data.put("items", new DocxRenderData(new File(path + "templates/MonthlyReportSummaryDetailItem.docx"), dataList));
            detailList.add(data);

            daily += monthlyReport.getDaily() == null ? 0 : monthlyReport.getDaily();
            meeting += monthlyReport.getMeeting() == null ? 0 : monthlyReport.getMeeting();
            doorToDoor += monthlyReport.getDoorToDoor() == null ? 0 : monthlyReport.getDoorToDoor();
            consultation += monthlyReport.getConsultation() == null ? 0 : monthlyReport.getConsultation();
            networkAssistance += monthlyReport.getNetworkAssistance() == null ? 0 : monthlyReport.getNetworkAssistance();
            dataAndFunction += monthlyReport.getDataAndFunction() == null ? 0 : monthlyReport.getDataAndFunction();
            train += monthlyReport.getTrain() == null ? 0 : monthlyReport.getTrain();
            documents += monthlyReport.getDocuments() == null ? 0 : monthlyReport.getDocuments();
        }

        String templateFileName = path + "templates/MonthlyReportSummary.docx";
        Map params = new HashMap();
        params.put("year", dateTime.getYear());
        params.put("month", dateTime.getMonthOfYear());
        params.put("daily", daily);
        params.put("meeting", meeting);
        params.put("doorToDoor", doorToDoor);
        params.put("consultation", consultation);
        params.put("networkAssistance", networkAssistance);
        params.put("dataAndFunction", dataAndFunction);
        params.put("train", train);
        params.put("documents", documents);
        params.put("details", new DocxRenderData(new File(path + "templates/MonthlyReportSummaryDetail.docx"), detailList));
        XWPFTemplate template = XWPFTemplate.compile(templateFileName).render(params);
        return template;
    }

    public List<Map> getMonthlyReportStatus() {

        return jdbcTemplate.queryForList("select a.objectid system_id, a.name, b.status status from sed_system a " +
                " left join sed_monthlyreport b on a.objectid = b.system_id and b.deleted = 0 and b.month = ?" +
                " inner join sed_company c on a.company_id = c.objectid " +
                " where a.deleted = 0 order by c.name, a.objectid", getCurrentMonth()).stream().map(data -> new HashMap() {{
            put("systemId", data.get("system_id"));
            put("systemName", data.get("name"));
            put("status", data.get("status"));
        }}).collect(Collectors.toList());
    }
}
