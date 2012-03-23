package com.cannontech.selenium.core.locators;

public abstract class Locator {
    
    /**
     * This method adds the supplied string to the locator using the locators APPENDER_FRAGMENT.
     */
    public abstract Locator append(String locatorFragment);
    
    /**
     * This method adds the supplied locator to the locator using the locators APPENDER_FRAGMENT.
     * @param cssLocatorPath
     * @return
     */
    public abstract Locator append(Locator cssLocatorPath);

    /** 
     * This method is used for internal process and should never be made public.  If you want the locator
     * string use generateLocatorString()
     */
    protected abstract String getLocatorPath();

    /**
     * This method returns the full locator string used for selenium.
     * This method also takes care of add the prefix necessary for a selenium type. 
     */
    public abstract String generateLocatorString();

}