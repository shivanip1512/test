package com.cannontech.web.stars.dr.hardware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.stars.dr.hardware.service.ThermostatActivationService;

public class ThermostatActivationController extends MultiActionController {
    private ThermostatActivationService thermostatActivationService;
    
    public void setThermostatActivationService(final ThermostatActivationService thermostatActivationService) {
        this.thermostatActivationService = thermostatActivationService;
    }
    
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        String accountNumber = ServletRequestUtils.getStringParameter(request, "accountnumber");
        String serialNumber = ServletRequestUtils.getStringParameter(request, "serialnumber");
        
        mav.setViewName("hardware/thermostat/Activation.jsp");
        mav.addObject("accountNumber", accountNumber);
        mav.addObject("serialNumber", serialNumber);
        return mav;
    }
    
    public ModelAndView activate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        final String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "accountnumber");
        final String serialNumber = ServletRequestUtils.getRequiredStringParameter(request, "serialnumber");

        if (accountNumber.equals("") || serialNumber.equals("")) {
            ModelAndView mav = 
                createStatusView("Both Account and Serial Number are required.", Boolean.TRUE, null, null);
            return mav;
        }
        
        boolean result = this.thermostatActivationService.activate(accountNumber, serialNumber);
        if (!result) {
            String message = "Activation for SerialNumber: " + serialNumber + " for AccountNumber: " + accountNumber + " failed.";
            ModelAndView mav = createStatusView(message, Boolean.TRUE, accountNumber, serialNumber);
            return mav;
        }
        
        String message = "Thermostat with Serial Number: " + serialNumber + " activated successfully.";
        ModelAndView mav =  createStatusView(message, Boolean.FALSE, null, null);
        return mav;
    }
    
    private ModelAndView createStatusView(final String message, final Boolean error, final String accountNumber, final String serialNumber) {
        final ModelAndView mav = new ModelAndView();
        mav.addObject("message", message);
        mav.addObject("error", error);
        mav.addObject("accountNumber", accountNumber);
        mav.addObject("serialNumber", serialNumber);
        mav.setViewName("hardware/thermostat/Status.jsp");
        return mav;
    }
    
}
