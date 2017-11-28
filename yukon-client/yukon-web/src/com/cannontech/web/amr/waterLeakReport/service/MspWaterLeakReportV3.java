package com.cannontech.web.amr.waterLeakReport.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.model.Address;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.service.v3.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MspWaterLeakReportV3 extends MspWaterLeakReport {

    @Autowired private MeterDao meterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakCustomerInfoService mspCustomerInfoService;
    @Autowired private MultispeakFuncs multispeakFuncs;

    private Cache<Integer, MspMeterAccountInfo> mspMeterAccountInfoMap =
        CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(1, TimeUnit.HOURS).build();

    private class MspMeterAccountInfo {
        com.cannontech.msp.beans.v3.Customer mspCustomer;
        com.cannontech.msp.beans.v3.ServiceLocation mspServLoc;
        com.cannontech.msp.beans.v3.Meter mspMeter;
        List<String> phoneNumbers;
        List<String> emailAddresses;
    }

    @Override
    public String getCisDetails(ModelMap model, YukonUserContext userContext, int paoId, MultispeakVendor mspVendor) {

        MspMeterAccountInfo mspMeterAccountInfo = mspMeterAccountInfoMap.getIfPresent(paoId);
        if (mspMeterAccountInfo == null) {
            YukonMeter meter = meterDao.getForId(paoId);

            mspMeterAccountInfo = new MspMeterAccountInfo();
            mspMeterAccountInfo.mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
            mspMeterAccountInfo.mspServLoc = mspObjectDao.getMspServiceLocation(meter, mspVendor);
            mspMeterAccountInfo.mspMeter = mspObjectDao.getMspMeter(meter, mspVendor);
            mspMeterAccountInfo.phoneNumbers =
                mspCustomerInfoService.getPhoneNumbers(mspMeterAccountInfo.mspCustomer, userContext);
            mspMeterAccountInfo.emailAddresses =
                mspCustomerInfoService.getEmailAddresses(mspMeterAccountInfo.mspCustomer, userContext);
            mspMeterAccountInfoMap.put(paoId, mspMeterAccountInfo);
        }

        model.addAttribute("mspPhoneNumbers", mspMeterAccountInfo.phoneNumbers);
        model.addAttribute("mspEmailAddresses", mspMeterAccountInfo.emailAddresses);
        model.addAttribute("mspCustomer", mspMeterAccountInfo.mspCustomer);
        model.addAttribute("mspServLoc", mspMeterAccountInfo.mspServLoc);
        model.addAttribute("mspMeter", mspMeterAccountInfo.mspMeter);
        Address custAddress = multispeakFuncs.getCustomerAddressInfo(mspMeterAccountInfo.mspCustomer);
        model.addAttribute("custAddress", custAddress);
        Address servLocAddress = multispeakFuncs.getServLocAddressInfo(mspMeterAccountInfo.mspServLoc);
        model.addAttribute("servLocAddress", servLocAddress);
        setupMspVendorModelInfo(userContext, model);
        return "waterLeakReport/accountInfoAjax.jsp";
    }

}