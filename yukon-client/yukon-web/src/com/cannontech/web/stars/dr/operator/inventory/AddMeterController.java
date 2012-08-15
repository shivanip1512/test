package com.cannontech.web.stars.dr.operator.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.roles.yukon.EnergyCompanyRole.MeteringType;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE)
@RequestMapping("operator/inventory/addMeter/*")
public class AddMeterController {
    
    @Autowired private PaoDao paoDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;

    @RequestMapping
    public String view(ModelMap model, LiteYukonUser user, FlashScope flash, Integer mctId) {
        String view = "redirect:../view";
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        MeteringType value= ecRolePropertyDao.getPropertyEnumValue(YukonRoleProperty.METER_MCT_BASE_DESIGNATION, EnergyCompanyRole.MeteringType.class,  ec);
        
        if (value == MeteringType.yukon) {
            LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(mctId);
            String meterName = liteYukonPAO.getPaoName();
            hardwareEventLogService.hardwareMeterCreationAttemptedByOperator(user, meterName);
            
            /* Tracking meters as MCTs */
            int inventoryId = hardwareUiService.addYukonMeter(mctId, null, user);
            model.addAttribute("inventoryId", inventoryId);
            
            model.addAttribute("showInstallNotes", false);
            
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.yukonMeterAdded", meterName));
        } else {
            /* Tracking meters through inventory tables alone, call these 'meter profiles' */
            
            view = "operator/inventory/meterProfile.jsp";
        }
        return view;
    }
    
}