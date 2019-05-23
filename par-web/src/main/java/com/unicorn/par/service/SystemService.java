package com.unicorn.par.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.System;
import com.unicorn.par.domain.po.*;
import com.unicorn.par.domain.vo.SystemInfo;
import com.unicorn.par.repository.FunctionRepository;
import com.unicorn.par.repository.SystemRepository;
import com.unicorn.par.repository.SystemSupervisorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
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

    @Autowired
    private SystemSupervisorRepository systemSupervisorRepository;

    @Autowired
    private SupervisorService supervisorService;

    public Page<System> getSystem(QueryInfo queryInfo) {

        return systemRepository.findAll(queryInfo);
    }

    public List<SystemInfo> getSystemList(Integer self) {

        List<System> systemList;
        Sort sort = new Sort(Sort.Direction.ASC, "company.name").and(new Sort(Sort.Direction.ASC, "objectId"));
        if (self == null || self != 1) {
            systemList = systemRepository.findAll(sort);
        } else {
            Accendant currentAccendant = accendantService.getCurrentAccendant();
            Supervisor currentSupervisor = supervisorService.getCurrentSupervisor();
            if (currentAccendant != null) {
                QSystem system = QSystem.system;
                BooleanExpression expression = system.company.objectId.eq(currentAccendant.getCompany().getObjectId());
                systemList = systemRepository.findAll(expression, sort);
            } else if (currentSupervisor != null) {
                QSystem system = QSystem.system;
                BooleanExpression expression = system.supervisors.any().supervisor.objectId.eq(currentSupervisor.getObjectId());
                systemList = systemRepository.findAll(expression, sort);
            } else {
                systemList = new ArrayList();
            }
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
            current.setDescription(system.getDescription());
            systemSupervisorRepository.deleteBySystemId(current.getObjectId());
        }

        for (SystemSupervisor systemSupervisor : system.getSupervisors()) {
            systemSupervisor.setSystem(current);
            systemSupervisorRepository.save(systemSupervisor);
        }
    }

    public void deleteSystem(Long objectId) {

        systemRepository.deleteById(objectId);
    }

    public void deleteSystem(List<Long> objectIds) {

        objectIds.forEach(this::deleteSystem);
    }

    @Cacheable(value = "functionCount")
    public long functionCount(Long objectId) {

        return functionRepository.count(QFunction.function.system.objectId.eq(objectId));
    }
}
