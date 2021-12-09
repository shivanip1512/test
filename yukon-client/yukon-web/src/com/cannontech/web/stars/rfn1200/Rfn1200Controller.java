package com.cannontech.web.stars.rfn1200;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.rfn.service.RfnDeviceDeletionMessageService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/device/rfn1200")
public class Rfn1200Controller {

    private static final Logger log = YukonLogManager.getLogger(Rfn1200Controller.class);
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnDeviceDeletionMessageService rfnDeviceDeletionMessageService;

    @GetMapping("/{id}")
    public String view(@PathVariable int id, ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("id", id);
        String name = dbCache.getAllPaosMap().get(id).getPaoName();
        model.addAttribute("name", name);
        model.addAttribute("deviceNames", getDevicesNamesForPort(userContext, request, id, name));
        return "/rfn1200/view.jsp";
    }

    @DeleteMapping("/delete/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    public String delete(@PathVariable int id, FlashScope flash, YukonUserContext userContext) {
        LiteYukonPAObject device = dbCache.getAllPaosMap().get(id);
        try {
            rfnDeviceDeletionMessageService.sendRfnDeviceDeletionRequest(id);
            deviceDao.removeDevice(id);
            
            rfnDeviceEventLogService.rfn1200Deleted(device.getPaoName(), userContext.getYukonUser().getUsername());
            
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", device.getPaoName()));
            return "redirect:" + "/stars/device/commChannel/list";
        }
        catch (Exception e) {
            log.error("Unable to delete RFN-1200 " + device.getPaoName(), e);
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", device.getPaoName(), e.getMessage()));
            return "redirect:" + "/stars/device/rfn1200/" + id;
        }
    }

    /**
     * Returns comma separated device names for that port 
     */
    private String getDevicesNamesForPort(YukonUserContext userContext, HttpServletRequest request, int portId, String commChannelName) {
        try {
            String assignedDevicesUrl = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/" + portId + "/devicesAssigned");
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext,
                    request, assignedDevicesUrl, HttpMethod.GET, DeviceBaseModel.class, Object.class);
            List<DeviceBaseModel> devicesList = new ArrayList<>();
            if (response.getStatusCode() == HttpStatus.OK) {
                devicesList = ((SearchResults<DeviceBaseModel>)response.getBody()).getResultList();
            }

            if (!devicesList.isEmpty()) {
                return devicesList.stream().map(device -> device.getDeviceName()).collect(Collectors.joining(", "));
            }
        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
        } catch (RestClientException ex) {
            log.error("Error while retrieving assigned devices for comm Channel: {}. Error: {}", commChannelName, ex.getMessage());
        }
        return null;
    }
}
