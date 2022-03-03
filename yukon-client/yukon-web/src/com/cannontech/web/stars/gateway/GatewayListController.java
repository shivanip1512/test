package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayFirmwareUpgradeService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class GatewayListController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    
    @Autowired private GatewayControllerHelper helper;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RfnGatewayFirmwareUpgradeService rfnGatewayFirmwareUpgradeService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private GatewayBulkUpdateValidator bulkUpdateValidator;

    @GetMapping({ "/gateways", "/gateways/" })
    public String gateways(ModelMap model) {
        model.addAttribute("infrastructureWarningDeviceCategory", InfrastructureWarningDeviceCategory.GATEWAY);
        return "gateways/list.jsp";
    }

    @GetMapping("/gateways/list")
    public String gatewaysList(ModelMap model, YukonUserContext userContext, FlashScope flash,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting) {
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        helper.buildGatewayListModel(model, userContext, sorting, gateways);
        return "gateways/gatewayTable.jsp";
    }

    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @GetMapping("/gateways/manageFirmware")
    public String firmwareDetails(ModelMap model) {
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        model.addAttribute("dataExists", gateways.stream().anyMatch(gateway -> (gateway.getData() != null)));
        return "gateways/manageFirmware.jsp";
    }

    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @GetMapping("/gateways/firmwareDetailsList")
    public String firmwareList(ModelMap model, YukonUserContext userContext, FlashScope flash,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting) {
        helper.buildFirmwareListModel(model, userContext, sorting);
        return "gateways/firmwareTable.jsp";
    }

    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @GetMapping("/gateways/manageCertificates")
    public String certificateUpdates() {
        return "gateways/manageCertificates.jsp";
    }

    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @GetMapping("/gateways/certificateDetailsList")
    public String certificateList(ModelMap model, YukonUserContext userContext,
            @DefaultSort(dir = Direction.desc, sort = "TIMESTAMP") SortingParameters sorting) {
        helper.buildCertificateListModel(model, userContext, sorting);
        return "gateways/certificateTable.jsp";
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @GetMapping("/gateways/update")
    public String bulkUpdateGatewaysPopup(ModelMap model) {
    	GatewayBulkUpdateModel updateModel = new GatewayBulkUpdateModel();
    	GatewayNMIPAddressPort mostUsedNMIPAddressPort = helper.getMostUsedGatewayNMIPPort();
    	if (mostUsedNMIPAddressPort != null) {
    		updateModel.setNmIpAddress(mostUsedNMIPAddressPort.getNmIpAddress());
    		updateModel.setNmPort(mostUsedNMIPAddressPort.getNmPort());
    	}
    	model.addAttribute("settings", updateModel);
        model.addAttribute("nmIPAddressPorts", helper.getAllGatewayNMIPPorts());
        return "gateways/bulkUpdate.jsp";
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @PostMapping("/gateways/update")
    public String bulkUpdateGateways(@ModelAttribute("settings") GatewayBulkUpdateModel gatewayUpdateModel, BindingResult result,
    		ModelMap model, HttpServletResponse resp, YukonUserContext userContext) {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
    	bulkUpdateValidator.validate(gatewayUpdateModel, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("nmIPAddressPorts", helper.getAllGatewayNMIPPorts());
            return "gateways/bulkUpdate.jsp";
        }
        List<String> gatewaySuccess = new ArrayList<String>();
        List<String> gatewayErrors = new ArrayList<String>();
        try {
	        for (int gatewayId : gatewayUpdateModel.getGatewayIds()) {
	        	RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(gatewayId);
	            RfnGatewayData.Builder builder = new RfnGatewayData.Builder();
	            RfnGatewayData data = builder.copyOf(gateway.getData())
	           .nmIpAddress(gatewayUpdateModel.getNmIpAddress())
	           .nmPort(gatewayUpdateModel.getNmPort())
	           .build();  
	            gateway.setData(data);
				GatewayUpdateResult updateResult = rfnGatewayService.updateGateway(gateway, userContext.getYukonUser());
				if (updateResult == GatewayUpdateResult.SUCCESSFUL) {
					gatewaySuccess.add(gateway.getName());
				} else {
					gatewayErrors.add(gateway.getName());
				}
	        }
	        if (!gatewaySuccess.isEmpty()) {
	        	json.put("successMessage", 
	        			accessor.getMessage(baseKey + "bulkUpdate.gatewaySuccessMessage", StringUtils.collectionToDelimitedString(gatewaySuccess, ", ")));
	        }
	        if (!gatewayErrors.isEmpty()) {
	        	json.put("errorMessage", 
	        			accessor.getMessage(baseKey + "bulkUpdate.gatewayErrorMessage", StringUtils.collectionToDelimitedString(gatewayErrors, ", ")));
	        }
		} catch (NmCommunicationException e) {
            log.error("Failed communicating to NM while updating gateways.", e);
            json.put("errorMessage", accessor.getMessage(baseKey + "error.comm"));
		}
        return JsonUtils.writeResponse(resp, json);
    }

    @RequestMapping("/gateways/data")
    public @ResponseBody Map<Integer, Object> data(YukonUserContext userContext) {
        String defaultUpdateServerUrl = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
        Map<Integer, Object> json = new HashMap<>();
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(gateways.stream()
            .map(gateway -> gateway.getPaoIdentifier().getPaoId())
            .collect(Collectors.toList()));
        try {
            Map<String, String>  upgradeVersions = rfnGatewayFirmwareUpgradeService.getFirmwareUpdateServerVersions();
            gateways.forEach(gateway -> {
                if (gateway.getData() != null) {
                    String updateServerUrl = gateway.getData().getUpdateServerUrl();
                    if (updateServerUrl == null) {
                        updateServerUrl = defaultUpdateServerUrl;
                    }
                    String upgradeVersion = upgradeVersions.get(updateServerUrl);
                    gateway.setUpgradeVersion(upgradeVersion); 
                }
                Map<String, Object> data = helper.buildGatewayModel(gateway, userContext);
                data.put("hasNotes", notesList.contains(gateway.getPaoIdentifier().getPaoId()));
                json.put(gateway.getPaoIdentifier().getPaoId(), data);
            });
            
        } catch (NmCommunicationException e){
            log.error("Retrieval of firmware update server versions failed", e);
        }
        return json;
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.INTERACT)
    @RequestMapping("/gateways/{id}/connect")
    public @ResponseBody Map<String, Object> connect(YukonUserContext userContext, @PathVariable int id) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.connectGateway(gateway.getPaoIdentifier());
            json.put("success", success);
        } catch (NmCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
        }
        
        return json;
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    @RequestMapping("/gateways/{id}/disconnect")
    public @ResponseBody Map<String, Object> disconnect(YukonUserContext userContext, @PathVariable int id) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.disconnectGateway(gateway.getPaoIdentifier());
            json.put("success", success);
        } catch (NmCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
        }
        
        return json;
    }
    
    @RequestMapping(value = "/gateways/{id}/collect-data", consumes = json, produces = json)
    public @ResponseBody Map<String, Object> collectData(YukonUserContext userContext, @PathVariable int id,
            @RequestBody DataTypeContainer dataTypeContainer) {

        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.collectData(gateway.getPaoIdentifier(), dataTypeContainer.types);
            json.put("success", success);
        } catch (NmCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
        }
        
        return json;
    }

    public enum CertificateUpdatesSortBy implements DisplayableEnum {
        TIMESTAMP,
        CERTIFICATE,
        FAILED,
        SUCCESSFUL,
        PENDING;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.gateways.manageCertificates." + name();
        }
    }

    public enum FirmwareUpdatesSortBy implements DisplayableEnum {
        TIMESTAMP,
        FAILED,
        SUCCESSFUL,
        PENDING;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.gateways.manageFirmware." + name();
        }
    }

    public enum GatewayListSortBy implements DisplayableEnum {
        NAME,
        SERIALNUMBER,
        FIRMWAREVERSION,
        LASTCOMMUNICATION;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.gateways.list." + name();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataTypeContainer {
        public DataType[] types;
    }
}