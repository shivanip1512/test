package com.cannontech.web.amr.tamperFlagProcessing;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.monitor.validators.TamperFlagMonitorValidator;
import com.cannontech.web.common.flashScope.FlashScope;
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
            tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
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
        //TamperFlagMonitor tamperFlagMonitor;
        try {
            if (tamperFlagMonitor.getTamperFlagMonitorId() != null) {
                isNewMonitor = false;
            }
        } catch (TamperFlagMonitorNotFoundException e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.amr.tamperFlagEditor.monitorNotFound"));
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
                tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());

            } else {

                // tamper flag group needs new name
                String currentProcessorName = tamperFlagMonitor.getTamperFlagMonitorName();
                if (!currentProcessorName.equals(tamperFlagMonitor.getTamperFlagMonitorName())) {

                    // try to retrieve group by new name (possible it could exist)
                    // if does not exist, get old group, give it new name
                    try {

                        deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.TAMPER_FLAG, tamperFlagMonitor.getTamperFlagMonitorName(), false);

                    } catch (NotFoundException e) {

                        // ok, it doesn't yet exist
                        StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(currentProcessorName);
                        tamperFlagGroup.setName(tamperFlagMonitor.getTamperFlagMonitorName());
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
            return "redirect:/meter/start";
        //}
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
    public String delete(HttpServletRequest request, LiteYukonUser user, ModelMap model, int tamperFlagMonitorId,
            FlashScope flash)
            throws Exception, ServletException {

        try {
            tamperFlagMonitorService.delete(tamperFlagMonitorId);
        } catch (TamperFlagMonitorNotFoundException e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.amr.tamperFlagEditor.monitorNotFound"));
            return "redirect:" + tamperFlagMonitorId + "/edit";
        }
        return "redirect:/meter/start";
    }

    // TOGGLE MONITOR EVALUATION SERVICE ENABLED/DISABLED
    @RequestMapping(value = "toggleEnabled", method = RequestMethod.POST)
    public String toggleEnabled(ModelMap model, int tamperFlagMonitorId, FlashScope flash) {

        try {
            tamperFlagMonitorService.toggleEnabled(tamperFlagMonitorId);
            model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
        } catch (OutageMonitorNotFoundException e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.amr.tamperFlagEditor.monitorNotFound"));
        }

        return "redirect:" + tamperFlagMonitorId + "/edit";
    }
}