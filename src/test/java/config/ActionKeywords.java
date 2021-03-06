package config;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import properties.General;
import testCasesWeb.DriverScript;
import utility.DriverException;
import utility.Log;
import utility.SeleniumRobot;
import utility.SeleniumUtils;

public class ActionKeywords extends Log {

    public static WebDriver driver;
    public static AppiumDriver<MobileElement> appiumDriver;
    public static MobileElement mobileElement;
    public static WebElement element;
    public static ReadObject objects = new ReadObject();
    private static SeleniumRobot robot = new SeleniumRobot();
    public static Properties allProperties;
    public static General objProps = new General();
    public static String SSDate;
    static String file;
    static String nodeURL;
    static DesiredCapabilities capabilities;
    private static JavascriptExecutor js;
    private static WebDriverWait waitVar;
    static String pageLoadStatus = null;
    private static int students;

    /**
     * Common functions/Actions to perform
     *
     * @param p
     * @param object
     * @param data
     * @return
     * @throws IOException
     * @throws Exception
     * @throws TimeoutException
     * @throws NoSuchElementException
     */

	/*####################################### Common Comands ############################################
     *
	 * Methods with all the common commands. 
	 * 
 	####################################################################################################*/

    //Grid method
    public static void externalComputer(Properties p, String object, String data, String Link) {
        //Browser,IPnode:port, example data = firefox,192.168.1.4:5566
        try {
            String gridInfo = data; //introduce the string as Browser,IPnode:port, example data = firefox,192.168.1.4:5566 in excel sheet
            String[] capability = gridInfo.split(",");
            String browser = capability[1]; //Browser
            String platform = capability[0]; //Platform

            String ipnodeport = platform; //IPnode:Port
            nodeURL = "http://" + ipnodeport + "/wd/hub";


            info("Establishing connection with: " + ipnodeport);

            if (browser.equalsIgnoreCase("firefox")) {
                capabilities = DesiredCapabilities.firefox();
                capabilities.setBrowserName("firefox");
                info("Mozilla browser started in " + ipnodeport);
                objProps.setRemark("Mozilla browser started in " + ipnodeport);
            } else if (browser.equalsIgnoreCase("chrome")) {
                capabilities = DesiredCapabilities.chrome();
                capabilities.setBrowserName("chrome");
                info("Chrome browser started" + ipnodeport);
                objProps.setRemark("Chrome browser started" + ipnodeport);
            } else if (browser.equalsIgnoreCase("ie")) {
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setBrowserName("ie");
                info("Internet Explorer browser started" + ipnodeport);
                objProps.setRemark("Internet Explorer browser started" + ipnodeport);
            } else if (browser.equalsIgnoreCase("opera")) {
                capabilities = DesiredCapabilities.operaBlink();
                capabilities.setBrowserName("opera");
                info("Opera browser started" + ipnodeport);
                objProps.setRemark("Opera browser started" + ipnodeport);
            } else {
                throw new DriverException("Invalid browser option!");
            }

            if (platform.equalsIgnoreCase("windows")) {
                capabilities.setPlatform(Platform.WINDOWS);
                info("Testing on Windows platform");
                objProps.setRemark("Testing on Windows platform");
            } else if (platform.equalsIgnoreCase("linux")) {
                capabilities.setPlatform(Platform.LINUX);
                info("Testing on Linux platform");
                objProps.setRemark("Testing on Linux platform");
            } else if (platform.equalsIgnoreCase("mac")) {
                capabilities.setPlatform(Platform.MAC);
                info("Testing on Mac platform");
                objProps.setRemark("Testing on Mac platform");
            } else {
                throw new DriverException("Invalid platform selection.");
            }

            driver = new RemoteWebDriver(new URL(nodeURL), capabilities);
            int implicitWaitTime = (10);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
            //modify, add set/get position and size cases
        } catch (Exception e) {
            //Set the value of result variable to false
            new DriverException("Not able to open the selected browser, review your configuration.", e);
        }
    }

    //Grid method
    public static void mobileBrowser(Properties p, String object, String data, String Link) {
        //example data = android, device_name, firefox,192.168.1.4:5566
        try {
            String mobileInfo = data;
            String[] capability = mobileInfo.split(",");
            String platform = capability[0];
            String deviceName = capability[1];
            String browser = capability[2];
            String host = capability[3];

            nodeURL = "http://" + host + "/wd/hub";
            DesiredCapabilities capabilities = new DesiredCapabilities();
            info("Establishing connection with: " + host + " using platform " + platform);

            // we need to define platform name
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, platform);
            // Set the device name as well (you can give any name)
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);

            switch (browser.toLowerCase()){
                case "chrome":
                    capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, BrowserType.CHROME);
                    //Set ChromeDriver location
                    capabilities.setCapability("chromedriverExecutable",Constants.CHROME_MOBILE_DRIVER_PATH + "chromedriver34.exe");
                    info("Chrome browser started");
                    break;
                case "firefox":
                    capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, BrowserType.FIREFOX);
                    info("Firefox browser started");
                    break;
            }

            driver = new AndroidDriver<MobileElement>(new URL(nodeURL), capabilities);
            int implicitWaitTime = (10);
            driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
            //modify, add set/get position and size cases
        } catch (Exception e) {
            //Set the value of result variable to false
            new DriverException("Not able to open the selected browser, review your configuration.", e);
        }
    }

    //Open browser driver
    public static boolean openBrowser(Properties p, String object, String data, String Link) throws DriverException, IOException {
        allProperties = objects.getObjectProperties();
        info("Opening Browser");

        String getOS = System.getProperty("os.name").toLowerCase();
        String osName = "";
        if (getOS.contains("mac")) {
            osName = "mac";
        } else if (getOS.contains("win")) {
            osName = "windows";
        } else if (getOS.contains("nix") || getOS.contains("nux") || getOS.contains("aix")) {
            osName = "linux";
        }

        try {
            switch (data.toLowerCase()) {
                case "chrome":
                    if (osName.equals("windows")) {
                        System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER + ".exe");
                    } else {
                        System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER);
                    }
                    driver = new ChromeDriver();
                    info("Chrome browser started");
                    objProps.setRemark("Chrome browser started");
                    break;
                case "ie":
                    if (osName.equals("windows")) {
                        System.setProperty("webdriver.ie.driver", Constants.IE_DRIVER + ".exe");
                    }
                    driver = new InternetExplorerDriver();
                    info("IE browser started");
                    objProps.setRemark("IE browser started");
                    break;
                case "firefox":
                    if (osName.equals("windows")) {
                        System.setProperty("webdriver.gecko.driver", Constants.FIREFOX_DRIVER + ".exe");
                    } else {
                        System.setProperty("webdriver.gecko.driver", Constants.FIREFOX_DRIVER);
                    }
                    driver = new FirefoxDriver();
                    info("Mozilla browser started");
                    objProps.setRemark("Mozilla browser started");
                    break;
                case "safari":
                    if (osName.equals("mac")) {
                        driver = new SafariDriver();
                        info("Safari browser started");
                        objProps.setRemark("Safari browser started");
                    }
                    break;
                case "edge":
                    if (osName.equals("windows")) {
                        System.setProperty("webdriver.edge.driver", Constants.EDGE_DRIVER + ".exe");
                        driver = new EdgeDriver();
                        info("Edge browser started");
                        objProps.setRemark("Edge browser started");
                    }
                    break;
                default:
                    throw new DriverException("Invalid browser selection.");
            }

            driver.manage().window().maximize();
            //modify, add set/get position and size cases
            return true;
            //This block will execute only in case of an exception
        } catch (Exception e) {
            //Set the value of result variable to false
            new DriverException("Not able to open the selected browser.", e);
            return false;
        }
    }

    //Sync web page
    public static boolean sync(Properties p, String object, String data, String Link) throws DriverException {
        try {
            objProps.setRemark("Waiting for Page to Load");
            info("Waiting for Page to Load");
            driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
            /*do {
                js = (JavascriptExecutor) driver;
                pageLoadStatus = (String) js.executeScript("return document.readyState");
            } while (!pageLoadStatus.equals("complete"));*/
            return true;
        } catch (Exception e) {
            new DriverException(driver, "navigateError_", "Not able to navigate.", e);
            return false;
        }
    }

    //Get url of application
    public static boolean navigate(Properties p, String object, String data, String Link) throws DriverException {
        try {
            data = (data.startsWith("http://")) ? data : (data.startsWith("https://") ? data : "http://" + data);
            objProps.setRemark("Navigating to URL: " + data);
            info("Navigating to URL: " + data);
            driver.navigate().to(data);
            do {
                js = (JavascriptExecutor) driver;
                pageLoadStatus = (String) js.executeScript("return document.readyState");
            } while (!pageLoadStatus.equals("complete"));
            return true;
        } catch (Exception e) {
            new DriverException(driver, "navigateError_", "Not able to navigate.", e);
            return false;
        }
    }

    //Navigating back in the web page
    public static boolean goPageBack(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Navigating Back...");
            info("Navigating Back...");
            driver.navigate().back();
            do {
                js = (JavascriptExecutor) driver;
                pageLoadStatus = (String) js.executeScript("return document.readyState");
            } while (!pageLoadStatus.equals("complete"));
            return true;
        } catch (Exception e) {
            new DriverException(driver, "clickError_", "Not able to return into the previous page", e);
            return false;
        }
    }

    //Navigating forward in the web page
    public static boolean goPageForward(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Navigating Forward...");
            info("Navigating Forward...");
            driver.navigate().forward();
            do {
                js = (JavascriptExecutor) driver;
                pageLoadStatus = (String) js.executeScript("return document.readyState");
            } while (!pageLoadStatus.equals("complete"));
            return true;
        } catch (Exception e) {
            new DriverException(driver, "clickError_", "Not able to go forward", e);
            return false;
        }
    }

    //Refreshing the web page
    public static boolean refreshPage(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Refreshing the page...");
            info("Refreshing the page...");
            driver.navigate().refresh();
            do {
                js = (JavascriptExecutor) driver;
                pageLoadStatus = (String) js.executeScript("return document.readyState");
            } while (!pageLoadStatus.equals("complete"));
            return true;
        } catch (Exception e) {
            new DriverException(driver, "clickError_", "Not able to refresh the page", e);
            return false;
        }
    }

    //Perform click
    public static boolean click(Properties p, String object, String data, String Link) {
        if(data.equalsIgnoreCase("no")){return true;}
        try {
            objProps.setRemark("Clicking on Webelement " + object);
            info("Clicking on Webelement " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            element.click();
            return true;
        } catch (Exception e) {
            new DriverException(driver, "clickError_", "Not able to click on element.", e);
            return false;
        }
    }

    //Set text on control
    public static boolean input(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Entering the text: " + data + " in " + object);
            info("Entering the text: " + data + " in " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            element.sendKeys(data);
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "inputError_", "Not able to type text.", e);
            } else {
                new DriverException(driver, "inputError_", "Not able to type text.", e);
            }
            return false;
        }
    }

    //Set text on control
    public static boolean clear(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Cleaning: " + object);
            info("Cleaning: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            element.clear();
            return true;
        } catch (Exception e) {
            new DriverException(driver, "clearError_", "Not able to clear text.", e);
            return false;
        }
    }

    //Wait
    public static boolean waitFor(Properties p, String object, String data, String Link) {
        try {
            double value_timedouble = Double.parseDouble(data);
            int value_time = (int) value_timedouble;
            Thread.sleep(value_time * 1000);
            objProps.setRemark("Wait for " + data + " seconds");
            info("Wait for " + data + " seconds");
            return true;
        } catch (TimeoutException | InterruptedException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for Alert
    public static boolean wairForAlert(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for alert to be present");
            info("Waiting for alert to be present");
            waitVar.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "wairForAlertError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for element clickable
    public static boolean waitForElementClickable(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object);
            info("Waiting for element: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            waitVar.until(ExpectedConditions.elementToBeClickable(findElement(p, object)));
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitForElementClickableError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for element to be selected
    public static boolean waitForElementToBeSelected(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object);
            info("Waiting for element: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            waitVar.until(ExpectedConditions.elementToBeSelected(findElement(p, object)));
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitForElementToBeSelectedError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for frame available and switch to it
    public static boolean waitForFrameAndSwitchToIt(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object);
            info("Waiting for element: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            try {
                waitVar.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(object));
            } catch (NoSuchFrameException e) {
                waitVar.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(findElement(p, object)));
            }
            return true;
        } catch (NoSuchFrameException e) {
            new DriverException(driver, "NoFrameAvailableError_", "No Frame Available", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for invisibility of element
    public static boolean waitInvisibilityOfElement(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object);
            info("Waiting for element: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            waitVar.until(ExpectedConditions.invisibilityOfElementLocated(findElementBy(p, object)));
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitInvisibilityOfElementError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for presence of all elements
    public static boolean waitForPresenceOfAllElement(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object);
            info("Waiting for element: " + object);
            waitVar.until(ExpectedConditions.presenceOfAllElementsLocatedBy(findElementBy(p, object)));
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitForPresenceOfAllElementError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for presence of element
    public static boolean waitForPresenceOfElement(Properties p, String object, String data, String Link) throws DriverException {
        try {
            // Modified  27/09/2017 Gilberto Sánchez Mares
            if (data.equalsIgnoreCase("ignore")) {
                objProps.setRemark(" ");
                info(" ");
                return true;
            }
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for presence of element: " + object);
            info("Waiting for presence of element: " + object);
            waitVar.until(ExpectedConditions.presenceOfElementLocated(findElementBy(p, object)));
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitForPresenceOfElementError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for text to be present
    public static boolean waitForTextPresent(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object + " and text: " + data);
            info("Waiting for element: " + object + " and text: " + data);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            waitVar.until(ExpectedConditions.textToBePresentInElementLocated(findElementBy(p, object), data));
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitForTextPresentError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for title contains
    public static boolean waitForTitleContains(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for title: " + data);
            info("Waiting for title: " + data);
            waitVar.until(ExpectedConditions.titleContains(data));
            return true;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for URL match
    public static boolean waitForUrlMatch(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for Url: " + data);
            info("Waiting for Url: " + data);
            waitVar.until(ExpectedConditions.urlToBe(data));
            return true;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for all elements visible
    public static boolean waitForAllElementsVisible(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object);
            info("Waiting for element: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            waitVar.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(findElementBy(p, object)));
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitForAllElementsVisibleError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Explicit wait for element visible
    public static boolean waitForElementVisible(Properties p, String object, String data, String Link) throws DriverException {
        try {
            waitVar = new WebDriverWait(driver, 20);
            objProps.setRemark("Waiting for element: " + object);
            info("Waiting for element: " + object);
            waitVar.until(ExpectedConditions.visibilityOfElementLocated(findElementBy(p, object)));
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            return true;
        } catch (NoSuchElementException e) {
            new DriverException(driver, "waitForElementVisibleError_", "Not element to wait.", e);
            return false;
        } catch (TimeoutException e) {
            new DriverException("Error waiting...", e);
            return false;
        }
    }

    //Close browser
    public static boolean closeBrowser(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Closing the browser");
            info("Closing the browser");
            driver.quit();
            return true;
        } catch (Exception e) {
            new DriverException("Not able to close the browser", e);
            return false;
        }
    }

    //Gett text from text boxes
    public static boolean getTextBoxText(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            info("Getting the text: " + data + " from: " + object);
            SeleniumUtils.fnHighlightMe(driver, element);
            String text = element.getAttribute("value");
            if (data.equals("")) {
                objProps.setRemark("Getting the text: " + text);
                info("Getting the text: " + text);
            } else if (!data.equals("") && data.equals(text)) {
                objProps.setRemark("The text: " + text + " is equals to: " + data);
                info("The text: " + text + " is equals to: " + data);
            } else if (!data.equals("") && !data.equals(text)) {
                objProps.setRemark("The text: " + text + " is not equals to: " + data);
                info("The text: " + text + " is not equals to: " + data);
                new DriverException("The text: " + text + " is not equals to: " + data);
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "getTextBoxTextError_", "Not able to get text.", e);
            return false;
        }
    }

    //You can use submit() on any element within the form, not just on the submit button itself.
    public static boolean submitForm(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Form Submitted");
            info("Form Submitted");
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            element.submit();
            return true;
        } catch (Exception e) {
            new DriverException(driver, "submitFormError_", "Not able to submit the form.", e);
            return false;
        }
    }

    //Switching between frames
    public static boolean switchToFrame(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Switch to Frame: " + object);
            info("Switch to Frame: " + object);
            try {
                driver.switchTo().frame(object);
            } catch (NoSuchFrameException fe) {
                driver.switchTo().frame(findElement(p, object));
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "switchToFrameError_", "Not able to switch between frames.", e);
            return false;
        }
    }

    //Switching between windows
    public static boolean switchToWindow(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Switch to window: " + data);
            info("Switch to window: " + data);
            driver.switchTo().window(data); //in data include the name of the window
            return true;
        } catch (Exception e) {
            new DriverException(driver, "switchToWindowError_", "Not switch to window.", e);
            return false;
        }
    }

    //Scroll up(0, -250)/down(0,250)
    public static boolean scrollWindow(Properties p, String object, String data, String Link) {
        try {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("window.scrollBy(" + data + ")", "");
            objProps.setRemark("Scroll executed!");
            info("Scroll executed!");
            return true;
        } catch (TimeoutException e) {
            new DriverException("Timeout, Not able to scroll down/up.", e);
            return false;
        } catch (Exception e) {
            new DriverException("Not able to scroll down/up.", e);
            return false;
        }
    }

    //Taking snapshot
    public static boolean snapShot(Properties p, String object, String data, String Link) {
        element = (object.equals("")) ? null : findElement(p, object);
        SSDate = SeleniumUtils.createFolder(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()).toString());
        file = Constants.ScreenShots_Folder + data + "_CustomScreenShot\\" + SSDate.toString() + ".png";
        TakesScreenshot scrShot = null;
        File SrcFile = null;
        try {
            if (element != null) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='4px solid yellow'", element);
                //Convert web driver object to TakeScreenshot
                scrShot = ((TakesScreenshot) driver);
                SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
                //Move image file to new destination
                File DestFile = new File(file);
                //Copy file at destination
                FileUtils.copyFile(SrcFile, DestFile);

                //***********************************************
//					ExcelUtils.hyperlinkScreenshot(file); -- Not able to create hyperlink: Address of hyperlink must be a valid URI
                js.executeScript("arguments[0].style.border=''", element);
            } else {
                scrShot = ((TakesScreenshot) driver);
                SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            }

            File DestFile = new File(file);
            FileUtils.copyFile(SrcFile, DestFile);
//			ExcelUtils.hyperlinkScreenshot(file); -- Not able to create hyperlink: Address of hyperlink must be a valid URI
            info("Custom Snapshot took!");
            objProps.setRemark("Custom Snapshot took!");
            info("Snapshot created in: " + file);
            objProps.setCustomScreenShot(file);
            return true;
        } catch (Exception e) {
            objProps.setRemark("Unable to take screenshots");
            new DriverException("Unable to take screenshots", e);
            return false;
        }
    }

    //Robot
    public static boolean robot(Properties p, String object, String data, String Link) {
        try {
            info("Emulating keyboard event");
            objProps.setRemark("Emulating keyboard event");
            if (data.toLowerCase().contains("word")) {
                String gridInfo = data;
                String[] capability = gridInfo.split(":");
                String word = capability[1]; //Browser

                char[] userChar = word.toCharArray();
                for (char userLetter : userChar) {
                    robot.SimulateStandardShortcut(Character.toString(userLetter));
                }
            } else {
                robot.SimulateStandardShortcut(data);
            }
            return true;
        } catch (Exception e) {
            new DriverException("Not able to perform keyboard events.", e);
            return false;
        }
    }

    //Execute JavaScript Code
    public static boolean executeJavaScript(Properties p, String object, String data, String Link) {
        try {
            info("Executing JavaScript");
            objProps.setRemark("Executing JavaScript");
            element = (p != null || !element.equals("")) ? findElement(p, object) : null;
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            if (element != null) {
                jse.executeScript(data, element);
            } else {
                jse.executeScript(data);
            }
            return true;
        } catch (Exception e) {
            new DriverException("Unable to execute JavaScript", e);
            return false;
        }
    }

    //Accept alert
    public static boolean acceptAlert(Properties p, String object, String data, String Link) {
        try {
            driver.switchTo().alert().accept();
            objProps.setRemark("Alert window accepted");
            info("Alert window accepted");
            return true;
        } catch (Exception e) {
            new DriverException(driver, "acceptAlertError_", "Not able to accept the alert", e);
            return false;
        }
    }

    //Dismiss alerts
    public static boolean dismissAlert(Properties p, String object, String data, String Link) {
        try {
            driver.switchTo().alert().dismiss();
            objProps.setRemark("Alert window dismissed");
            info("Alert window dismissed");
            return true;
        } catch (Exception e) {
            new DriverException(driver, "dismissAlertError_", "Not able to dismiss alert", e);
            return false;
        }
    }

    //Getting text from alert
    public static boolean getTextFromAlert(Properties p, String object, String data, String Link) {
        try {
            if (data.isEmpty()) {
                objProps.setRemark("Alert window text is: " + driver.switchTo().alert().getText());
                info("Alert window text is: " + driver.switchTo().alert().getText());
            } else if (!data.isEmpty() && driver.switchTo().alert().getText().equals(data)) {
                objProps.setRemark(driver.switchTo().alert().getText() + " is equal to: " + data);
                info(driver.switchTo().alert().getText() + " is equal to: " + data);
            } else {
                new DriverException(driver, "AlertError_", driver.switchTo().alert().getText() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "AlertError_", "Not able to get text from alert", e);
            return false;
        }
    }

    //Set text to alert
    public static boolean setTextToAlert(Properties p, String object, String data, String Link) {
        try {
            driver.switchTo().alert().sendKeys(data);
            objProps.setRemark("Alert window set text is: " + data);
            info("Alert window set text is: " + data);
            return true;
        } catch (Exception e) {
            new DriverException(driver, "AlertError_", "Not able to set text to alert", e);
            return false;
        }
    }

    //Select element by text from drop down box
    public static boolean selectByTextFromList(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Selecting " + data + " from DropDown box: " + object);
            info("Selecting " + data + " from DropDown box: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            new Select(findElement(p, object)).selectByVisibleText(data);
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "dropdownError_", "Not able to select the element from dropdown.", e);
            } else {
                new DriverException(driver, "dropdownError_", "Not able to select the element from dropdown.", e);
            }
            return false;
        }
    }

    //Select element by value from drop down box
    public static boolean selectByValueFromList(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Selecting " + data + " from DropDown box: " + object);
            info("Selecting " + data + " from DropDown box: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            new Select(findElement(p, object)).selectByValue(data);
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "dropdownError_", "Not able to select the element from dropdown.", e);
            } else {
                new DriverException(driver, "dropdownError_", "Not able to select the element from dropdown.", e);
            }
            return false;
        }
    }

    //Select element by index from drop down box
    public static boolean selectByIndexFromList(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Selecting " + data + " from DropDown box: " + object);
            info("Selecting " + data + " from DropDown box: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            new Select(findElement(p, object)).selectByIndex(Integer.parseInt(data));
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "dropdownError_", "Not able to select the element from dropdown.", e);
            } else {
                new DriverException(driver, "dropdownError_", "Not able to select the element from dropdown.", e);
            }
            return false;
        }
    }

    //Deselect element by text from drop down box
    public static boolean deselectByTextFromList(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Deselecting " + data + " from DropDown box: " + object);
            info("Deselecting " + data + " from DropDown box: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            new Select(findElement(p, object)).deselectByVisibleText(data);
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "deselectError_", "Not able to deselect element from dropdown.", e);
            } else {
                new DriverException(driver, "deselectError_", "Not able to deselect element from dropdown.", e);
            }
            return false;
        }
    }

    //Deselect element by value from drop down box
    public static boolean deselectByValueFromList(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Deselecting " + data + " from DropDown box: " + object);
            info("Deselecting " + data + " from DropDown box: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            new Select(findElement(p, object)).deselectByValue(data);
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "deselectError_", "Not able to deselect element from dropdown.", e);
            } else {
                new DriverException(driver, "deselectError_", "Not able to deselect element from dropdown.", e);
            }
            return false;
        }
    }

    //Deselect element by index from drop down box
    public static boolean deselectByIndexFromList(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Deselecting " + data + " from DropDown box: " + object);
            info("Deselecting " + data + " from DropDown box: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            new Select(findElement(p, object)).deselectByIndex(Integer.parseInt(data));
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "deselectError_", "Not able to deselect element from dropdown.", e);
            } else {
                new DriverException(driver, "deselectError_", "Not able to deselect element from dropdown.", e);
            }
            return false;
        }
    }

    //Deselect all elements from drop down box
    public static boolean deselectAllFromList(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Deselecting All items from DropDown box: " + object);
            info("Deselecting All items from DropDown box: " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            new Select(findElement(p, object)).deselectAll();
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "deselectError_", "Not able to deselect elements from dropdown.", e);
            } else {
                new DriverException(driver, "deselectError_", "Not able to deselect element from dropdown.", e);
            }
            return false;
        }
    }

    //Get class from an element
    public static boolean getElementClass(Properties p, String object, String data, String Link) {
        try {
            WebElement element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (data.isEmpty()) {
                objProps.setRemark("Getting the class: " + element.getClass() + " from " + object);
                info("Getting the class: " + element.getClass() + " from " + object);
            } else if (data != null && element.getClass().equals(data)) {
                objProps.setRemark(element.getClass() + " is equal to: " + data);
                info(element.getClass() + " is equal to: " + data);
            } else {
                new DriverException(driver, element, "getElementClassError_", element.getClass() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            DriverScript.bResult = false;
            objProps.setRemark("Something went wrong getting information.");
            new DriverException(driver, "getElementClassError_", "Something went wrong getting element class.", e);
            return false;
        }
    }

    //Get location from an element
    public static boolean getElementLocation(Properties p, String object, String data, String Link) {
        try {
            WebElement element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (data.isEmpty()) {
                objProps.setRemark("Getting the location: " + element.getLocation() + " of " + object);
                info("Getting the location: " + element.getLocation() + " of " + object);
            } else if (data != null && element.getLocation().equals(data)) {
                objProps.setRemark(element.getLocation() + " is equal to: " + data);
                info(element.getLocation() + " is equal to: " + data);
            } else {
                new DriverException(driver, element, "getElementLocationError_", element.getLocation() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            DriverScript.bResult = false;
            objProps.setRemark("Something went wrong getting information.");
            new DriverException(driver, "getElementLocationError_", "Something went wrong getting element location.", e);
            return false;
        }
    }

    //Get class, location, size, tagname, text, attribute or css value from an element
    public static boolean getElementSize(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (data.isEmpty()) {
                objProps.setRemark("Getting the size: " + element.getSize() + " of " + object);
                info("Getting the size: " + element.getSize() + " of " + object);
            } else if (data != null && element.getSize().equals(data)) {
                objProps.setRemark(element.getSize() + " is equal to: " + data);
                info(element.getSize() + " is equal to: " + data);
            } else {
                new DriverException(driver, element, "getElementSizeError_", element.getSize() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "getElementSizeError_", "Something went wrong getting element size.", e);
            return false;
        }
    }

    //Get tagname from an element
    public static boolean getElementTagname(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (data.isEmpty()) {
                objProps.setRemark("Getting the tag name: " + element.getTagName() + " from " + object);
                info("Getting the tag name: " + element.getTagName() + " from " + object);
            } else if (data != null && element.getTagName().equals(data)) {
                objProps.setRemark(element.getTagName() + " is equal to: " + data);
                info(element.getTagName() + " is equal to: " + data);
            } else {
                objProps.setRemark(element.getTagName() + " is not equal to: " + data);
                new DriverException(driver, element, "getElementTagnameError_", element.getTagName() + " is not equal to: " + data);
                DriverScript.bResult = false;
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "getElementTagnameError_", "Something went wrong getting element tagname", e);
            return false;
        }
    }

    //Get text from an element
    public static boolean getElementText(Properties p, String object, String data, String Link) {
        // Modified 27/09/2017, Gilberto Sánchez Mares
        if (data.equalsIgnoreCase("ignore") || data.equals("")) {
            objProps.setRemark(" ");
            info(" ");
            return true;
        }
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (data.isEmpty()) {
                objProps.setRemark("Getting the text: " + element.getText() + " from " + object);
                info("Getting the text: " + element.getText() + " from " + object);
            } else if (!data.equals("") && element.getText().equals(data)) {
//			else if (!data.equals("") && element.getText().equals(data)){
                objProps.setRemark(element.getText() + " is equal to: " + data);
                info(element.getText() + " is equal to: " + data);
            } else {
                new DriverException(driver, element, "getElementTextError_", element.getText() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "getElementTextError_", "Something went wrong getting element text", e);
            return false;
        }
    }

    //Get attribute from an element
    public static boolean getElementAttribute(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            objProps.setRemark("Getting the text: " + element.getAttribute(data) + " from " + object);
            info("Getting the text: " + element.getAttribute(data) + " from " + object);
            return true;
        } catch (Exception e) {
            new DriverException(driver, "getElementAttributeError_", "Something went wrong getting element attribute", e);
            return false;
        }
    }

    //Get css value from an element
    public static boolean getElementCSS(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            objProps.setRemark("Getting the text: " + element.getCssValue(data) + " from " + object);
            info("Getting the text: " + element.getCssValue(data) + " from " + object);
            return true;
        } catch (Exception e) {
            new DriverException(driver, "getElementCSSError_", "Something went wrong getting element css value", e);
            return false;
        }
    }

    //Get class from driver
    public static boolean getPageClass(Properties p, String object, String data, String Link) {
        try {
            if (data.isEmpty()) {
                objProps.setRemark("Getting the class: " + driver.getClass());
                info("Getting the class: " + driver.getClass());
            } else if (data != null && driver.getClass().equals(data)) {
                objProps.setRemark(driver.getClass() + " is equal to: " + data);
                info(driver.getClass() + " is equal to: " + data);
            } else {
                new DriverException(driver, "getPageClass_", driver.getClass() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException("Something went wrong getting page class", e);
            return false;
        }
    }

    //Get current URL
    public static boolean getPageCurrentUrl(Properties p, String object, String data, String Link) {
        try {
            if (data.isEmpty()) {
                objProps.setRemark("Getting the current URL: " + driver.getCurrentUrl());
                info("Getting the current URL: " + driver.getCurrentUrl());
            } else if (data != null && driver.getCurrentUrl().equals(data)) {
                objProps.setRemark(driver.getCurrentUrl() + " is equal to: " + data);
                info(driver.getCurrentUrl() + " is equal to: " + data);
            } else {
                new DriverException(driver, "getPageCurrentUrlError_", driver.getCurrentUrl() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException("Something went wrong getting page current url", e);
            return false;
        }
    }

    //Get page source
    public static boolean getPageSource(Properties p, String object, String data, String Link) {
        try {
            if (data.isEmpty()) {
                objProps.setRemark("Getting the page source: " + driver.getPageSource());
                info("Getting the page source: " + driver.getPageSource());
            } else if (data != null && driver.getPageSource().equals(data)) {
                objProps.setRemark(driver.getPageSource() + " is equal to: " + data);
                info(driver.getPageSource() + " is equal to: " + data);
            } else {
                new DriverException(driver.getPageSource() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException("Something went wrong getting page source", e);
            return false;
        }
    }

    //Get class, current URL, page source, title or window handle(s) from driver
    public static boolean getPageTitle(Properties p, String object, String data, String Link) {
        try {
            if (data.isEmpty()) {
                objProps.setRemark("Getting the page title: " + driver.getTitle());
                info("Getting the page title: " + driver.getTitle());
            } else if (data != null && driver.getTitle().equals(data)) {
                objProps.setRemark(driver.getTitle() + " is equal to: " + data);
                info(driver.getTitle() + " is equal to: " + data);
            } else {
                new DriverException(driver.getTitle() + " is not equal to: " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException("Something went wrong getting page title", e);
            return false;
        }

    }

    //Get window handle from driver
    public static boolean getWindowHandle(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Getting the window handle: " + driver.getWindowHandle());
            info("Getting the window handle: " + driver.getWindowHandle());
            return true;
        } catch (Exception e) {
            new DriverException("Something went wrong getting window handle", e);
            return false;
        }
    }

    //Get window handles from driver
    public static boolean getWindowHandles(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Getting the window handles: " + driver.getWindowHandles());
            info("Getting the window handles: " + driver.getWindowHandles());
            return true;
        } catch (Exception e) {
            new DriverException("Something went wrong getting windows handle", e);
            return false;
        }
    }

    //Condition element is selected
    public static boolean elementIsSelected(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (element.isSelected()) {
                objProps.setRemark(object + " is Selected");
                info(object + " is Selected");
            } else {
                new DriverException(driver, element, "elementIsSelectedError_", object + " is not Selected");
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "elementIsSelectedError_", "Something went wrong evaluating the condition stablished.", e);
            return false;
        }
    }

    //Condition element is displayed
    public static boolean elementIsDisplayed(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (element.isDisplayed()) {
                objProps.setRemark(object + " is Displayed");
                info(object + " is Displayed");
            } else {
                new DriverException(driver, element, "elementIsDisplayedError_", object + " is not Displayed");
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "elementIsDisplayedError_", "Something went wrong evaluating the condition stablished.", e);
            return false;
        }
    }

    //Condition element is enabled
    public static boolean elementIsEnabled(Properties p, String object, String data, String Link) {
        try {
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            if (element.isEnabled()) {
                objProps.setRemark(object + " is Enabled");
                info(object + " is Enabled");
            } else {
                new DriverException(driver, element, "elementIsEnabledError_", object + " is not Enabled");
                return false;
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "elementIsEnabledError_", "Something went wrong evaluating the condition stablished.", e);
            return false;
        }
    }
    //####################################################################################################

	/*####################################### Actions Comands ############################################
	 * 
	 * Handling special keyboard and mouse events are done using the Advanced User Interactions API. 
	 * It contains the Actions and the Action classes that are needed when executing these events. 
	 * The following are the most commonly used keyboard and mouse events provided by the Actions class.
	 * 
 	####################################################################################################*/

    //Perform double click
    public static boolean doubleClick(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Double Clicking on Webelement " + object);
            info("Double Clicking on Webelement " + object);
            Actions builder = new Actions(driver);

            if (!object.equals("")) {
                element = findElement(p, object);
                SeleniumUtils.fnHighlightMe(driver, element);
                Action doubleclick = builder
                        .doubleClick(element)
                        .build();
                doubleclick.perform();
            } else {
                Action doubleclick = builder
                        .doubleClick()
                        .build();
                doubleclick.perform();
            }
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "dblclickError_", "Not able to perform double click.", e);
            } else {
                new DriverException(driver, "dblclickError_", "Not able to perform double click.", e);
            }
            return false;
        }
    }

    //Set text on control using actions
    public static boolean inputActions(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Entering the text in " + object);
            info("Entering the text in " + object);
            Actions builder3 = new Actions(driver);

            if (!object.equals("")) {
                element = findElement(p, object);
                SeleniumUtils.fnHighlightMe(driver, element);
                Action settext = builder3
                        .sendKeys(element, data)
                        .build();
                settext.perform();
            } else {
                Action settext = builder3
                        .sendKeys(data)
                        .build();
                settext.perform();
            }
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "inputactionError_", "Not able to type text.", e);
            } else {
                new DriverException(driver, "inputactionError_", "Not able to type text.", e);
            }
            return false;
        }
    }

    //Move mouse over an specified element
    public static boolean moveToElement(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Moving mouse to Element " + object);
            info("Moving mouse to Element " + object);
            element = findElement(p, object);
            SeleniumUtils.fnHighlightMe(driver, element);
            Actions builder2 = new Actions(driver);
            Action mouseOver = builder2
                    .moveToElement(element)
                    .build();
            mouseOver.perform();
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "movetoeleError_", "Not able to move to element defined.", e);
            } else {
                new DriverException(driver, "movetoeleError_", "Not able to move to element defined.", e);
            }
            return false;
        }
    }

    //Click and hold an specified element
    public static boolean clickAndHold(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Click and Hold on element " + object);
            info("Click and Hold on element " + object);
            Actions builder3 = new Actions(driver);

            if (!object.equals("")) {
                element = findElement(p, object);
                SeleniumUtils.fnHighlightMe(driver, element);
                Action clickAndHold = builder3
                        .clickAndHold(element)
                        .build();
                clickAndHold.perform();
            } else {
                Action clickAndHold = builder3
                        .clickAndHold()
                        .build();
                clickAndHold.perform();
            }
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "clickandholdError_", "Not able to click and hold.", e);
            } else {
                new DriverException(driver, "clickandholdError_", "Not able to click and hold.", e);
            }
            return false;
        }
    }

    //Performs a context-click at the current mouse location.
    public static boolean rightClick(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Right Click on element " + object);
            info("Right Click on element " + object);
            Actions builder4 = new Actions(driver);

            if (!object.equals("")) {
                element = findElement(p, object);
                SeleniumUtils.fnHighlightMe(driver, element);
                Action contextClick = builder4
                        .contextClick(element)
                        .build();
                contextClick.perform();
            } else {
                Action contextClick = builder4
                        .contextClick()
                        .build();
                contextClick.perform();
            }
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "rightclickError_", "Not able to perform right click.", e);
            } else {
                new DriverException(driver, "rightclickError_", "Not able to perform right click.", e);
            }
            return false;
        }
    }

    //Performs a modifier key press. Does not release the modifier key - subsequent interactions may assume it's kept pressed.
    public static boolean keyDown(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Key Down pressed: " + data);
            info("Key Down pressed: " + data);
            Actions builder5 = new Actions(driver);

            if (!object.equals("")) {
                element = findElement(p, object);
                SeleniumUtils.fnHighlightMe(driver, element);
                Action keyDown = builder5
                        .keyDown(element, Keys.valueOf(data))
                        // for all the codes see: https://selenium.googlecode.com/git-history/master/docs/api/java/org/openqa/selenium/Keys.html
                        .build();
                keyDown.perform();
            } else {
                Action keyDown = builder5
                        .keyDown(Keys.valueOf(data))
                        .build();
                keyDown.perform();
            }
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "keydownError_", "Not able perform keydown.", e);
            } else {
                new DriverException(driver, "keydownError_", "Not able perform keydown.", e);
            }
            return false;
        }
    }

    //Performs a key release.
    public static boolean keyUp(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Key Up pressed: " + data);
            info("Key Up pressed: " + data);
            Actions builder6 = new Actions(driver);

            if (!object.equals("")) {
                element = findElement(p, object);
                SeleniumUtils.fnHighlightMe(driver, element);
                Action keyup = builder6
                        .keyUp(element, Keys.valueOf(data))
                        // for all the codes see: https://selenium.googlecode.com/git-history/master/docs/api/java/org/openqa/selenium/Keys.html
                        .build();
                keyup.perform();
            } else {
                Action keyup = builder6
                        .keyUp(Keys.valueOf(data))
                        .build();
                keyup.perform();
            }
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "keyupError_", "Not able to perform keyup.", e);
            } else {
                new DriverException(driver, "keyupError_", "Not able to perform keyup.", e);
            }
            return false;
        }
    }

    //It releases the left mouse button at the current mouse location.
    public static boolean release(Properties p, String object, String data, String Link) {
        try {
            objProps.setRemark("Click and Hold released");
            info("Click and Hold released");
            Actions builder7 = new Actions(driver);

            if (!object.equals("")) {
                element = findElement(p, object);
                SeleniumUtils.fnHighlightMe(driver, element);
                Action release = builder7
                        .release(element)
                        .build();
                release.perform();
            } else {
                Action release = builder7
                        .release()
                        .build();
                release.perform();
            }
            return true;
        } catch (Exception e) {
            if (element != null) {
                new DriverException(driver, element, "releaseError_", "Not able to perform release.", e);
            } else {
                new DriverException(driver, "releaseError_", "Not able to perform release.", e);
            }
            return false;
        }
    }
    //####################################################################################################


    /*##################################### Customized Methods ###########################################
     *
     * Methods created for specific functionality.
     *
     ####################################################################################################*/
    //Select multiple child elements, txt box, links, etc...
    public static boolean getStringAllObjects(Properties p, String object, String data, String Link) {
        try {
            List<WebElement> Elements = driver.findElements(findElementBy(p, object));
            String[] objectTexts = new String[Elements.size()];
            int i = 0;

            for (WebElement e : Elements) {
                SeleniumUtils.fnHighlightMe(driver, e);
                objectTexts[i] = e.getText();
                if (data.isEmpty()) {
                    objProps.setRemark("The object text is: " + objectTexts[i]);
                    info("The object text is: " + objectTexts[i]);
                } else if (!data.isEmpty()) {
                    String[] lnk_data_text = data.split(",");
                    if (Elements.size() == lnk_data_text.length - 1) {
                        for (String wrd : lnk_data_text) {
                            if (wrd.equals(objectTexts[i])) {
                                objProps.setRemark("The link text is: " + e.getText() + " is equal to: " + wrd);
                                info("The link text is: " + e.getText() + " is equal to: " + wrd);
                                break;
                            }
                        }
                    } else {
                        new DriverException("The objects in Elements and your Data aren't the same lenght");
                        return false;
                    }
                    i++;
                }
            }
            objProps.setRemark("All elements was found!");
            info("All elements was found!");
            return true;
        } catch (Exception e) {
            new DriverException(driver, "allObjectsError_", "Not able to finds element(s).", e);
            return false;
        }
    }

    //Demo custom actions
    public static boolean clickOnAllLinks(Properties p, String object, String data, String Link) {
        try {
            List<WebElement> Elements = driver.findElements(findElementBy(p, object));

            String[] linkTexts = new String[Elements.size()];
            int i = 0;

            for (WebElement e : Elements) {
                SeleniumUtils.fnHighlightMe(driver, e);
                linkTexts[i] = e.getText();
                if (data.isEmpty()) {
                    objProps.setRemark("The link text is: " + linkTexts[i]);
                    info("The link text is: " + linkTexts[i]);
                } else if (!data.isEmpty()) {//&&linkTexts[i].contentEquals(data)){
                    String[] lnk_data_text = data.split(",");
                    for (String wrd : lnk_data_text) {
                        if (wrd.contentEquals(linkTexts[i])) {
                            objProps.setRemark("The link text is: " + linkTexts[i] + " is equal to: " + wrd);
                            info("The link text is: " + linkTexts[i] + " is equal to: " + wrd);
                            break;
                        }
                    }

                }

                i++;
            }
            for (String text : linkTexts) {
                driver.findElement(By.linkText(text)).click();
                objProps.setRemark("Clicked on: " + text);
                info("Clicked on: " + text);
                Thread.sleep(3000);
                driver.navigate().back();
            }

            objProps.setRemark("All elements was found!");
            info("All elements was found!");
            return true;
        } catch (Exception e) {
            new DriverException(driver, "allObjectsError_", "Not able to finds element(s).", e);
            return false;
        }
    }

    //login web page with active directory
    public static boolean loginWindowsCredentials(Properties p, String object, String data, String Link) {
        try {
            String credentials = data; //introduce the string as user,pass in excel sheet
            String[] capability = credentials.split(",");
            String userName = capability[1]; //user
            String password = capability[0]; //pass
            char[] userChar = userName.toCharArray();
            char[] passChar = password.toCharArray();

            for (char userLetter : userChar) {
                robot.SimulateStandardShortcut(Character.toString(userLetter));
            }

            Thread.sleep(1000);
            robot.SimulateStandardShortcut("tab");

            for (char passLetter : passChar) {
                robot.SimulateStandardShortcut(Character.toString(passLetter));
            }
            Log.info("Typed user: " + userName + " with password: " + password);
            objProps.setRemark("System credentials entered");
            return true;
        } catch (Exception e) {
            new DriverException("Not able to type Active Directory credentials", e);
            return false;
        }
    }

    //Calling auto it exe
    public static boolean callAutoit(Properties p, String object, String data, String Link) {
        try {
            Desktop.getDesktop().open(new File(data));
            Log.info("AutoIt exe was executed!");
            objProps.setRemark("AutoIt exe was executed!");
            return true;
        } catch (Exception e) {
            new DriverException("Not able to type run Autoit Exe", e);
            return false;
        }
    }

    //Find all rows in table and identify the last row
    public static boolean lastTableItem(Properties p, String object, String data, String Link) {
        int i = 1;
        int j = returnGridItems(p, object);
        try {
            do {
                element = findElement(p, data + "[" + Integer.toString(i) + "]");
                if (i == j) {
                    Actions builderItemTable = new Actions(driver);
                    Action mouseOver = builderItemTable
                            .moveToElement(element)
                            .build();
                    mouseOver.perform();
                    JavascriptExecutor jse = (JavascriptExecutor) driver;
                    jse.executeScript("window.scrollBy(0,250)", "");
                    SeleniumUtils.fnHighlightMe(driver, element);
                    Log.info("Items found: " + j + ", " + element.getText());
                    objProps.setRemark("Items found: " + j + ", " + element.getText());
                }
                i++;
            } while (i <= j);
            return true;
        } catch (Exception e) {
            new DriverException(driver, "lastTableItemError_", "No items found", e);
            return false;
        }
    }

    //Find one row item in web table
    public static boolean findTableRow(Properties p, String object, String data, String Link) {
        try {
            if (!data.equals("")) {
                element = findElement(p, object + "[" + data + "]");
                SeleniumUtils.fnHighlightMe(driver, element);
                Log.info("element: " + element.getText());
                objProps.setRemark("element: " + element.getText());
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "findTableRowError_", "No items found", e);
            return false;
        }
    }

    //Clicking on item using the text
    public static boolean clickOnTextItem(Properties p, String object, String data, String Link) {
        try {
            if (!data.equals("")) {
                try {
                    element = returnXpath(p, ".//*[text()='" + data + "']");
                } catch (NoSuchElementException ne) {
                    element = returnXpath(p, ".//*[contains(text(),'" + data + "')]");
                }

                SeleniumUtils.fnHighlightMe(driver, element);
                Log.info("Clicking on element: " + element.getText());
                objProps.setRemark("Clicking on element: " + element.getText());
                element.click();
            } else {
                robot.SimulateStandardShortcut("esc");
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "clickOnTextItemError_" + data, "No item " + data + " found", e);
            return false;
        }
    }

    //Click on item by index
    public static boolean clickOnItemByIndex(Properties p, String object, String data, String Link) {
        try {
            if (!data.equals("")) {
                if (data.contains(",")) {
                    String text = data;
                    String[] capability = text.split(",");
                    //string firstXpathPart = capability[0];
                    String iterationNumber = capability[0];
                    String secondXpathPart = capability[1];
                    element = findElement(p, object + "[" + iterationNumber + "]" + secondXpathPart);
                } else {
                    element = findElement(p, object + "[" + data + "]");
                }
                SeleniumUtils.fnHighlightMe(driver, element);
                element.click();
                Log.info(element.getText() + " , was Clicked!");
                objProps.setRemark(element.getText() + " , was Clicked!");
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "clickOnItemByIndexError_" + data, "No item " + data + " found", e);
            return false;
        }
    }

    //Search object by index
    public static boolean searchItemByIndex(Properties p, String object, String data, String Link) {
        try {
            if (!data.equals("")) {
                if (data.contains(",")) {
                    String text = data;
                    String[] capability = text.split(",");
                    //string firstXpathPart = capability[0];
                    String iterationNumber = capability[0];
                    String secondXpathPart = capability[1];
                    element = findElement(p, object + "[" + iterationNumber + "]" + secondXpathPart);
                } else {
                    element = findElement(p, object + "[" + data + "]");
                }
                SeleniumUtils.fnHighlightMe(driver, element);
                Log.info(element.getText() + " , was Found!");
                objProps.setRemark(element.getText() + " , was Found!");
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "searchItemByIndexError_", "No item " + data + " found", e);
            return false;
        }
    }

    //Search object by text
    public static boolean searchObjectByText(Properties p, String object, String data, String Link) {
        try {
            if (!data.equals("")) {
                try {
                    element = returnXpath(p, ".//*[text()='" + data + "']");
                } catch (NoSuchElementException ne) {
                    element = returnXpath(p, ".//*[contains(text(),'" + data + "')]");
                }

                SeleniumUtils.fnHighlightMe(driver, element);
                Log.info(element.getText() + " , was Found!");
                objProps.setRemark(element.getText() + " , was Found!");
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "NoitemsfoundError_", "No items found", e);
            return false;
        }
    }

    //Verify an Increment o Decrement
    public static boolean verifyChangeOnValue(Properties p, String object, String data, String Link) {
        try {
            if (!data.equals("")) {
                try {
                    element = returnXpath(p, ".//*[text()='" + data + "']");
                } catch (NoSuchElementException ne) {
                    element = returnXpath(p, ".//*[contains(text(),'" + data + "')]");
                }

                SeleniumUtils.fnHighlightMe(driver, element);

                Log.info(element.getText() + " , was Found!");
                objProps.setRemark(element.getText() + " , was Found!");
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "NoitemsfoundError_", "No items found", e);
            return false;
        }
    }

    //####################################################################################################

 /*##################################### Customized Coursekey MEthods ###########################################
 *
 * Methods created for specific functionality on CK application.
 *
 ####################################################################################################*/
    public static boolean createUser(Properties p, String object, String data, String Link) {
        String user = "gilberto.aspros+" + (new Random().nextInt(10000)) + "@gmail.com";
        try {
            objProps.setRemark("Creating user " + user);
            info("Creating user " + user);
            WebElement email = driver.findElement(By.id("registration-email"));
            SeleniumUtils.fnHighlightMe(driver, email);
            email.clear();
            email.sendKeys(user);

            WebElement confirmEmail = driver.findElement(By.id("registration-confirm-email"));
            SeleniumUtils.fnHighlightMe(driver, confirmEmail);
            confirmEmail.clear();
            confirmEmail.sendKeys(user);
            return true;
        } catch (Exception e) {
            new DriverException(driver, "createUserError_", "Not able to type text.", e);
            return false;
        }
    }

    public static boolean courseInformation(Properties p, String object, String data, String Link) {
        String course = "New Automated Course " + (new Random().nextInt(10000));
        try {
            objProps.setRemark("Creating course " + course);
            info("Creating course " + course);
            WebElement txtCourseName = driver.findElement(By.xpath("//input[starts-with(@id,'undefined-ECON101')]"));
            SeleniumUtils.fnHighlightMe(driver, txtCourseName);
            txtCourseName.clear();
            txtCourseName.sendKeys(course);

            WebElement txtCourseTitle = driver.findElement(By.xpath("//input[starts-with(@id,'undefined-Principles')]"));
            SeleniumUtils.fnHighlightMe(driver, txtCourseTitle);
            txtCourseTitle.clear();
            txtCourseTitle.sendKeys(course);
            return true;
        } catch (Exception e) {
            new DriverException(driver, "createUserError_", "Not able to type text.", e);
            return false;
        }
    }

    public static boolean selectDates(Properties p, String object, String data, String Link) {
        //String strStartDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(new Date());
        waitVar = new WebDriverWait(driver, 20);
        String[] date = data.split(","); //data must have month, day, year
        String strMonth = date[0];
        String strDay = date[1];
        String strYear = date[2];

        try{
            objProps.setRemark("Selecting Start and End Date");
            info("Selecting Start and End Date");

            //Select Start Date
            WebElement dtpStartDate=driver.findElement(By.xpath("//input[starts-with(@id,'undefined-undefined-SelectStartDate')]"));
            waitVar.until(ExpectedConditions.visibilityOf(dtpStartDate));
            SeleniumUtils.fnHighlightMe(driver, dtpStartDate);
            dtpStartDate.click();

            driver.findElement(By.xpath("//div[12]/div/div[1]/div/div/div/div/div[2]/div[2]/button[2]")).click();

            Thread.sleep(1500);

            WebElement dtpEndDate=driver.findElement(By.xpath("//input[starts-with(@id,'undefined-undefined-SelectEndDate')]"));
            waitVar.until(ExpectedConditions.visibilityOf(dtpEndDate));
            SeleniumUtils.fnHighlightMe(driver, dtpEndDate);
            dtpEndDate.click();

            WebElement lnkYear = driver.findElement(By.xpath("//div[13]/div/div[1]/div/div/div/div/div[1]/div[1]/div/div"));
            SeleniumUtils.fnHighlightMe(driver, lnkYear);
            lnkYear.click();

            List<WebElement> yearsList = driver.findElements(By.xpath("//div[13]/div/div[1]/div/div/div/div/div[2]/div[1]/div/div/button"));
            for(WebElement year: yearsList){
                if(year.getText().equalsIgnoreCase(strYear)){
                    year.click();
                    break;
                }
            }

            driver.findElement(By.xpath("//div[13]/div/div[1]/div/div/div/div/div[2]/div[2]/button[2]")).click();
            Thread.sleep(1500);

            return true;
        } catch (Exception e) {
            new DriverException(driver, "starDateError_", "Not able to add course start date.", e);
            return false;
        }
    }

    public static boolean selectTime(Properties p, String object, String data, String Link) {
        waitVar = new WebDriverWait(driver, 20);
        JavascriptExecutor js = (JavascriptExecutor)driver;;
        String[] time = data.split(","); //data must be hour:min:AM/PM, hour:min:AM/PM
        String[] strStartTime = time[0].split(":");
        String[] strEndTime = time[1].split(":");

        String startHour = strStartTime[0];
        String startMin = strStartTime[1];
        String startAmPm = strStartTime[2];

        String endHour = strEndTime[0];
        String endMin = strEndTime[1];
        String endAmPm = strEndTime[2];

        try{
            objProps.setRemark("Selecting Start and End time");
            info("Selecting Start and End Time");

            //Select Start Date
            WebElement dtpStartTime=driver.findElement(By.xpath("//input[starts-with(@id,'undefined-undefined-SelectStartTime')]"));
            waitVar.until(ExpectedConditions.visibilityOf(dtpStartTime));
            SeleniumUtils.fnHighlightMe(driver, dtpStartTime);
            dtpStartTime.click();

            // Select Start Hour
            List<WebElement> startHours = driver.findElements(By.xpath("//div[14]/div/div[1]/div/div/div[1]/div/div[2]/div[2]/span"));
            for (WebElement hour: startHours){
                if(hour.getText().equals(startHour)){
                    SeleniumUtils.fnHighlightMe(driver, hour);
                    js.executeScript("arguments[0].click();", hour);
                    //hour.click();
                    break;
                }
            }

            List<WebElement> startMinutes = driver.findElements(By.xpath("//div[14]/div/div[1]/div/div/div[1]/div/div[2]/div[2]/span"));
            for (WebElement min: startMinutes){
                if(min.getText().equals(startMin)){SeleniumUtils.fnHighlightMe(driver, min);min.click();break;}
            }

            WebElement starAM = driver.findElement(By.xpath("//div[14]/div/div[1]/div/div/div[1]/div/div[1]/div/div[3]/div[2]"));
            WebElement startPM = driver.findElement(By.xpath("//div[14]/div/div[1]/div/div/div[1]/div/div[1]/div/div[3]/div[1]"));

            if (startAmPm.equalsIgnoreCase("am")) {
                SeleniumUtils.fnHighlightMe(driver, starAM);
                starAM.click();
            } else {
                SeleniumUtils.fnHighlightMe(driver, startPM);
                startPM.click();
            }

            driver.findElement(By.xpath("//div[14]/div/div[1]/div/div/div[2]/button[2]")).click();

            Thread.sleep(1500);

            WebElement dtpEndtime=driver.findElement(By.xpath("//input[starts-with(@id,'undefined-undefined-SelectEndTime')]"));
            waitVar.until(ExpectedConditions.visibilityOf(dtpEndtime));
            SeleniumUtils.fnHighlightMe(driver, dtpEndtime);
            dtpEndtime.click();

            // Select End Hour
            List<WebElement> endHours = driver.findElements(By.xpath("//div[15]/div/div[1]/div/div/div[1]/div/div[2]/div[2]/span"));
            for (WebElement hour: endHours){
                if(hour.getText().equals(endHour)){
                    SeleniumUtils.fnHighlightMe(driver, hour);
                    js.executeScript("arguments[0].click();", hour);
                    //hour.click();
                    break;}
            }

            List<WebElement> endMinutes = driver.findElements(By.xpath("//div[15]/div/div[1]/div/div/div[1]/div/div[2]/div[2]/span"));
            for (WebElement min: endMinutes){
                if(min.getText().equals(endMin)){SeleniumUtils.fnHighlightMe(driver, min); min.click();break;}
            }

            WebElement endAM = driver.findElement(By.xpath("//div[15]/div/div[1]/div/div/div[1]/div/div[1]/div/div[3]/div[2]"));
            WebElement endPM = driver.findElement(By.xpath("//div[15]/div/div[1]/div/div/div[1]/div/div[1]/div/div[3]/div[1]"));

            if (endAmPm.equalsIgnoreCase("am")) {
                SeleniumUtils.fnHighlightMe(driver, endAM);
                endAM.click();
            } else {
                SeleniumUtils.fnHighlightMe(driver, endPM);
                endPM.click();
            }
            //Hour
            // min //div[15]/div/div[1]/div/div/div[1]/div/div[2]/div[2]/span
            //AM //div[15]/div/div[1]/div/div/div[1]/div/div[1]/div/div[3]/div[2]
            //PM //div[15]/div/div[1]/div/div/div[1]/div/div[1]/div/div[3]/div[1]


            driver.findElement(By.xpath("//div[15]/div/div[1]/div/div/div/div/div[2]/div[2]/button[2]")).click();
            Thread.sleep(1500);

            return true;
        } catch (Exception e) {
            new DriverException(driver, "starDateError_", "Not able to add course start date.", e);
            return false;
        }
    }

    public static boolean studentsGrades(Properties p, String object, String data, String Link){
        try{
            List<WebElement> studentAssessmentList = driver.findElements(By.xpath("//div[@class='fixedDataTableRowLayout_rowWrapper']"));
            students = studentAssessmentList.size();
            for (WebElement element : studentAssessmentList){
                SeleniumUtils.fnHighlightMe(driver,element);
            }
            return true;
        }catch(Exception e){
            new DriverException(driver, "assessmentsError__", "Not able see student assessments.", e);
            return false;
        }
    }

    public static boolean nextStudentAll(Properties p, String object, String data, String Link){
        try{
            for (int i = 0; i<students; i++) {
                JavascriptExecutor jse = (JavascriptExecutor) driver;
                jse.executeScript("window.scrollBy(0,250)", "");
                element = driver.findElement(By.xpath("//*[text()='Next Student']"));
                //waitVar.until(ExpectedConditions.visibilityOf(element));
                SeleniumUtils.fnHighlightMe(driver, element);
                element.click();
                Thread.sleep(2000);
            }
            return true;
        }catch(Exception e){
            new DriverException(driver, "assessmentsError__", "Not able see student assessments.", e);
            return false;
        }
    }

    public static boolean getStudentAttendanceResults(Properties p, String object, String data, String Link){
        try{
            List<WebElement> studentAttendanceList = driver.findElements(By.xpath("//div[@class='row middle-xs between-xs']"));
            for (WebElement element : studentAttendanceList){
                SeleniumUtils.fnHighlightMe(driver,element);
            }
            return true;
        }catch(Exception e){
            new DriverException(driver, "assessmentsError__", "Not able see student assessments.", e);
            return false;
        }

    }

    public static boolean courseDays(Properties p, String object, String data, String Link) {
        objProps.setRemark("Adding days " + data);
        info("Adding days " + data);
        try{
            if(data.toLowerCase().contains("sun")||data.toLowerCase().contains("sunday")){
                WebElement btnSun = driver.findElement(By.xpath("//button/div/div/*[contains(text(),'SUN')]"));
                SeleniumUtils.fnHighlightMe(driver, btnSun);
                btnSun.click();
            }

            if(data.toLowerCase().contains("mon")||data.toLowerCase().contains("monday")){
                WebElement btnMon = driver.findElement(By.xpath("//button/div/div/*[contains(text(),'MON')]"));
                SeleniumUtils.fnHighlightMe(driver, btnMon);
                btnMon.click();
            }

            if(data.toLowerCase().contains("tue")||data.toLowerCase().contains("tuesday")){
                WebElement btnTue = driver.findElement(By.xpath("//button/div/div/*[contains(text(),'TUE')]"));
                SeleniumUtils.fnHighlightMe(driver, btnTue);
                btnTue.click();
            }

            if(data.toLowerCase().contains("wed")||data.toLowerCase().contains("wednesday")){
                WebElement btnWed = driver.findElement(By.xpath("//button/div/div/*[contains(text(),'WED')]"));
                SeleniumUtils.fnHighlightMe(driver, btnWed);
                btnWed.click();
            }

            if(data.toLowerCase().contains("thu")||data.toLowerCase().contains("thursday")){
                WebElement btnThu = driver.findElement(By.xpath("//button/div/div/*[contains(text(),'THU')]"));
                SeleniumUtils.fnHighlightMe(driver, btnThu);
                btnThu.click();
            }

            if(data.toLowerCase().contains("fri")||data.toLowerCase().contains("friday")){
                WebElement btnFri = driver.findElement(By.xpath("//button/div/div/*[contains(text(),'FRI')]"));
                SeleniumUtils.fnHighlightMe(driver, btnFri);
                btnFri.click();
            }

            if(data.toLowerCase().contains("sat")||data.toLowerCase().contains("saturday")){
                WebElement btnSat = driver.findElement(By.xpath("//button/div/div/*[contains(text(),'SAT')]"));
                SeleniumUtils.fnHighlightMe(driver, btnSat);
                btnSat.click();
            }
            return true;
        } catch (Exception e) {
            new DriverException(driver, "starDateError_", "Not able to add course start date.", e);
            return false;
        }
    }

    //####################################################################################################
    private static WebElement findElement(Properties p, String locator) {
        String objLocator = (p != null) ? (p.getProperty(locator) != null ? p.getProperty(locator) : locator) : locator;
        try {
            return driver.findElement(By.id(objLocator));
        } catch (NoSuchElementException e) {
            return returnElementName(objLocator);
        }catch (WebDriverException we){
            return appiumDriver.findElementById(objLocator);
        }
    }


    private static WebElement returnElementName(String name) {
        try {
            return driver.findElement(By.name(name));
        } catch (NoSuchElementException e) {
            return returnElementXpath(name);
        }
    }


    private static WebElement returnElementXpath(String xpath) {
        try {
            return driver.findElement(By.xpath(xpath));
        } catch (NoSuchElementException e) {
            return returnElementCssSelector(xpath);
        }
    }


    private static WebElement returnElementCssSelector(String cssSelector) {
        try {
            return driver.findElement(By.cssSelector(cssSelector));
        } catch (NoSuchElementException e) {
            return returnElementClassName(cssSelector);
        }
    }


    private static WebElement returnElementClassName(String className) {
        try {
            return driver.findElement(By.className(className));
        } catch (NoSuchElementException e) {
            return returnElementLinkText(className);
        }
    }


    private static WebElement returnElementLinkText(String linkText) {
        try {
            return driver.findElement(By.partialLinkText(linkText));
        } catch (NoSuchElementException e) {
            return returnElementTagname(linkText);
        }
    }


    private static WebElement returnElementTagname(String tagName) {
        try {
            return driver.findElement(By.tagName(tagName));
        } catch (NoSuchElementException ne) {
            try {
                throw new DriverException("Class ActionKeywords | Method findElement | Exception desc: Can not find the element: " + tagName);
            } catch (DriverException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            DriverScript.bResult = false;
            return null;
        }
    }


    private static By findElementBy(Properties p, String locator) {
        String objLocator = (p != null) ? (p.getProperty(locator) != null ? p.getProperty(locator) : locator) : locator;
        try {
            driver.findElement(By.id(objLocator));
            return By.id(objLocator);
        } catch (NoSuchElementException e) {
            return returnElementByName(objLocator);
        }
    }


    private static By returnElementByName(String name) {
        try {
            driver.findElement(By.name(name));
            return By.name(name);
        } catch (NoSuchElementException e) {
            return returnElementByXpath(name);
        }
    }


    private static By returnElementByXpath(String xpath) {
        try {
            driver.findElement(By.xpath(xpath));
            return By.xpath(xpath);
        } catch (NoSuchElementException e) {
            return returnElementByCssSelector(xpath);
        }
    }


    private static By returnElementByCssSelector(String cssSelector) {
        try {
            driver.findElement(By.cssSelector(cssSelector));
            return By.cssSelector(cssSelector);
        } catch (NoSuchElementException e) {
            return returnElementByClassName(cssSelector);
        }
    }


    private static By returnElementByClassName(String className) {
        try {
            driver.findElement(By.className(className));
            return By.className(className);
        } catch (NoSuchElementException e) {
            return returnElementByLinkText(className);
        }
    }


    private static By returnElementByLinkText(String linkText) {
        try {
            driver.findElement(By.partialLinkText(linkText));
            return By.partialLinkText(linkText);
        } catch (NoSuchElementException e) {
            return returnElementByTagname(linkText);
        }
    }


    private static By returnElementByTagname(String tagName) {
        try {
            driver.findElement(By.tagName(tagName));
            return By.tagName(tagName);
        } catch (NoSuchElementException ne) {
            try {
                throw new DriverException("Class ActionKeywords | Method findElementBy | Exception desc: Can not find the element: " + tagName);
            } catch (DriverException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            DriverScript.bResult = false;
            return null;
        }
    }

    //Returns only Xpath


    private static WebElement returnXpath(Properties p, String xpath) {
        String objLocator = (p != null) ? (p.getProperty(xpath) != null ? p.getProperty(xpath) : xpath) : xpath;
        return driver.findElement(By.xpath(objLocator));
    }


    public static int returnGridItems(Properties p, String Locator) {
        int ItemNumber;
        try {
            List<WebElement> ListItems = driver.findElements(findElementBy(p, Locator));
            ItemNumber = ListItems.size();
            Log.info("Items found in Tracking grid are: " + ItemNumber);
            return ItemNumber;
        } catch (Exception ex) {
            new DriverException("Common | Method returnGridTrackingItems | Exception desc: " + ex.getMessage());
            return 0;
        }
    }
}