package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Accendant;
import com.unicorn.par.domain.po.Inspection;
import com.unicorn.par.domain.po.QInspection;
import com.unicorn.par.domain.vo.InspectionInfo;
import com.unicorn.par.service.AccendantService;
import com.unicorn.par.service.InspectionService;
import com.unicorn.system.domain.po.User;
import com.unicorn.system.service.UserService;
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

    @Autowired
    private UserService userService;

    @Autowired
    private AccendantService accendantService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<InspectionInfo> list(PageInfo pageInfo, String keyword) {

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

        User currentUser = userService.getCurrentUser();
        String roleTag = currentUser.getUserRoleList().get(0).getRole().getTag();
        if ("Accendant".equals(roleTag)) {
            Accendant currentAccendant = accendantService.getCurrentAccendant();
            expression = expression.and(inspection.accendant.objectId.eq(currentAccendant.getObjectId()));
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
