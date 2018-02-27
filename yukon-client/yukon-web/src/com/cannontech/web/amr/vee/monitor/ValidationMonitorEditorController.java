package com.cannontech.web.amr.vee.monitor;

import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.monitor.validators.ValidationMonitorValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/vee/monitor/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
public class ValidationMonitorEditorController {

    private final static Logger log = YukonLogManager.getLogger(ValidationMonitorEditorController.class);
    @Autowired private ValidationMonitorDao validationMonitorDao;
    @Autowired private ValidationMonitorService validationMonitorService;
    @Autowired private ValidationMonitorValidator validator;

    @RequestMapping(value = "{validationMonitorId}/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int validationMonitorId) {

        ValidationMonitor validationMonitor = validationMonitorDao.getById(validationMonitorId);

        model.addAttribute("mode", PageEditMode.EDIT);
        if (model.containsAttribute("validationMonitor")) {
            validationMonitor = (ValidationMonitor) model.get("validationMonitor");
        }
        model.addAttribute("validationMonitor", validationMonitor);
        return "vee/monitor/edit.jsp";
    }

    @RequestMapping("create")
    public String create(ModelMap model) {

        ValidationMonitor validationMonitor = new ValidationMonitor();
        model.addAttribute("mode", PageEditMode.CREATE);
        if (model.containsAttribute("validationMonitor")) {
            validationMonitor = (ValidationMonitor) model.get("validationMonitor");
        }
        model.addAttribute("validationMonitor", validationMonitor);
        return "vee/monitor/edit.jsp";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@ModelAttribute("validationMonitor") ValidationMonitor validationMonitor, BindingResult result,
            FlashScope flash, RedirectAttributes attrs) {

        boolean isNewMonitor = true;
        try {
            if (validationMonitor.getValidationMonitorId() != null) {
                isNewMonitor = false;
            }
        } catch (ValidationMonitorNotFoundException e) {
            return "redirect:/meter/start";
        }

        /* Enable the monitor. */
        if (isNewMonitor) {
            validationMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
        }

        /* Validate inputs. */
        validator.validate(validationMonitor, result);

        if (result.hasErrors()) {
            /* Editing error. redirect to edit page with error. */
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return bindAndForward(validationMonitor, result, attrs);
        }

        attrs.addFlashAttribute("validationMonitor", validationMonitor);

        log.debug("Saving validationMonitor: isNewMonitor=" + isNewMonitor + ", validationMonitor="
            + validationMonitor.toString());
        if (isNewMonitor) {
            validationMonitorService.create(validationMonitor);
        } else {
            validationMonitorService.update(validationMonitor);
        }
        return "redirect:/meter/start";
    }
    
    private String bindAndForward(ValidationMonitor validationMonitor, BindingResult result, RedirectAttributes attrs) {
        
        attrs.addFlashAttribute("validationMonitor", validationMonitor);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.validationMonitor", result);
        if (validationMonitor.getValidationMonitorId() == null) {
            return "redirect:create";
        }
        return "redirect:" + validationMonitor.getValidationMonitorId() + "/edit";
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(ModelMap model, int deleteValidationMonitorId) throws Exception, ServletException {
        
        if (!validationMonitorService.delete(deleteValidationMonitorId)) {
            model.addAttribute("editError",
                "Could not delete validation monitor.  Monitor with id: " + deleteValidationMonitorId + " not found.");
            return "redirect:" + deleteValidationMonitorId + "/edit";
        }
        return "redirect:/meter/start";
    }
    
    @RequestMapping(value = "toggleEnabled", method = RequestMethod.POST)
    public String toggleEnabled(ModelMap model, int validationMonitorId) throws Exception, ServletException {
        try {
        	validationMonitorService.toggleEnabled(validationMonitorId);
            model.addAttribute("validationMonitorId", validationMonitorId);
        } catch (ValidationMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
        }
        
        return "redirect:" + validationMonitorId + "/edit";
    }
    
}