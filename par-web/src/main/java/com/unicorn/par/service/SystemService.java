package com.unicorn.par.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Accendant;
import com.unicorn.par.domain.po.QFunction;
import com.unicorn.par.domain.po.QSystem;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.domain.vo.SystemInfo;
import com.unicorn.par.repository.FunctionRepository;
import com.unicorn.par.repository.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SystemService {

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private AccendantService accendantService;

    public Page<System> getSystem(QueryInfo queryInfo) {

        return systemRepository.findAll(queryInfo);
    }

    public List<SystemInfo> getSystemList(Integer self) {

        List<System> systemList;
        if (self == null || self != 1) {
            systemList = systemRepository.findAll();
        } else {
            Accendant currentAccendant = accendantService.getCurrentAccendant();
            QSystem system = QSystem.system;
            BooleanExpression expression = system.company.objectId.eq(currentAccendant.getCompany().getObjectId());
            systemList = systemRepository.findAll(expression);
        }
        QFunction function = QFunction.function;
        return systemList.stream().map(system ->
                SystemInfo.valueOf(system, functionRepository.findAll(
                        function.system.objectId.eq(system.getObjectId())
                        , new Sort(Sort.Direction.ASC, "orderNo")
                ))
        ).collect(Collectors.toList());
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
            current.setSupervisor(system.getSupervisor());
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
