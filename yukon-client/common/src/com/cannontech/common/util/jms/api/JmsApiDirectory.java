package com.cannontech.common.util.jms.api;

import static com.cannontech.common.util.jms.api.JmsApiCategory.*;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.*;
import static com.cannontech.common.util.jms.api.JmsCommunicationPattern.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveRequest;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveResponse;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveResponse;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReply;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.amr.rfn.message.event.RfnEventArchiveRequest;
import com.cannontech.amr.rfn.message.event.RfnEventArchiveResponse;
import com.cannontech.amr.rfn.message.read.RfnMeterReadDataReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadRequest;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveRequest;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveResponse;
import com.cannontech.broker.message.request.BrokerSystemMetricsRequest;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.message.alarm.AlarmArchiveRequest;
import com.cannontech.common.rfn.message.alarm.AlarmArchiveResponse;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveRequest;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveResponse;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoRequest;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoResponse;
import com.cannontech.common.rfn.message.gateway.GatewayActionResponse;
import com.cannontech.common.rfn.message.gateway.GatewayArchiveRequest;
import com.cannontech.common.rfn.message.gateway.GatewayCollectionRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestResponse;
import com.cannontech.common.rfn.message.gateway.GatewayCreateRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayEditRequest;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySetConfigRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySetConfigResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAck;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionRequest;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResponse;
import com.cannontech.common.rfn.message.location.LocationResponse;
import com.cannontech.common.rfn.message.location.LocationResponseAck;
import com.cannontech.common.rfn.message.metadata.RfnMetadataRequest;
import com.cannontech.common.rfn.message.metadata.RfnMetadataResponse;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReply;
import com.cannontech.common.rfn.message.network.RfnNeighborDataRequest;
import com.cannontech.common.rfn.message.network.RfnParentReply;
import com.cannontech.common.rfn.message.network.RfnParentRequest;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReply;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataRequest;
import com.cannontech.common.rfn.message.node.RfnNodeCommArchiveRequest;
import com.cannontech.common.rfn.message.node.RfnNodeCommArchiveResponse;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveResponse;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveResponse;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastRequest;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReply;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReply;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastRequest;
import com.cannontech.infrastructure.model.InfrastructureWarningsRefreshRequest;
import com.cannontech.infrastructure.model.InfrastructureWarningsRequest;
import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenResponse;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.thirdparty.messaging.SmartUpdateRequestMessage;

/**
 * This is intended to be the single repository for all JmsApi information in the Yukon Java code.<br><br>
 * 
 * To add messaging for a new feature, create a new public static final JmsApi object here, and add it to the 
 * {@code jmsApis} map with a category.<br><br>
 * 
 * {@code JmsApi.builder} requires that all APIs have a name, description, communicationPattern, sender, receiver, queue 
 * and requestMessage specified. Additionally, you will need to specify ackQueue, responseQueue, ackMessage and
 * responseMessage if the communicationPattern involves ack or response. Multiple senders and receivers may also be
 * specified. (For example, if NM or a Yukon simulator can both receive a particular message.)
 * 
 * To define any messaging that is sent over a temp queue, use JmsQueue.TEMP_QUEUE.
 */
public final class JmsApiDirectory {
    private static final Comparator<JmsApi<?,?,?>> API_COMPARATOR = (api1, api2) -> api1.getName().compareTo(api2.getName());
    /*
     * WARNING: If you add any static fields to JmsApiDirectory that are not JmsApi definitions, they need to be 
     * filtered in JmsApiDirectoryTest!
     */
    
    public static final JmsApi<InfrastructureWarningsRequest,?,?> INFRASTRUCTURE_WARNINGS = 
            JmsApi.builder(InfrastructureWarningsRequest.class)
                  .name("Infrastructure Warnings Widget Refresh")
                  .description("Sent from the Infrastructure Warnings widget to the Infrastructure Warnings service "
                               + "to recalculate the warnings.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.infrastructure.InfrastructureWarningsRequest"))
                  .requestMessage(InfrastructureWarningsRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<InfrastructureWarningsRefreshRequest,?,?> INFRASTRUCTURE_WARNINGS_CACHE_REFRESH = 
            JmsApi.builder(InfrastructureWarningsRefreshRequest.class)
                  .name("Infrastructure Warnings WS Cache Refresh")
                  .description("Sent from the Infrastructure Warnings service to the Infrastructure Warnings Widget service "
                               + "to notify the the widget service that recalcutaion is done and the warnings and summary caches can be refreshed.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.infrastructure.InfrastructureWarningsRefreshRequest"))
                  .requestMessage(InfrastructureWarningsRefreshRequest.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    //TODO: this queue name is wrong in exportedServicesContext.xml
    public static final JmsApi<DeviceDataMonitorStatusRequest,?,DeviceDataMonitorStatusResponse> DEVICE_DATA_MONITOR_STATUS =
            JmsApi.builder(DeviceDataMonitorStatusRequest.class, DeviceDataMonitorStatusResponse.class)
                  .name("Device Data Monitor Status")
                  .description("Request from the Device Data Monitor front-end to the Device Data Monitor Calculation "
                               + "Service, to get the status of the recalculation.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.dataDeviceMonitor.RecalculateStatus"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(DeviceDataMonitorStatusRequest.class)
                  .responseMessage(DeviceDataMonitorStatusResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<DeviceDataMonitorMessage,?,?> DEVICE_DATA_MONITOR_RECALC =
            JmsApi.builder(DeviceDataMonitorMessage.class)
                  .name("Device Data Monitor Recalculation")
                  .description("Notification from the Device Data Monitor front-end to the Device Data Monitor "
                          + "Calculation Service, to initiate a recalculation.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.amr.dataDeviceMonitor.RecalculateRequest"))
                  .requestMessage(DeviceDataMonitorMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<OutageJmsMessage,?,?> STATUS_POINT_MONITOR_OUTAGE =
            JmsApi.builder(OutageJmsMessage.class)
                  .name("Status Point Monitor Outage")
                  .description("Outage notification sent from the Status Point Monitor to Multispeak code, which will "
                          + "send an outage event to any connected Multispeak vendors that support it.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.amr.OutageJmsMessage"))
                  .requestMessage(OutageJmsMessage.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    //This is only referenced in Spring configuration XML - monitorsContext.xml and pointInjectionContext.xml
    public static final JmsApi<RichPointData,?,?> RICH_POINT_DATA =
            JmsApi.builder(RichPointData.class)
                  .name("Rich Point Data")
                  .description("Sends point data (on a JMS topic) from the Rich Point Data Service to monitors for "
                          + "processing.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.topic.RichPointUpdateObj"))
                  .requestMessage(RichPointData.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RfnExpressComUnicastRequest,RfnExpressComUnicastReply,RfnExpressComUnicastDataReply> RFN_EXPRESSCOM_UNICAST_WITH_DATA = 
            JmsApi.builder(RfnExpressComUnicastRequest.class, RfnExpressComUnicastReply.class, RfnExpressComUnicastDataReply.class)
                  .name("RFN Expresscom Unicast With Data")
                  .description("Attempts to send a unicast request for a RFN device. Will expect TWO responses. The " +
                               "first is a status message indicating this is a known device and a command will be " + 
                               "tried, or a command is not possible for this device.  This response should come back " +
                               "within seconds. The second response is the actual data.  This response is only " +
                               "expected if the first response was 'OK'.  This response can take anywhere from " +
                               "seconds to minutes to tens of minutes depending on network performance.")
                  .communicationPattern(REQUEST_ACK_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.dr.rfn.ExpressComUnicastRequest"))
                  .ackQueue(JmsQueue.TEMP_QUEUE)
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnExpressComUnicastRequest.class)
                  .ackMessage(RfnExpressComUnicastReply.class)
                  .responseMessage(RfnExpressComUnicastDataReply.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<RfnExpressComUnicastRequest,?,RfnExpressComUnicastReply> RFN_EXPRESSCOM_UNICAST = 
            JmsApi.builder(RfnExpressComUnicastRequest.class, RfnExpressComUnicastReply.class)
                  .name("RFN Expresscom Unicast")
                  .description("Attempts to send a unicast request for a RFN device. Will expect ONE response. The " +
                               "response is a status message indicating this is a known device and a command will be " +
                               "tried, or a command is not possible for this device. This response should come back " +
                               "within seconds.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.dr.rfn.ExpressComUnicastRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnExpressComUnicastRequest.class)
                  .responseMessage(RfnExpressComUnicastReply.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<RfnExpressComUnicastRequest,RfnExpressComUnicastReply,RfnExpressComUnicastDataReply> RFN_EXPRESSCOM_UNICAST_BULK =
            JmsApi.builder(RfnExpressComUnicastRequest.class, RfnExpressComUnicastReply.class, RfnExpressComUnicastDataReply.class)
                  .name("RFN Expresscom Unicast Bulk")
                  .description("Attempts to send unicast requests in bulk for a RFN devices. Will expect ONE response, "
                          + "however there could be a second (data) response on the response queue. The first is a "
                          + "status message indicating this is a known device and a command will be tried, or a "
                          + "command is not possible for this device. This response should come back within seconds. "
                          + "The second response if desired would be the actual data. This response is only expected "
                          + "if the first response was 'OK' and the request's responseExpected field was set to true. "
                          + "This response can take anywhere from seconds to minutes depending on network performance.")
                  .communicationPattern(REQUEST_ACK_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.dr.rfn.ExpressComBulkUnicastRequest"))
                  .ackQueue(JmsQueue.TEMP_QUEUE)
                  .responseQueue(new JmsQueue("yukon.qr.obj.dr.rfn.ExpressComBulkUnicastResponse"))
                  .requestMessage(RfnExpressComUnicastRequest.class)
                  .ackMessage(RfnExpressComUnicastReply.class)
                  .responseMessage(RfnExpressComUnicastDataReply.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
                  
    public static final JmsApi<RfnExpressComBroadcastRequest,?,?> RFN_EXPRESSCOM_BROADCAST =
            JmsApi.builder(RfnExpressComBroadcastRequest.class)
                  .name("RFN Expresscom Broadcast")
                  .description("Sends a broadcast message request out to the entire RFN network. Does not explicitly "
                          + "expect responses at this point.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest"))
                  .requestMessage(RfnExpressComBroadcastRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<DeviceDataStreamingConfigRequest,?,DeviceDataStreamingConfigResponse> DATA_STREAMING_CONFIG =
            JmsApi.builder(DeviceDataStreamingConfigRequest.class, DeviceDataStreamingConfigResponse.class)
                  .name("Data Streaming Config")
                  .description("Sends a verification, config or sync request to Network Manager. For a verification " +
                               "request, a successful response means that the config request can be sent. For a " + 
                               "config request, a successful response means that Network Manager has updated its " +
                               "records to reflect the data streaming configuration change, and Yukon must now send " +
                               "the corresponding changes to the devices (via Porter).")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.dataStreaming.request"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(DeviceDataStreamingConfigRequest.class)
                  .responseMessage(DeviceDataStreamingConfigResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<GatewayDataStreamingInfoRequest,?,GatewayDataStreamingInfoResponse> GATEWAY_DATA_STREAMING_INFO =
            JmsApi.builder(GatewayDataStreamingInfoRequest.class, GatewayDataStreamingInfoResponse.class)
                  .name("Gateway Data Streaming Info")
                  .description("Sends a request for gateway data streaming information to Network Manager for the " +
                               "specified gateways. When response is received, generates point data and sends it to "
                               + "dispatch.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.dataStreaming.request"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayDataStreamingInfoRequest.class)
                  .responseMessage(GatewayDataStreamingInfoResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<RfnPrimaryRouteDataRequest,?,RfnPrimaryRouteDataReply> NETWORK_PRIMARY_ROUTE =
        JmsApi.builder(RfnPrimaryRouteDataRequest.class, RfnPrimaryRouteDataReply.class)
              .name("Network Primary Route")
              .description("Asks NM for the device's route. NM can return NO_PARENT if primary route information is"
                           + " requested for battery node (water meter). NM searches for the battery node's parent "
                           + "first, then finds the parent's primary route and returns that as the battery node's "
                           + "primary route.")
              .communicationPattern(REQUEST_RESPONSE)
              .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.network.data.request"))
              .responseQueue(JmsQueue.TEMP_QUEUE)
              .requestMessage(RfnPrimaryRouteDataRequest.class)
              .responseMessage(RfnPrimaryRouteDataReply.class)
              .sender(YUKON_WEBSERVER)
              .receiver(NETWORK_MANAGER)
              .receiver(YUKON_SIMULATORS)
              .build();
    
    public static final JmsApi<RfnNeighborDataRequest,?,RfnNeighborDataReply> NETWORK_NEIGHBOR =
        JmsApi.builder(RfnNeighborDataRequest.class, RfnNeighborDataReply.class)
              .name("Network Neighbor")
              .description("Asks NM for the device's neighbors.")
              .communicationPattern(REQUEST_RESPONSE)
              .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.network.data.request"))
              .responseQueue(JmsQueue.TEMP_QUEUE)
              .requestMessage(RfnNeighborDataRequest.class)
              .responseMessage(RfnNeighborDataReply.class)
              .sender(YUKON_WEBSERVER)
              .receiver(NETWORK_MANAGER)
              .receiver(YUKON_SIMULATORS)
              .build();
    
    public static final JmsApi<RfnParentRequest,?,RfnParentReply> NETWORK_PARENT =
        JmsApi.builder(RfnParentRequest.class, RfnParentReply.class)
              .name("Network Parent")
              .description("Asks NM for the device's parent information.")
              .communicationPattern(REQUEST_RESPONSE)
              .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.network.data.request"))
              .responseQueue(JmsQueue.TEMP_QUEUE)
              .requestMessage(RfnParentRequest.class)
              .responseMessage(RfnParentReply.class)
              .sender(YUKON_WEBSERVER)
              .receiver(NETWORK_MANAGER)
              .receiver(YUKON_SIMULATORS)
              .build();
    
    public static final JmsApi<GatewayCreateRequest,?,GatewayUpdateResponse> RF_GATEWAY_CREATE =
        JmsApi.builder(GatewayCreateRequest.class, GatewayUpdateResponse.class)
              .name("RF Gateway Create")
              .description("Sent by Yukon on RF Gateway creation to tell Network Manager to create the matching gateway "
                      + "object on its side.")
              .communicationPattern(REQUEST_RESPONSE)
              .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayUpdateRequest"))
              .responseQueue(JmsQueue.TEMP_QUEUE)
              .requestMessage(GatewayCreateRequest.class)
              .responseMessage(GatewayUpdateResponse.class)
              .sender(YUKON_WEBSERVER)
              .receiver(NETWORK_MANAGER)
              .receiver(YUKON_SIMULATORS)
              .build();
    
    public static final JmsApi<GatewayEditRequest,?,GatewayUpdateResponse> RF_GATEWAY_EDIT =
            JmsApi.builder(GatewayEditRequest.class, GatewayUpdateResponse.class)
                  .name("RF Gateway Edit")
                  .description("Sent by Yukon when editing an RF Gateway, to tell Network Manager to make matching "
                          + "changes on its side.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayUpdateRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayEditRequest.class)
                  .responseMessage(GatewayUpdateResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<GatewayDeleteRequest,?,GatewayUpdateResponse> RF_GATEWAY_DELETE =
            JmsApi.builder(GatewayDeleteRequest.class, GatewayUpdateResponse.class)
                  .name("RF Gateway Delete")
                  .description("Sent by Yukon when deleting an RF Gateway, to tell Network manager to also delete the "
                          + "gateway object on its side.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayUpdateRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayDeleteRequest.class)
                  .responseMessage(GatewayUpdateResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<GatewayDeleteRequest,?,?> RF_GATEWAY_DELETE_FROM_NM =
            JmsApi.builder(GatewayDeleteRequest.class)
                  .name("RF Gateway Delete from NM")
                  .description("Sent by Network Manager when deleting an RF Gateway, to tell Yukon to also delete the "
                          + "Gateway object on its side.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayDeleteRequest"))
                  .requestMessage(GatewayDeleteRequest.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_SIMULATORS)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<GatewayScheduleDeleteRequest,?,GatewayActionResponse> RF_GATEWAY_SCHEDULE_DELETE =
            JmsApi.builder(GatewayScheduleDeleteRequest.class, GatewayActionResponse.class)
                  .name("RF Gateway Schedule Delete")
                  .description("Sends a schedule delete request for an RF Gateway to Network Manager.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayActionRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayScheduleDeleteRequest.class)
                  .responseMessage(GatewayActionResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<GatewayCollectionRequest,?,GatewayActionResponse> RF_GATEWAY_COLLECTION =
            JmsApi.builder(GatewayCollectionRequest.class, GatewayActionResponse.class)
                  .name("RF Gateway Collection")
                  .description("Sends a data collection request for an RF Gateway to Network Manager.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayActionRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayCollectionRequest.class)
                  .responseMessage(GatewayActionResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<GatewayConnectRequest,?,GatewayActionResponse> RF_GATEWAY_CONNECT =
            JmsApi.builder(GatewayConnectRequest.class, GatewayActionResponse.class)
                  .name("RF Gateway Connect")
                  .description("Sends a connect request for an RF Gateway to Network Manager.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayActionRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayConnectRequest.class)
                  .responseMessage(GatewayActionResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<GatewayScheduleRequest,?,GatewayActionResponse> RF_GATEWAY_SCHEDULE_REQUEST =
            JmsApi.builder(GatewayScheduleRequest.class, GatewayActionResponse.class)
                  .name("RF Gateway Schedule Request")
                  .description("Sends a schedule request for an RF Gateway to Network Manager.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayActionRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayScheduleRequest.class)
                  .responseMessage(GatewayActionResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<GatewayConnectionTestRequest,?,GatewayConnectionTestResponse> RF_GATEWAY_CONNECTION_TEST =
            JmsApi.builder(GatewayConnectionTestRequest.class, GatewayConnectionTestResponse.class)
                  .name("RF Gateway Connection Test")
                  .description("Sends a connection test request for an RF Gateway to Network Manager.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayActionRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayConnectionTestRequest.class)
                  .responseMessage(GatewayConnectionTestResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<RfnUpdateServerAvailableVersionRequest,?,RfnUpdateServerAvailableVersionResponse> RF_UPDATE_SERVER_AVAILABLE_VERSION =
            JmsApi.builder(RfnUpdateServerAvailableVersionRequest.class, RfnUpdateServerAvailableVersionResponse.class)
                  .name("RF Update Server Available Version")
                  .description("Requests the firmware version available on the firmware update server from Network "
                          + "Manager.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.UpdateServerAvailableVersionRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnUpdateServerAvailableVersionRequest.class)
                  .responseMessage(RfnUpdateServerAvailableVersionResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<RfnGatewayFirmwareUpdateRequest,?,RfnGatewayFirmwareUpdateResponse> RF_GATEWAY_FIRMWARE_UPGRADE =
            JmsApi.builder(RfnGatewayFirmwareUpdateRequest.class, RfnGatewayFirmwareUpdateResponse.class)
                  .name("RF Gateway Firmware Upgrade")
                  .description("Sends a firmware upgrade request from Yukon to Network Manager, specifying a set of "
                          + "gateways and update server info. Response is sent by Network Manager when the upgrade "
                          + "attempt is complete, processed on a different queue.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.RfnGatewayFirmwareUpdateRequest"))
                  .responseQueue(new JmsQueue("yukon.qr.obj.common.rfn.RfnGatewayFirmwareUpdateResponse"))
                  .requestMessage(RfnGatewayFirmwareUpdateRequest.class)
                  .responseMessage(RfnGatewayFirmwareUpdateResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<RfnGatewayUpgradeRequest,RfnGatewayUpgradeRequestAck,RfnGatewayUpgradeResponse> RF_GATEWAY_CERTIFICATE_UPDATE =
            JmsApi.builder(RfnGatewayUpgradeRequest.class, RfnGatewayUpgradeRequestAck.class, RfnGatewayUpgradeResponse.class)
                  .name("RF Gateway Certificate Update")
                  .description("Sends a certificate update package to old 'gateway 1.5's via Network Manager. "
                          + "The acknowledgement is sent back quickly on a temp queue. The gateway's upgrade reply is "
                          + "sent back on the gateway data queue some time later, when the action completes.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayUpgradeRequest"))
                  .ackQueue(JmsQueue.TEMP_QUEUE)
                  .responseQueue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayData"))
                  .requestMessage(RfnGatewayUpgradeRequest.class)
                  .ackMessage(RfnGatewayUpgradeRequestAck.class)
                  .responseMessage(RfnGatewayUpgradeResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .sender(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<GatewayDataRequest,?,GatewayDataResponse> RF_GATEWAY_DATA =
            JmsApi.builder(GatewayDataRequest.class, GatewayDataResponse.class)
                  .name("RF Gateway Data")
                  .description("Sent from Yukon to Network Manager to request current data for an RF Gateway.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayDataRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewayDataRequest.class)
                  .responseMessage(GatewayDataResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .sender(YUKON_SERVICE_MANAGER)
                  .sender(YUKON_WATCHDOG)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<Serializable,?,?> RF_GATEWAY_DATA_INTERNAL =
            JmsApi.builder(Serializable.class)
                  .name("RF Gateway Data (Internal)")
                  .description("Yukon Service Manager takes gateway data (which receives it first, from Network "
                          + "Manager) and passes it to Yukon webserver on a topic.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayDataTopic"))
                  .requestMessage(Serializable.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    public static final JmsApi<GatewayDataResponse,?,?> RF_GATEWAY_DATA_UNSOLICITED = 
            JmsApi.builder(GatewayDataResponse.class)
                  .name("RF Gateway Data (Unsolicited)")
                  .description("Sent from Network Manager to Yukon to provide updated RF Gateway data. (This is the "
                          + "same as the response to an RF Gateway Data request, but is sent unsolicited by NM "
                          + "whenever the gateway data changes).")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayData"))
                  .requestMessage(GatewayDataResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_SIMULATORS)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<GatewayArchiveRequest,?,?> RF_GATEWAY_ARCHIVE = 
            JmsApi.builder(GatewayArchiveRequest.class)
                  .name("Gateway Archive Request")
                  .description("Sent by Network Manager to notify Yukon that a new gateway has been created.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewayArchiveRequest"))
                  .requestMessage(GatewayArchiveRequest.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_SIMULATORS)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RfnMeterDemandResetRequest,?,RfnMeterDemandResetReply> RFN_METER_DEMAND_RESET =
            JmsApi.builder(RfnMeterDemandResetRequest.class, RfnMeterDemandResetReply.class)
                  .name("RFN Meter Demand Reset")
                  .description("Sends a demand reset to a meter.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.rfn.MeterDemandResetRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnMeterDemandResetRequest.class)
                  .responseMessage(RfnMeterDemandResetReply.class)
                  .sender(YUKON_WEBSERVER)
                  .sender(YUKON_EIM)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<RfnMetadataRequest,?,RfnMetadataResponse> RFN_METADATA =
            JmsApi.builder(RfnMetadataRequest.class, RfnMetadataResponse.class)
                  .name("RFN Metadata")
                  .description("Sends a request for an RFN device's metadata from Yukon to Network Manager.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.MetadataRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnMetadataRequest.class)
                  .responseMessage(RfnMetadataResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<RfnMeterDisconnectRequest,RfnMeterDisconnectInitialReply,RfnMeterDisconnectConfirmationReply> RFN_METER_DISCONNECT =
            JmsApi.builder(RfnMeterDisconnectRequest.class, RfnMeterDisconnectInitialReply.class, RfnMeterDisconnectConfirmationReply.class)
                  .name("RFN Meter Disconnect")
                  .description("Sends a disconnect request to an RFN meter via Network Manager. The initial reply "
                          + "indicates either that the command will be sent, or that there was an error sending. The "
                          + "final response indicates the ultimate success or failure of the operation.")
                  .communicationPattern(REQUEST_ACK_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.rfn.MeterDisconnectRequest"))
                  .ackQueue(JmsQueue.TEMP_QUEUE)
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnMeterDisconnectRequest.class)
                  .ackMessage(RfnMeterDisconnectInitialReply.class)
                  .responseMessage(RfnMeterDisconnectConfirmationReply.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<RfnMeterReadRequest,RfnMeterReadReply,RfnMeterReadDataReply> RFN_METER_READ =
            JmsApi.builder(RfnMeterReadRequest.class, RfnMeterReadReply.class, RfnMeterReadDataReply.class)
                  .name("Rfn Meter Read")
                  .description("Attempts to send a read request for an RFN meter. The first response is a status "
                          + "message indicating this is a known meter and a read will be tried, or a read is not "
                          + "possible for this meter. This response should come back within seconds. The second "
                          + "response is the actual read data. This response is only expected if the first response "
                          + "was OK. This response can take anywhere from seconds to minutes depending on network "
                          + "performance.")
                  .communicationPattern(REQUEST_ACK_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.rfn.MeterReadRequest"))
                  .ackQueue(JmsQueue.TEMP_QUEUE)
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnMeterReadRequest.class)
                  .ackMessage(RfnMeterReadReply.class)
                  .responseMessage(RfnMeterReadDataReply.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<RfnMeterReadingArchiveRequest,?,RfnMeterReadingArchiveResponse> RFN_METER_READ_ARCHIVE = 
            JmsApi.builder(RfnMeterReadingArchiveRequest.class, RfnMeterReadingArchiveResponse.class)
                  .name("Rfn Meter Read Archive")
                  .description("A notification from Network Manager to Yukon to provide new meter data.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest"))
                  .responseQueue(new JmsQueue("yukon.qr.obj.amr.rfn.MeterReadingArchiveResponse"))
                  .requestMessage(RfnMeterReadingArchiveRequest.class)
                  .responseMessage(RfnMeterReadingArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RfnLcrReadingArchiveRequest,?,RfnLcrReadingArchiveResponse> RFN_LCR_READ_ARCHIVE = 
            JmsApi.builder(RfnLcrReadingArchiveRequest.class, RfnLcrReadingArchiveResponse.class)
                  .name("RFN LCR Read Archive")
                  .description("A notification from Network Manager to Yukon to provide new LCR data.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest"))
                  .responseQueue(new JmsQueue("yukon.qr.obj.dr.rfn.LcrReadingArchiveResponse"))
                  .requestMessage(RfnLcrReadingArchiveRequest.class)
                  .responseMessage(RfnLcrReadingArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .sender(YUKON_SIMULATORS)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RfnLcrArchiveRequest,?,RfnLcrArchiveResponse> RFN_LCR_ARCHIVE =
            JmsApi.builder(RfnLcrArchiveRequest.class, RfnLcrArchiveResponse.class)
                  .name("RFN LCR Archive")
                  .description("A notification from Network Manager to Yukon for creation of a new LCR device.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest"))
                  .responseQueue(new JmsQueue("yukon.qr.obj.dr.rfn.LcrReadingArchiveResponse"))
                  .requestMessage(RfnLcrArchiveRequest.class)
                  .responseMessage(RfnLcrArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
        
    public static final JmsApi<RfnAlarmArchiveRequest,?,RfnAlarmArchiveResponse> RF_ALARM_ARCHIVE =
            JmsApi.builder(RfnAlarmArchiveRequest.class, RfnAlarmArchiveResponse.class)
                  .name("RF Alarm Archive")
                  .description("A notification from Network Manager to Yukon to provide RF alarm data.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.rfn.AlarmArchiveRequest"))
                  .responseQueue(new JmsQueue("yukon.qr.obj.amr.rfn.AlarmArchiveResponse"))
                  .requestMessage(RfnAlarmArchiveRequest.class)
                  .responseMessage(RfnAlarmArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RfnEventArchiveRequest,?,RfnEventArchiveResponse> RF_EVENT_ARCHIVE =
            JmsApi.builder(RfnEventArchiveRequest.class, RfnEventArchiveResponse.class)
                  .name("RF Event Archive")
                  .description("A notification from Network Manager to Yukon to provide RF event data.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.rfn.EventArchiveRequest"))
                  .responseQueue(new JmsQueue("yukon.qr.obj.amr.rfn.EventArchiveResponse"))
                  .requestMessage(RfnEventArchiveRequest.class)
                  .responseMessage(RfnEventArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<LocationResponse,?,LocationResponseAck> LOCATION = 
            JmsApi.builder(LocationResponse.class, LocationResponseAck.class)
                  .name("Location")
                  .description("A notification from Network Manager to Yukon that a device location has been updated.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.amr.rfn.LocationResponse"))
                  .responseQueue(new JmsQueue("yukon.qr.obj.amr.rfn.LocationResponseAck"))
                  .requestMessage(LocationResponse.class)
                  .responseMessage(LocationResponseAck.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<CollectionRequest,?,?> DATA_COLLECTION =
            JmsApi.builder(CollectionRequest.class)
                  .name("Data Collection Widget - Data Collection")
                  .description("Request that is initiated from the widget refresh button, sent from the Data Collection "
                          + "Widget Service to the Point Data Collection Service. If data collection is not underway, "
                          + "this initiates a fresh re-collection of the latest data. Afterward, the Point Data "
                          + "Collection Service sends a data collection recalculation request back to initate a "
                          + "recalculation based on the new data.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.data.collection.CollectionRequest"))
                  .requestMessage(CollectionRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RecalculationRequest,?,?> DATA_COLLECTION_RECALCULATION =
            JmsApi.builder(RecalculationRequest.class)
                  .name("Data Collection Widget - Recalculation")
                  .description("After a data collection is performed (periodically or due to a request from the UI), "
                          + "the Point Data Collection service sends a recalculation request from Yukon Service "
                          + "Manager to the webserver to initiate a recalculation for the Data Collection Widget. ")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.qr.obj.data.collection.RecalculationRequest"))
                  .requestMessage(RecalculationRequest.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    public static final JmsApi<SimulatorRequest,?,SimulatorResponse> SIMULATORS =
            JmsApi.builder(SimulatorRequest.class, SimulatorResponse.class)
                  .name("Simulators")
                  .description("This is the communication channel used by the Yukon web UI to send requests to, and"
                          + "receive responses from, the Yukon simulator service. These include configuration changes, "
                          + "starting and stopping simulators, and requesting current simulator statuses.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.simulators.SimulatorRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(SimulatorRequest.class)
                  .responseMessage(SimulatorResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<YukonTextMessage,?,?> ZIGBEE_SEP_TEXT =
            JmsApi.builder(YukonTextMessage.class)
                  .name("Zigbee Smart Energy Profile Text Message")
                  .description("Sent to initate a text message to a Zigbee device.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.stream.dr.smartEnergyProfileTextMessage.Send"))
                  .requestMessage(YukonTextMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<YukonCancelTextMessage,?,?> ZIGBEE_SEP_TEXT_CANCEL =
            JmsApi.builder(YukonCancelTextMessage.class)
                  .name("Zigbee Smart Energy Profile Text Message Cancellation")
                  .description("Sent to cancel a text message sent to a Zigbee device.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.stream.dr.smartEnergyProfileTextMessage.Cancel"))
                  .requestMessage(YukonCancelTextMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<SmartUpdateRequestMessage,?,?> ZIGBEE_SMART_UPDATE =
            JmsApi.builder(SmartUpdateRequestMessage.class)
                  .name("Zigbee Smart Update")
                  .description("Sent from Yukon webserver to Service Manager, which activates smart polling on a "
                          + "zigbee device.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.dr.smartUpdateRequest"))
                  .requestMessage(SmartUpdateRequestMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<LmReportedAddress,?,?> LM_ADDRESS_NOTIFICATION =
            JmsApi.builder(LmReportedAddress.class)
                  .name("LM Address Notification")
                  .description("Sends a load management device's address information, to be cached in the Yukon "
                          + "web server.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.dr.rfn.LmAddressNotification"))
                  .requestMessage(LmReportedAddress.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    public static final JmsApi<BrokerSystemMetricsRequest,?,?> BROKER_SYSTEM_METRICS =
            JmsApi.builder(BrokerSystemMetricsRequest.class)
                  .name("Broker System Metrics")
                  .description("Sends system metrics data from the Yukon Message Broker to the Yukon Service Manager "
                          + "service, which converts them to point data and passes them to Dispatch.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.service.obj.common.broker.ArchiveSystemPoint"))
                  .requestMessage(BrokerSystemMetricsRequest.class)
                  .sender(YUKON_MESSAGE_BROKER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RfnArchiveStartupNotification,?,?> ARCHIVE_STARTUP =
            JmsApi.builder(RfnArchiveStartupNotification.class)
                  .name("Archive Startup")
                  .description("Sent by Yukon to notify Network Manager that Yukon has just started up. Upon receipt, "
                          + "Network Manager will send all data that should be synced at Yukon startup. This can also "
                          + "be re-sent to force the re-sync with Network Manager (this should be done sparingly and "
                          + "is generally not necessary).")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.common.rfn.ArchiveStartupNotification"))
                  .requestMessage(RfnArchiveStartupNotification.class)
                  .sender(YUKON_WEBSERVER)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(NETWORK_MANAGER)
                  .build();
    
    public static final JmsApi<SmartNotificationEvent,?,?> SMART_NOTIFICATION_INFRASTRUCTURE_WARNINGS_EVENT =
            JmsApi.builder(SmartNotificationEvent.class)
                  .name("Smart Notification Infrastructure Warnings Event")
                  .description("Sent by the Infrastructure Warnings service, to the Smart Notification Infrastructure "
                          + "Warnings decider, when an infrastructure warning occurs. The decider then determines when "
                          + "to send a notification, who to send it to, and what form it should take.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifEvent.event.infrastructureWarnings"))
                  .requestMessage(SmartNotificationEvent.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<SmartNotificationEventMulti,?,?> SMART_NOTIFICATION_DEVICE_DATA_MONITOR_EVENT= 
            JmsApi.builder(SmartNotificationEventMulti.class)
                  .name("Smart Notifications Device Data Monitor Event")
                  .description("Sent by the Device Data Monitor service, to the Smart Notification Device Data Monitor "
                          + "decider, when a DDM event occurs. The decider then determines when "
                          + "to send a notification, who to send it to, and what form it should take.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifEvent.event.deviceDataMonitor"))
                  .requestMessage(SmartNotificationEventMulti.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<SmartNotificationMessageParametersMulti, Serializable, Serializable> SMART_NOTIFICATION_MESSAGE_PARAMETERS =
            JmsApi.builder(SmartNotificationMessageParametersMulti.class)
                  .name("Smart Notifications Message Parameters")
                  .description("Sent by the Smart Notification deciders when they have determined that a notification "
                          + "message should be sent. Processed by the Smart Notification assembler, which assembles a "
                          + "complete message to be sent out to recipients.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifEvent.assembler"))
                  .requestMessage(SmartNotificationMessageParametersMulti.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<SmartNotificationEventMulti,?,?> SMART_NOTIFICATION_YUKON_WATCHDOG_EVENT= 
            JmsApi.builder(SmartNotificationEventMulti.class)
                  .name("Smart Notifications Yukon Watchdog Event")
                  .description("Sent by the yukon watchdog service, to the Smart Notification Yukon Watchdog "
                          + "decider, when a event occurs. The decider then determines when "
                          + "to send a notification, who to send it to, and what form it should take.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifEvent.event.yukonWatchdog"))
                  .requestMessage(SmartNotificationEventMulti.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<SmartNotificationEventMulti,?,?> SMART_NOTIFICATION_METER_DR_EVENT= 
            JmsApi.builder(SmartNotificationEventMulti.class)
                  .name("Smart Notifications Meter Demand Response Event")
                  .description("Sent by the MeterDisconnectMessageListener when event event was recieved to notify user of the results.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifEvent.event.meterDr"))
                  .requestMessage(SmartNotificationEventMulti.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<SmartNotificationEventMulti,?,?> SMART_NOTIFICATION_DATA_IMPORT_EVENT= 
            JmsApi.builder(SmartNotificationEventMulti.class)
                  .name("Smart Notifications Data Import Event")
                  .description("Sent by the ScheduledDataImportTask and AccountImportService, to the Smart Notification Data Import "
                          + "decider, when data import is completed, import can be either scheduled or manual. The decider "
                          + "then determines when to send a notification, who to send it to, and what form it should take.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifEvent.event.dataImport"))
                  .requestMessage(SmartNotificationEventMulti.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();

    public static final JmsApi<DailyDigestTestParams,?,?> SMART_NOTIFICATION_DAILY_DIGEST_TEST = 
            JmsApi.builder(DailyDigestTestParams.class)
                  .name("Smart Notification Daily Digest Test")
                  .description("Sent from the test controller to the daily digest service to kick off a digest event "
                          + "at any time, for testing purposes.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifTest.dailyDigest"))
                  .requestMessage(DailyDigestTestParams.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<GatewaySetConfigRequest,?,GatewaySetConfigResponse> RF_GATEWAY_SET_CONFIG =
            JmsApi.builder(GatewaySetConfigRequest.class, GatewaySetConfigResponse.class)
                  .name("Gateway Set Config")
                  .description("Sends gateway config update request to NM.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("yukon.qr.obj.common.rfn.GatewaySetConfigRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(GatewaySetConfigRequest.class)
                  .responseMessage(GatewaySetConfigResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<RfnMetadataMultiRequest,?,RfnMetadataMultiResponse> RF_METADATA_MULTI =
            JmsApi.builder(RfnMetadataMultiRequest.class, RfnMetadataMultiResponse.class)
                  .name("Rfn Meta Data Multi")
                  .description("Sends metadata request to NM.")
                  .communicationPattern(REQUEST_MULTI_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.metadatamulti.request"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnMetadataMultiRequest.class)
                  .responseMessage(RfnMetadataMultiResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .build();
    
    public static final JmsApi<EcobeeAuthTokenRequest,?,EcobeeAuthTokenResponse> ECOBEE_AUTH_TOKEN =
            JmsApi.builder(EcobeeAuthTokenRequest.class, EcobeeAuthTokenResponse.class)
            .name("Ecobee Auth Token")
            .description("Sent from Service Manager and Webserver and received by Service manager to generate Ecobee Auth Token")
            .communicationPattern(REQUEST_RESPONSE)
            .queue(new JmsQueue("yukon.ecobee.auth.token.EcobeeAuthTokenRequest"))
            .responseQueue(JmsQueue.TEMP_QUEUE)
            .requestMessage(EcobeeAuthTokenRequest.class)
            .responseMessage(EcobeeAuthTokenResponse.class)
            .sender(YUKON_WEBSERVER)
            .sender(YUKON_SERVICE_MANAGER)
            .receiver(YUKON_SERVICE_MANAGER)
            .build();
    
    public static final JmsApi<AlarmArchiveRequest,?,AlarmArchiveResponse> NM_ALARM =
            JmsApi.builder(AlarmArchiveRequest.class, AlarmArchiveResponse.class)
            .name("NM Gateway Alarms")
            .description("Sent from NM and received by Yukon manager to handle processing of NM Alarms")
            .communicationPattern(REQUEST_RESPONSE)
            .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.alarm.notification"))
            .responseQueue(new JmsQueue("com.eaton.eas.yukon.networkmanager.alarm.confirm"))
            .requestMessage(AlarmArchiveRequest.class)
            .responseMessage(AlarmArchiveResponse.class)
            .sender(NETWORK_MANAGER)
            .receiver(YUKON_SERVICE_MANAGER)
            .build();
    
    public static final JmsApi<Serializable,?,?> RFN_DEVICE_CREATION_ALERT =
            JmsApi.builder(Serializable.class)
                  .name("RFN Device Creation Alert")
                  .description("Yukon Service Manager passes alerts from RFN Device Creation Service to Yukon "
                          + "Webserver AlertService when RFN device fails to be created")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("com.eaton.eas.yukon.alert"))
                  .requestMessage(Serializable.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    public static final JmsApi<RfnDeviceArchiveRequest,?,RfnDeviceArchiveResponse> RFN_DEVICE_ARCHIVE =
            JmsApi.builder(RfnDeviceArchiveRequest.class, RfnDeviceArchiveResponse.class)
                  .name("RFN Device Archive")
                  .description("A notification from Network Manager to Yukon for creation of a new device.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnDeviceArchiveRequest"))
                  .responseQueue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnDeviceArchiveResponse"))
                  .requestMessage(RfnDeviceArchiveRequest.class)
                  .responseMessage(RfnDeviceArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<RfnStatusArchiveRequest,?,RfnStatusArchiveResponse> RFN_STATUS_ARCHIVE =
            JmsApi.builder(RfnStatusArchiveRequest.class, RfnStatusArchiveResponse.class)
                  .name("RFN Status Archive")
                  .description("A notification from Network Manager to Yukon to archive status.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnStatusArchiveRequest"))
                  .responseQueue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnStatusArchiveResponse"))
                  .requestMessage(RfnStatusArchiveRequest.class)
                  .responseMessage(RfnStatusArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<MeterProgramStatusArchiveRequest,?,?> METER_PROGRAM_STATUS_ARCHIVE =
            JmsApi.builder(MeterProgramStatusArchiveRequest.class)
                  .name("Meter Program Status Archive")
                  .description("A notification to archive program status.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("com.eaton.eas.yukon.MeterProgramStatusArchiveRequest"))
                  .requestMessage(MeterProgramStatusArchiveRequest.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static JmsApi<RfnNodeCommArchiveRequest,?,RfnNodeCommArchiveResponse> RFN_NODE_COMM_ARCHIVE =
            JmsApi.builder(RfnNodeCommArchiveRequest.class, RfnNodeCommArchiveResponse.class)
                  .name("RFN Node Comm Archive")
                  .description("A notification from Network Manager to Yukon to archive gateway to node mapping.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnNodeCommArchiveRequest"))
                  .responseQueue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnNodeCommArchiveResponse"))
                  .requestMessage(RfnNodeCommArchiveRequest.class)
                  .responseMessage(RfnNodeCommArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_WEBSERVER_DEV_PAGES)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    /*
     * WARNING: JmsApiDirectoryTest will fail if you don't add each new JmsApi to the category map below!
     */
    
    public static Map<JmsApiCategory, List<JmsApi<?,?,?>>> getQueueDescriptions() {
        EnumMap<JmsApiCategory, List<JmsApi<?,?,?>>> jmsApis = new EnumMap<>(JmsApiCategory.class);
        
        //For readability, these are alphabetized by category, then api name
        addApis(jmsApis, DATA_STREAMING, 
                DATA_STREAMING_CONFIG, 
                GATEWAY_DATA_STREAMING_INFO);
        
        addApis(jmsApis, DIGI_ZIGBEE, 
                ZIGBEE_SEP_TEXT, 
                ZIGBEE_SEP_TEXT_CANCEL,
                ZIGBEE_SMART_UPDATE);
        
        addApis(jmsApis, MONITOR, 
                DEVICE_DATA_MONITOR_STATUS, 
                DEVICE_DATA_MONITOR_RECALC,
                RICH_POINT_DATA,
                STATUS_POINT_MONITOR_OUTAGE);
        
        addApis(jmsApis, OTHER, 
                ARCHIVE_STARTUP, 
                BROKER_SYSTEM_METRICS,
                LM_ADDRESS_NOTIFICATION,
                LOCATION,
                RFN_DEVICE_CREATION_ALERT,
                SIMULATORS,
                ECOBEE_AUTH_TOKEN);
        
        addApis(jmsApis, RFN_LCR, 
                RFN_EXPRESSCOM_BROADCAST, 
                RFN_EXPRESSCOM_UNICAST,
                RFN_EXPRESSCOM_UNICAST_BULK,
                RFN_EXPRESSCOM_UNICAST_WITH_DATA,
                RFN_LCR_ARCHIVE,
                RFN_LCR_READ_ARCHIVE);
        
        addApis(jmsApis, RFN_METER, 
                RFN_METER_DEMAND_RESET, 
                RFN_METER_DISCONNECT,
                RFN_METER_READ,
                RFN_METER_READ_ARCHIVE,
                METER_PROGRAM_STATUS_ARCHIVE);
        
        addApis(jmsApis, RF_GATEWAY, 
                RF_GATEWAY_ARCHIVE, 
                RF_GATEWAY_CERTIFICATE_UPDATE,
                RF_GATEWAY_COLLECTION,
                RF_GATEWAY_CONNECT,
                RF_GATEWAY_CONNECTION_TEST,
                RF_GATEWAY_CREATE, 
                RF_GATEWAY_DATA,
                RF_GATEWAY_DATA_INTERNAL,
                RF_GATEWAY_DATA_UNSOLICITED,
                RF_GATEWAY_DELETE,
                RF_GATEWAY_DELETE_FROM_NM,
                RF_GATEWAY_EDIT,
                RF_GATEWAY_FIRMWARE_UPGRADE,
                RF_GATEWAY_SCHEDULE_DELETE,
                RF_GATEWAY_SCHEDULE_REQUEST,
                RF_UPDATE_SERVER_AVAILABLE_VERSION,
                RF_GATEWAY_SET_CONFIG,
                NM_ALARM);
        
        addApis(jmsApis, RF_NETWORK, 
                NETWORK_NEIGHBOR, 
                NETWORK_PARENT,
                NETWORK_PRIMARY_ROUTE);
        
        addApis(jmsApis, RF_MISC, 
                RFN_METADATA, 
                RF_METADATA_MULTI,
                RF_ALARM_ARCHIVE,
                RF_EVENT_ARCHIVE,
                RFN_DEVICE_ARCHIVE,
                RFN_STATUS_ARCHIVE,
                RFN_NODE_COMM_ARCHIVE);
        
        addApis(jmsApis, SMART_NOTIFICATION,
                SMART_NOTIFICATION_INFRASTRUCTURE_WARNINGS_EVENT,
                SMART_NOTIFICATION_DEVICE_DATA_MONITOR_EVENT,
                SMART_NOTIFICATION_MESSAGE_PARAMETERS,
                SMART_NOTIFICATION_DAILY_DIGEST_TEST,
                SMART_NOTIFICATION_YUKON_WATCHDOG_EVENT,
                SMART_NOTIFICATION_DATA_IMPORT_EVENT,
                SMART_NOTIFICATION_METER_DR_EVENT);
        
        addApis(jmsApis, WIDGET_REFRESH,
                DATA_COLLECTION,
                DATA_COLLECTION_RECALCULATION,
                INFRASTRUCTURE_WARNINGS,
                INFRASTRUCTURE_WARNINGS_CACHE_REFRESH);
        
        return jmsApis;
    }
    
    private static void addApis(Map<JmsApiCategory, List<JmsApi<?,?,?>>> jmsApis, 
                               JmsApiCategory category, JmsApi<?,?,?>...apis) {
        
        var categoryApiList = jmsApis.computeIfAbsent(category, unused -> new ArrayList<>());
        categoryApiList.addAll(Arrays.asList(apis));
        categoryApiList.sort(API_COMPARATOR);
    }
    
    private JmsApiDirectory() {
        // Utility class to hold JmsApi constants. Not instantiable.
    }
}
