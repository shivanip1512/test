package com.cannontech.selenium.core.locators;


public class CssLocator extends Locator {

    private final static String LOCATOR_PREFIX = "css=";
    private final static String APPENDER_FRAGMENT = " ";
    private final String cssLocatorPath;
    
    public CssLocator(String cssLocatorPath) {
        this.cssLocatorPath = cssLocatorPath;
    }
    
    @Override
    public Locator append(String cssFragment) {
        return new CssLocator(this.cssLocatorPath + APPENDER_FRAGMENT + cssFragment);
    }
    
    @Override
    public Locator append(Locator cssLocatorPath) {
        return new CssLocator(this.cssLocatorPath + APPENDER_FRAGMENT + cssLocatorPath.getLocatorPath());
    }
    
    @Override
    protected String getLocatorPath() {
        return cssLocatorPath;
    }
    
    @Override
    public String generateLocatorString() {
        return LOCATOR_PREFIX + cssLocatorPath;
    }
    
    @Override
    public String toString() {
        return generateLocatorString();
    }

}