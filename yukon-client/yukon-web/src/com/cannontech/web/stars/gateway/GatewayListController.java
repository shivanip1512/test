package com.cannontech.web.stars.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;
import com.cannontech.common.rfn.service.RfnGatewayCertificateUpdateService;
import com.cannontech.common.rfn.service.RfnGatewayFirmwareUpgradeService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class GatewayListController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    
    @Autowired private GatewayControllerHelper helper;
    @Autowired private RfnGatewayCertificateUpdateService certificateUpdateService;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RfnGatewayFirmwareUpgradeService rfnGatewayFirmwareUpgradeService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoNotesService paoNotesService;

    @RequestMapping(value = { "/gateways", "/gateways/" }, method = RequestMethod.GET)
    public String gateways(ModelMap model, YukonUserContext userContext, FlashScope flash,
                           @DefaultSort(dir = Direction.desc, sort = "TIMESTAMP") SortingParameters sorting) {

        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        
        // Check for gateways with duplicate colors
        // If any are found, output a flash scope warning to notify the user
        Multimap<Short, RfnGateway> duplicateColorGateways = rfnGatewayService.getDuplicateColorGateways(gateways);
        if (!duplicateColorGateways.isEmpty()) {
            StringBuilder gatewaysString = new StringBuilder(); 
            for (Short color : duplicateColorGateways.keySet()) {
                Set<String> gatewayNames = duplicateColorGateways.get(color)
                                                                 .stream()
                                                                 .map(RfnGateway::getName)
                                                                 .collect(Collectors.toSet());
                gatewaysString.append(color)
                              .append(" (")
                              .append(StringUtils.join(gatewayNames, ", "))
                              .append(") ");
            }
            YukonMessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.operator.gateways.list.duplicateColors", 
                                                                                    gatewaysString);
            flash.setWarning(message);
        }
        helper.addText(model, userContext);
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(gateways.stream()
                                                                             .map(gateway -> gateway.getPaoIdentifier().getPaoId())
                                                                             .collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);
        model.addAttribute("deviceType", InfrastructureWarningDeviceCategory.GATEWAY);
        return "gateways/list.jsp";
    }

    @GetMapping("/gateways/firmwareDetails")
    public String firmwareDetails(ModelMap model, YukonUserContext userContext,
            @DefaultSort(dir = Direction.desc, sort = "TIMESTAMP") SortingParameters sorting) {
        List<RfnGatewayFirmwareUpdateSummary> firmwareUpdates = rfnGatewayFirmwareUpgradeService.getFirmwareUpdateSummaries();
        Direction dir = sorting.getDirection();
        FirmwareUpdatesSortBy sortBy = FirmwareUpdatesSortBy.valueOf(sorting.getSort());
        Collections.sort(firmwareUpdates, GatewayControllerHelper.getFirmwareComparator(sorting, sortBy));
        model.addAttribute("firmwareUpdates", firmwareUpdates);
        helper.addText(model, userContext);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (FirmwareUpdatesSortBy column : FirmwareUpdatesSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        return "gateways/firmwareUpdates.jsp";
    }

    @GetMapping("/gateways/certificateUpdates")
    public String certificateUpdates(ModelMap model, YukonUserContext userContext,
            @DefaultSort(dir = Direction.desc, sort = "TIMESTAMP") SortingParameters sorting) {
        List<CertificateUpdate> certUpdates = certificateUpdateService.getAllCertificateUpdates();
        Direction dir = sorting.getDirection();
        CertificateUpdatesSortBy sortBy = CertificateUpdatesSortBy.valueOf(sorting.getSort());
        Collections.sort(certUpdates, GatewayControllerHelper.getCertificateComparator(sorting, sortBy));
        model.addAttribute("certUpdates", certUpdates);
        helper.addText(model, userContext);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (CertificateUpdatesSortBy column : CertificateUpdatesSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        return "gateways/certificateUpdates.jsp";
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
            return "yukon.web.modules.operator.gateways.certificateUpdates." + name();
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataTypeContainer {
        public DataType[] types;
    }
}