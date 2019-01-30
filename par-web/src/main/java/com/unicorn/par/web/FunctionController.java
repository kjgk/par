package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Function;
import com.unicorn.par.domain.po.QFunction;
import com.unicorn.par.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/function")
public class FunctionController {

    @Autowired
    private FunctionService functionService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Function> list(PageInfo pageInfo, String keyword, Long systemId) {

        QFunction function = QFunction.function;

        BooleanExpression expression = function.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(function.name.containsIgnoreCase(s));
            }
        }
        if (systemId != null) {
            expression = expression.and(function.system.objectId.eq(systemId));
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.ASC, "system.name").and(new Sort(Sort.Direction.ASC, "orderNo"))
        );
        return functionService.getFunction(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BasicInfo> list() {

        return functionService.getFunction();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createFunction(@RequestBody Function function) {

        functionService.saveFunction(function);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateFunction(@PathVariable("objectId") Long objectId, @RequestBody Function function) {

        function.setObjectId(objectId);
        functionService.saveFunction(function);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteFunction(@PathVariable("objectId") Long objectId) {

        functionService.deleteFunction(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        functionService.deleteFunction(objectIds);
    }
}
