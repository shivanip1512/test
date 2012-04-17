package com.cannontech.selenium.solvents.metering.service.impl;

import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.metering.service.MeterSeleniumService;

public class MeterSeleniumServiceImpl implements MeterSeleniumService {

    @Override
    public void findMeterByName(String meterName) {
        CommonSolvent common = new CommonSolvent();
        
        common.clickLinkByName("Home");
        common.clickLinkByName("Metering");
        common.assertEqualsTextIsPresent("Device Name:");
        common.enterInputTextByFormId("filterForm", "Device Name", meterName);
        common.clickButtonBySpanText("Search");
    }

    @Override
    public void readMeterInfoWidget() {
        CommonSolvent common = new CommonSolvent();
        //changes
        
        
        // This needs to locate by ID.  There are 3 buttons labeled 'Read Now' on this page, 
        // this clicks the first one found. Luckily that is the right one in this case.
        common.clickButtonBySpanTextWithAjaxWait("Read Now");  
        common.assertEqualsTextIsNotPresent("There was an error reading the meter:");
        common.assertEqualsTextIsPresent("Successful Read");
    }

}
