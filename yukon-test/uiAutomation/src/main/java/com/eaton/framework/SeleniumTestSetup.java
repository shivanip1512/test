package com.eaton.framework;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.eaton.framework.drivers.DriverFactory;
import com.eaton.pages.LoginPage;
import com.eaton.pages.PageBase;

public class SeleniumTestSetup {

    private static WebDriver driver;

    private static DriverExtensions driverExt;

    private static String baseUrl;

    private static Logger logger;

    private static final String EXCEPTION_MSG = "Exception :";

    private static Random randomNum;

    private static boolean loggedIn = false;

    private static String screenShotPath;

    @BeforeSuite(alwaysRun = true)
    public static void beforeSuite() {

        try {
            setRandomNum(new Random());
            logger = setupLogger();
            initialSetup();
            navigateToLoginPage();
            login();
        } catch (Exception ex) {
            logger.fine(EXCEPTION_MSG + ex);
        }
    }

    public static void initialSetup() {

        try {
            ConfigFileReader configFileReader = new ConfigFileReader();

            setBaseUrl(configFileReader.getApplicationUrl());

            setDriver(new DriverFactory().getWebDriver(configFileReader.getBrowser(),
                    Boolean.parseBoolean(configFileReader.getUseRemoteDriver()),
                    Boolean.parseBoolean(configFileReader.getRunHeadless()),
                    configFileReader.getProxy(),
                    configFileReader.getProxyFlag()));
            setDriverExt();
            setScreenShotPath(configFileReader.getScreenShotPath());
        } catch (Exception ex) {
            logger.fine(EXCEPTION_MSG + ex);
        }
    }

    public static void navigateToLoginPage() {
        SeleniumTestSetup.driver.navigate().to(getBaseUrl());
    }

    public static <T extends PageBase> T getInstance(Class<T> pageClass) {
        try {

            return PageFactory.initElements(driver, pageClass);
        } catch (Exception ex) {
            logger.fine(EXCEPTION_MSG + ex);
            throw ex;
        }
    }

    public static Logger setupLogger() {

        Logger newLogger = Logger.getLogger("selenium.logger");
        newLogger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        newLogger.addHandler(handler);

        return newLogger;
    }

    public static void login() {
        if (!loggedIn) {
            LoginPage loginPage = new LoginPage(SeleniumTestSetup.driverExt);

            loggedIn = loginPage.login();
        }
    }

    public static WebDriver getDriver() {
        return SeleniumTestSetup.driver;
    }

    private static void setDriver(WebDriver driver) {
        SeleniumTestSetup.driver = driver;
    }

    public static DriverExtensions getDriverExt() {
        return driverExt;
    }

    private static void setDriverExt() {
        SeleniumTestSetup.driverExt = new DriverExtensions(SeleniumTestSetup.driver);
    }

    private static void setScreenShotPath(String screenShotPath) {
        SeleniumTestSetup.screenShotPath = screenShotPath;
    }

    public static String getScreenShotPath() {
        return SeleniumTestSetup.screenShotPath;
    }

    public static String getCurrentUrl() {
        return SeleniumTestSetup.driver.getCurrentUrl();
    }

    public static String getPageUrlFromCurrentUrl() {
        String baseUrl = getCurrentUrl();

        String[] parts = baseUrl.split("/", 2);

        return parts[1];
    }

    private static void setBaseUrl(String baseUrl) {
        SeleniumTestSetup.baseUrl = baseUrl;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static Random getRandomNum() {
        return randomNum;
    }

    public static void setRandomNum(Random randomNum) {
        SeleniumTestSetup.randomNum = randomNum;
    }

    public static Logger getLogger() {
        return logger;
    }

    public boolean waitForUrlToLoad(String expectedUrl, Optional<Integer> timeOutSeconds) {
        Integer timeOut = timeOutSeconds.orElse(null);

        Integer waitTime;

        if (timeOut == null) {
            waitTime = 5000;
        } else if (timeOut < 5) {
            waitTime = 5000;
        } else {
            waitTime = timeOut * 1000;
        }

        long startTime = System.currentTimeMillis();

        boolean expectedUrlLoaded = false;
        while (!expectedUrlLoaded && System.currentTimeMillis() - startTime < waitTime) {
            String currentUrl = driver.getCurrentUrl();

            expectedUrlLoaded = currentUrl.contains(expectedUrl);
        }

        return expectedUrlLoaded;
    }

    public void waitForPageToLoad(String pageTitle, Optional<Integer> timeOutSeconds) {
        Integer timeOut = timeOutSeconds.orElse(null);

        Integer waitTime;

        if (timeOut == null) {
            waitTime = 1;
        } else {
            waitTime = timeOut;
        }

        SeleniumTestSetup.driverExt.getDriverWait(Optional.of(waitTime))
                .until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".page-heading"), pageTitle));
    }

    public void refreshPage(PageBase page) {
        if (getCurrentUrl().equals(getBaseUrl() + page.getPageUrl())) {
            driver.navigate().refresh();
        } else {
            navigate(page.getPageUrl());
        }
    }

    public static void waitForLoadingSpinner() {
        String display = "";

        long startTime = System.currentTimeMillis();
        while (!display.equals("display: none;") || System.currentTimeMillis() - startTime < 2000) {
            try {
                display = driverExt.findElement(By.id("modal-glass"), Optional.empty()).getAttribute("style");
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public static void waitUntilSuccessMessageDisplayed() {
        String classAttribute = "dn";

        long startTime = System.currentTimeMillis();
        while (classAttribute.contains("dn") || System.currentTimeMillis() - startTime < 3000) {
            try {
                classAttribute = driverExt.findElement(By.cssSelector(".yukon-content .user-message"), Optional.empty())
                        .getAttribute("class");
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public static void waitUntilModalVisibleByDescribedBy(String describedBy) {
        boolean displayed = false;

        long startTime = System.currentTimeMillis();

        while (!displayed && System.currentTimeMillis() - startTime < 300) {
            try {
                displayed = driverExt.findElement(By.cssSelector("[aria-describedby='" + describedBy + "']"), Optional.of(0))
                        .isDisplayed();
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public static void waitUntilModalClosedByDescribedBy(String describedBy) {
        boolean displayed = true;

        long startTime = System.currentTimeMillis();

        while (displayed && System.currentTimeMillis() - startTime < 300) {
            try {
                displayed = driverExt.findElement(By.cssSelector("[aria-describedby='" + describedBy + "']"), Optional.of(0))
                        .isDisplayed();
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public static void waitUntilModalVisibleByTitle(String modalTitle) {
        List<WebElement> elements;
        Optional<WebElement> el;
        boolean found = false;

        long startTime = System.currentTimeMillis();

        while (!found && System.currentTimeMillis() - startTime < 300) {
            try {
                elements = driverExt.findElements(By.cssSelector(".ui-dialog .ui-dialog-title"), Optional.of(0));

                el = elements.stream().filter(element -> element.getText().equals(modalTitle)).findFirst();
                found = el.isPresent();
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public static void waitUntilModalClosedByTitle(String modalTitle) {
        List<WebElement> elements;
        Optional<WebElement> el;
        boolean found = true;

        long startTime = System.currentTimeMillis();

        while (found && System.currentTimeMillis() - startTime < 100) {
            try {
                elements = driverExt.findElements(By.cssSelector(".ui-dialog .ui-dialog-title"), Optional.empty());

                el = elements.stream().filter(element -> element.getText().equals(modalTitle)).findFirst();
                found = el.isPresent();
                if (!found) {
                    return;
                }
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
                found = false;
            }
        }
    }

    public static void waitUntilModalClosed(WebElement modal) {
        String display = "";

        long startTime = System.currentTimeMillis();
        while (!display.equals("display: none;") && System.currentTimeMillis() - startTime < 100) {
            try {
                display = modal.getAttribute("style");
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public void navigate(String url) {
        SeleniumTestSetup.driver.navigate().to(getBaseUrl() + url);

        waitForUrlToLoad(url, Optional.empty());
    }

    public static void moveToElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();
    }

    public static void scrollToElement(WebElement element) {
        JavascriptExecutor je = (JavascriptExecutor) driver;
        je.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @AfterSuite(alwaysRun = true)
    public static void afterSuite() {
        getDriver().quit();
    }
}
