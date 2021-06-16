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
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private DeviceDao deviceDao;

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
    public String delete(@PathVariable int id, FlashScope flash) {
        //TODO: Delete RFN 1200 Device and display success/failure message
        String deviceName = dbCache.getAllPaosMap().get(id).getPaoName();
        
        
        LiteYukonPAObject device = dbCache.getAllPaosMap().get(id);
       // String meterName = serverDatabaseCache.getAllMeters().get(id).getMeterNumber();
        try {
            deviceDao.removeDevice(id);
            
            //clean up event log add to RfnDeviceEventLogService example below 
           // meteringEventLogService.meterDeleted(meter.getPaoName(), meterName, user.getUsername());
            
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.delete.successful", deviceName));
           // return "redirect:/meter/start";
        }
        catch (Exception e) {
            //log.error("Unable to delete meter with id " + meter.getPaoName(), e);
           // flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.amr.delete.failure", meter.getPaoName()));
           // return "redirect:/meter/home?deviceId="+id;
        }
        
        //if success
     //   flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", deviceName));
        //if failure
        //flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", deviceName, e.getMessage()));
        return "redirect:" + "/stars/device/commChannel/list";
    }

    /**
     * Returns comma separated device names for that port 
     */
    private String getDevicesNamesForPort(YukonUserContext userContext, HttpServletRequest request, int portId, String commChannelName) {
        try {
            String assignedDevicesUrl = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/" + portId + "/devicesAssigned");
            List<DeviceBaseModel> devicesList = getDeviceBaseModelResponse(userContext, request, assignedDevicesUrl);

            if (!devicesList.isEmpty()) {
                return devicesList.stream().map(device -> device.getName()).collect(Collectors.joining(", "));
            }
        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
        } catch (RestClientException ex) {
            log.error("Error while retrieving assigned devices for comm Channel: {}. Error: {}", commChannelName, ex.getMessage());
        }
        return null;
    }
    
    /**
     * Get the response in form of DevicesBaseModel
     */
    private List<DeviceBaseModel> getDeviceBaseModelResponse(YukonUserContext userContext, HttpServletRequest request, String url) {
        List<DeviceBaseModel> deviceBaseModelList = new ArrayList<>();

        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForList(userContext,
                                                                                    request,
                                                                                    url,
                                                                                    DeviceBaseModel.class,
                                                                                    HttpMethod.GET,
                                                                                    DeviceBaseModel.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            deviceBaseModelList = (List<DeviceBaseModel>) response.getBody();
        }
        return deviceBaseModelList;
    }

}
