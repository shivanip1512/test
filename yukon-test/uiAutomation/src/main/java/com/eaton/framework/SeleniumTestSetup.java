package com.eaton.framework;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
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
import com.github.javafaker.Faker;

public class SeleniumTestSetup {

    private static WebDriver driver;

    private static DriverExtensions driverExt;

    private static String baseUrl;

    private static Logger logger;

    private static final String EXCEPTION_MSG = "Exception :";

    private static boolean loggedIn = false;

    private static String screenShotPath;

    private static String database;

    private static Faker faker;

    private boolean refreshPage = false;

    @BeforeSuite(alwaysRun = true)
    public static void beforeSuite() {
        try {
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
            setDriverExt(new DriverExtensions(SeleniumTestSetup.driver));
            setFaker(new Faker());
            setScreenShotPath(configFileReader.getScreenShotPath());
            setDatabase(configFileReader.getDatabaseParameter());
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

    private static void setDriverExt(DriverExtensions driverExt) {
        SeleniumTestSetup.driverExt = driverExt;
    }

    public static Faker getFaker() {
        return SeleniumTestSetup.faker;
    }

    private static void setFaker(Faker faker) {
        SeleniumTestSetup.faker = faker;
    }

    private static void setScreenShotPath(String screenShotPath) {
        SeleniumTestSetup.screenShotPath = screenShotPath;
    }

    public static String getScreenShotPath() {
        return SeleniumTestSetup.screenShotPath;
    }

    private static void setDatabase(String database) {
        SeleniumTestSetup.database = database;
    }

    public static String getDatabase() {
        return SeleniumTestSetup.database;
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

    public static Logger getLogger() {
        return logger;
    }

    public static boolean waitForUrlToLoad(String expectedUrl, Optional<Integer> timeOutSeconds) {
        Integer timeOut = timeOutSeconds.orElse(3000);

        long startTime = System.currentTimeMillis();

        boolean expectedUrlLoaded = false;
        while (!expectedUrlLoaded && ((System.currentTimeMillis() - startTime) < timeOut)) {
            String currentUrl = driver.getCurrentUrl();

            expectedUrlLoaded = currentUrl.contains(expectedUrl);
        }

        return expectedUrlLoaded;
    }

    public void waitForPageToLoad(String pageTitle, Optional<Integer> timeOutSeconds) {
        Integer timeOut = timeOutSeconds.orElse(1);

        SeleniumTestSetup.driverExt.getDriverWait(Optional.of(timeOut))
                .until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".page-heading"), pageTitle));
    }

    public void refreshPage(PageBase page) {
        if (getCurrentUrl().equals(getBaseUrl() + page.getPageUrl())) {
            JavascriptExecutor je = (JavascriptExecutor) driver;
            je.executeScript("document.location.reload(true);");
        } else {
            navigate(page.getPageUrl());
        }
    }

    public static void waitForLoadingSpinner() {
        String display = "";

        long startTime = System.currentTimeMillis();

        while (!display.equals("display: none;") && (System.currentTimeMillis() - startTime < 2000)) {
            try {
                display = driverExt.findElement(By.id("modal-glass"), Optional.of(0)).getAttribute("style");
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public static void waitUntilModalOpenByDescribedBy(String describedBy) {
        Integer count;
        long startTime = System.currentTimeMillis();

        do {
            count = driverExt.findElements(By.cssSelector("[aria-describedby='" + describedBy + "']"), Optional.of(0)).size();
        } while (count.equals(0) && ((System.currentTimeMillis() - startTime) < 2000));
    }

    public static void waitUntilModalOpenDisplayBlock(String describedBy) {
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < 2000) {

            String style = driverExt.findElement(By.cssSelector("[aria-describedby='" + describedBy + "']"), Optional.of(0))
                    .getAttribute("style");

            if (style.contains("display: block")) {
                break;
            }
        }
    }

    public static void waitUntilModalClosedByDescribedBy(String describedBy) {
        long startTime = System.currentTimeMillis();
        Integer count;

        do {
            count = driverExt.findElements(By.cssSelector("[aria-describedby='" + describedBy + "']"), Optional.of(0)).size();
        } while (count.equals(1) && ((System.currentTimeMillis() - startTime) < 2000));
    }

    /**
     * Use this method when going to a page for the first time and table has not loaded
     * @param parentElement - Optional
     */
    public static void waitUntilTableVisiable(Optional<WebElement> parentElement) {
        
        WebElement display = null;

        long startTime = System.currentTimeMillis();

        while (display == null && (System.currentTimeMillis() - startTime < 2000)) {
            try {
            	if (parentElement.isPresent()) {
                    display = parentElement.get().findElement(By.cssSelector("[class='.compact-results-table']"));	
            	} else {
            		display = driverExt.findElement(By.cssSelector("[class='.compact-results-table']"), Optional.of(0));
            	}
            	
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }
        
    public static void waitUntilModalClosedDisplayNone(String describedBy) {
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < 2000) {

            String style = driverExt.findElement(By.cssSelector("[aria-describedby='" + describedBy + "']"), Optional.of(0))
                    .getAttribute("style");

            if (style.contains("display: none")) {
                break;
            }
        }
    }

    public static void waitUntilModalInvisibleByDescribedBy(String describedBy) {
        driverExt.waitUntilElementInvisibleByCssLocator("[aria-describedby='" + describedBy + "']");
    }

    /**
     * @param modalTitle title of the modal
     * 
     *                   only use this method if area-describedby uses a dynamic id and
     *                   can not use method waitUntilModalVisibleByDescribedBy
     */
    public static void waitUntilModalOpenByTitle(String modalTitle) {
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < 2000) {
            List<WebElement> list = driverExt.findElements(By.cssSelector(".ui-dialog[aria-labelledby^='ui-id']"),
                    Optional.of(0));

            Optional<WebElement> el = list.stream()
                    .filter(x -> x.findElement(By.cssSelector(".ui-dialog-title")).getText().contains(modalTitle)).findFirst();

            if (el.isPresent()) {
                break;
            }
        }
    }

    /**
     * @param modalTitle title of the modal
     * 
     *                   only use this method if area-describedby uses a dynamic id and
     *                   can not use method waitUntilModalClosedByDescribedBy
     */
    public static void waitUntilModalClosedByTitle(String modalTitle) {
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < 2000) {
            List<WebElement> list = driverExt.findElements(By.cssSelector(".ui-dialog[aria-describedby^='ui-id']"),
                    Optional.of(0));

            try {
                Optional<WebElement> el = list.stream()
                        .filter(x -> x.findElement(By.cssSelector(".ui-dialog-title")).getText().contains(modalTitle))
                        .findFirst();

                if (!el.isPresent()) {
                    break;
                }
            } catch (StaleElementReferenceException ex) {

            }
        }
    }

    public static void waitUntilDropDownMenuOpen() {
        String display = "display: none;";

        long startTime = System.currentTimeMillis();

        while (display.equals("display: none;") && (System.currentTimeMillis() - startTime < 2000)) {
            try {
                List<WebElement> menus = driverExt.findElements(By.cssSelector(".dropdown-menu"), Optional.of(0));

                Optional<WebElement> el = menus.stream().filter(x -> x.getAttribute("style").contains("display: block;"))
                        .findFirst();

                if (el.isPresent()) {
                    display = el.get().getAttribute("style");
                }
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }
    }

    public static void navigate(String url) {
        String pageUrl = getBaseUrl() + url;

        driver.get(pageUrl);
        waitForUrlToLoad(pageUrl, Optional.empty());
    }

    public static int getResponseCode(String urlString) throws IOException {
        URL u = new URL(urlString);
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setRequestMethod("GET");
        huc.connect();

        return huc.getResponseCode();
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

    public boolean getRefreshPage() {
        return refreshPage;
    }

    public void setRefreshPage(boolean refreshPage) {
        this.refreshPage = refreshPage;
    }

    @AfterSuite(alwaysRun = true)
    public static void afterSuite() {
        getDriver().quit();
    }
}
