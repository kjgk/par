package com.unicorn.par.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.exception.ServiceException;
import com.unicorn.core.service.EnvironmentService;
import com.unicorn.par.domain.enumeration.InspectionResult;
import com.unicorn.par.domain.enumeration.TicketSource;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.domain.po.*;
import com.unicorn.par.domain.vo.*;
import com.unicorn.par.repository.FunctionRepository;
import com.unicorn.par.repository.InspectionDetailRepository;
import com.unicorn.par.repository.InspectionRepository;
import com.unicorn.par.repository.SystemRepository;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.service.ContentAttachmentService;
import com.unicorn.utils.DateUtils;
import com.unicorn.utils.SnowflakeIdWorker;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
@Transactional
public class InspectionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionDetailRepository inspectionDetailRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ContentAttachmentService contentAttachmentService;

    @Autowired
    private AccendantService accendantService;

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private SnowflakeIdWorker idWorker;

    public InspectionMonthResult getInspectionMonthResult(String month, Long systemId) {

        InspectionMonthResult result = new InspectionMonthResult();
        result.setOnlineDate(projectService.getOnlineDate());
        QInspection inspection = QInspection.inspection;
        BooleanExpression expression = inspection.isNotNull();

        // 获取当月和上月的数据
        DateTime dateTime = new DateTime()
                .withTimeAtStartOfDay()
                .withDayOfMonth(1)
                .withMonthOfYear(Integer.valueOf(month.substring(4)))
                .withYear(Integer.valueOf(month.substring(0, 4)))
                .minusWeeks(1);
        DateTime endTime = dateTime.plusMonths(1).plusWeeks(3);

        expression = expression
                .and(inspection.inspectionTime.goe(dateTime.toDate()))
                .and(inspection.inspectionTime.loe(endTime.toDate()));

        // 获取日历信息
        while (dateTime.isBefore(endTime)) {
            result.getDateInfo().put(dateTime.getMillis(), !holidayService.isWorkday(dateTime.toDate()));
            dateTime = dateTime.plusDays(1);
        }

        // 获取巡检信息
        if (systemId == null) {
            return result;
        } else {
            expression = expression.and(inspection.system.objectId.eq(systemId));
        }
        inspectionRepository.findAll(expression, new Sort(Sort.Direction.ASC, "inspectionTime"))
                .forEach(ins -> {
                    InspectionMonthResult.Detail detail = new InspectionMonthResult.Detail();
                    DateTime insTime = new DateTime(ins.getInspectionTime());
                    detail.setDate(insTime.withTimeAtStartOfDay().toDate());
                    detail.setSegment(ins.getSegment());
                    detail.setInspectionId(ins.getObjectId());

                    int allResult = 1;      // 所有功能点是否正常
                    for (InspectionDetail inspectionDetail : ins.getDetailList()) {
                        if (inspectionDetail.getResult() == 0) {
                            allResult = 0;
                            break;
                        }
                    }
                    if (ins.getDelay() == 0 && allResult == 1) {
                        detail.setValue(InspectionResult.Good);
                    }
                    if (ins.getDelay() == 0 && allResult == 0) {
                        detail.setValue(InspectionResult.Bad);
                    }
                    if (ins.getDelay() == 1 && allResult == 1) {
                        detail.setValue(InspectionResult.GoodAndDelay);
                    }
                    if (ins.getDelay() == 1 && allResult == 0) {
                        detail.setValue(InspectionResult.BadAndDelay);
                    }
                    result.getDetailList().put(detail.toString(), detail);
                });

        int segment = getInspectionSegment(new Date())[0];
        DateTime startTime = new DateTime().withTimeAtStartOfDay();
        boolean exists1 = inspectionRepository.exists(
                inspection.system.objectId.eq(systemId)
                        .and(inspection.inspectionTime.between(startTime.toDate(), startTime.plusDays(1).toDate()))
                        .and(inspection.segment.eq(1))
                        .and(inspection.deleted.eq(0))
        );
        boolean exists2 = inspectionRepository.exists(
                inspection.system.objectId.eq(systemId)
                        .and(inspection.inspectionTime.between(startTime.toDate(), startTime.plusDays(1).toDate()))
                        .and(inspection.segment.eq(3))
                        .and(inspection.deleted.eq(0))
        );
        if (exists1) {
            result.setSegmentResult1(1);
        } else {
            if (segment < 1) {
                result.setSegmentResult1(null);
            } else if (segment == 1) {
                result.setSegmentResult1(0);
            } else {
                result.setSegmentResult1(2);
            }
        }
        if (exists2) {
            result.setSegmentResult2(1);
        } else {
            if (segment < 3) {
                result.setSegmentResult2(null);
            } else if (segment == 3) {
                result.setSegmentResult2(0);
            } else {
                result.setSegmentResult2(2);
            }
        }

        return result;
    }

    public InspectionInfo getInspectionInfo(Long objectId) {

        return buildInspectionInfo(inspectionRepository.get(objectId));
    }

    public void saveInspection(Inspection inspection) {

        List<FileUploadInfo> invalidAttachments = new ArrayList();
        int[] segmentInfo = getInspectionSegment(new Date());
        if (segmentInfo[0] % 2 == 0) {
            throw new ServiceException("请在每天【8:30-10:00】和【12:30-14:00】提交巡检记录！");
        }
        Accendant currentAccendant = accendantService.getCurrentAccendant();
        Supervisor currentSupervisor = supervisorService.getCurrentSupervisor();
        Inspection current = inspectionRepository.save(inspection);
        boolean exception = false;
        current.setInspectionTime(new Date());
        // 巡检人可以是系统维护人员也可以是项目管理员
        current.setAccendant(currentAccendant);
        current.setSupervisor(currentSupervisor);
        current.setSegment(segmentInfo[0]);
        current.setDelay(segmentInfo[1]);
        current.setAuto(0);
        for (InspectionDetail inspectionDetail : inspection.getDetailList()) {
            boolean error = inspectionDetail.getResult() == 0;
            InspectionDetail detail = inspectionDetailRepository.save(inspectionDetail);
            detail.setInspection(current);
            List<ContentAttachment> contentAttachments = new ArrayList();
            for (FileUploadInfo fileUploadInfo : inspectionDetail.getScreenshots()) {
                ContentAttachment contentAttachment = new ContentAttachment();
                contentAttachment.setFileInfo(fileUploadInfo);
                contentAttachment.setRelatedType(InspectionDetail.class.getSimpleName());
                contentAttachment.setRelatedId(detail.getObjectId());
                contentAttachments.add(contentAttachment);
                if (error) {
                    invalidAttachments.add(fileUploadInfo);
                }
            }
            exception = exception || error;
            contentAttachmentService.save(InspectionDetail.class.getSimpleName(), detail.getObjectId(), null, contentAttachments);
        }

        // 如有异常功能点，则创建巡检工单
        if (exception && !StringUtils.isEmpty(inspection.getMessage())) {
            Ticket ticket = new Ticket();
            ticket.setPriority(1);
            ticket.setSource(TicketSource.Inspection);
            ticket.setContent(inspection.getMessage());
            ticket.setSystem(inspection.getSystem());
            ticket.setAttachments(invalidAttachments);
            if (currentSupervisor != null) {
                ticket.setContacts(currentSupervisor.getUsername());
                ticket.setPhoneNo(currentSupervisor.getPhoneNo());
            }
            ticketService.saveTicket(ticket);

            // 如果是运维人员提交的巡检记录，则自动接单
            if (currentAccendant != null) {
                ticketService.acceptTicket(ticket.getObjectId());
            }
        }
    }

    public void deleteInspection(Long objectId) {

        inspectionRepository.logicDelete(objectId);
    }

    public void deleteInspection(List<Long> objectIds) {

        objectIds.forEach(this::deleteInspection);
    }

    /**
     * 保存自动巡检
     */
    public void saveAutoInspection(AutoInspection autoInspection) {

        System system = systemRepository.get(autoInspection.getSystemId());
        if (system == null) {
            throw new ServiceException("系统ID不正确！");
        }

        boolean workday = holidayService.isWorkday(new DateTime().withTimeAtStartOfDay().toDate());
        if (!workday) {
            throw new ServiceException("休息日不需要巡检！");
        }

        int[] segmentInfo = getInspectionSegment(new Date());
        if (segmentInfo[0] % 2 == 0) {
            throw new ServiceException("请在每天【8:30-10:00】和【12:30-14:00】提交巡检记录！");
        }

        Inspection inspection = new Inspection();
        inspection.setSystem(system);
        inspection = inspectionRepository.save(inspection);
        inspection.setInspectionTime(new Date());
        inspection.setSegment(segmentInfo[0]);
        inspection.setDelay(segmentInfo[1]);
        inspection.setAuto(1);

        List<Function> functionList = functionRepository.findAll(
                QFunction.function.system.objectId.eq(system.getObjectId()), new Sort(Sort.Direction.ASC, "orderNo"));

        if (CollectionUtils.isEmpty(autoInspection.getDetailList())) {
            throw new ServiceException("巡检明细不能为空！");
        }

        if (functionList.size() != autoInspection.getDetailList().size()) {
            throw new ServiceException("功能点个数不一致！");
        }

        BASE64Decoder decoder = new sun.misc.BASE64Decoder();

        for (int i = 0; i < functionList.size(); i++) {
            AutoInspection.Detail detail = autoInspection.getDetailList().get(i);
            Function function = functionList.get(i);
            InspectionDetail inspectionDetail = new InspectionDetail();
            inspectionDetail.setFunction(function);
            inspectionDetail.setInspection(inspection);
            inspectionDetail.setResult(detail.getResult());
            inspectionDetail = inspectionDetailRepository.save(inspectionDetail);
            List<ContentAttachment> contentAttachments = new ArrayList();
            for (String image : detail.getScreenshots()) {
                image = image.replaceAll("data:image/jpeg;base64,", "")
                        .replaceAll("data:image/png;base64,", "");
                long tempFilename = idWorker.nextId();

                // 解码图片，并保存到临时目录
                OutputStream out = null;
                try {
                    byte[] bytes = decoder.decodeBuffer(image);
                    for (int index = 0; index < bytes.length; ++index) {
                        if (bytes[index] < 0) {
                            bytes[index] += 256;
                        }
                    }
                    out = new FileOutputStream(environmentService.getTempPath() + "/" + tempFilename);
                    out.write(bytes);
                    out.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ServiceException("巡检图片格式错误！");
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                ContentAttachment contentAttachment = new ContentAttachment();
                contentAttachment.setFileInfo(FileUploadInfo.valueOf(tempFilename + "", "screenshots_auto.jpg"));
                contentAttachment.setRelatedType(InspectionDetail.class.getSimpleName());
                contentAttachment.setRelatedId(inspectionDetail.getObjectId());
                contentAttachments.add(contentAttachment);
            }
            contentAttachmentService.save(InspectionDetail.class.getSimpleName(), inspectionDetail.getObjectId(), null, contentAttachments);
        }
    }

    /**
     * 获取巡检统计信息
     */
    public InspectionMonthReport getInspectionMonthReport(Integer year, Integer month) {

        InspectionMonthReport result = new InspectionMonthReport();

        DateTime monthStartDate = new DateTime()
                .withYear(year)
                .withMonthOfYear(month)
                .withDayOfMonth(1)
                .withTimeAtStartOfDay();
        DateTime startOfDay = new DateTime().withTimeAtStartOfDay();
        List<Integer> defaultValues = new ArrayList();

        for (int i = 0; i < DateUtils.getDaysOfMonth(monthStartDate.toDate()); i++) {
            DateTime dateTime = monthStartDate.plusDays(i);
            if (dateTime.isAfterNow()) {
                defaultValues.add(InspectionResult.NotYet);
            } else {
                boolean workday = holidayService.isWorkday(dateTime.toDate());
                if (workday && dateTime.isEqual(startOfDay)) {
                    defaultValues.add(100); // 100表示当天
                } else {
                    defaultValues.add(workday ? InspectionResult.No : InspectionResult.Unnecessary);
                }
            }
            result.getDateList().add(i + 1 + "号");
        }

        List<System> systemList;
        Supervisor currentSupervisor = supervisorService.getCurrentSupervisor();
        Sort sort = new Sort(Sort.Direction.ASC, "company.name").and(new Sort(Sort.Direction.ASC, "objectId"));
        if (currentSupervisor != null) {
            systemList = systemRepository.findAll(QSystem.system.supervisors.any().supervisor.objectId.eq(currentSupervisor.getObjectId()), sort);
        } else {
            systemList = systemRepository.findAll(sort);
        }

        Collections.reverse(systemList);

        Date startDate = monthStartDate.toDate();
        Date endDate = monthStartDate.plusMonths(1).toDate();
        Map<String, Object[]> inspectionResults = new HashMap();
        List<String> badInspections = jdbcTemplate.queryForList("select system_id || '-' || date_part('D', inspection_time) || '-' || segment from sed_inspection a" +
                " inner join sed_inspectiondetail b on a.objectid = b.inspection_id" +
                " where a.inspection_time between ? and ? and b.result = 0", String.class, startDate, endDate);
        jdbcTemplate.queryForList("select system_id || '-' || date_part('D', inspection_time) || '-' || segment inspection_key, objectid, delay from sed_inspection a" +
                " where a.inspection_time between ? and ?", startDate, endDate).forEach(data -> {
            String inspectionKey = (String) data.get("inspection_key");
            Long inspectionId = (Long) data.get("objectid");
            Integer delay = (Integer) data.get("delay");
            Integer value;
            if (badInspections.contains(inspectionKey)) {
                value = delay == 1 ? InspectionResult.BadAndDelay : InspectionResult.Bad;
            } else {
                value = delay == 1 ? InspectionResult.GoodAndDelay : InspectionResult.Good;
            }
            inspectionResults.put(inspectionKey, new Object[]{value, inspectionId});
        });

        int systemIndex = 0;
        for (System system : systemList) {
            long functionCount = systemService.functionCount(system.getObjectId());
            if (functionCount == 0) {
                // 如果系统下没有功能点，则不需要统计巡检
                continue;
            }
            Long systemId = system.getObjectId();
            result.getSystemList().add(system.getName());
            int dateIndex = 0;
            for (Integer defaultValue : defaultValues) {
                Integer segment1DefaultValue = defaultValue;
                Integer segment3DefaultValue = defaultValue;
                if (defaultValue != null && defaultValue.equals(100)) {
                    int segment = getInspectionSegment(new Date())[0];
                    if (segment == 0 || segment == 1) {
                        segment1DefaultValue = InspectionResult.NotYet;
                        segment3DefaultValue = InspectionResult.NotYet;
                    }
                    if (segment == 2 || segment == 3) {
                        segment1DefaultValue = InspectionResult.No;
                        segment3DefaultValue = InspectionResult.NotYet;
                    }
                    if (segment == 4) {
                        segment1DefaultValue = InspectionResult.No;
                        segment3DefaultValue = InspectionResult.No;
                    }
                }
                Object[] segment1Value = inspectionResults.get(systemId + "-" + (dateIndex + 1) + "-1");
                Object[] segment3Value = inspectionResults.get(systemId + "-" + (dateIndex + 1) + "-3");
                // 上午
                if (segment1Value == null) {
                    result.getValues().add(new Object[]{dateIndex, systemIndex, 1, segment1DefaultValue, null});
                } else {
                    result.getValues().add(new Object[]{dateIndex, systemIndex, 1, segment1Value[0], segment1Value[1]});
                }
                // 下午
                if (segment3Value == null) {
                    result.getValues().add(new Object[]{dateIndex, systemIndex, 3, segment3DefaultValue, null});
                } else {
                    result.getValues().add(new Object[]{dateIndex, systemIndex, 3, segment3Value[0], segment3Value[1]});
                }
                dateIndex++;
            }
            systemIndex++;
        }
        return result;
    }

    public InspectionMonthSummary getInspectionMonthSummary(Integer year, Integer month) {

        InspectionMonthSummary result = new InspectionMonthSummary();
        DateTime startTime = new DateTime()
                .withTimeAtStartOfDay()
                .withDayOfMonth(1)
                .withMonthOfYear(month)
                .withYear(year);
        DateTime endTime = startTime.plusMonths(1);
        String sql = "select system_id, delay, b.result, count(1) from sed_inspection a, \n" +
                "(select inspection_id, min(result) result from sed_inspectiondetail group by inspection_id) b\n" +
                "where a.objectid = b.inspection_id and a.inspection_time > ? and a.inspection_time < ?\n" +
                "group by a.system_id, a.delay, b.result\n";
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql, startTime.toDate(), endTime.toDate());
        Map<String, Integer> inspectionInfo = new HashMap();
        for (Map<String, Object> data : dataList) {
            inspectionInfo.put(data.get("system_id").toString() + data.get("delay") + data.get("result"), ((Long) data.get("count")).intValue());
        }

        int total = holidayService.workdaysOfMonth(year, month) * 2;
        // 如果是当月
        if (startTime.isEqual(new DateTime().withTimeAtStartOfDay().withDayOfMonth(1))) {
            total = (holidayService.workdayOfMonth(new Date()) - 1) * 2;
            int[] inspectionSegment = getInspectionSegment(new Date());
            if (inspectionSegment[0] > 0) {
                total++;
            }
            if (inspectionSegment[0] > 2) {
                total++;
            }
        }
        result.setTotal(total);

        Sort sort = new Sort(Sort.Direction.ASC, "company.name").and(new Sort(Sort.Direction.ASC, "objectId"));
        List<System> systemList = systemRepository.findAll(sort);
        for (System system : systemList) {
            long functionCount = systemService.functionCount(system.getObjectId());
            if (functionCount == 0) {
                // 如果系统下没有功能点，则不需要统计巡检
                continue;
            }
            Integer good = inspectionInfo.get(system.getObjectId() + "01");
            Integer bad = inspectionInfo.get(system.getObjectId() + "00");
            Integer goodAndDelay = inspectionInfo.get(system.getObjectId() + "11");
            Integer badAndDelay = inspectionInfo.get(system.getObjectId() + "10");
            good = good == null ? 0 : good;
            bad = bad == null ? 0 : bad;
            goodAndDelay = goodAndDelay == null ? 0 : goodAndDelay;
            badAndDelay = badAndDelay == null ? 0 : badAndDelay;
            Integer no = total - good - bad - goodAndDelay - badAndDelay;
            if (no < 0) {
                // bug
                no = 0;
            }
            result.getDetailList().add(new InspectionMonthSummary.Detail(
                    system.getName(), good, bad, goodAndDelay, badAndDelay, no
            ));
        }

        return result;
    }

    private int[] getInspectionSegment(Date date) {

        int segment;
        int delay = 0;
        int minuteOfDay = new DateTime(date).getMinuteOfDay();
        if (minuteOfDay < 8.5 * 60) {
            segment = 0;
        } else if (minuteOfDay <= 10 * 60) {
            segment = 1;
        } else if (minuteOfDay <= 11 * 60) {
            segment = 1;
            delay = 1;
        } else if (minuteOfDay < 12.5 * 60) {
            segment = 2;
        } else if (minuteOfDay <= 14 * 60) {
            segment = 3;
        } else if (minuteOfDay <= 15 * 60) {
            segment = 3;
            delay = 1;
        } else {
            segment = 4;
        }
        return new int[]{segment, delay};
    }

    private InspectionInfo buildInspectionInfo(Inspection inspection) {

        InspectionInfo inspectionInfo = new InspectionInfo();
        inspectionInfo.setObjectId(inspection.getObjectId());
        if (inspection.getAccendant() != null) {
            inspectionInfo.setUsername(inspection.getAccendant().getUsername());
        } else if (inspection.getSupervisor() != null) {
            inspectionInfo.setUsername(inspection.getSupervisor().getUsername());
        } else {
            inspectionInfo.setUsername(inspection.getCreatedBy().getName());
        }

        inspectionInfo.setInspectionTime(inspection.getInspectionTime());
        inspectionInfo.setSystemId(inspection.getSystem().getObjectId());
        inspectionInfo.setSystemName(inspection.getSystem().getName());
        inspectionInfo.setMessage(inspection.getMessage());
        inspectionInfo.setDetailList(new ArrayList());

        for (InspectionDetail inspectionDetail : inspection.getDetailList()) {
            InspectionInfo.Detail detail = new InspectionInfo.Detail();
            detail.setObjectId(inspectionDetail.getObjectId());
            detail.setName(inspectionDetail.getFunction().getName());
            detail.setResult(inspectionDetail.getResult());
            detail.setScreenshots(contentAttachmentService.getImageAttachmentLinks(inspectionDetail.getObjectId()));
            inspectionInfo.getDetailList().add(detail);
        }

        return inspectionInfo;
    }

}
