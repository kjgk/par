package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.QDocument;
import com.unicorn.par.domain.po.Document;
import com.unicorn.par.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Document> list(PageInfo pageInfo, String keyword, String category) {

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
}
