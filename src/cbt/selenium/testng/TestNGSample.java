package cbt.selenium.testng;
/*
 * Run from the xml suit file
 */

import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class TestNGSample {

  private String username = "mikeh";
  private String api_key = "";  
  
  private WebDriver driver;  

  @BeforeClass
  @org.testng.annotations.Parameters(value={"os", "browser"})
  public void setUp(String os,String browser) throws Exception {
    DesiredCapabilities capability = new DesiredCapabilities();
    capability.setCapability("os_api_name", os);
    capability.setCapability("browser_api_name", browser);
    capability.setCapability("name", "TestNG-Parallel");
    driver = new RemoteWebDriver(
      new URL("http://" + username + ":" + api_key + "@hub.crossbrowsertesting.com:80/wd/hub"),
      capability);
  }  

  @Test
  public void testSimple() throws Exception {
    driver.get("http://www.google.com");
    System.out.println("Page title is: " + driver.getTitle());
    Assert.assertEquals("Google", driver.getTitle());
    WebElement element = driver.findElement(By.name("q"));
    element.sendKeys("CrossBrowserTesting.com");
    element.submit();
  }

  @AfterClass  
  public void tearDown() throws Exception {  
    driver.quit();  
  }
}
