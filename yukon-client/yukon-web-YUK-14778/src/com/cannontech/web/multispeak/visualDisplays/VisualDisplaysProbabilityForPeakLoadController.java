package com.cannontech.web.multispeak.visualDisplays;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSupplier;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSuppliersEnum;
import com.cannontech.web.multispeak.visualDisplays.service.PowerSupplierFactory;
import com.cannontech.web.multispeak.visualDisplays.service.VisualDisplaysService;
import com.cannontech.web.security.annotation.CheckGlobalSetting;

@Controller
@CheckGlobalSetting(GlobalSettingType.MSP_LM_MAPPING_SETUP)
public class VisualDisplaysProbabilityForPeakLoadController {
    
    @Autowired private VisualDisplaysService visualDisplaysService;
    @Autowired private PowerSupplierFactory powerSupplierFactory;
    @Autowired private DateFormattingService dateFormatting;
    
    @RequestMapping("visualDisplays/probabilityForPeak/home")
    public String home(ModelMap model) {
        
        // available power suppliers
        List<PowerSuppliersEnum> suppliers = visualDisplaysService.getAvailablePowerSuppliers();
        
        List<PowerSupplier> powerSuppliers = new ArrayList<>();
        for (PowerSuppliersEnum supplier : suppliers) {
            PowerSupplier powerSupplier = powerSupplierFactory.getPowerSupplierForType(supplier);
            powerSuppliers.add(powerSupplier);
        }
        model.addAttribute("powerSuppliers", powerSuppliers);
        
        // current time
        Date now  = new Date();
        model.addAttribute("now", now);
        
        return "visualDisplays/probabilityForPeakLoad.jsp";
    }
    
}