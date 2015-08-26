package cbt.selenium.junit;
/*
 * Run as a junit test
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@RunWith(Parallelized.class)
public class JUnitParallel {
  private String username = "mikeh";
  private String api_key = "";
  private String os;
  private String browser;

  @Parameterized.Parameters
  public static LinkedList<String[]> getEnvironments() throws Exception {
    LinkedList<String[]> env = new LinkedList<String[]>();

    //define OS's and browsers
    env.add(new String[]{"Win7x64-C2", "IE10"});
    env.add(new String[]{"Win8.1", "Chrome43x64"});

    //add more browsers here

    return env;
  }


  public JUnitParallel(String os_api_name, String browser_api_name) {
    this.os = os_api_name;
    this.browser = browser_api_name;
  }

  private WebDriver driver;
  
  @Before
  public void setUp() throws Exception {  
    DesiredCapabilities capability = new DesiredCapabilities();
    capability.setCapability("os_api_name", os);
    capability.setCapability("browser_api_name", browser);
    capability.setCapability("name", "JUnit-Parallel");
    driver = new RemoteWebDriver(
      new URL("http://" + username + ":"+ api_key + "@hub.crossbrowsertesting.com:80/wd/hub"),
      capability
    );  
  }  

  @Test  
  public void testSimple() throws Exception {  
    driver.get("http://www.google.com");
    String title = driver.getTitle();
    System.out.println("Page title is: " + title);
    WebElement element = driver.findElement(By.name("q"));
    element.sendKeys("CrossBrowserTesting.com");
    element.submit();
    driver = new Augmenter().augment(driver);
    File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    try {
      FileUtils.copyFile(srcFile, new File("Screenshot.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() throws Exception {  
    driver.quit();  
  }
}
