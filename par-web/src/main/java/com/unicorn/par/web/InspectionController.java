package com.unicorn.par.web;

import com.unicorn.par.domain.po.Inspection;
import com.unicorn.par.domain.vo.InspectionInfo;
import com.unicorn.par.domain.vo.InspectionMonthResult;
import com.unicorn.par.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.unicorn.base.web.ApiNamespace.API_V1;

@RestController
@RequestMapping(API_V1 + "/inspection")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @RequestMapping(method = RequestMethod.GET)
    public InspectionMonthResult getInspectionMonthResult(String month, Long systemId) {

        return inspectionService.getInspectionMonthResult(month, systemId);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.GET)
    public InspectionInfo getInspectionInfo(@PathVariable("objectId") Long objectId) {

        return inspectionService.getInspectionInfo(objectId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createInspection(@RequestBody Inspection inspection) {

        inspectionService.saveInspection(inspection);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.PATCH)
    public void updateInspection(@PathVariable("objectId") Long objectId, @RequestBody Inspection inspection) {

        inspection.setObjectId(objectId);
        inspectionService.saveInspection(inspection);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.DELETE)
    public void deleteInspection(@PathVariable("objectId") Long objectId) {

        inspectionService.deleteInspection(objectId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody List<Long> objectIds) {

        inspectionService.deleteInspection(objectIds);
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public List getInspectionReport(String date, String viewMode) {

        return inspectionService.getInspectionReport(viewMode, date);
    }
}
