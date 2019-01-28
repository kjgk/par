package com.unicorn.par.service;

import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Region;
import com.unicorn.par.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    public Page<Region> getRegion(QueryInfo queryInfo) {

        return regionRepository.findAll(queryInfo);
    }

    public List<BasicInfo> getRegion() {

        return regionRepository.list();
    }

    public Region getRegion(Long objectId) {

        return regionRepository.get(objectId);
    }

    public void saveRegion(Region region) {

        Region current;
        if (StringUtils.isEmpty(region.getObjectId())) {
            current = regionRepository.save(region);
        } else {
            current = regionRepository.getOne(region.getObjectId());
            current.setName(region.getName());
            current.setShortName(region.getShortName());
            current.setTag(region.getTag());
            current.setDescription(region.getDescription());
        }
    }

    public void deleteRegion(Long objectId) {

        regionRepository.deleteById(objectId);
    }

    public void deleteRegion(List<Long> objectIds) {

        objectIds.forEach(this::deleteRegion);
    }
}
