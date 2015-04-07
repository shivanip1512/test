package com.cannontech.web.stars.dr.hardware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.service.DeviceVerificationService;
import com.cannontech.util.ServletUtil;

public class DeviceVerificationController extends MultiActionController {
    private DeviceVerificationService verificationService;

    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        final ModelAndView mav = new ModelAndView();
        mav.setViewName("hardware/deviceverification/Verification.jsp");
        return mav;
    }

    public ModelAndView verify(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        final String serialNumber = ServletRequestUtils.getRequiredStringParameter(request, "serialnumber");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        if (serialNumber.equals("")) {
            String message = "All fields are required.";
            ModelAndView mav = createStatusView(message);
            return mav;
        }
        
        boolean result = verificationService.verify(user, serialNumber);
        if (!result) {
            String message = "FAILED";
            ModelAndView mav = createStatusView(message);
            return mav;
        }
        
        String message = "SUCCESS";
        ModelAndView mav =  createStatusView(message);
        return mav;
    }

    private ModelAndView createStatusView(final String message) {
        final ModelAndView mav = new ModelAndView();
        mav.addObject("message", message);
        mav.setViewName("hardware/deviceverification/Status.jsp");
        return mav;
    }
    
    public void setDeviceVerificationService(DeviceVerificationService verificationService) {
        this.verificationService = verificationService;
    }
}
