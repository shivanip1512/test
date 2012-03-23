package com.cannontech.selenium.core.locators;


public class LinkLocator extends Locator {

    private final static String LOCATOR_PREFIX = "link=";
    private final static String APPENDER_FRAGMENT = "";
    private final String linkLocatorPath;
    
    public LinkLocator(String linkLocatorPath) {
        this.linkLocatorPath = linkLocatorPath;
    }
    
    @Override
    public Locator append(String linkFragment) {
        return new LinkLocator(this.linkLocatorPath + APPENDER_FRAGMENT + linkFragment);
    }
    
    @Override
    public Locator append(Locator linkLocatorPath) {
        return new LinkLocator(this.linkLocatorPath + APPENDER_FRAGMENT + linkLocatorPath.getLocatorPath());
    }
    
    @Override
    protected String getLocatorPath() {
        return linkLocatorPath;
    }
    
    @Override
    public String generateLocatorString() {
        return LOCATOR_PREFIX + linkLocatorPath;
    }
    
    @Override
    public String toString() {
        return generateLocatorString();
    }

}