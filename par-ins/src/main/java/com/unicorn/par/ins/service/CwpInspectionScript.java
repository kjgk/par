package com.unicorn.par.ins.service;

import com.unicorn.par.ins.model.AutoInspection;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "车辆清洗备案网")
public class CwpInspectionScript implements InspectionScript {

    public String getSystemName() {
        return "车辆清洗备案网";
    }

    public AutoInspection doInspection() throws Exception {

        final Long systemId = 540220231499382784L;
        final String url = "http://101.227.180.58/cwp";

        AutoInspection autoInspection = new AutoInspection();
        autoInspection.setSystemId(systemId);
        AutoInspection.Detail loginSegment = new AutoInspection.Detail();
        AutoInspection.Detail searchSegment = new AutoInspection.Detail();
        autoInspection.getDetailList().add(loginSegment);
        autoInspection.getDetailList().add(searchSegment);

        WebDriver driver = null;
        try {
            log.info("初始化chrome浏览器");
            driver = new ChromeDriver();
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
                driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[2]/div/div/div[1]/div/div/div[2]/form/div[2]/div/input"))
                        .sendKeys("admin", Keys.TAB, "123456", Keys.TAB, "_**_", Keys.ENTER);

                Thread.sleep(1000l);

                // 验证功能点是否正常
                driver.findElement(By.xpath("//*[@id=\"header-wrapper\"]/div[2]/div[3]/ul/li[2]/div/a"));

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

            /*===================================== 清洗点搜索 =====================================*/
            funcName = "清洗点搜索";
            try {
                log.info("【{}】正在测试...", funcName);

                // 打开搜索页面
                driver.navigate().to(url);

                // 搜索
                driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[2]/div/div/div[2]/div/div/div[2]/div/section[3]/form/div[1]/input"))
                        .sendKeys("轮胎", Keys.ENTER);

                Thread.sleep(1000l);

                // 滚动页面
                JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                jsExecutor.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[2]/div/div/div[2]/div/div/div[1]/h3")));

                // 验证功能点是否正常
                driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[2]/div/div/div[2]/div/div/div[3]/table"));

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
                driver.close();
            }
        }
    }

}
