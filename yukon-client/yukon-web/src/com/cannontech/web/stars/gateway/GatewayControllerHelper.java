package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.ConflictType;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.Radio;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;
import com.cannontech.common.rfn.service.RfnGatewayCertificateUpdateService;
import com.cannontech.common.rfn.service.RfnGatewayFirmwareUpgradeService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.stars.gateway.GatewayListController.CertificateUpdatesSortBy;
import com.cannontech.web.stars.gateway.GatewayListController.FirmwareUpdatesSortBy;
import com.cannontech.web.stars.gateway.GatewayListController.GatewayListSortBy;

public class GatewayControllerHelper {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CronExpressionTagService cronService;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private RfnGatewayFirmwareUpgradeService rfnGatewayFirmwareUpgradeService;
    @Autowired private RfnGatewayCertificateUpdateService certificateUpdateService;
    @Autowired private RfnGatewayService rfnGatewayService;

    public void addGatewayMessages(ModelMap model, YukonUserContext userContext) {
        
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
            dataJson.put("nmip", data.getNmIpAddress());
            dataJson.put("nmport", data.getNmPort());
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

    public static Comparator<RfnGateway> getGatewayListComparator(SortingParameters sorting,
            GatewayListSortBy sortBy) {
        Comparator<RfnGateway> comparator = (o1, o2) -> {
            return o1.getName().compareToIgnoreCase(o2.getName());
        };
        if (sortBy == GatewayListSortBy.SERIALNUMBER) {
            comparator = (o1, o2) -> (o1.getRfnIdentifier().getSensorSerialNumber().compareTo(o2.getRfnIdentifier().getSensorSerialNumber()));
        }
        if (sortBy == GatewayListSortBy.LASTCOMMUNICATION) {
            comparator = (o1, o2) -> {
                if (o1.getData() != null && o2.getData() != null) {
                    return o1.getData().getLastCommStatus().compareTo(o2.getData().getLastCommStatus());
                } else {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            };
        }
        if (sortBy == GatewayListSortBy.FIRMWAREVERSION) {
            comparator = (o1, o2) -> {
                if (o1.getData() != null && o2.getData() != null) {
                    return o1.getData().getReleaseVersion().compareTo(o2.getData().getReleaseVersion());
                } else {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            };
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        return comparator;
    }

    public static Comparator<RfnGatewayFirmwareUpdateSummary> getFirmwareComparator(SortingParameters sorting,
            FirmwareUpdatesSortBy sortBy) {
        Comparator<RfnGatewayFirmwareUpdateSummary> comparator = (o1, o2) -> {
            return o1.getSendDate().compareTo(o2.getSendDate());
        };
        if (sortBy == FirmwareUpdatesSortBy.PENDING) {
            comparator = (o1, o2) -> (o1.getGatewayUpdatesPending() - o2.getGatewayUpdatesPending());
        }
        if (sortBy == FirmwareUpdatesSortBy.FAILED) {
            comparator = (o1, o2) -> (o1.getGatewayUpdatesFailed() - o2.getGatewayUpdatesFailed());
        }
        if (sortBy == FirmwareUpdatesSortBy.SUCCESSFUL) {
            comparator = (o1, o2) -> (o1.getGatewayUpdatesSuccessful() - o2.getGatewayUpdatesSuccessful());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        return comparator;
    }

    public static Comparator<CertificateUpdate> getCertificateComparator(SortingParameters sorting,
            CertificateUpdatesSortBy sortBy) {
        Comparator<CertificateUpdate> comparator = (o1, o2) -> {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        };
        if (sortBy == CertificateUpdatesSortBy.CERTIFICATE) {
            comparator = (o1, o2) -> (o1.getFileName().compareToIgnoreCase(o2.getFileName()));
        }
        if (sortBy == CertificateUpdatesSortBy.PENDING) {
            comparator = (o1, o2) -> (o1.getPending().size() - o2.getPending().size());
        }
        if (sortBy == CertificateUpdatesSortBy.FAILED) {
            comparator = (o1, o2) -> (o1.getFailed().size() - o2.getFailed().size());
        }
        if (sortBy == CertificateUpdatesSortBy.SUCCESSFUL) {
            comparator = (o1, o2) -> (o1.getSuccessful().size() - o2.getSuccessful().size());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        return comparator;
    }

    public void buildGatewayListModel(ModelMap model, YukonUserContext userContext, SortingParameters sorting,
            List<RfnGateway> gateways) {
        Direction dir = sorting.getDirection();
        GatewayListSortBy sortBy = GatewayListSortBy.valueOf(sorting.getSort());
        Collections.sort(gateways, GatewayControllerHelper.getGatewayListComparator(sorting, sortBy));
        model.addAttribute("gateways", gateways);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (GatewayListSortBy column : GatewayListSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(gateways.stream()
                                                                             .map(gateway -> gateway.getPaoIdentifier().getPaoId())
                                                                             .collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);
    }

    public void buildFirmwareListModel(ModelMap model, YukonUserContext userContext, SortingParameters sorting) {
        List<RfnGatewayFirmwareUpdateSummary> firmwareUpdates = rfnGatewayFirmwareUpgradeService.getFirmwareUpdateSummaries();
        Direction dir = sorting.getDirection();
        FirmwareUpdatesSortBy sortBy = FirmwareUpdatesSortBy.valueOf(sorting.getSort());
        Collections.sort(firmwareUpdates, GatewayControllerHelper.getFirmwareComparator(sorting, sortBy));
        model.addAttribute("firmwareUpdates", firmwareUpdates);
        addGatewayMessages(model, userContext);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (FirmwareUpdatesSortBy column : FirmwareUpdatesSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
    }

    public void buildCertificateListModel(ModelMap model, YukonUserContext userContext, SortingParameters sorting) {
        List<CertificateUpdate> certUpdates = certificateUpdateService.getAllCertificateUpdates();
        Direction dir = sorting.getDirection();
        CertificateUpdatesSortBy sortBy = CertificateUpdatesSortBy.valueOf(sorting.getSort());
        Collections.sort(certUpdates, GatewayControllerHelper.getCertificateComparator(sorting, sortBy));
        model.addAttribute("certUpdates", certUpdates);
        addGatewayMessages(model, userContext);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (CertificateUpdatesSortBy column : CertificateUpdatesSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
    }
    
    public Set<GatewayNMIPAddressPort> getAllGatewayNMIPPorts() {
    	Set<GatewayNMIPAddressPort> nmIPAddressPorts = new HashSet<GatewayNMIPAddressPort>();
        Set<RfnGateway> allGateways = rfnGatewayService.getAllGateways();
        allGateways.removeIf(g -> g.getData() == null);
        nmIPAddressPorts = allGateways
                .stream()
                .filter(g -> g.getData().getNmIpAddress() != null && g.getData().getNmPort() != null)
                .map(g -> {
                    return (new GatewayNMIPAddressPort(g.getData().getNmIpAddress(), g.getData().getNmPort()));
                })
                .collect(Collectors.toSet());
    	
    	return nmIPAddressPorts;
    
    }
    
    public GatewayNMIPAddressPort getMostUsedGatewayNMIPPort() {
        Set<RfnGateway> allGateways = rfnGatewayService.getAllGateways();
        allGateways.removeIf(g -> g.getData() == null);
        String mostNmIpAddress = allGateways.stream()
                .filter(g -> g.getData().getNmIpAddress() != null)
                // summarize NmIpAddress
                .collect(Collectors.groupingBy(g -> g.getData().getNmIpAddress(), Collectors.counting()))
                // fetch the max entry
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        if (mostNmIpAddress != null) {
            Integer nmPort = allGateways.stream()
                    .filter(g -> g.getData().getNmIpAddress().equals(mostNmIpAddress))
                    .findFirst().orElse(null)
                    .getData()
                    .getNmPort();
            return new GatewayNMIPAddressPort(mostNmIpAddress, nmPort);
        }
        return null;
    }
}