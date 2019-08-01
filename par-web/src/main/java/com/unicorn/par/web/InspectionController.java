package com.unicorn.par.web;

import com.unicorn.par.domain.po.Inspection;
import com.unicorn.par.domain.vo.*;
import com.unicorn.par.service.InspectionService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    public InspectionMonthReport getInspectionMonthReport(Integer year, Integer month) {

        return inspectionService.getInspectionMonthReport(year, month);
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public InspectionMonthSummary getInspectionMonthSummary(Integer year, Integer month) {

        return inspectionService.getInspectionMonthSummary(year, month);
    }

    @RequestMapping(value = "/summary/export", method = RequestMethod.GET)
    public void exportInspectionMonthSummary(Integer year, Integer month, HttpServletResponse response) throws IOException {

        XSSFWorkbook xssfWorkbook = inspectionService.exportInspectionMonthSummary(year, month);

        response.setHeader("Content-Disposition", "filename=" +
                new String((year + "年" + month + "月各运维公司巡检统计表.xlsx").getBytes("GBK"), StandardCharsets.ISO_8859_1));
        response.setContentType("application/octet-stream");

        xssfWorkbook.write(response.getOutputStream());
        xssfWorkbook.close();
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @RequestMapping(value = "/auto", method = RequestMethod.POST)
    public void saveAutoInspection(@RequestBody AutoInspection autoInspection) {

        inspectionService.saveAutoInspection(autoInspection);
    }
}
