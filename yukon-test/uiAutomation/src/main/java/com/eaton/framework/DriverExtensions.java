package com.eaton.framework;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverExtensions {
    
    private WebDriverWait driverWait;
    private WebDriver driver;
    private static final Integer TIME_OUT_SECONDS = 5;
    
    public DriverExtensions(WebDriver driver) {
        this.driver = driver;
        this.driverWait = new WebDriverWait(this.driver, TIME_OUT_SECONDS);
    }
    
    public WebDriverWait getDriverWait() {
        return this.driverWait;
    }
    
    public WebDriver getDriver() {
        return this.driver;
    }
    
    /**
     * @param timeOutSeconds if Optional.empty() then will wait 5 seconds or will use the amount of seconds passed in.
     * @return WebDriverWait
     */
    public WebDriverWait getDriverWait(Optional<Integer> timeOutSeconds) {
        Integer timeOut = timeOutSeconds.orElse(null);
        
        if ( timeOut == null ) return driverWait;
        
        return new WebDriverWait(this.driver, timeOut);
    }
    
    /**
     * 
     * @param by The selector used to find the element
     * @param timeOutSeconds Passing in empty Optional uses default timeout, Passing in 0 uses no wait Passing in anything greater than 0 uses that as the timeout
     * @return
     */
    public WebElement findElement(By by, Optional<Integer> timeOutSeconds) {        
        Integer timeOut = timeOutSeconds.orElse(null);
        
        if ( timeOut == null ) { return this.driverWait.until(drv -> drv.findElement(by)); }
        
        if ( timeOut == 0 ) { 
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); 
            
            return this.driver.findElement(by); }                 
        
        driverWait.withTimeout(Duration.ofSeconds(timeOut));
        
        WebElement element = driverWait.until(drv -> drv.findElement(by));
        
        driverWait.withTimeout(Duration.ofSeconds(TIME_OUT_SECONDS));
        
        return element;
    }
    
    /**
     * 
     * @param parentElement The parent element used to find the element
     * @param by The selector used to find the element
     * @param timeOutSeconds Passing in empty Optional uses default timeout, Passing in 0 uses no wait Passing in anything greater than 0 uses that as the timeout
     * @return
     */
    public WebElement findElement(WebElement parentElement, By by, Optional<Integer> timeOutSeconds) {
        
        if(parentElement == null) return findElement(by, timeOutSeconds);
        
        Integer timeOut = timeOutSeconds.orElse(null);
        
        if ( timeOut == null )  return this.driverWait.until(drv -> parentElement.findElement(by)); 
        
        if ( timeOut == 0 )  return parentElement.findElement(by);         
        
        driverWait.withTimeout(Duration.ofSeconds(timeOut));
        
        WebElement element = driverWait.until(drv -> parentElement.findElement(by));
        
        driverWait.withTimeout(Duration.ofSeconds(TIME_OUT_SECONDS));
        
        return element;                                
    }
    
    public List<WebElement> findElements(By by, Optional<Integer> timeOutSeconds) {
        Integer timeOut = timeOutSeconds.orElse(null);
        
        if ( timeOut == null ) { return this.driverWait.until(drv -> drv.findElements(by)); }
        
        if ( timeOut == 0 ) { 
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); 
            
            return this.driver.findElements(by); }       
        
        driverWait.withTimeout(Duration.ofSeconds(timeOut));
        
        List<WebElement> element = driverWait.until(drv -> drv.findElements(by));
        
        driverWait.withTimeout(Duration.ofSeconds(TIME_OUT_SECONDS));
        
        return element;
    }
    
    public List<WebElement> findElements(WebElement parentElement, By by, Optional<Integer> timeOutSeconds) {
        if (parentElement == null) return findElements(by, timeOutSeconds);
        
        Integer timeOut = timeOutSeconds.orElse(null);
        
        if ( timeOut == null ) { return this.driverWait.until(drv -> parentElement.findElements(by)); }
                        
        if ( timeOut == 0 ) { return parentElement.findElements(by); }        
        
        driverWait.withTimeout(Duration.ofSeconds(timeOut));
        
        List<WebElement> element = driverWait.until(drv -> parentElement.findElements(by));
        
        driverWait.withTimeout(Duration.ofSeconds(TIME_OUT_SECONDS));
        
        return element;
    }
    
    public void waitUntilElementVisibleByCssLocator(String by) {
        driverWait.withTimeout(Duration.ofSeconds(1));
        
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(by)));
    }
    
    public void waitUntilElementClickable(WebElement element) {
        driverWait.withTimeout(Duration.ofSeconds(1));
        
        driverWait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    public void waitUntilElementInvisible(WebElement element) {
        driverWait.withTimeout(Duration.ofSeconds(1));
        
        driverWait.until(ExpectedConditions.invisibilityOf(element));
    }
    
    public void waitUntilElementInvisibleByCssLocator(String locator) {
        driverWait.withTimeout(Duration.ofSeconds(1));
        
        driverWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(locator)));
    }  
    
    public void waitUntilStalenessOfElement(WebElement element) {
        driverWait.withTimeout(Duration.ofSeconds(3));
        
        driverWait.until(ExpectedConditions.stalenessOf(element));
    } 
    
    public void waitUntilVisibilityOfElements(List<WebElement> elements) {
        driverWait.withTimeout(Duration.ofSeconds(3));
        
        driverWait.until(ExpectedConditions.visibilityOfAllElements(elements));
    } 
}

