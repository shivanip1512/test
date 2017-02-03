package com.cannontech.web.amr.waterLeakReport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public abstract class MspWaterLeakReport {

    @Autowired public GlobalSettingDao globalSettingDao;

    public void setupMspVendorModelInfo(YukonUserContext userContext, ModelMap model) {
        int vendorId = globalSettingDao.getInteger(GlobalSettingType.MSP_PRIMARY_CB_VENDORID);
        boolean hasVendorId = vendorId <= 0 ? false : true;
        model.addAttribute("hasVendorId", hasVendorId);
    }


    /**
     * Make multispeak calls to get cis information to display.
     * 
     * @param model - Model object to update.
     * @param userContext - User context.
     * @param paoId - paoid
     * @param mspVendor - Multispeak CB client
     */
    protected abstract String getCisDetails(ModelMap model, YukonUserContext userContext, int paoId,
            MultispeakVendor mspVendor);
}
