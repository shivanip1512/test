package com.cannontech.web.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.user.YukonUserContext;

@Controller
@RequestMapping("/errorCodes/*")
public class ErrorCodesViewerController {
    
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @RequestMapping
    public String view(ModelMap model, YukonUserContext userContext)
            throws ServletRequestBindingException {
        
        Iterable<DeviceErrorDescription> allErrors = deviceErrorTranslatorDao.getAllErrors();
        model.addAttribute("allErrors", allErrors);
        return "errorCodes/errorCodes.jsp";
    }
    
    @Autowired
    public void setDeviceErrorTranslatorDao(DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }
}