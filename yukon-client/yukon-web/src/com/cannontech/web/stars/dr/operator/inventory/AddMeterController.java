package com.cannontech.web.stars.dr.operator.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE)
@RequestMapping("operator/inventory/addMeter/*")
public class AddMeterController {
    
    @Autowired private PaoDao paoDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareUiService hardwareUiService;

    @RequestMapping("view")
    public String view(ModelMap model, LiteYukonUser user, FlashScope flash, Integer mctId) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(mctId);
        String meterName = liteYukonPAO.getPaoName();
        hardwareEventLogService.hardwareMeterCreationAttempted(user, meterName, EventSource.OPERATOR);
        
        /* Tracking meters as MCTs */
        int inventoryId = hardwareUiService.addYukonMeter(mctId, null, user);
        model.addAttribute("inventoryId", inventoryId);
        
        model.addAttribute("showInstallNotes", false);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.yukonMeterAdded", meterName));

        return "redirect:../view";
    }
    
}