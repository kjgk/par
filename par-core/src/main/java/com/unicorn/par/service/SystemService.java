package com.unicorn.par.service;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.repository.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SystemService {

    @Autowired
    private SystemRepository systemRepository;

    public Page<System> getSystem(QueryInfo queryInfo) {

        return systemRepository.findAll(queryInfo);
    }

    public List<BasicInfo> getSystem() {

        return systemRepository.list();
    }

    public System getSystem(Long objectId) {

        return systemRepository.get(objectId);
    }

    public void saveSystem(System system) {

        System current;
        if (StringUtils.isEmpty(system.getObjectId())) {
            current = systemRepository.save(system);
        } else {
            current = systemRepository.getOne(system.getObjectId());
            current.setName(system.getName());
            current.setUrl(system.getUrl());
            current.setCompany(system.getCompany());
            current.setDescription(system.getDescription());
        }
    }

    public void deleteSystem(Long objectId) {

        systemRepository.deleteById(objectId);
    }

    public void deleteSystem(List<Long> objectIds) {

        objectIds.forEach(this::deleteSystem);
    }
}
