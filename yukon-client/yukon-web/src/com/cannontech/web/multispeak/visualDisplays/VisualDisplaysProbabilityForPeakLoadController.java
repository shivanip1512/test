package com.cannontech.web.multispeak.visualDisplays;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.web.multispeak.visualDisplays.model.PowerSupplier;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSuppliersEnum;
import com.cannontech.web.multispeak.visualDisplays.service.PowerSupplierFactory;
import com.cannontech.web.multispeak.visualDisplays.service.VisualDisplaysService;

public class VisualDisplaysProbabilityForPeakLoadController extends VisualDisplaysBaseController {

	
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
        
        // current time
        Date now  = new Date();
        mav.addObject("now", now);
        
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
