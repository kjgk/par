package com.unicorn.par.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.exception.ServiceException;
import com.unicorn.par.domain.po.Inspection;
import com.unicorn.par.domain.po.InspectionDetail;
import com.unicorn.par.domain.po.QInspection;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                    detail.setResult(1);
                    result.getDetailList().put(detail.toString(), detail);
                });

        Integer segment = getInspectionSegment(new Date());
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

    private Integer getInspectionSegment(Date date) {

        int minuteOfDay = new DateTime(date).getMinuteOfDay();
        if (minuteOfDay < 8.5 * 60) {
            return 0;
        } else if (minuteOfDay <= 9.5 * 60) {
            return 1;
        } else if (minuteOfDay < 12.5 * 60) {
            return 2;
        } else if (minuteOfDay <= 13.5 * 60) {
            return 3;
        }
        return 4;
    }

    public void saveInspection(Inspection inspection) {

        Inspection current;
        if (StringUtils.isEmpty(inspection.getObjectId())) {
            Integer inspectionSegment = getInspectionSegment(new Date());
            if (inspectionSegment % 2 == 0) {
                throw new ServiceException("请在每天【8:30-9:30】和【12:30-13:30】提交巡检记录！");
            }
            current = inspectionRepository.save(inspection);
            current.setInspectionTime(new Date());
            current.setAccendant(accendantService.getCurrentAccendant());
            current.setSegment(inspectionSegment);
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
            // todo 修改
        }
    }

    public void deleteInspection(Long objectId) {

        inspectionRepository.logicDelete(objectId);
    }

    public void deleteInspection(List<Long> objectIds) {

        objectIds.forEach(this::deleteInspection);
    }

    @Cacheable(value = "inspectionReport")
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
                defaultValues.add(null);
            } else {
                boolean workday = holidayService.isWorkday(dateTime.toDate());
                if (workday && dateTime.isEqual(startOfDay)) {
                    defaultValues.add(100); // 100表示当天
                } else {
                    defaultValues.add(workday ? 0 : -1);
                }
            }
            result.getDateList().add(i + 1 + "号");
        }

        List<System> systemList = systemRepository.findAll(new Sort(Sort.Direction.ASC, "company.name"));

        List<String> inspections = jdbcTemplate.queryForList("select system_id || '-' || date_part('D', inspection_time) || '-' || segment from sed_inspection" +
                " where inspection_time between ? and ?", String.class, monthStartDate.toDate(), monthStartDate.plusMonths(1).toDate());

        int systemIndex = 0;
        for (System system : systemList) {
            result.getSystemList().add(system.getName());
            int dateIndex = 0;
            for (Integer defaultValue : defaultValues) {
                Integer segment1DefaultValue = defaultValue;
                Integer segment3DefaultValue = defaultValue;
                if (defaultValue != null && defaultValue.equals(100)) {
                    Integer segment = getInspectionSegment(new Date());
                    if (segment == 0 || segment == 1) {
                        segment1DefaultValue = null;
                        segment3DefaultValue = null;
                    }
                    if (segment == 2 || segment == 3) {
                        segment1DefaultValue = 0;
                        segment3DefaultValue = null;
                    }
                    if (segment == 4) {
                        segment1DefaultValue = 0;
                        segment3DefaultValue = 0;
                    }
                }
                result.getValues().add(new Integer[]{dateIndex, systemIndex, 1, inspections.contains(system.getObjectId() + "-" + (dateIndex + 1) + "-1") ? new Integer(1) : segment1DefaultValue});   // 上午
                result.getValues().add(new Integer[]{dateIndex, systemIndex, 3, inspections.contains(system.getObjectId() + "-" + (dateIndex + 1) + "-3") ? new Integer(1) : segment3DefaultValue});   // 下午
                dateIndex++;
            }
            systemIndex++;
        }
        return result;
    }

    private InspectionInfo buildInspectionInfo(Inspection inspection) {

        InspectionInfo inspectionInfo = new InspectionInfo();
        inspectionInfo.setObjectId(inspection.getObjectId());
        inspectionInfo.setAccendantId(inspection.getAccendant().getObjectId());
        inspectionInfo.setAccendantName(inspection.getAccendant().getUsername());
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
