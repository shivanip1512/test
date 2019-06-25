package com.cannontech.web.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.cannontech.common.pao.PaoType;
import com.cannontech.mbean.ServerDatabaseCache;

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
            break;
        }
    }
}
