package com.unicorn.par.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.exception.ServiceException;
import com.unicorn.par.domain.enumeration.InspectionResult;
import com.unicorn.par.domain.po.*;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.domain.vo.InspectionInfo;
import com.unicorn.par.domain.vo.InspectionMonthResult;
import com.unicorn.par.domain.vo.InspectionMonthSummary;
import com.unicorn.par.repository.InspectionDetailRepository;
import com.unicorn.par.repository.InspectionRepository;
import com.unicorn.par.repository.SystemRepository;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.service.ContentAttachmentService;
import com.unicorn.utils.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
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
    private ContentAttachmentService contentAttachmentService;

    @Autowired
    private AccendantService accendantService;

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private ProjectService projectService;

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
                .minusMonths(1);
        DateTime endTime = dateTime.plusMonths(2);

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

    private int[] getInspectionSegment(Date date) {

        int segment = 0;
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
        } else if (minuteOfDay <= 18 * 60) {
            segment = 3;
            delay = 1;
        } else {
            segment = 4;
        }
        return new int[]{segment, delay};
    }

    public void saveInspection(Inspection inspection) {

        Inspection current;
        if (StringUtils.isEmpty(inspection.getObjectId())) {
            int[] segmentInfo = getInspectionSegment(new Date());
            if (segmentInfo[0] % 2 == 0) {
                throw new ServiceException("请在每天【8:30-10:00】和【12:30-14:00】提交巡检记录！");
            }
            current = inspectionRepository.save(inspection);
            current.setInspectionTime(new Date());
            // 巡检人可以是系统维护人员也可以是项目管理员
            current.setAccendant(accendantService.getCurrentAccendant());
            current.setSupervisor(supervisorService.getCurrentSupervisor());
            current.setSegment(segmentInfo[0]);
            current.setDelay(segmentInfo[1]);
            for (InspectionDetail inspectionDetail : inspection.getDetailList()) {
                InspectionDetail detail = inspectionDetailRepository.save(inspectionDetail);
                detail.setInspection(current);
                List<ContentAttachment> contentAttachments = new ArrayList();
                for (FileUploadInfo fileUploadInfo : inspectionDetail.getScreenshots()) {
                    ContentAttachment contentAttachment = new ContentAttachment();
                    contentAttachment.setFileInfo(fileUploadInfo);
                    contentAttachment.setRelatedType(InspectionDetail.class.getSimpleName());
                    contentAttachment.setRelatedId(detail.getObjectId());
                    contentAttachments.add(contentAttachment);
                }
                contentAttachmentService.save(InspectionDetail.class.getSimpleName(), detail.getObjectId(), null, contentAttachments);
            }
        } else {
            current = inspectionRepository.getOne(inspection.getObjectId());
            // todo 修改巡检记录 目前不允许修改
        }
    }

    public void deleteInspection(Long objectId) {

        inspectionRepository.logicDelete(objectId);
    }

    public void deleteInspection(List<Long> objectIds) {

        objectIds.forEach(this::deleteInspection);
    }

    public InspectionMonthSummary getInspectionReport(Integer year, Integer month) {

        InspectionMonthSummary result = new InspectionMonthSummary();

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
        Map<String, Integer> inspectionResults = new HashMap();
        List<String> badInspections = jdbcTemplate.queryForList("select system_id || '-' || date_part('D', inspection_time) || '-' || segment from sed_inspection a" +
                " inner join sed_inspectiondetail b on a.objectid = b.inspection_id" +
                " where a.inspection_time between ? and ? and b.result = 0", String.class, startDate, endDate);
        jdbcTemplate.queryForList("select system_id || '-' || date_part('D', inspection_time) || '-' || segment inspection_key, delay from sed_inspection a" +
                " where a.inspection_time between ? and ?", startDate, endDate).forEach(data -> {
            String inspectionKey = (String) data.get("inspection_key");
            Integer delay = (Integer) data.get("delay");
            Integer value;
            if (badInspections.contains(inspectionKey)) {
                value = delay == 1 ? InspectionResult.BadAndDelay : InspectionResult.Bad;
            } else {
                value = delay == 1 ? InspectionResult.GoodAndDelay : InspectionResult.Good;
            }
            inspectionResults.put(inspectionKey, value);
        });

        int systemIndex = 0;
        for (System system : systemList) {
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
                Integer segment1Value = inspectionResults.get(systemId + "-" + (dateIndex + 1) + "-1");
                Integer segment3Value = inspectionResults.get(systemId + "-" + (dateIndex + 1) + "-3");
                result.getValues().add(new Integer[]{dateIndex, systemIndex, 1, segment1Value == null ? segment1DefaultValue : segment1Value});   // 上午
                result.getValues().add(new Integer[]{dateIndex, systemIndex, 3, segment3Value == null ? segment3DefaultValue : segment3Value});   // 下午
                dateIndex++;
            }
            systemIndex++;
        }
        return result;
    }

    private InspectionInfo buildInspectionInfo(Inspection inspection) {

        InspectionInfo inspectionInfo = new InspectionInfo();
        inspectionInfo.setObjectId(inspection.getObjectId());
        if (inspection.getAccendant() != null) {
            inspectionInfo.setUsername(inspection.getAccendant().getUsername());
        } else if (inspection.getSupervisor() != null) {
            inspectionInfo.setUsername(inspection.getSupervisor().getUsername());
        }

        inspectionInfo.setInspectionTime(inspection.getInspectionTime());
        inspectionInfo.setSystemId(inspection.getSystem().getObjectId());
        inspectionInfo.setSystemName(inspection.getSystem().getName());
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
