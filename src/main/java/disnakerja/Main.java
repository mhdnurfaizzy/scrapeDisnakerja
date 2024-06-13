package disnakerja;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Specify the path to your extension .crx file
        String extensionPath = "C:\\Users\\HOME\\IdeaProjects\\scrapeDisnakerja\\GIGHMMPIOBKLFEPJOCNAMGKKBIGLIDOM_6_3_0_0.crx";

        // Initialize ChromeOptions
        ChromeOptions options = new ChromeOptions();

        // Add the extension using addExtensions method
        options.addExtensions(new File(extensionPath));

        // Initialize WebDriver with ChromeOptions
        WebDriver driver = new ChromeDriver(options);

        //Close tab adblock
        String mainWindowHandler = driver.getWindowHandle(); // store mainWindowHandler for future references
        //line of code that opens a new TAB / Window
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.numberOfWindowsToBe(2));  //induce WebDriverWait
        Set<String> handles = driver.getWindowHandles();
        Iterator<String> iterator = handles.iterator();
        while (iterator.hasNext())
        {
            String subWindowHandler = iterator.next();
            if (!mainWindowHandler.equalsIgnoreCase(subWindowHandler))
            {
                driver.switchTo().window(subWindowHandler);
            }
        }

        driver.manage().window().maximize();

        // Implicit Wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        // Explicit Wait
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to the Disnakerja website
        driver.get("https://www.disnakerja.com/lokasi/banten/");


        int pageCount = 1;
        // Loop through pages
        while (true) {
            System.out.println("=================================================");
            System.out.printf("===  Scraping page %s  ===\n", pageCount);
            System.out.println("=================================================");

            // Locate the list of job postings
            List<WebElement> jobGroupList = driver.findElements(By.cssSelector("article[class*='gmr-box-content']"));

            for (int i = 0; i < jobGroupList.size(); i++) {
                // Re-locate the job group list to avoid stale element exception
                jobGroupList = driver.findElements(By.cssSelector("article[class*='gmr-box-content']"));

                // Get the specific job group element
                WebElement jobGroup = jobGroupList.get(i);

                // Locate the list of job titles within the job group
                List<WebElement> jobTitleList = jobGroup.findElements(By.cssSelector("div[class*='content-thumbnail']"));

                for (int j = 0; j < jobTitleList.size(); j++) {
                    // Re-locate the job title list to avoid stale element exception
                    jobTitleList = jobGroup.findElements(By.cssSelector("div[class*='content-thumbnail']"));

                    // Get the specific job title element
                    WebElement jobTitleElement = jobTitleList.get(j);

                    // Wait until the job title element is clickable
                    wait.until(ExpectedConditions.elementToBeClickable(jobTitleElement));

                    Thread.sleep(1000);

                    // Use Actions to click the job title element
                    jobTitleElement.click();

                    // Optionally, wait for the detail page to load
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#description")));

                    //Extract job name
                    String jobName = driver.findElement(By.cssSelector(".entry-title")).getText();
                    System.out.println("=================================================");
                    System.out.printf("===  Job Name: %s  \n", jobName);
                    System.out.println("=================================================");

                    // Extract job details
                    String jobDetail = driver.findElement(By.cssSelector("#description")).getText();
                    System.out.println("===  Job Details: ");
                    System.out.println(jobDetail);

                    // Navigate back to the job listing page
                    driver.navigate().back();

                    // Scroll to the job title element
                    JavascriptExecutor jse = (JavascriptExecutor)driver;
                    jse.executeScript("window.scrollBy(0,60)");

                    // Optionally, wait for the listing page to load
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("article[class*='gmr-box-content']")));
                }
            }

            // Attempt to find the next page button
            WebElement nextPageButton = null;
            try {
                nextPageButton = driver.findElement(By.cssSelector("a.next.page-numbers"));
            } catch (Exception e) {
                System.out.println("No more pages available.");
                break;
            }

            // Click on the next page button
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextPageButton);
            wait.until(ExpectedConditions.elementToBeClickable(nextPageButton));
            nextPageButton.click();

            // Increment page count
            pageCount++;

            // Optionally, wait for the next page to load
            Thread.sleep(2000); // Adjust sleep time as necessary
        }

        // Close the browser
        driver.quit();
    }
}
