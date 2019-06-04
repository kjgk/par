package com.unicorn.par.ins.service;

import com.unicorn.par.ins.model.AutoInspection;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j(topic = "绿化市容信息共享平台")
public class LhsrInspectionScript implements InspectionScript {

    @Value("${auto-inspection-config.lhsr.system-id}")
    private Long systemId;

    @Value("${auto-inspection-config.lhsr.url}")
    private String url;

    public String getSystemName() {
        return "绿化市容信息共享平台";
    }

    public AutoInspection doInspection() throws Exception {

        AutoInspection autoInspection = new AutoInspection();
        autoInspection.setSystemId(systemId);
        AutoInspection.Detail loginSegment = new AutoInspection.Detail();
        AutoInspection.Detail keywordSearchSegment = new AutoInspection.Detail();
        AutoInspection.Detail categorySearchSegment = new AutoInspection.Detail();
        autoInspection.getDetailList().add(loginSegment);
        autoInspection.getDetailList().add(keywordSearchSegment);
        autoInspection.getDetailList().add(categorySearchSegment);

        WebDriver driver = null;
        try {
            log.info("初始化chrome浏览器");
            driver = new ChromeDriver(new ChromeOptions().setHeadless(false));
            driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            driver.manage().window().setSize(new Dimension(1280, 960));

            // 打开首页
            driver.get(url);

            /*===================================== 登录 =====================================*/
            String funcName = "登录";
            try {
                log.info("【{}】正在测试...", funcName);

                // 截图
                loginSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

                // 填写用户名密码并登录
                driver.findElement(By.id("username"))
                        .sendKeys("admin", Keys.TAB, "123456", Keys.ENTER);

                Thread.sleep(1000l);

                // 验证功能点是否正常
                driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/div[2]/ul/li[2]/div/a"));

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

            /*===================================== 关键字搜索 =====================================*/
            funcName = "关键字搜索";
            try {
                log.info("【{}】正在测试...", funcName);

                // 搜索
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div[1]/form/div/div[1]/input"))
                        .sendKeys("绿化", Keys.ENTER);

                Thread.sleep(1000l);

                // 验证功能点是否正常
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div[3]/div"));

                // 截图
                keywordSearchSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                keywordSearchSegment.setResult(1);

                log.info("【{}】功能正常！", funcName);
            } catch (NotFoundException e) {
                // 错误截图
                keywordSearchSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                log.error("【{}】功能异常！{}", funcName, e.getMessage());
            }

            /*===================================== 分类搜索 =====================================*/
            funcName = "分类搜索";
            try {
                log.info("【{}】正在测试...", funcName);

                driver.navigate().to(url + "/index#/search/category");

                Thread.sleep(2000l);

                // 依次展开左侧树节点
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[1]/div/div[2]/div[1]/div/ol/li/div/a"))
                        .click();
                Thread.sleep(8000l);
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[1]/div/div[2]/div[1]/div/ol/li/ol/li[1]/div/a"))
                        .click();
                Thread.sleep(2000l);
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[1]/div/div[2]/div[1]/div/ol/li/ol/li[1]/ol/li[6]/div/div/div"))
                        .click();


                // 设置查询条件并点击查询
                Thread.sleep(5000l);
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div/div[2]/form/div[1]/div/a/div")).click();
                Thread.sleep(1000l);
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div/div[2]/form/div[1]/div/ul/li[12]")).click();
                Thread.sleep(5000l);
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div/div[2]/form/div[2]/div[6]/div[1]/a/div")).click();
                Thread.sleep(1000l);
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div/div[2]/form/div[2]/div[6]/div[1]/ul/li[1]")).click();
                Thread.sleep(1000l);
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div/div[2]/form/div[3]/button")).click();

                Thread.sleep(8000l);

                // 验证功能点是否正常
                driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div/div/div/div[2]/div/div[2]/div"));

                // 截图
                categorySearchSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                categorySearchSegment.setResult(1);

                log.info("【{}】功能正常！", funcName);
            } catch (NotFoundException e) {
                // 错误截图
                categorySearchSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
                log.error("【{}】功能异常！{}", funcName, e.getMessage());
            }

            return autoInspection;
        } finally {
            if (driver != null) {
                driver.close();
            }
        }
    }

}
