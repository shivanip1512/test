package com.cannontech.web.amr.tamperFlagProcessing;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
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
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.DeviceGroupInUse;
import com.cannontech.common.device.groups.DeviceGroupInUseException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.monitor.validators.TamperFlagMonitorValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/tamperFlagProcessing/*")
@CheckRoleProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING)
public class TamperFlagEditorController {

    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private TamperFlagMonitorService tamperFlagMonitorService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private TamperFlagMonitorValidator validator;
    
    private static final String baseKey = "yukon.web.modules.amr.tamperFlagEditor";
    private Logger log = YukonLogManager.getLogger(TamperFlagEditorController.class);

    // EDIT
    @RequestMapping(value = "{tamperFlagMonitorId}/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int tamperFlagMonitorId) {

        TamperFlagMonitor tamperFlagMonitor =
                tamperFlagMonitorDao.getById(tamperFlagMonitorId);

        model.addAttribute("mode", PageEditMode.EDIT);
        if (model.containsAttribute("tamperFlagMonitor")) {
            tamperFlagMonitor = (TamperFlagMonitor) model.get("tamperFlagMonitor");
        }
        model.addAttribute("tamperFlagGroupBase", deviceGroupService.getFullPath(SystemGroupEnum.TAMPER_FLAG));
        model.addAttribute("tamperFlagMonitor", tamperFlagMonitor);
        return "tamperFlagProcessing/edit.jsp";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(ModelMap model) {

        TamperFlagMonitor tamperFlagMonitor = null;
        model.addAttribute("mode", PageEditMode.CREATE);
        if (model.containsAttribute("tamperFlagMonitor")) {
            tamperFlagMonitor = (TamperFlagMonitor) model.get("tamperFlagMonitor");
        } else {
            tamperFlagMonitor=new TamperFlagMonitor();
            tamperFlagMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
        }
        model.addAttribute("tamperFlagGroupBase", deviceGroupService.getFullPath(SystemGroupEnum.TAMPER_FLAG));
        model.addAttribute("tamperFlagMonitor", tamperFlagMonitor);
        return "tamperFlagProcessing/edit.jsp";
    }


    // UPDATE
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@ModelAttribute("tamperFlagMonitor") TamperFlagMonitor tamperFlagMonitor, BindingResult result,
            FlashScope flash, RedirectAttributes attrs) throws Exception, ServletException {
        // new processor?
        boolean isNewMonitor = true;
        try {
            if (tamperFlagMonitor.getTamperFlagMonitorId() != null) {
                isNewMonitor = false;
            }
        } catch (TamperFlagMonitorNotFoundException e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".monitorNotFound"));
            return "redirect:" + tamperFlagMonitor.getTamperFlagMonitorId() + "/edit";
        }

        // redirect to edit page with error
        validator.validate(tamperFlagMonitor, result);
        if (result.hasErrors()) {
            /* Editing error. redirect to edit page with error. */
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return bindAndForward(tamperFlagMonitor, result, attrs);
        }

            // TAMPER FLAG GROUP
            if (isNewMonitor) {

                // create new group
                tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getName());

            } else {

                // tamper flag group needs new name
                TamperFlagMonitor existingTamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitor.getTamperFlagMonitorId());
                String currentProcessorName = existingTamperFlagMonitor.getName();
                if (!currentProcessorName.equals(tamperFlagMonitor.getName())) {

                    // try to retrieve group by new name (possible it could exist)
                    // if does not exist, get old group, give it new name
                    try {

                        deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.TAMPER_FLAG, tamperFlagMonitor.getName(), false);

                    } catch (NotFoundException e) {

                        // ok, it doesn't yet exist
                        StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(currentProcessorName);
                        tamperFlagGroup.setName(tamperFlagMonitor.getName());
                        deviceGroupEditorDao.updateGroup(tamperFlagGroup);
                    }
                }
            }

            // ENABLE MONITORING
            if (isNewMonitor) {
                tamperFlagMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
            }

            // finish processor setup, save/update

            log.debug("Saving tamperFlagMonitor: isNewMonitor=" + isNewMonitor + ", tamperFlagMonitor=" + tamperFlagMonitor.toString());
            if (isNewMonitor) {
                tamperFlagMonitorService.create(tamperFlagMonitor);
            } else {
                tamperFlagMonitorService.update(tamperFlagMonitor);
            }
            // redirect to edit page with processor
        attrs.addAttribute("tamperFlagMonitorId", tamperFlagMonitor.getTamperFlagMonitorId());
        return "redirect:/amr/tamperFlagProcessing/process/process";
    }

    private String bindAndForward(TamperFlagMonitor tamperFlagMonitor, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("tamperFlagMonitor", tamperFlagMonitor);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.tamperFlagMonitor", result);
        if (tamperFlagMonitor.getTamperFlagMonitorId() == null) {
            return "redirect:create";
        }
        return "redirect:" + tamperFlagMonitor.getTamperFlagMonitorId() + "/edit";
    }
    
    // DELETE
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(@ModelAttribute("tamperFlagMonitor") TamperFlagMonitor tamperFlagMonitor,
            FlashScope flashScope) throws Exception, ServletException {
        try {
            tamperFlagMonitorService.delete(tamperFlagMonitor.getTamperFlagMonitorId());
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".monitorDeleted",
                tamperFlagMonitor.getName()));
        } catch (TamperFlagMonitorNotFoundException e) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".monitorNotFound"));
            return "redirect:" + tamperFlagMonitor.getTamperFlagMonitorId() + "/edit";
        } catch (DeviceGroupInUseException e) {
            log.error("Could not delete tamper flag monitor : ", e);
            List<MessageSourceResolvable> messages = new ArrayList<>();
            messages.add(new WebMessageSourceResolvable(baseKey + ".delete.error", tamperFlagMonitor.getName()));
            for (DeviceGroupInUse deviceGroupInUse : e.getReferences()) {
                MessageSourceResolvable message = new WebMessageSourceResolvable(e.getMessageKey(), deviceGroupInUse.getGroupName(), deviceGroupInUse.getReferenceType(), 
                                                                                   deviceGroupInUse.getName(), deviceGroupInUse.getOwner());
                messages.add(message);
            }
            flashScope.setError(messages, FlashScopeListType.NONE);
        }
        return "redirect:/meter/start";
    }

    // TOGGLE MONITOR EVALUATION SERVICE ENABLED/DISABLED
    @RequestMapping(value = "toggleEnabled", method = RequestMethod.POST)
    public String toggleEnabled(ModelMap model, int tamperFlagMonitorId, FlashScope flash) {

        try {
            tamperFlagMonitorService.toggleEnabled(tamperFlagMonitorId);
        } catch (TamperFlagMonitorNotFoundException e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".monitorNotFound"));
        }

        return "redirect:" + tamperFlagMonitorId + "/edit";
    }
}