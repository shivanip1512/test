package com.cannontech.web.common.vee.monitor;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/vee/monitor/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
public class ValidationMonitorEditorController {

    private ValidationMonitorDao validationMonitorDao;
    private ValidationMonitorService validationMonitorService;
    private Logger log = YukonLogManager.getLogger(ValidationMonitorEditorController.class);

    @RequestMapping
    public String edit(ModelMap model, Integer validationMonitorId, String editError, String name, String deviceGroupName, 
                       Double threshold, Boolean reread, Double slopeError, Double peakHeightMinimum, Boolean setQuestionable){
        if(validationMonitorId == null) validationMonitorId = -1;
        ValidationMonitor validationMonitor = null;
        try {
            if( validationMonitorId > -1 ){
                validationMonitor = validationMonitorDao.getById(validationMonitorId);
                model.addAttribute("mode", PageEditMode.EDIT);
            } else {
                validationMonitor = new ValidationMonitor();
                model.addAttribute("mode", PageEditMode.CREATE);
            }
            
            /* Use entered values instead of existing values if present. */
            /* When validation failed, they don't have to retype everything. */
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
    
    @RequestMapping(method=RequestMethod.POST)
    public String update(ModelMap model, int validationMonitorId, String name, String deviceGroupName, Double threshold,
                         Boolean reread, Double slopeError, Double peakHeightMinimum, Boolean setQuestionable) throws Exception, ServletException {
        
        String editError = null;
        
        /* Is this a new monitor? */
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
        } else if (StringUtils.isBlank(deviceGroupName)) {
            editError = "Device group required.";
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
            validationMonitorDao.saveOrUpdate(validationMonitor);
            validationMonitorId = validationMonitor.getValidationMonitorId();
            
            return "redirect:/meter/start";
        }
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String delete(ModelMap model, int deleteValidationMonitorId) throws Exception, ServletException {
        
        if(!validationMonitorDao.delete(deleteValidationMonitorId)){
            model.addAttribute("editError", "Could not delete validation monitor.  Monitor with id: " + deleteValidationMonitorId + " not found.");
            return "redirect:edit";
        }
        
        return "redirect:/meter/start";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String toggleEnabled(ModelMap model, int validationMonitorId) throws Exception, ServletException {
        try {
        	validationMonitorService.toggleEnabled(validationMonitorId);
            model.addAttribute("validationMonitorId", validationMonitorId);
        } catch (ValidationMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
        }
        
        return "redirect:edit";
    }
    
    @Autowired
    public void setValidationMonitorDao(ValidationMonitorDao validationMonitorDao){
        this.validationMonitorDao = validationMonitorDao;
    }
    
    @Autowired
    public void setValidationMonitorService(ValidationMonitorService validationMonitorService) {
		this.validationMonitorService = validationMonitorService;
	}
}
