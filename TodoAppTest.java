// Getting started: http://docs.seleniumhq.org/docs/03_webdriver.jsp
// API details: https://github.com/SeleniumHQ/selenium#selenium

// Unirest is the recommended way to interact with RESTful APIs in Java
// http://unirest.io/java.html

// runs test against http://crossbrowsertesting.github.io/todo-app.html

import java.net.URL;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static org.junit.Assert.*;


class TodoAppTest {

    static String username = "user%40email.com"; // Your username
    static String authkey = "12345";  // Your authkey
    String testScore = "unset";

    public static void main(String[] args) throws Exception {

        TodoAppTest myTest = new TodoAppTest();

        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("name", " Todo App Example");
        caps.setCapability("build", "1.0");
        caps.setCapability("browserName", "Chrome");            // request the latest version of chrome by default
        caps.setCapability("platform", "Windows 10");           // To specify version, setCapability("version", "desired version")
        caps.setCapability("screen_resolution", "1366x768");
        caps.setCapability("record_video", "true");
        caps.setCapability("record_network", "false");


        RemoteWebDriver driver = new RemoteWebDriver(new URL("http://" + username + ":" + authkey +"@hub.crossbrowsertesting.com:80/wd/hub"), caps);
        System.out.println(driver.getSessionId());

        // we wrap the test in a try catch loop so we can log assert failures in our system
        try {

            // load the page url
            System.out.println("Loading Url");
            driver.get("http://crossbrowsertesting.github.io/todo-app.html");

            // maximize the window - DESKTOPS ONLY
            //System.out.println("Maximizing window");
            //driver.manage().window().maximize();

            System.out.println("Checking Box");
            driver.findElement(By.name("todo-4")).click();

            System.out.println("Checking Another Box");
            driver.findElement(By.name("todo-5")).click();

            // If both clicks worked, then the following List should be have length 2
            List elems = driver.findElements(By.className("done-true"));
            // So we'll assert that this is correct.
            assertEquals(2, elems.size());

            System.out.println("Entering Text");
            driver.findElement(By.id("todotext")).sendKeys("Run your first Selenium Test");
            driver.findElement(By.id("addbutton")).click();

            // Let's also assert that the todo we added is present in the list.
            String spanText = driver.findElementByXPath("/html/body/div/div/div/ul/li[6]/span").getText();
            assertEquals("Run your first Selenium Test", spanText);

            System.out.println("Archiving old todos");
            driver.findElement(By.linkText("archive")).click();

            // If our archive link worked, then the following list should have length 4.
            elems = driver.findElements(By.className("done-false"));
            // So will assert that this is true as well.
            assertEquals(4, elems.size());


            System.out.println("Taking Snapshot");
            myTest.takeSnapshot(driver.getSessionId().toString());

            // if we get to this point, then all the assertions have passed
            // that means that we can set the score to pass in our system
            myTest.testScore = "pass";
        }
        catch(AssertionError ae) {

             // if we have an assertion error, take a snapshot of where the test fails
            // and set the score to "fail"
            String snapshotHash = myTest.takeSnapshot(driver.getSessionId().toString());
            myTest.setDescription(driver.getSessionId().toString(), snapshotHash, ae.toString());
            myTest.testScore = "fail";

        }
        finally {

            System.out.println("Test complete: " + myTest.testScore);

            // here we make an api call to actually send the score
            myTest.setScore(driver.getSessionId().toString(), myTest.testScore);

            // and quit the driver
            driver.quit();
        }
    }

    public JsonNode setScore(String seleniumTestId, String score) throws UnirestException {
        // Mark a Selenium test as Pass/Fail
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .field("action","set_score")
                .field("score", score)
                .asJson();
        return response.getBody();
    }

    public String takeSnapshot(String seleniumTestId) throws UnirestException {
        /*
         * Takes a snapshot of the screen for the specified test.
         * The output of this function can be used as a parameter for setDescription()
         */
        HttpResponse<JsonNode> response = Unirest.post("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .asJson();
        // grab out the snapshot "hash" from the response
        String snapshotHash = (String) response.getBody().getObject().get("hash");

        return snapshotHash;
    }

    public JsonNode setDescription(String seleniumTestId, String snapshotHash, String description) throws UnirestException{
        /*
         * sets the description for the given seleniemTestId and snapshotHash
         */
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots/{snapshotHash}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .routeParam("snapshotHash", snapshotHash)
                .field("description", description)
                .asJson();
        return response.getBody();
    }
}
