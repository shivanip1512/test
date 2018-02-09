package com.cannontech.web.amr.vee.monitor;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/vee/monitor/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
public class ValidationMonitorEditorController {

    private final static Logger log = YukonLogManager.getLogger(ValidationMonitorEditorController.class);
    @Autowired private ValidationMonitorDao validationMonitorDao;
    @Autowired private ValidationMonitorService validationMonitorService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceGroupService deviceGroupService;

    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(ModelMap model, Integer validationMonitorId, String editError, String name, String deviceGroupName, 
                       Double threshold, Boolean reread, Double slopeError, Double peakHeightMinimum, Boolean setQuestionable){
        if(validationMonitorId == null) {
            validationMonitorId = -1;
        }
        ValidationMonitor validationMonitor = null;
        try {
            if( validationMonitorId > -1 ) {
                validationMonitor = validationMonitorDao.getById(validationMonitorId);
                model.addAttribute("mode", PageEditMode.EDIT);
            } else {
                validationMonitor = new ValidationMonitor();
                model.addAttribute("mode", PageEditMode.CREATE);
            }
            
            //Use entered values instead of existing values if present. 
            //When validation failed, they don't have to retype everything.
            if (name == null) {
                name = validationMonitor.getName();
            }
            if (deviceGroupName == null) {
                deviceGroupName = validationMonitor.getDeviceGroupName();
            }
            if (threshold == null) {
                threshold = validationMonitor.getReasonableMaxKwhPerDay();
            }
            if (reread == null) {
                reread = validationMonitor.isReReadOnUnreasonable();
            }
            if(slopeError == null){
                slopeError = validationMonitor.getKwhSlopeError();
            }
            if(peakHeightMinimum == null){
                peakHeightMinimum = validationMonitor.getPeakHeightMinimum();
            }
            if(setQuestionable == null){
                setQuestionable = validationMonitor.isSetQuestionableOnPeak();
            }
        } catch (ValidationMonitorNotFoundException e ){
            model.addAttribute("editError", e.getMessage());
            return "redirect:edit";
        }
        
        model.addAttribute("editError", editError);
        model.addAttribute("validationMonitorId", validationMonitorId);
        model.addAttribute("name", name);
        model.addAttribute("editPageDesc", name);
        model.addAttribute("deviceGroupName", deviceGroupName);
        model.addAttribute("threshold", threshold);
        model.addAttribute("reread", reread);
        model.addAttribute("slopeError", slopeError);
        model.addAttribute("peakHeightMinimum", peakHeightMinimum);
        model.addAttribute("setQuestionable", setQuestionable);
        
        model.addAttribute("validationMonitor", validationMonitor);
        
        return "vee/monitor/edit.jsp";
    }
    
    @RequestMapping(value="update", method=RequestMethod.POST)
    public String update(ModelMap model, int validationMonitorId, String name, String deviceGroupName, Double threshold,
                         Boolean reread, Double slopeError, Double peakHeightMinimum, Boolean setQuestionable, YukonUserContext userContext) throws Exception, ServletException {
        
        String editError = null;
        
        boolean isNewMonitor = true;
        ValidationMonitor validationMonitor;
        try {
            if (validationMonitorId < 0) {
                validationMonitor = new ValidationMonitor();
            } else {
                validationMonitor = validationMonitorDao.getById(validationMonitorId);
                isNewMonitor = false;
            }
        } catch (ValidationMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
            return "redirect:edit";
        }
        
        /* Enable the monitor. */
        if (isNewMonitor) {
            validationMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
        }
        
        /* Validate inputs. */
        if (StringUtils.isBlank(name)) {
            editError = "Name required.";
        } else if (isNewMonitor && validationMonitorDao.processorExistsWithName(name)) { /* New monitor, check name. */
            editError = "Validation Monitor with name \"" + name + "\" already exists.";
        } else if (!isNewMonitor && !validationMonitor.getName().equals(name) && validationMonitorDao.processorExistsWithName(name)) { /* Existing monitor, new name, check name. */
            editError = "Validation Monitor with name \"" + name + "\" already exists.";
        } else if (!DeviceGroupUtil.isValidName(name)) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            editError = accessor.getMessage("yukon.web.error.deviceGroupName.containsIllegalChars");
        } else if (deviceGroupService.findGroupName(deviceGroupName) == null) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            editError = accessor.getMessage("yukon.web.modules.amr.invalidGroupName");
        } else if (threshold == null || threshold < 0) {
            editError= "Unreasonable Threshold must be greater than or equal to 0.";
        } else if (slopeError == null || slopeError < 0) {
            editError = "Slope Error must be greater than or equal to 0.";
        } else if (peakHeightMinimum == null || peakHeightMinimum < 0) {
            editError = "Peak Height Minimum must be greater than or equal to 0.";
        }
        
        /* Editing error. redirect to edit page with error. */
        if (editError != null) {
            model.addAttribute("editError", editError);
            model.addAttribute("validationMonitorId", validationMonitorId);
            model.addAttribute("name", name);
            model.addAttribute("deviceGroupName", deviceGroupName);
            model.addAttribute("threshold", threshold);
            model.addAttribute("reread", reread);
            model.addAttribute("reread", reread == null ? false : true);
            model.addAttribute("slopeError", slopeError);
            model.addAttribute("peakHeightMinimum", peakHeightMinimum);
            model.addAttribute("setQuestionable", setQuestionable == null ? false : true);
            return "redirect:edit";
            
        /* Input validation passed, save or update. */
        } else {
            validationMonitor.setName(name);
            validationMonitor.setDeviceGroupName(deviceGroupName);
            validationMonitor.setReasonableMaxKwhPerDay(threshold);
            validationMonitor.setReReadOnUnreasonable(reread == null ? false : true);
            validationMonitor.setKwhSlopeError(slopeError);
            validationMonitor.setPeakHeightMinimum(peakHeightMinimum);
            validationMonitor.setQuestionableOnPeak(setQuestionable == null ? false : true);
            
            log.debug("Saving validationMonitor: isNewMonitor=" + isNewMonitor + ", validationMonitor=" + validationMonitor.toString());
            if (isNewMonitor) {
                validationMonitorService.create(validationMonitor);
            } else {
                validationMonitorService.update(validationMonitor);
            }
            return "redirect:/meter/start";
        }
    }
    
    @RequestMapping(value="delete", method=RequestMethod.POST)
    public String delete(ModelMap model, int deleteValidationMonitorId) throws Exception, ServletException {
        
        if(!validationMonitorService.delete(deleteValidationMonitorId)){
            model.addAttribute("editError", "Could not delete validation monitor.  Monitor with id: " + deleteValidationMonitorId + " not found.");
            return "redirect:edit";
        }
        return "redirect:/meter/start";
    }
    
    @RequestMapping(value="toggleEnabled", method=RequestMethod.POST)
    public String toggleEnabled(ModelMap model, int validationMonitorId) throws Exception, ServletException {
        try {
        	validationMonitorService.toggleEnabled(validationMonitorId);
            model.addAttribute("validationMonitorId", validationMonitorId);
        } catch (ValidationMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
        }
        
        return "redirect:edit";
    }
}