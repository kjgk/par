package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.QCompany;
import com.unicorn.par.domain.po.Company;
import com.unicorn.par.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Company> list(PageInfo pageInfo, String keyword) {

        QCompany company = QCompany.company;

        BooleanExpression expression = company.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(company.name.containsIgnoreCase(s));
            }
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.ASC, "objectId")
        );
        return companyService.getCompany(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BasicInfo> list() {

        return companyService.getCompany();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createCompany(@RequestBody Company company) {

        companyService.saveCompany(company);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateCompany(@PathVariable("objectId") Long objectId, @RequestBody Company company) {

        company.setObjectId(objectId);
        companyService.saveCompany(company);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteCompany(@PathVariable("objectId") Long objectId) {

        companyService.deleteCompany(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        companyService.deleteCompany(objectIds);
    }
}
