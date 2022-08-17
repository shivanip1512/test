package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.ConflictType;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.Radio;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;

public class GatewayControllerHelper {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CronExpressionTagService cronService;
    
    public void addText(ModelMap model, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<String, String> text = new HashMap<>();
        text.put("connect.pending", accessor.getMessage(baseKey + "connect.pending"));
        text.put("connect.success", accessor.getMessage(baseKey + "connect.success"));
        text.put("connect.failure", accessor.getMessage(baseKey + "connect.failure"));
        text.put("login.successful", accessor.getMessage(baseKey + "login.successful"));
        text.put("login.failed", accessor.getMessage(baseKey + "login.failed"));
        text.put("disconnect.pending", accessor.getMessage(baseKey + "disconnect.pending"));
        text.put("disconnect.success", accessor.getMessage(baseKey + "disconnect.success"));
        text.put("disconnect.failure", accessor.getMessage(baseKey + "disconnect.failure"));
        text.put("collect.data.pending", accessor.getMessage(baseKey + "collect.data.pending"));
        text.put("collect.data.success", accessor.getMessage(baseKey + "collect.data.success"));
        text.put("collect.data.failure", accessor.getMessage(baseKey + "collect.data.failure"));
        text.put("collect.data.title", accessor.getMessage(baseKey + "collect.data.title"));
        text.put("cert.update.more", accessor.getMessage(baseKey + "cert.update.more"));
        text.put("cert.update.label", accessor.getMessage(baseKey + "cert.update.label"));
        text.put("firmware.update.label", accessor.getMessage(baseKey + "firmware.update.label"));
        text.put("complete", accessor.getMessage("yukon.common.complete"));
        text.put("port", accessor.getMessage(baseKey + "port"));
        
        model.addAttribute("text", text);
        
    }
    
    /** Build a i18n friendly json model of the gateway */
    public Map<String, Object> buildGatewayModel(RfnGateway gateway, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Map<String, Object> gatewayJson = new HashMap<>();
        gatewayJson.put("name", gateway.getName());
        gatewayJson.put("paoId", gateway.getPaoIdentifier());
        gatewayJson.put("rfnId", gateway.getRfnIdentifier());
        gatewayJson.put("location", gateway.getLocation());
        
        RfnGatewayData data = gateway.getData();
        if (data == null) {
            gatewayJson.put("data", null);
        } else {
            
            Map<String, Object> dataJson = new HashMap<>();
            dataJson.put("connected", data.getConnectionStatus() == ConnectionStatus.CONNECTED);
            dataJson.put("connectionStatusText", accessor.getMessage(baseKey + "connectionStatus." 
                    + data.getConnectionStatus()));
            dataJson.put("ip", data.getIpAddress());
            dataJson.put("port", data.getPort());
            dataJson.put("dataStreamingLoadingPercent", data.getDataStreamingLoadingPercent());
            dataJson.put("lastComm", data.getLastCommStatus());
            dataJson.put("lastCommText", accessor.getMessage(baseKey + "lastCommStatus." + data.getLastCommStatus()));
            dataJson.put("lastCommTimestamp", data.getLastCommStatusTimestamp());
            dataJson.put("collectionWarning", gateway.isTotalCompletionLevelWarning());
            dataJson.put("collectionDanger", gateway.isTotalCompletionLevelDanger());
            dataJson.put("collectionPercent", gateway.getTotalCompletionPercentage());
            dataJson.put("hwVersion", data.getHardwareVersion());
            dataJson.put("swVersion", data.getSoftwareVersion());
            dataJson.put("usVersion", data.getUpperStackVersion());
            dataJson.put("radioVersion", data.getRadioVersion());
            dataJson.put("releaseVersion", data.getReleaseVersion());
            dataJson.put("hasUpdateVersion", gateway.isUpgradeAvailable());
            dataJson.put("versionConflict", !data.getVersionConflicts().isEmpty());
            dataJson.put("ipv6Prefix", data.getIpv6Prefix());
            if (!data.getVersionConflicts().isEmpty()) {
                List<String> conflicts = new ArrayList<>();
                for (ConflictType type : data.getVersionConflicts()) {
                    conflicts.add(accessor.getMessage(baseKey + "conflictType." + type));
                    dataJson.put("versionConflicts", StringUtils.join(conflicts, ", "));
                }
            } else {
                dataJson.put("versionConflicts", accessor.getMessage("yukon.common.none.choice"));
            }
            
            dataJson.put("appMode", data.getMode());
            dataJson.put("appModeNormal", data.getMode() == AppMode.NORMAL);
            dataJson.put("admin", data.getAdmin().getUsername());
            dataJson.put("superAdmin", data.getSuperAdmin().getUsername());
            dataJson.put("connType", accessor.getMessage(baseKey + "connectionType."+ data.getConnectionType()));
            
            List<Map<String, Object>> radios = new ArrayList<>();
            for (Radio radio : data.getRadios()) {
                Map<String, Object> json = new HashMap<>();
                json.put("type", accessor.getMessage(baseKey + "radioType." + radio.getType()));
                String mac = com.cannontech.common.util.StringUtils.colonizeMacAddress(radio.getMacAddress());
                json.put("mac", accessor.getMessage(baseKey + "macAddress", mac));
                json.put("version", accessor.getMessage(baseKey + "version", radio.getVersion()));
                json.put("timestamp", radio.getTimestamp());
                radios.add(json);
            }
            dataJson.put("radios", radios);
            
            String schedule = cronService.getDescription(data.getCollectionSchedule(), userContext);
            dataJson.put("schedule", schedule);
            
            gatewayJson.put("data", dataJson);
            gatewayJson.put("isDataStreamingSupported",  gateway.isDataStreamingSupported());
        }
        
        return gatewayJson;
    }
    
    public void sortSequences(List<DataSequence> sequences, YukonUserContext userContext) {
        
        final MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Collections.sort(sequences, new Comparator<DataSequence>() {
            @Override
            public int compare(DataSequence o1, DataSequence o2) {
                String type1 = accessor.getMessage(baseKey + "sequenceType." + o1.getType());
                String type2 = accessor.getMessage(baseKey + "sequenceType." + o2.getType());
                return type1.compareToIgnoreCase(type2);
            }
        });
    }
    
}