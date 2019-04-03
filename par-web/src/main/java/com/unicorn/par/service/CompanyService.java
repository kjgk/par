package com.unicorn.par.service;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Company;
import com.unicorn.par.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Page<Company> getCompany(QueryInfo queryInfo) {

        return companyRepository.findAll(queryInfo);
    }

    public List<BasicInfo> getCompany() {

        return companyRepository.list();
    }

    public Company getCompany(Long objectId) {

        return companyRepository.get(objectId);
    }

    public void saveCompany(Company company) {

        Company current;
        if (StringUtils.isEmpty(company.getObjectId())) {
            current = companyRepository.save(company);
        } else {
            current = companyRepository.getOne(company.getObjectId());
            current.setName(company.getName());
            current.setShortName(company.getShortName());
            current.setDescription(company.getDescription());
        }
    }

    public void deleteCompany(Long objectId) {

        companyRepository.deleteById(objectId);
    }

    public void deleteCompany(List<Long> objectIds) {

        objectIds.forEach(this::deleteCompany);
    }
}
