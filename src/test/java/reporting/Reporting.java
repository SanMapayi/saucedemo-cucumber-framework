package reporting;

//Listener Class used to generate extent report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reporting extends TestListenerAdapter
{
    public ExtentHtmlReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest logger;


    public void onStart(ITestContext testContext)
    {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());//time stamp
        String repName="Test-Report-"+timeStamp+".html";

        Path rootDir = Paths.get(System.getProperty("user.dir"));
        Path testOutputDir = rootDir.resolve("test-output");

        // Ensure the test-output directory exists
        try {
            Files.createDirectories(testOutputDir); // Creates the directory if it doesn't exist
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create test-output directory", e);
        }

        Path reportLocation = testOutputDir.resolve(repName);



        htmlReporter = new ExtentHtmlReporter(reportLocation.toFile());//specify location of the report

        Path configPath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "config", "extent-config.xml");
        htmlReporter.loadXMLConfig(configPath.toString());

        extent=new ExtentReports();

        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Host name","localhost");
        extent.setSystemInfo("Environment","QA");
        extent.setSystemInfo("user","Oluwasanmi");

        htmlReporter.config().setDocumentTitle("car checking"); // Tile of report
        htmlReporter.config().setReportName("Functional Test Automation Report"); // name of the report
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP); //location of the chart
        htmlReporter.config().setTheme(Theme.STANDARD);
    }

    public void onTestSuccess(ITestResult tr)
    {
        logger=extent.createTest(tr.getName()); // create new entry in the report
        logger.log(Status.PASS,MarkupHelper.createLabel(tr.getName(),ExtentColor.GREEN)); // send the passed information to the report with GREEN color highlighted
    }

    public void onTestFailure(ITestResult tr)
    {
        logger=extent.createTest(tr.getName()); // create new entry in the report
        logger.log(Status.FAIL,MarkupHelper.createLabel(tr.getName(),ExtentColor.RED)); // send the passed information to the report with GREEN color highlighted
        String rootDir = System.getProperty("user.dir");
        String fileName = tr.getName()+".png";
        Path screenshotPath = Path.of(rootDir, "Screenshots", fileName);


        File f = new File(screenshotPath.toString());

        if(f.exists())
        {
            try {
                logger.fail("Screenshot is below:" + logger.addScreenCaptureFromPath(screenshotPath.toString()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void onTestSkipped(ITestResult tr)
    {
        logger=extent.createTest(tr.getName()); // create new entry in the report
        logger.log(Status.SKIP,MarkupHelper.createLabel(tr.getName(),ExtentColor.ORANGE));
    }

    public void onFinish(ITestContext testContext)
    {
        extent.flush();
    }
}
