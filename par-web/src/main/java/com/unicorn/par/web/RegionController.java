package com.unicorn.par.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.core.domain.vo.BasicInfo;
import com.unicorn.core.query.PageInfo;
import com.unicorn.core.query.QueryInfo;
import com.unicorn.par.domain.po.Region;
import com.unicorn.par.domain.po.QRegion;
import com.unicorn.par.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/region")
public class RegionController {

    @Autowired
    private RegionService regionService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Region> list(PageInfo pageInfo, String keyword) {

        QRegion region = QRegion.region;

        BooleanExpression expression = region.isNotNull();
        if (!StringUtils.isEmpty(keyword)) {
            for (String s : keyword.split(" ")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                expression = expression.and(region.name.containsIgnoreCase(s));
            }
        }
        QueryInfo queryInfo = new QueryInfo(expression, pageInfo,
                new Sort(Sort.Direction.DESC, "createdDate")
        );
        return regionService.getRegion(queryInfo);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BasicInfo> list() {

        return regionService.getRegion();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createRegion(@RequestBody Region region) {

        regionService.saveRegion(region);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateRegion(@PathVariable("objectId") Long objectId, @RequestBody Region region) {

        region.setObjectId(objectId);
        regionService.saveRegion(region);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteRegion(@PathVariable("objectId") Long objectId) {

        regionService.deleteRegion(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        regionService.deleteRegion(objectIds);
    }
}
