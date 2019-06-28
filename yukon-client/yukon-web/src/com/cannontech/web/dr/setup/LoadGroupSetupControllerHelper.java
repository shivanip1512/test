package com.cannontech.web.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.mbean.ServerDatabaseCache;
import com.google.common.collect.ImmutableList;

@Service
public class LoadGroupSetupControllerHelper {

    @Autowired ServerDatabaseCache cache;

    /**
     * Each load group can set its model attributes here.
     */
    public void buildModelMap(PaoType type, ModelMap model) {
        switch (type) {
        case LM_GROUP_EXPRESSCOMM:
            model.addAttribute("routes", cache.getAllRoutes());
            break;
        case LM_GROUP_RFN_EXPRESSCOMM:
            break;
        case LM_GROUP_ITRON:
            model.addAttribute("relayIds", ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8));
            break;
        case LM_GROUP_DIGI_SEP:
            model.addAttribute("deviceClassList", SepDeviceClass.values());
            break;
        }
    }
}
