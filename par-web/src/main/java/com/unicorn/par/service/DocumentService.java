package com.unicorn.par.service;

import com.unicorn.core.domain.po.Code;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Document;
import com.unicorn.par.domain.po.QSystem;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.repository.DocumentRepository;
import com.unicorn.par.repository.SystemRepository;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.service.ContentAttachmentService;
import com.unicorn.system.service.CodeService;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private ContentAttachmentService contentAttachmentService;

    @Autowired
    private SystemRepository systemRepository;

    public Page<Document> getDocument(QueryInfo queryInfo) {

        return documentRepository.findAll(queryInfo).map(document -> {
            document.setAttachment(contentAttachmentService.getAttachmentInfo(document.getObjectId()));
            return document;
        });
    }

    public List<BasicInfo> getDocument() {

        return documentRepository.list();
    }

    public Document getDocument(Long objectId) {

        return documentRepository.get(objectId);
    }

    public void saveDocument(Document document) {

        Document current;
        if (StringUtils.isEmpty(document.getObjectId())) {
            String tag = document.getCategory().getTag();
            document.setCategory(null);
            current = documentRepository.save(document);
            current.setUploader(userService.getCurrentUser());
            List<Code> documentCategories = codeService.getCodeByTag("DocumentCategory").getChildList();
            for (Code category : documentCategories) {
                if (!StringUtils.isEmpty(tag) && tag.equals(category.getTag())) {
                    current.setCategory(category);
                    break;
                }
            }
        } else {
            // 不允许修改
            return;
        }

        if (document.getAttachment() != null) {
            ContentAttachment contentAttachment = new ContentAttachment();
            contentAttachment.setFileInfo(FileUploadInfo.valueOf(document.getAttachment().getTempFilename(), document.getAttachment().getFilename()));
            contentAttachment.setRelatedType(Document.class.getSimpleName());
            contentAttachment.setRelatedId(current.getObjectId());
            contentAttachmentService.save(contentAttachment);
        }
    }

    public void deleteDocument(Long objectId) {

        documentRepository.deleteById(objectId);
    }

    public void deleteDocument(List<Long> objectIds) {

        objectIds.forEach(this::deleteDocument);
    }


    public List<Map> summaryDocument(Date startDate, Date endDate, String category) {

        List<Map> result = new ArrayList();
        List<Code> documentCategories = codeService.getCodeByTag("DocumentCategory").getChildList();
        Long categoryId = null;
        for (Code code : documentCategories) {
            if (category.equals(code.getTag())) {
                categoryId = code.getObjectId();
                break;
            }
        }
        if (categoryId == null) {
            return result;
        }
        Map<Long, Long> counts = new HashMap<>();
        for (Map<String, Object> data : jdbcTemplate.queryForList("select a.system_id, count(*) count from sed_document a " +
                "where a.deleted = 0 and a.created_date between ? and ? and a.category_id = ? group by a.system_id", startDate, endDate, categoryId)) {
            counts.put((Long) data.get("system_id"), (Long) data.get("count"));
        }

        Sort sort = new Sort(Sort.Direction.ASC, "company.name").and(new Sort(Sort.Direction.ASC, "objectId"));
        List<System> systemList = systemRepository.findAll(QSystem.system.enabled.eq(1), sort);
        for (System system : systemList) {
            result.add(new HashMap() {{
                put("systemId", system.getObjectId());
                put("systemName", system.getName());
                put("count", counts.getOrDefault(system.getObjectId(), 0L));
            }});
        }
        return result;
    }
}
