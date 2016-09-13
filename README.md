## selenium-java

Selenium script examples using Java for CrossBrowserTesting.com

##### Requirements

- Ensure you are using Java 1.8
- Download the latest version of Selenium 2 jar (these scripts have not been tested with Selenium 3 at this time)
- These examples make use the [Unirest HTTP library](http://unirest.io/java.html) for making API calls. Ensure you are using the proper dependencies or download the included unirest jar for your classpath.

### Run a Basic Test

##### Compile on the command line
```
javac -cp selenium-server-standalone-2.53.0.jar:unirest-java-1.4.10-SNAPSHOT-jar-with-dependencies.jar BasicTest.java 
```

##### Run on the command line
```
java -cp selenium-server-standalone-2.53.0.jar:unirest-java-1.4.10-SNAPSHOT-jar-with-dependencies.jar:. BasicTest
```

