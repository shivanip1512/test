package com.cannontech.selenium.test.metering;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSelenium;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.metering.service.MeterSeleniumService;
import com.cannontech.selenium.solvents.metering.service.impl.MeterSeleniumServiceImpl;

public class TestPolyphaseMeterInfoWidgetSelenium extends SolventSeleniumTestCase{
    private MeterSeleniumService meterService = new MeterSeleniumServiceImpl();
    private SolventSelenium solventSelenium;
    
    private final String POLYPHASE_METER = "*Cart MCT-430S4";
    
    @Before
    public void init(){
        solventSelenium = start();
    }
    @Test
    public void testPolyphaseReadNow() {
        LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
        try {
            loginLogoutSolvent.cannonLogin(LoginLogoutSolvent.DEFAULT_CTI_LOGIN);
            meterService.findMeterByName(POLYPHASE_METER);
            meterService.readMeterInfoWidget();
        } finally {
            loginLogoutSolvent.yukonLogout();
        }
    }
}
