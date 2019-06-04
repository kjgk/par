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

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j(topic = "科技创新共享平台")
public class LhsrkjInspectionScript implements InspectionScript {

    private InspectionConfigurationProperties inspectionConfigurationProperties;

    public String getSystemName() {
        return "科技创新共享平台";
    }

    public AutoInspection doInspection() throws Exception {

        Long systemId = inspectionConfigurationProperties.getSystemConfig().get("lhsrkj").getSystemId();
        String url = inspectionConfigurationProperties.getSystemConfig().get("lhsrkj").getUrl();

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

                // 点击登录按钮
                driver.findElement(By.xpath("/html/body/div/div[1]/div/div[3]/a")).click();

                Thread.sleep(2000l);

                // 填写用户名密码
                driver.findElement(By.cssSelector(".ngdialog input[name=\"account\"]")).sendKeys("18501655270", Keys.TAB, "Aa123456");

                // 截图
                loginSegment.getScreenshots().add(((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

                // 登录
                driver.findElement(By.cssSelector(".ngdialog button.submit")).click();

                Thread.sleep(2000l);

                // 验证功能点是否正常
                driver.findElement(By.xpath("/html/body/div/div[1]/div/div[2]/div/a[4]/i"));

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

                List<String> keywords = Arrays.asList("绿化", "垃圾", "行道树", "超市");

                // 打开搜索页面
                driver.navigate().to(url + "/#/search/" + keywords.get(new Random().nextInt(keywords.size())));

                Thread.sleep(2000l);

                // 验证功能点是否正常
                driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div[2]/div/div/div[2]/div[1]/span"));

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
