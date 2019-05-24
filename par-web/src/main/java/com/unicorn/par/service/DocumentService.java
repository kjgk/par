package com.unicorn.par.service;

import com.unicorn.core.domain.po.Code;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.domain.vo.FileUploadInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Document;
import com.unicorn.par.repository.DocumentRepository;
import com.unicorn.std.domain.po.ContentAttachment;
import com.unicorn.std.service.ContentAttachmentService;
import com.unicorn.system.service.CodeService;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private ContentAttachmentService contentAttachmentService;

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
}
