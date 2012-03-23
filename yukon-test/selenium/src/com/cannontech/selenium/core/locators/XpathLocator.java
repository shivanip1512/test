package com.cannontech.selenium.core.locators;


public class XpathLocator extends Locator {

    private final static String LOCATOR_PREFIX = "";
    private final static String APPENDER_FRAGMENT = "//";
    private final String locatorPath;
    
    public XpathLocator(String xpathLocatorPath) {
        this.locatorPath = xpathLocatorPath;
    }
    
    @Override
    public Locator append(String xpathFragment) {
        return new XpathLocator(this.locatorPath + APPENDER_FRAGMENT + xpathFragment);
    }
    
    @Override
    public Locator append(Locator xpathLocatorPath) {
        return new XpathLocator(this.locatorPath + APPENDER_FRAGMENT + xpathLocatorPath.getLocatorPath());
    }
    
    @Override
    protected String getLocatorPath() {
        return locatorPath;
    }
    
    @Override
    public String generateLocatorString() {
        return LOCATOR_PREFIX + locatorPath;
    }
    
    @Override
    public String toString() {
        return generateLocatorString();
    }

}