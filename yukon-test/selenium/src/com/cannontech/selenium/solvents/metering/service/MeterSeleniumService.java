package com.cannontech.selenium.solvents.metering.service;

public interface MeterSeleniumService  {
    /**
     * This method attempts to find a meter by searching for it by name.
     */
    public void findMeterByName(String meterName);
    
    public void readMeterInfoWidget();

}
