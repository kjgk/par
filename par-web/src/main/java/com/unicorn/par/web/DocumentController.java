package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.base.web.BaseController;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Accendant;
import com.unicorn.par.domain.po.Document;
import com.unicorn.par.domain.po.QDocument;
import com.unicorn.par.domain.po.Supervisor;
import com.unicorn.par.service.AccendantService;
import com.unicorn.par.service.DocumentService;
import com.unicorn.par.service.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/document")
public class DocumentController extends BaseController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private AccendantService accendantService;

    @Autowired
    private SupervisorService supervisorService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Document> list(PageInfo pageInfo, String keyword, Long systemId, String category) {

        QDocument document = QDocument.document;

        BooleanExpression expression = document.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(document.name.containsIgnoreCase(s));
            }
        }
        if (!StringUtils.isEmpty(category)) {
            expression = expression.and(document.category.tag.eq(category));
        }
        if (systemId != null) {
            expression = expression.and(document.system.objectId.eq(systemId));
        } else {
            Accendant currentAccendant = accendantService.getCurrentAccendant();
            Supervisor currentSupervisor = supervisorService.getCurrentSupervisor();
            if (currentSupervisor != null) {
                expression = expression.and(document.system.supervisors.any().supervisor.objectId.eq(currentSupervisor.getObjectId()));
            }
            if (currentAccendant != null) {
                expression = expression.and(document.system.company.objectId.eq(currentAccendant.getCompany().getObjectId()));
            }
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.DESC, "createdDate")
        );
        return documentService.getDocument(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BasicInfo> list() {

        return documentService.getDocument();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createDocument(@RequestBody Document document) {

        documentService.saveDocument(document);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateDocument(@PathVariable("objectId") Long objectId, @RequestBody Document document) {

        document.setObjectId(objectId);
        documentService.saveDocument(document);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteDocument(@PathVariable("objectId") Long objectId) {

        documentService.deleteDocument(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        documentService.deleteDocument(objectIds);
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public List summaryDocument(Date startDate, Date endDate, String category) {

        return documentService.summaryDocument(startDate, endDate, category);
    }

}
