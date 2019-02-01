package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Inspection;
import com.unicorn.par.domain.po.QInspection;
import com.unicorn.par.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/inspection")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Inspection> list(PageInfo pageInfo, String keyword) {

        QInspection inspection = QInspection.inspection;

        BooleanExpression expression = inspection.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(inspection.name.containsIgnoreCase(s));
            }
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.DESC, "createdDate")
        );
        return inspectionService.getInspection(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BasicInfo> list() {

        return inspectionService.getInspection();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createInspection(@RequestBody Inspection inspection) {

        inspectionService.saveInspection(inspection);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateInspection(@PathVariable("objectId") Long objectId, @RequestBody Inspection inspection) {

        inspection.setObjectId(objectId);
        inspectionService.saveInspection(inspection);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteInspection(@PathVariable("objectId") Long objectId) {

        inspectionService.deleteInspection(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        inspectionService.deleteInspection(objectIds);
    }
}
