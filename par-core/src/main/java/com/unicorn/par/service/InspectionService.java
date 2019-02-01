package com.unicorn.par.service;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Inspection;
import com.unicorn.par.domain.po.InspectionDetail;
import com.unicorn.par.repository.InspectionDetailRepository;
import com.unicorn.par.repository.InspectionRepository;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.domain.vo.FileUploadInfo;
import com.unicorn.std.service.ContentAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionDetailRepository inspectionDetailRepository;

    @Autowired
    private ContentAttachmentService contentAttachmentService;

    @Autowired
    private AccendantService accendantService;

    public Page<Inspection> getInspection(QueryInfo queryInfo) {

        return inspectionRepository.findAll(queryInfo);
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
            current = inspectionRepository.save(inspection);
            current.setInspectionTime(new Date());
            current.setAccendant(accendantService.getCurrentAccendant());
            for (InspectionDetail inspectionDetail : inspection.getDetailList()) {
                inspectionDetail.setInspection(current);
                InspectionDetail detail = inspectionDetailRepository.save(inspectionDetail);
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

        inspectionRepository.deleteById(objectId);
    }

    public void deleteInspection(List<Long> objectIds) {

        objectIds.forEach(this::deleteInspection);
    }
}
