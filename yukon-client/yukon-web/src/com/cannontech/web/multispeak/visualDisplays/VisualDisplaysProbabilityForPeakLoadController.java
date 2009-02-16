package com.cannontech.web.multispeak.visualDisplays;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSupplier;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSuppliersEnum;
import com.cannontech.web.multispeak.visualDisplays.service.PowerSupplierFactory;
import com.cannontech.web.multispeak.visualDisplays.service.VisualDisplaysService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MSP_LM_MAPPING_SETUP)
public class VisualDisplaysProbabilityForPeakLoadController extends MultiActionController {

	
	private VisualDisplaysService visualDisplaysService;
	private PowerSupplierFactory powerSupplierFactory;
	
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("visualDisplays/probabilityForPeakLoad.jsp");
        
        // available power suppliers
        List<PowerSuppliersEnum> availablePowerSuppliers = visualDisplaysService.getAvailablePowerSuppliers();
        
        List<PowerSupplier> powerSuppliers = new ArrayList<PowerSupplier>();
        for (PowerSuppliersEnum powerSupplierEnum : availablePowerSuppliers) {
        	
        	PowerSupplier powerSupplier = powerSupplierFactory.getPowerSupplierForType(powerSupplierEnum);
        	powerSuppliers.add(powerSupplier);
        }
        mav.addObject("powerSuppliers", powerSuppliers);
        
        return mav;
    }
	
	@Autowired
	public void setVisualDisplaysService(VisualDisplaysService visualDisplaysService) {
		this.visualDisplaysService = visualDisplaysService;
	}

	@Autowired
	public void setPowerSupplierFactory(PowerSupplierFactory powerSupplierFactory) {
		this.powerSupplierFactory = powerSupplierFactory;
	}
}
