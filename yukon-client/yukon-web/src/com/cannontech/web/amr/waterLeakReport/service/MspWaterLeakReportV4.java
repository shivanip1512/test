package com.cannontech.web.amr.waterLeakReport.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.model.Address;
import com.cannontech.msp.beans.v5.enumerations.PhoneTypeKind;
import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.service.v5.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MspWaterLeakReportV4 extends MspWaterLeakReport {

    @Autowired private MeterDao meterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakCustomerInfoService mspCustomerInfoService;
    @Autowired private MultispeakFuncs multispeakFuncs;

    private Cache<Integer, MspMeterAccountInfo> mspMeterAccountInfoMap =
        CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(1, TimeUnit.HOURS).build();

    private class MspMeterAccountInfo {
        Customer mspCustomer; 
        ServiceLocation mspServLoc;
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
            if (mspMeterAccountInfo.mspCustomer.getContactInfo() != null) {
                mspMeterAccountInfo.phoneNumbers =
                    mspCustomerInfoService.getPhoneNumbers(
                        mspMeterAccountInfo.mspCustomer.getContactInfo().getPhoneNumbers(), userContext);
                mspMeterAccountInfo.emailAddresses =
                    mspCustomerInfoService.getEmailAddresses(
                        mspMeterAccountInfo.mspCustomer.getContactInfo().getEMailAddresses(), userContext);
            }
            mspMeterAccountInfoMap.put(paoId, mspMeterAccountInfo);

        }
        model.addAttribute("mspPhoneNumbers", mspMeterAccountInfo.phoneNumbers);
        model.addAttribute("mspEmailAddresses", mspMeterAccountInfo.emailAddresses);
        model.addAttribute("mspCustomer", mspMeterAccountInfo.mspCustomer);
        model.addAttribute("mspServLoc", mspMeterAccountInfo.mspServLoc);
        
        if (mspMeterAccountInfo.mspCustomer.getContactInfo() != null
            && mspMeterAccountInfo.mspCustomer.getContactInfo().getAddressItems() != null) {
            List<Address> custAddressInfo = multispeakFuncs.getAddressList(mspMeterAccountInfo.mspCustomer.getContactInfo().getAddressItems());
            model.addAttribute("custAddressInfo", custAddressInfo.get(0));
        }

        if (mspMeterAccountInfo.mspServLoc != null && mspMeterAccountInfo.mspServLoc.getContactInfo() != null
            && mspMeterAccountInfo.mspServLoc.getContactInfo().getAddressItems() != null) {
            List<Address> servLocAddresses = multispeakFuncs.getAddressList(mspMeterAccountInfo.mspServLoc.getContactInfo().getAddressItems());
            model.addAttribute("servLocAddresses", servLocAddresses.get(0));
        }
        
        Map<PhoneTypeKind, String> primaryContact = multispeakFuncs.getPrimaryContacts(mspMeterAccountInfo.mspCustomer);
        model.addAttribute("homePhone", primaryContact.get(PhoneTypeKind.HOME));
        model.addAttribute("dayPhone", primaryContact.get(PhoneTypeKind.BUSINESS));

        setupMspVendorModelInfo(userContext, model);
        return "waterLeakReport/accountInfoAjax.jsp";
    }
}