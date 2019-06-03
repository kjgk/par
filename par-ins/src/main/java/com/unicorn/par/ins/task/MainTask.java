package com.unicorn.par.ins.task;

import com.unicorn.par.ins.model.AutoInspection;
import com.unicorn.par.ins.service.CwpInspectionScript;
import com.unicorn.par.ins.service.InspectionScript;
import com.unicorn.par.ins.service.InspectionService;
import com.unicorn.par.ins.service.LhsrkjInspectionScript;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
@Slf4j
public class MainTask {

    private InspectionService inspectionService;

    private LhsrkjInspectionScript lhsrkjInspectionScript;

    private CwpInspectionScript cwpInspectionScript;

    //    @Scheduled(cron = "0 45 8,12 * * ?")
    @Scheduled(fixedDelay = 1000000)
    public void autoInspection() {

        new ArrayList<InspectionScript>() {{
            add(lhsrkjInspectionScript);
            add(cwpInspectionScript);
        }}.forEach(inspectionScript -> {
            log.info("【{}】开始巡检", inspectionScript.getSystemName());
            try {
                AutoInspection autoInspection = inspectionScript.doInspection();
                log.info("【{}】开始提交巡检记录", inspectionScript.getSystemName());
                inspectionService.postAutoInspection(autoInspection);
                log.info("【{}】巡检记录提交成功！", inspectionScript.getSystemName());
            } catch (Exception e) {
                log.error("【{}】巡检出错！{}", inspectionScript.getSystemName(), e.toString());
            }
            log.info("【{}】巡检完成", inspectionScript.getSystemName());
        });
    }
}
