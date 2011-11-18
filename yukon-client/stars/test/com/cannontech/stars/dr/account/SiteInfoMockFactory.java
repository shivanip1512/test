package com.cannontech.stars.dr.account;

import com.cannontech.common.model.SiteInformation;

public class SiteInfoMockFactory {
    public static SiteInformation getSiteInfo1() {
        SiteInformation siteInfo1 =  new SiteInformation();
        
        siteInfo1.setSubstationName("SuperStation");
        siteInfo1.setFeeder("SuperFeeder");
        siteInfo1.setPole("SouthPole");
        siteInfo1.setServiceVoltage("120");
        siteInfo1.setTransformerSize("2400");

        return siteInfo1;
    }
}