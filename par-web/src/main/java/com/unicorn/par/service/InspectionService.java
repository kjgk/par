package com.unicorn.par.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.exception.ServiceException;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Inspection;
import com.unicorn.par.domain.po.InspectionDetail;
import com.unicorn.par.domain.po.QInspection;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.domain.vo.InspectionInfo;
import com.unicorn.par.repository.InspectionDetailRepository;
import com.unicorn.par.repository.InspectionRepository;
import com.unicorn.par.repository.SystemRepository;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.service.ContentAttachmentService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class InspectionService {

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

    public Page<InspectionInfo> getInspection(QueryInfo queryInfo) {

        return inspectionRepository.findAll(queryInfo).map(this::buildInspectionInfo);
    }

    public List<BasicInfo> getInspection() {

        return inspectionRepository.list();
    }

    public Inspection getInspection(Long objectId) {

        return inspectionRepository.get(objectId);
    }

    public void saveInspection(Inspection inspection) {

        Inspection current;
        if (StringUtils.isEmpty(inspection.getObjectId())) {
            int minuteOfDay = new DateTime().getMinuteOfDay();
            if (!((minuteOfDay >= 8.5 * 60 && minuteOfDay <= 9.5 * 60) || (minuteOfDay >= 12.5 * 60 && minuteOfDay <= 13.5 * 60))) {
                throw new ServiceException("请在每天【8:30-9:30】和【12:30-13:30】提交巡检记录！");
            }
            current = inspectionRepository.save(inspection);
            current.setInspectionTime(new Date());
            current.setAccendant(accendantService.getCurrentAccendant());
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
    public List getInspectionReport(String viewMode, String date) {

        List result = new ArrayList();
        Integer year = Integer.valueOf(date.substring(0, 4));
        Integer week = Integer.valueOf(date.substring(4));

        DateTime weekStartTime = new DateTime()
                .withYear(year)
                .withWeekOfWeekyear(week)
                .withDayOfWeek(1)
                .withTimeAtStartOfDay();

        BooleanExpression expression = QInspection.inspection.inspectionTime.between(weekStartTime.toDate(), weekStartTime.plusWeeks(1).toDate());
        List<Inspection> inspectionList = inspectionRepository.findAll(expression, new Sort(Sort.Direction.ASC, "inspectionTime"));

        List<Integer> defaultValues = new ArrayList();
        for (int i = 1; i <= 7; i++) {
            if (weekStartTime.plusDays(i).isAfterNow()) {
                defaultValues.add(null);
            } else {
                defaultValues.add(0);
            }
        }

        for (System system : systemRepository.findAll(new Sort(Sort.Direction.ASC, "company.name"))) {
            Map item = new HashMap();
            List<Integer> values = Arrays.asList(new Integer[defaultValues.size()]);
            Collections.copy(values, defaultValues);
            item.put("systemName", system.getName());
            item.put("values", values);
            for (Inspection inspection : inspectionList) {
                if (!system.getObjectId().equals(inspection.getSystem().getObjectId())) {
                    continue;
                }
                int dayOfWeek = new DateTime(inspection.getInspectionTime()).getDayOfWeek();
                int value = 1;
                for (InspectionDetail inspectionDetail : inspection.getDetailList()) {
                    if (inspectionDetail.getResult() != 1) {
                        value = 2;
                        break;
                    }
                }
                values.set(dayOfWeek - 1, value);
            }
            result.add(item);
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
