package com.eaton.framework;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.eaton.framework.drivers.DriverFactory;
import com.eaton.pages.LoginPage;
import com.eaton.pages.PageBase;

public class SeleniumTestSetup {

    private static WebDriver driver;

    private static String baseUrl;

    private static SoftAssert softAssertion;

    private static Logger logger;

    private static final String EXCEPTION_MSG = "Exception :";

    @BeforeSuite
    public static void beforeSuite() {

        try {
            setSoftAssertion(new SoftAssert());
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

            // Change this to get the parameters from a config file
            setDriver(new DriverFactory().getWebDriver(configFileReader.getBrowser(),
                    Boolean.parseBoolean(configFileReader.getUseRemoteDriver()), configFileReader.getDriverPath(),
                    Boolean.parseBoolean(configFileReader.getRunHeadless())));
        } catch (Exception ex) {
            logger.fine(EXCEPTION_MSG + ex);
        }
    }

    public static void navigateToLoginPage() {
        SeleniumTestSetup.driver.navigate().to(getBaseUrl());
    }

//	public static void navigateToPage(PageBase page){				
//		
//		if (page.getRequiresLogin()) {
//			login();
//		}
//		
//		driver.get(getBaseUrl() + page.getPageUrl());
//	}

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

        LoginPage loginPage = new LoginPage(SeleniumTestSetup.driver, getBaseUrl());

        loginPage.login();
    }

    public static WebDriver getDriver() {
        return SeleniumTestSetup.driver;
    }

    private static void setDriver(WebDriver driver) {
        SeleniumTestSetup.driver = driver;
    }

    public static String getCurrentUrl() {
        return SeleniumTestSetup.driver.getCurrentUrl();
    }

    private static void setBaseUrl(String baseUrl) {
        SeleniumTestSetup.baseUrl = baseUrl;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static SoftAssert getSoftAssertion() {
        return softAssertion;
    }

    private static void setSoftAssertion(SoftAssert softAssertion) {
        SeleniumTestSetup.softAssertion = softAssertion;
    }

    public static Logger getLogger() {
        return logger;
    }

    @AfterSuite(alwaysRun = true)
    public static void afterSuite() {
        SeleniumTestSetup.driver.close();
        SeleniumTestSetup.driver.quit();
    }
}
