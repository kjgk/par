package com.unicorn.par.ins.task;

import com.unicorn.par.ins.model.AutoInspection;
import com.unicorn.par.ins.service.InspectionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class MainTask {

    private InspectionService inspectionService;

    @Scheduled(fixedDelay = 1000 * 1020)
    public void autoInspection() {

        final String url = "https://lhsrkj.com";
        final String systemName = "科技创新共享平台";
        final Long systemId = 540220344225497088L;

        log.info("开始启动自动巡检，系统名称【{}】，系统id【{}】，网址【{}】", systemName, systemId, url);

        AutoInspection.Detail loginSegment = new AutoInspection.Detail();
        AutoInspection.Detail searchSegment = new AutoInspection.Detail();

        WebDriver driver = null;
        try {
            log.info("初始化chrome浏览器");
            driver = new ChromeDriver();
            driver.manage().window().setSize(new Dimension(1280, 960));

            /*===================================== 登录 =====================================*/

            log.info("【登录】正在测试...");
            // 打开首页
            driver.get(url);

            // 点击登录按钮
            driver.findElement(By.xpath("/html/body/div/div[1]/div/div[3]/a")).click();

            Thread.sleep(1000l);

            // 填写用户名密码
            driver.findElement(By.cssSelector(".ngdialog input[name=\"account\"]")).sendKeys("18501655270", Keys.TAB, "Aa123456");

            // 截图
            loginSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

            // 登录
            driver.findElement(By.cssSelector(".ngdialog button.submit")).click();

            Thread.sleep(2000l);

            // 截图
            loginSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

            log.info("【登录】功能正常！");

            /*===================================== 关键字搜索 =====================================*/

            log.info("【关键字搜索】正在测试...");

            // 打开搜索页面
            driver.navigate().to(url + "/#/search/垃圾");

            Thread.sleep(2000l);

            // 截图
            searchSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

            log.info("【关键字搜索】功能正常！");

            log.info("【{}】开始提交巡检记录", systemName);
            AutoInspection autoInspection = new AutoInspection();
            autoInspection.setSystemId(systemId);
            autoInspection.getDetailList().add(loginSegment);
            autoInspection.getDetailList().add(searchSegment);

            // 提交巡检记录
            inspectionService.postAutoInspection(autoInspection);
            log.info("【{}】巡检记录提交成功！", systemName);
            log.info("【{}】自动巡检完成！", systemName);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("【{}】自动巡检失败！{}", systemName, e.toString());
        } finally {
            if (driver != null) {
                driver.close();
            }
        }
    }
}
