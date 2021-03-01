package com.eaton.pages.support;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SupportBundlePage extends PageBase {

	//Public
    public static final String DEFAULT_URL = Urls.Support.SUPPORT_BUNDLE;
    public static final String PAGE_TITLE = "Support Bundle";
    public static final By SUPPORT_BUNDLE_ITEMS_CONTAINER_SELECTOR = By.cssSelector("#mainDiv .stacked");
    public static final By STATUS_FIELD_SELECTOR = By.cssSelector(".name-value-table:nth-child(2) .value");

    //Private
    private WebElement supportBundleItemsContainer;
    private WebElement statusField;
    
    //================================================================================
    // Constructors Section
    //================================================================================
    
    public SupportBundlePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL;
    }
    
    //================================================================================
    // Private Section
    //================================================================================
    
    //================================================================================
    // Public Methods
    //================================================================================
    
    public Boolean pollSupportBundleItemsStatusNot(String value) {
	   	 WebDriverWait wait = new WebDriverWait(driverExt.getDriver(), 40);
	   	 Boolean result = false;
	   	 try {
	   		result = wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(SUPPORT_BUNDLE_ITEMS_CONTAINER_SELECTOR, value)));
	   	 }catch(TimeoutException timeoutEx) {
	   	 }
	   	 
	   	 return result;
    }
    
    public Boolean pollSupportBundleStatus(String value) {
	   	 WebDriverWait wait = new WebDriverWait(driverExt.getDriver(), 40);
	   	 Boolean result = false;
	   	 try {
	   		 result = wait.until(ExpectedConditions.textToBePresentInElementLocated(STATUS_FIELD_SELECTOR, value));
	   	 }catch(TimeoutException timeoutEx) {
	   	 }
	   	 
	   	 return result;
    }
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================

    public WebElement getSupportBundleItemsContainer() {
    	supportBundleItemsContainer = driverExt.findElement(SUPPORT_BUNDLE_ITEMS_CONTAINER_SELECTOR, Optional.empty());
    	return supportBundleItemsContainer;
    }
    
    public WebElement getSupportBundleStatus() {
    	statusField = driverExt.findElement(STATUS_FIELD_SELECTOR, Optional.empty());
    	return statusField;
    }  
}
