package com.cannontech.web.widget;

import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.NEIGHBOR_COUNT;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.NODE_DATA;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.NODE_NETWORK_INFO;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.READY_BATTERY_NODE_COUNT;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.node.CellularIplinkRelayData;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.node.NodeNetworkInfo;
import com.cannontech.common.rfn.message.route.RouteData;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

@Controller
@RequestMapping("/rfnDeviceMetadataWidget/*")
public class RfnDeviceMetadataWidget extends AdvancedWidgetControllerBase {

    private static final Logger log = YukonLogManager.getLogger(RfnDeviceMetadataWidget.class);

    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    @Autowired private NmNetworkService nmNetworkService;
    @Autowired private DateFormattingService dateFormattingService;

    private String keyPrefix = "yukon.web.widgets.RfnDeviceMetadataWidget.";

    @Autowired
    public RfnDeviceMetadataWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {

        addInput(simpleWidgetInput);
        setLazyLoad(true);
    }

    @RequestMapping("render")
    public String render(final ModelMap model, int deviceId, final YukonUserContext context) {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        
        String unknown = accessor.getMessage("yukon.common.unknown");

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("device", device);
        List<Pair<String, Object>> metadataPairs = new ArrayList<>();
        List<Pair<String, Object>> csrMetadataPairs = new ArrayList<>();
        try {
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaDataMultiResult = metadataMultiService
                    .getMetadataForDeviceRfnIdentifier(device.getRfnIdentifier(),
                            Set.of(REVERSE_LOOKUP_NODE_COMM, NODE_DATA, NODE_NETWORK_INFO, READY_BATTERY_NODE_COUNT,
                                    PRIMARY_FORWARD_NEIGHBOR_DATA, PRIMARY_FORWARD_ROUTE_DATA, NEIGHBOR_COUNT));

            RfnMetadataMultiQueryResult metadataMulti = metaDataMultiResult.get(device.getRfnIdentifier());

            Pair<String, Object> primaryGateway;
            DynamicRfnDeviceData deviceData = rfnDeviceDao.findDynamicRfnDeviceData(device.getPaoIdentifier().getPaoId());
            if (deviceData != null) {
                primaryGateway = Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_GATEWAY"), deviceData.getGateway().getName());
            } else {
                primaryGateway = Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_GATEWAY"), unknown);
            }
            metadataPairs.add(primaryGateway);
            csrMetadataPairs.add(primaryGateway);

            Pair<String, Object> reverseForward = Pair.of(accessor.getMessage(keyPrefix + "REVERSE_LOOKUP"), unknown);
            Pair<String, Object> commStatusPair = Pair.of(accessor.getMessage(keyPrefix + "COMM_STATUS"), "UNKNOWN");
            Pair<String, Object> commStatusTime = Pair.of(accessor.getMessage(keyPrefix + "COMM_STATUS_TIMESTAMP"), unknown);

            if (metadataMulti.isValidResultForMulti(REVERSE_LOOKUP_NODE_COMM)) {
                NodeComm comm = (NodeComm) metadataMulti.getMetadatas().get(REVERSE_LOOKUP_NODE_COMM);
                try {
                    RfnDevice reverseGateway = rfnDeviceDao.getDeviceForExactIdentifier(comm.getGatewayRfnIdentifier());
                    reverseForward = Pair.of(accessor.getMessage(keyPrefix + "REVERSE_LOOKUP"), reverseGateway.getName());
                } catch (Exception e) {
                    // we can't create a gateway from RfnIdentifier
                }

                NodeComm commStatus = nmNetworkService.getNodeCommStatusFromMultiQueryResult(device, metadataMulti);
                if (commStatus != null && commStatus.getNodeCommStatus() != null) {
                    commStatusPair = Pair.of(accessor.getMessage(keyPrefix + "COMM_STATUS"), commStatus.getNodeCommStatus().name());
                    String dateTime = dateFormattingService.format(commStatus.getNodeCommStatusTimestamp(), DateFormatEnum.DATEHM,
                            context);
                    commStatusTime = Pair.of(accessor.getMessage(keyPrefix + "COMM_STATUS_TIMESTAMP"), dateTime);
                }
            } else {
                log.info("REVERSE_LOOKUP_NODE_COMM not found for {}", device);
            }
            metadataPairs.add(reverseForward);
            csrMetadataPairs.add(reverseForward);
            metadataPairs.add(commStatusPair);
            csrMetadataPairs.add(commStatusPair);
            metadataPairs.add(commStatusTime);
            csrMetadataPairs.add(commStatusTime);
            
            if (metadataMulti.isValidResultForMulti(NODE_DATA)) {
                NodeData data = (NodeData) metadataMulti.getMetadatas().get(NODE_DATA);
                Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "NODE_SERIAL_NUMBER"),
                        data.getNodeSerialNumber());
                metadataPairs.add(pair);
                csrMetadataPairs.add(pair);
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "BOOT_LOADER_VERSION"), data.getBootLoaderVersion()));
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "HARDWARE_VERSION"), data.getHardwareVersion()));
                String dateTime = dateFormattingService.format(data.getInNetworkTimestamp(), DateFormatEnum.DATEHM, context);
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "IN_NETWORK_TIMESTAMP"), dateTime));
                metadataPairs.add(Pair.of(accessor.getMessage("yukon.web.modules.operator.mapNetwork.macAddress"), data.getMacAddress()));
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NODE_FIRMWARE_VERSION"), data.getFirmwareVersion()));
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NODE_TYPE"),
                        accessor.getMessage(keyPrefix + "NODE_TYPE." + data.getNodeType())));
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "PRODUCT_NUMBER"), data.getProductNumber()));
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "SUB_MODULE_FIRMWARE_VERSION"),
                        data.getSecondaryModuleFirmwareVersion()));
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "IPV6_ADDRESS"), data.getNodeIpv6Address()));
                if (data.getWifiSuperMeterData() != null) {
                    model.addAttribute("wifiSuperMeterData", data.getWifiSuperMeterData());
                }
                if (data.getCellularIplinkRelayData() != null) {
                    String cellPrefix = keyPrefix + "CellularData.";
                    CellularIplinkRelayData cellData = data.getCellularIplinkRelayData();
                    metadataPairs.add(Pair.of(accessor.getMessage(cellPrefix + "APN"), cellData.getApn()));
                    metadataPairs.add(Pair.of(accessor.getMessage(cellPrefix + "FIRMWARE_VERSION"), cellData.getFirmwareVersion()));
                    metadataPairs.add(Pair.of(accessor.getMessage(cellPrefix + "ICCID"), cellData.getIccid()));
                    metadataPairs.add(Pair.of(accessor.getMessage(cellPrefix + "IMEI"), cellData.getImei()));
                    metadataPairs.add(Pair.of(accessor.getMessage(cellPrefix + "IMSI"), cellData.getImsi()));
                    String modemEnabledValue = cellData.getModemEnabled() ? accessor.getMessage("yukon.common.enabled") : accessor.getMessage("yukon.common.disabled");
                    metadataPairs.add(Pair.of(accessor.getMessage(cellPrefix + "MODEM_ENABLED"), modemEnabledValue));
                    String simCardPresentValue = cellData.getSimCardPresent() ? accessor.getMessage(cellPrefix + "SIM_CARD_PRESENT.PRESENT") : accessor.getMessage(cellPrefix + "SIM_CARD_PRESENT.NOT_PRESENT");
                    metadataPairs.add(Pair.of(accessor.getMessage(cellPrefix + "SIM_CARD_PRESENT"), simCardPresentValue));
                    metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "virtualGatewayIpv6Address"), cellData.getVirtualGwIpv6Addr()));
                }
            } else {
                log.info("NODE_DATA not found for {}", device);
            }
            if (metadataMulti.isValidResultForMulti(NODE_NETWORK_INFO)) {
                NodeNetworkInfo info = (NodeNetworkInfo) metadataMulti.getMetadatas().get(NODE_NETWORK_INFO);
                if (!info.getNodeGroupNames().isEmpty()) {
                    Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "GROUPS"),
                            String.join(", ", info.getNodeGroupNames()));
                    metadataPairs.add(pair);
                    csrMetadataPairs.add(pair);
                }
                if (!info.getNodeNames().isEmpty()) {
                    metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NODE_NAMES"),
                            String.join(", ", info.getNodeNames())));
                }
                //metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "IPV6_ADDRESS"), info.getIpv6Address()));
                //metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "HOSTNAME"), info.getHostname()));
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "SENSOR_FIRMWARE_VERSION"), info.getSensorFirmwareVersion()));
            } else {
                log.info("NODE_NETWORK_INFO not found for {}", device);
            }
            if (metadataMulti.isValidResultForMulti(READY_BATTERY_NODE_COUNT)) {
                Integer count = (Integer) metadataMulti.getMetadatas().get(READY_BATTERY_NODE_COUNT);
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NUM_ASSOCIATIONS"), count));
            } else {
                log.info("READY_BATTERY_NODE_COUNT not found for {}", device);
            }
            if (metadataMulti.isValidResultForMulti(NEIGHBOR_COUNT)) {
                Integer count = (Integer) metadataMulti.getMetadatas().get(NEIGHBOR_COUNT);
                Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "NEIGHBOR_COUNT"), count);
                metadataPairs.add(pair);
                csrMetadataPairs.add(pair);
            } else {
                log.info("NEIGHBOR_COUNT not found for {}", device);
            }
            if (metadataMulti.isValidResultForMulti(PRIMARY_FORWARD_NEIGHBOR_DATA)) {
                NeighborData data = (NeighborData) metadataMulti.getMetadatas().get(PRIMARY_FORWARD_NEIGHBOR_DATA);
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_NEIGHBOR"), data.getNeighborMacAddress()));
                String dateTime = dateFormattingService.format(data.getNeighborDataTimestamp(), DateFormatEnum.DATEHM, context);
                metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_NEIGHBOR_DATA_TIMESTAMP"), dateTime));
                metadataPairs
                        .add(Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_NEIGHBOR_LINK_COST"), data.getNeighborLinkCost()));
            } else {
                log.info("PRIMARY_FORWARD_NEIGHBOR_DATA not found for {}", device);
            }
            if (metadataMulti.isValidResultForMulti(PRIMARY_FORWARD_ROUTE_DATA)) {
                RouteData data = (RouteData) metadataMulti.getMetadatas().get(PRIMARY_FORWARD_ROUTE_DATA);
                Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_GATEWAY_HOP_COUNT"),
                        data.getHopCount());
                metadataPairs.add(pair);
                csrMetadataPairs.add(pair);
            } else {
                log.info("PRIMARY_FORWARD_ROUTE_DATA not found for {}", device);
            }

            metadataPairs.removeIf(pair -> pair.getValue() == null);
            metadataPairs.sort(Comparator.comparing(pair -> pair.getKey()));
            csrMetadataPairs.removeIf(pair -> pair.getValue() == null);
            csrMetadataPairs.sort(Comparator.comparing(pair -> pair.getKey()));
            model.addAttribute("metadata", metadataPairs);
            model.addAttribute("csrMetadata", csrMetadataPairs);
            log.debug("metadata {}", metadataPairs);
            log.debug("csrMetadata {}", csrMetadataPairs);
        } catch (NmCommunicationException e) {
            String nmError = accessor.getMessage("yukon.web.modules.operator.mapNetwork.exception.commsError");
            model.addAttribute("error", nmError);
            log.error("Failed to get metadata for " + device.getPaoIdentifier().getPaoId(), e);
        }
        if (model.get("error") == null) {
            model.addAttribute("showAll", true);
        }
        return "rfnDeviceMetadataWidget/render.jsp";
    }
}