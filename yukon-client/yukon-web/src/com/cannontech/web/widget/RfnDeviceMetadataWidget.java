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
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.node.NodeNetworkInfo;
import com.cannontech.common.rfn.message.route.RouteData;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
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
    
    private String keyPrefix = "yukon.web.widgets.RfnDeviceMetadataWidget.";
       
    @Autowired
    public RfnDeviceMetadataWidget(@Qualifier("widgetInput.deviceId")
                SimpleWidgetInput simpleWidgetInput) {
        
        addInput(simpleWidgetInput);
        setLazyLoad(true);
    }

    @RequestMapping("render")
    public String render(final ModelMap model, int deviceId, final YukonUserContext context) {
    	MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
    	
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

			DynamicRfnDeviceData deviceData = rfnDeviceDao
					.findDynamicRfnDeviceData(device.getPaoIdentifier().getPaoId());
			if (deviceData != null) {
				Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_GATEWAY"),
						deviceData.getGateway().getName());
				metadataPairs.add(pair);
				csrMetadataPairs.add(pair);
			}
			
			if (metadataMulti.isValidResultForMulti(REVERSE_LOOKUP_NODE_COMM)) {
				NodeComm comm = (NodeComm) metadataMulti.getMetadatas().get(REVERSE_LOOKUP_NODE_COMM);
				Pair<String, Object> pair;
				try {
					RfnDevice reverseGateway = rfnDeviceDao.getDeviceForExactIdentifier(comm.getGatewayRfnIdentifier());
					pair = Pair.of(accessor.getMessage(keyPrefix + "REVERSE_LOOKUP"), reverseGateway.getName());
					metadataPairs.add(pair);
					csrMetadataPairs.add(pair);

				} catch (Exception e) {
					// we can't create a gateway from RfnIdentifier
				}

				NodeComm commStatus = nmNetworkService.getNodeCommStatusFromMultiQueryResult(device, metadataMulti);
				if (commStatus == null || commStatus.getNodeCommStatus() == null) {
					// primary forward and reverse lookup are not the same, set comm status to
					// unknown
					pair = Pair.of(accessor.getMessage(keyPrefix + "COMM_STATUS"), "UNKNOWN");
					metadataPairs.add(pair);
					csrMetadataPairs.add(pair);
				} else {
					pair = Pair.of(accessor.getMessage(keyPrefix + "COMM_STATUS"), commStatus.getNodeCommStatus());
					metadataPairs.add(pair);
					csrMetadataPairs.add(pair);
					pair = Pair.of(accessor.getMessage(keyPrefix + "COMM_STATUS_TIMESTAMP"),
							commStatus.getNodeCommStatusTimestamp());
					metadataPairs.add(pair);
					csrMetadataPairs.add(pair);
				}
			} else {
				log.info("REVERSE_LOOKUP_NODE_COMM not found for {}", device);
			}
			if (metadataMulti.isValidResultForMulti(NODE_DATA)) {
				NodeData data = (NodeData) metadataMulti.getMetadatas().get(NODE_DATA);
				Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "NODE_SERIAL_NUMBER"), data.getNodeSerialNumber());
				metadataPairs.add(pair);
				csrMetadataPairs.add(pair);
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "HARDWARE_VERSION"), data.getHardwareVersion()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "IN_NETWORK_TIMESTAMP"), data.getInNetworkTimestamp()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NODE_ADDRESS"), data.getFirmwareVersion()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NODE_FIRMWARE_VERSION"), data.getFirmwareVersion()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NODE_TYPE"), data.getNodeType()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "PRODUCT_NUMBER"), data.getProductNumber()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "SUB_MODULE_FIRMWARE_VERSION"), data.getSecondaryModuleFirmwareVersion()));
				if(data.getWifiSuperMeterData() != null) {
					  model.addAttribute("wifiSuperMeterData", data.getWifiSuperMeterData());
				}
			} else {
				log.info("NODE_DATA not found for {}", device);
			}
			if (metadataMulti.isValidResultForMulti(NODE_NETWORK_INFO)) {
				NodeNetworkInfo info = (NodeNetworkInfo) metadataMulti.getMetadatas().get(NODE_NETWORK_INFO);
				if (!info.getNodeGroupNames().isEmpty()) {
					Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "GROUPS"),
							String.join(",", info.getNodeGroupNames()));
					metadataPairs.add(pair);
					csrMetadataPairs.add(pair);
				}
				if (!info.getNodeNames().isEmpty()) {
					metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "NODE_NAMES"),
							String.join(",", info.getNodeNames())));
				}
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "IPV6_ADDRESS"), info.getIpv6Address()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "HOSTNAME"), info.getHostname()));
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
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_NEIGHBOR_DATA_TIMESTAMP"), data.getNeighborDataTimestamp()));
				metadataPairs.add(Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_NEIGHBOR_LINK_COST"), data.getNeighborLinkCost()));
			} else {
				log.info("PRIMARY_FORWARD_NEIGHBOR_DATA not found for {}", device);
			}
			if (metadataMulti.isValidResultForMulti(PRIMARY_FORWARD_ROUTE_DATA)) {
				RouteData data = (RouteData) metadataMulti.getMetadatas().get(PRIMARY_FORWARD_ROUTE_DATA);
				Pair<String, Object> pair = Pair.of(accessor.getMessage(keyPrefix + "PRIMARY_GATEWAY_HOP_COUNT"), data.getHopCount());
				metadataPairs.add(pair);
				csrMetadataPairs.add(pair);
			} else {
				log.info("PRIMARY_FORWARD_ROUTE_DATA not found for {}", device);
			}
	        
			metadataPairs.sort(Comparator.comparing(pair -> pair.getKey()));
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
		return "rfnDeviceMetadataWidget/render.jsp";
    }
}