package com.unicorn.par.service;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Function;
import com.unicorn.par.repository.FunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class FunctionService {

    @Autowired
    private FunctionRepository functionRepository;

    public Page<Function> getFunction(QueryInfo queryInfo) {

        return functionRepository.findAll(queryInfo);
    }

    public List<BasicInfo> getFunction() {

        return functionRepository.list();
    }

    public Function getFunction(Long objectId) {

        return functionRepository.get(objectId);
    }

    public void saveFunction(Function function) {

        Function current;
        if (StringUtils.isEmpty(function.getObjectId())) {
            current = functionRepository.save(function);
        } else {
            current = functionRepository.getOne(function.getObjectId());
            current.setName(function.getName());
            current.setSystem(function.getSystem());
            current.setOrderNo(function.getOrderNo());
            current.setDescription(function.getDescription());
        }
    }

    public void deleteFunction(Long objectId) {

        functionRepository.deleteById(objectId);
    }

    public void deleteFunction(List<Long> objectIds) {

        objectIds.forEach(this::deleteFunction);
    }
}
