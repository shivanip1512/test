package com.cannontech.web.amr.tamperFlagProcessing;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/tamperFlagProcessing/*")
@CheckRoleProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING)
public class TamperFlagEditorController {

    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private TamperFlagMonitorService tamperFlagMonitorService;
    @Autowired private DeviceGroupService deviceGroupService;

    private Logger log = YukonLogManager.getLogger(TamperFlagEditorController.class);

    // EDIT
    @RequestMapping("edit")
    public void edit(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException {

        // pass through due to error
        String editError = ServletRequestUtils.getStringParameter(request, "editError", null);
        int tamperFlagMonitorId = ServletRequestUtils.getIntParameter(request, "tamperFlagMonitorId", 0);
        String name = ServletRequestUtils.getStringParameter(request, "name", null);
        String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName", null);

        TamperFlagMonitor tamperFlagMonitor = null;
        try {
            model.addAttribute("mode", PageEditMode.CREATE);
            // existing tamper flag monitor
            if (tamperFlagMonitorId > 0) {

                tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
                model.addAttribute("mode", PageEditMode.EDIT);
                // use entered values instead of existing value if present
                if (name == null) {
                    name = tamperFlagMonitor.getTamperFlagMonitorName();
                }
                if (deviceGroupName == null) {
                    deviceGroupName = tamperFlagMonitor.getGroupName();
                }
            }

        } catch (TamperFlagMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
            return;
        }

        model.addAttribute("editError", editError);
        model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
        model.addAttribute("name", name);
        model.addAttribute("deviceGroupName", deviceGroupName);

        model.addAttribute("tamperFlagGroupBase", deviceGroupService.getFullPath(SystemGroupEnum.TAMPER_FLAG));
        model.addAttribute("tamperFlagMonitor", tamperFlagMonitor);

    }

    // UPDATE
    @RequestMapping("update")
    public String update(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws Exception, ServletException {

        String editError = null;
        int tamperFlagMonitorId = ServletRequestUtils.getIntParameter(request, "tamperFlagMonitorId", 0);
        String name = ServletRequestUtils.getStringParameter(request, "name", null);
        String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName", null);

        // new processor?
        boolean isNewMonitor = true;
        TamperFlagMonitor tamperFlagMonitor;
        try {
            if (tamperFlagMonitorId <= 0) {
                tamperFlagMonitor = new TamperFlagMonitor();
            } else {
                tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
                isNewMonitor = false;
            }
        } catch (TamperFlagMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
            return "redirect:edit";
        }

        if (StringUtils.isBlank(name)) {
            editError = "Name required.";
        } else if (!DeviceGroupUtil.isValidName(name)) {
            editError = "Name may not contain slashes.";
        } else if (isNewMonitor && tamperFlagMonitorDao.processorExistsWithName(name)) { // new monitor, check name
            editError = "Tamper Flag Monitor with name \"" + name + "\" already exists.";
        } else if (!isNewMonitor && !tamperFlagMonitor.getTamperFlagMonitorName().equals(name) 
                && tamperFlagMonitorDao.processorExistsWithName(name)) { // existing monitor, new name, check name
            editError = "Tamper Flag Monitor with name \"" + name + "\" already exists.";
        } else if (StringUtils.isBlank(deviceGroupName)) {
            editError = "Device group required.";
        }

        // editError. redirect to edit page with error
        if (editError != null) {

            model.addAttribute("editError", editError);
            model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
            model.addAttribute("name", name);
            model.addAttribute("deviceGroupName", deviceGroupName);
            return "redirect:edit";

            // ok. save or update
        } else {

            // TAMPER FLAG GROUP
            if (isNewMonitor) {

                // create new group
                tamperFlagMonitorService.getTamperFlagGroup(name);

            } else {

                // tamper flag group needs new name
                String currentProcessorName = tamperFlagMonitor.getTamperFlagMonitorName();
                if (!currentProcessorName.equals(name)) {

                    // try to retrieve group by new name (possible it could exist)
                    // if does not exist, get old group, give it new name
                    try {

                        deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.TAMPER_FLAG, name, false);

                    } catch (NotFoundException e) {

                        // ok, it doesn't yet exist
                        StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(currentProcessorName);
                        tamperFlagGroup.setName(name);
                        deviceGroupEditorDao.updateGroup(tamperFlagGroup);
                    }
                }
            }

            // ENABLE MONITORING
            if (isNewMonitor) {

                tamperFlagMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
            }

            // finish processor setup, save/update
            tamperFlagMonitor.setTamperFlagMonitorName(name);
            tamperFlagMonitor.setGroupName(deviceGroupName);

            log.debug("Saving tamperFlagMonitor: isNewMonitor=" + isNewMonitor + ", tamperFlagMonitor=" + tamperFlagMonitor.toString());
            if (isNewMonitor) {
                tamperFlagMonitorService.create(tamperFlagMonitor);
            } else {
                tamperFlagMonitorService.update(tamperFlagMonitor);
            }
            // redirect to edit page with processor
            return "redirect:/meter/start";
        }
    }

    // DELETE
    @RequestMapping("delete")
    public String delete(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws Exception, ServletException {

        int deleteTamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "deleteTamperFlagMonitorId");

        try {
            tamperFlagMonitorService.delete(deleteTamperFlagMonitorId);
        } catch (TamperFlagMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
            return "redirect:edit";
        }
        return "redirect:/meter/start";
    }

    // TOGGLE MONITOR EVALUATION SERVICE ENABLED/DISABLED
    @RequestMapping("toggleEnabled")
    public String toggleEnabled(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws Exception, ServletException {

        int tamperFlagMonitorId = ServletRequestUtils.getIntParameter(request, "tamperFlagMonitorId", 0);

        try {
            tamperFlagMonitorService.toggleEnabled(tamperFlagMonitorId);
            model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
        } catch (OutageMonitorNotFoundException e) {
            model.addAttribute("editError", e.getMessage());
        }

        return "redirect:edit";
    }
}