package com.unicorn.par.ins.service;

import com.unicorn.par.ins.config.InspectionConfigurationProperties;
import com.unicorn.par.ins.model.AutoInspection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j(topic = "科研管理平台")
public class LhsrkyInspectionScript implements InspectionScript {

    private InspectionConfigurationProperties inspectionConfigurationProperties;

    public String getSystemName() {
        return "科研管理平台";
    }

    public AutoInspection doInspection() throws Exception {

        InspectionConfigurationProperties.SystemConfig config = inspectionConfigurationProperties.getSystemConfig().get("lhsrky");
        if (config == null) {
            return null;
        }
        Long systemId = config.getSystemId();
        String url = config.getUrl();

        AutoInspection autoInspection = new AutoInspection();
        autoInspection.setSystemId(systemId);
        AutoInspection.Detail loginSegment = new AutoInspection.Detail();
        AutoInspection.Detail searchSegment = new AutoInspection.Detail();
        autoInspection.getDetailList().add(loginSegment);
        autoInspection.getDetailList().add(searchSegment);

        WebDriver driver = null;
        try {
            log.info("初始化chrome浏览器");
            ChromeOptions chromeOptions = new ChromeOptions()
                    .setHeadless(true);
            if (!StringUtils.isEmpty(inspectionConfigurationProperties.getChromeBinaryPath())) {
                chromeOptions.setBinary(inspectionConfigurationProperties.getChromeBinaryPath());
            }
            driver = new ChromeDriver(chromeOptions);
            driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            driver.manage().window().setSize(new Dimension(1280, 960));

            // 打开首页
            driver.get(url);

            /*===================================== 登录 =====================================*/
            String funcName = "登录";
            try {
                log.info("【{}】正在测试...", funcName);

                // 登录页
                driver.navigate().to(url + "/admin/login");

                Thread.sleep(2000);

                // 填写用户名密码
                driver.findElement(By.id("username")).sendKeys("admin", Keys.TAB, "Admin123");

                // 截图
                loginSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

                // 登录
                driver.findElement(By.xpath("//*[@id=\"root\"]/div[2]/div/div/form/div[3]/button")).click();

                Thread.sleep(2000);

                // 验证功能点是否正常
                driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/header/div[2]/ul/li[2]/div"));

                // 截图
                loginSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                loginSegment.setResult(1);

                log.info("【{}】功能正常！", funcName);
            } catch (NotFoundException e) {
                // 错误截图
                loginSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                log.error("【{}】功能异常！{}", funcName, e.getMessage());

                // 刷新页面
                driver.navigate().refresh();
            }

            /*===================================== 项目列表 =====================================*/
            funcName = "项目列表";
            try {
                log.info("【{}】正在测试...", funcName);

                // 打开项目列表
                driver.navigate().to(url + "/admin/research");

                Thread.sleep(2000);

                // 验证功能点是否正常
                driver.findElement(By.xpath("//*[@id=\"mainContainer\"]/main/div[2]/div[2]/div/div/div/div/div/div/table"));

                // 截图
                searchSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                searchSegment.setResult(1);

                log.info("【{}】功能正常！", funcName);
            } catch (NotFoundException e) {
                // 错误截图
                searchSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                log.error("【{}】功能异常！{}", funcName, e.getMessage());
            }
            return autoInspection;
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

}
