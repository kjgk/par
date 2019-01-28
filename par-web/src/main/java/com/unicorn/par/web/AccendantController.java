package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Accendant;
import com.unicorn.par.domain.po.QAccendant;
import com.unicorn.par.service.AccendantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/accendant")
public class AccendantController {

    @Autowired
    private AccendantService accendantService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Accendant> list(PageInfo pageInfo, String keyword) {

        QAccendant accendant = QAccendant.accendant;

        BooleanExpression expression = accendant.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(accendant.user.name.containsIgnoreCase(s));
            }
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.DESC, "createdDate")
        );
        return accendantService.getAccendant(queryInfo);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createAccendant(@RequestBody Accendant accendant) {

        accendantService.saveAccendant(accendant);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateAccendant(@PathVariable("objectId") Long objectId, @RequestBody Accendant accendant) {

        accendant.setObjectId(objectId);
        accendantService.saveAccendant(accendant);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteAccendant(@PathVariable("objectId") Long objectId) {

        accendantService.deleteAccendant(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        accendantService.deleteAccendant(objectIds);
    }
}
