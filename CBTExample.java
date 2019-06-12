import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CBTExample {

	public static void main(String[] args) {
		String username = "chase@crossbrowsertesting.com";
		String authkey = "notmyauthkey";

		CBTExample cbt = new CBTExample(username,authkey);

		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("name", "CBTExample");
		caps.setCapability("browserName", "Internet Explorer");
		caps.setCapability("version", "10"); // If this cap isn't specified, it will just get the latest one
		caps.setCapability("platform", "Windows 7 64-Bit");
		caps.setCapability("screenResolution", "1366x768");
		caps.setCapability("record_video", "true");
		caps.setCapability("record_network", "false");
		
		RemoteWebDriver driver = null;
		String score = null;

		try {
			driver = new RemoteWebDriver(cbt.getHubUrl(), caps);
			cbt.setSessionId(driver.getSessionId().toString());
			driver.get("https://www.crossbrowsertesting.com");
			cbt.takeSnapshot();
			cbt.setDescription("CBT Test");

			// depending on whether the value of the title is correct,
			// set the score to pass or fail via CBT's API
			if (driver.getTitle().equals("Cross Browser Testing Tool: 1500+ Real Browsers & Devices"))
				score = "pass";
			 else
				score = "fail";
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			if (driver != null)
				driver.quit();
			cbt.setScore(score);
		}
	}

	private String sessionId,username,authkey;
	private String apiUrl = "crossbrowsertesting.com/api/v3/selenium";
	public CBTExample(String username, String authkey) {
		// for java URL's must be character incoded. If you use
		// your email, let's replace that character

		if (username.contains("@")) {
			username = username.replace("@", "%40");
		}
		this.username = username;
		this.authkey = authkey;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setScore(String score) {
		String url = "https://" + apiUrl + "/" + this.sessionId;
		String payload = "{\"action\": \"set_score\", \"score\": \"" + score + "\"}";
		makeRequest("PUT", url,payload);
	}

	public void takeSnapshot() {
		if (this.sessionId != null) {
			String url = "https://" + apiUrl + "/" + this.sessionId + "/snapshots";
			String payload = "{\"selenium_test_id\": \"" + this.sessionId + "\"}";
			makeRequest("POST",url,payload);
		}
	}

	public void setDescription(String desc) {
		String url = "https://" + apiUrl + "/" + this.sessionId;
		String payload = "{\"action\": \"set_description\", \"description\": \"" + desc + "\"}";
		makeRequest("PUT", url,payload);
	}

	private void makeRequest(String requestMethod, String apiUrl, String payload) {
		URL url;
		String auth = "";

        if (username != null && authkey != null) {
            auth = "Basic " + Base64.encodeBase64String((username+":" + authkey).getBytes());
        }
        try {
            url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", auth);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();
            conn.getResponseMessage();
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
	}

	public URL getHubUrl() {
		URL hubUrl = null;
		try {
			hubUrl = new URL("http://" + username + ":" + authkey + "@hub.crossbrowsertesting.com:80/wd/hub");
		} catch (MalformedURLException e) {
			System.out.println("Invalid HUB URL");
		}
		return hubUrl;
	}
}
