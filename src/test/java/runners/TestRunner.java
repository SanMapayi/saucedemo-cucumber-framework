package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-report.html"
        },
        monochrome = true,
        tags = "@required or @optional"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
