package com.cannontech.common.util.jms.api;

import static com.cannontech.common.util.jms.api.JmsApiCategory.DATA_STREAMING;
import static com.cannontech.common.util.jms.api.JmsApiCategory.DIGI_ZIGBEE;
import static com.cannontech.common.util.jms.api.JmsApiCategory.DR_NOTIFICATION;
import static com.cannontech.common.util.jms.api.JmsApiCategory.MONITOR;
import static com.cannontech.common.util.jms.api.JmsApiCategory.OTHER;
import static com.cannontech.common.util.jms.api.JmsApiCategory.RFN_LCR;
import static com.cannontech.common.util.jms.api.JmsApiCategory.RFN_METER;
import static com.cannontech.common.util.jms.api.JmsApiCategory.RF_GATEWAY;
import static com.cannontech.common.util.jms.api.JmsApiCategory.RF_MISC;
import static com.cannontech.common.util.jms.api.JmsApiCategory.RF_NETWORK;
import static com.cannontech.common.util.jms.api.JmsApiCategory.SIMULATOR;
import static com.cannontech.common.util.jms.api.JmsApiCategory.SIMULATOR_MANAGEMENT;
import static com.cannontech.common.util.jms.api.JmsApiCategory.SMART_NOTIFICATION;
import static com.cannontech.common.util.jms.api.JmsApiCategory.WIDGET_REFRESH;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.NETWORK_MANAGER;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_CLOUD_SERVICE;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_EIM;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_FIELD_SIMULATOR;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_LOAD_MANAGEMENT;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_MESSAGE_BROKER;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_PORTER;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_SERVICE_MANAGER;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_SIMULATORS;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_WATCHDOG;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_WEBSERVER;
import static com.cannontech.common.util.jms.api.JmsCommunicatingService.YUKON_WEBSERVER_DEV_PAGES;
import static com.cannontech.common.util.jms.api.JmsCommunicationPattern.NOTIFICATION;
import static com.cannontech.common.util.jms.api.JmsCommunicationPattern.REQUEST_ACK_RESPONSE;
import static com.cannontech.common.util.jms.api.JmsCommunicationPattern.REQUEST_MULTI_RESPONSE;
import static com.cannontech.common.util.jms.api.JmsCommunicationPattern.REQUEST_RESPONSE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveRequest;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveResponse;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveResponse;
import com.cannontech.amr.rfn.message.dataRequest.RfnDeviceDataRequest;
import com.cannontech.amr.rfn.message.dataRequest.RfnDeviceDataResponse;
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
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.nmHeartbeat.message.NetworkManagerHeartbeatRequest;
import com.cannontech.common.nmHeartbeat.message.NetworkManagerHeartbeatResponse;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.message.alarm.AlarmArchiveRequest;
import com.cannontech.common.rfn.message.alarm.AlarmArchiveResponse;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveRequest;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveResponse;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigRequest;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigResponse;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoRequest;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfoResponse;
import com.cannontech.common.rfn.message.device.RfnDeviceDeleteConfirmationReply;
import com.cannontech.common.rfn.message.device.RfnDeviceDeleteInitialReply;
import com.cannontech.common.rfn.message.device.RfnDeviceDeleteRequest;
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
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.node.RfnNodeWiFiCommArchiveRequest;
import com.cannontech.common.rfn.message.node.RfnNodeWiFiCommArchiveResponse;
import com.cannontech.common.rfn.message.node.RfnRelayCellularCommArchiveRequest;
import com.cannontech.common.rfn.message.node.RfnRelayCellularCommArchiveResponse;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeRequest;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeResponse;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.eatonCloud.message.EatonCloudHeartbeatRequest;
import com.cannontech.dr.eatonCloud.message.EatonCloudHeartbeatResponse;
import com.cannontech.dr.eatonCloud.message.v1.EatonCloudAuthTokenRequestV1;
import com.cannontech.dr.eatonCloud.message.v1.EatonCloudAuthTokenResponseV1;
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
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.porter.message.DynamicPaoInfoRequest;
import com.cannontech.message.porter.message.DynamicPaoInfoResponse;
import com.cannontech.message.porter.message.MeterProgramValidationRequest;
import com.cannontech.message.porter.message.MeterProgramValidationResponse;
import com.cannontech.services.configurationSettingMessage.model.ConfigurationSettings;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenResponse;
import com.cannontech.simulators.message.request.AssetAvailArchiveSimulatorRequest;
import com.cannontech.simulators.message.request.EatonCloudDataRetrievalSimulatonRequest;
import com.cannontech.simulators.message.request.EatonCloudRuntimeCalcSimulatonRequest;
import com.cannontech.simulators.message.request.EatonCloudSecretRotationSimulationRequest;
import com.cannontech.simulators.message.request.FieldSimulatorStatusRequest;
import com.cannontech.simulators.message.request.ItronRuntimeCalcSimulatonRequest;
import com.cannontech.simulators.message.request.ModifyFieldSimulatorRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.FieldSimulatorStatusResponse;
import com.cannontech.simulators.message.response.ModifyFieldSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.DrProgramStatusJmsMessage;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;
import com.cannontech.stars.dr.jms.message.OptOutOptInJmsMessage;
import com.cannontech.support.rfn.message.RfnSupportBundleRequest;
import com.cannontech.support.rfn.message.RfnSupportBundleResponse;
import com.cannontech.thirdparty.messaging.SmartUpdateRequestMessage;
import com.cannontech.yukon.system.metrics.message.YukonMetric;

/**
 * This is intended to be the single repository for all JmsApi information in the Yukon Java code.<br><br>
 * 
 * To add messaging for a new feature, create a new public static final JmsApi object here, and add it to the 
 * {@code jmsApis} map with a category.<br><br>
 * 
 * {@code JmsApi.builder} requires that all APIs have a name, description, communicationPattern, sender, receiver, queue, 
 * timeToLive and requestMessage specified. Additionally, you will need to specify ackQueue, responseQueue, ackMessage and
 * responseMessage if the communicationPattern involves ack or response. Multiple senders and receivers may also be
 * specified. (For example, if NM or a Yukon simulator can both receive a particular message.)<br><br>
 * 
 * Default time-to-live is set to 1 Day. You can also specify your own time-to live as per 
 * your requirements (For example 12 Hours: Duration.standardHours(12)).<br><br>
 * 
 * To define any messaging that is sent over a temp queue, use JmsQueue.TEMP_QUEUE.
 * To define any messaging that is sent over a topic, set topic as true.
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
                  .disableLogging()
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
                  .topic(true)
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<DrAttributeDataJmsMessage,?,?> DATA_NOTIFICATION = 
            JmsApi.builder(DrAttributeDataJmsMessage.class)
                  .name("DR Data Notification")
                  .description("Send Demand Response Notification related to runTime/ShedTime,max/min/avg voltage to other Integrated systems")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.dr.DRNotificationMessage"))
                  .requestMessage(DrAttributeDataJmsMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    public static final JmsApi<EnrollmentJmsMessage,?,?> ENROLLMENT_NOTIFICATION = 
            JmsApi.builder(EnrollmentJmsMessage.class)
                  .name("DR Enrollment/UnEnrollment Notification")
                  .description("Send Demand Response Notification related to Enrollment/UnEnrollment to other Integrated systems")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.dr.DRNotificationMessage"))
                  .requestMessage(EnrollmentJmsMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_WEBSERVER)
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
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
        
    public static final JmsApi<NetworkTreeUpdateTimeRequest,?,?> NETWORK_TREE_UPDATE_REQUEST =
            JmsApi.builder(NetworkTreeUpdateTimeRequest.class)
                  .name("Network Tree Update Request")
                  .description("Sent by Yukon to ask Network Manager when the network tree was last updated or to ask Network Manager for a tree update. Upon receipt, "
                          + "Network Manager will either initate the network tree update and will notify Yukon when done or "
                          + "send a message to Yukon with last network tree update time.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.NetworkTreeUpdateTimeRequest"))
                  .requestMessage(NetworkTreeUpdateTimeRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<NetworkTreeUpdateTimeResponse,?,?> NETWORK_TREE_UPDATE_RESPONSE =
            JmsApi.builder(NetworkTreeUpdateTimeResponse.class)
                  .name("Network Tree Update Response")
                  .description("Sent by Network Manager to notify the Web Server of the network tree update")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.NetworkTreeUpdateTimeResponse"))
                  .requestMessage(NetworkTreeUpdateTimeResponse.class)
                  .sender(NETWORK_MANAGER)
                  .sender(YUKON_SIMULATORS)
                  .receiver(YUKON_WEBSERVER)
                  .logger(YukonLogManager.getRfnLogger())
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
              .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .receiver(NETWORK_MANAGER)
                  .receiver(YUKON_SIMULATORS)
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<Serializable,?,?> RF_GATEWAY_DATA_INTERNAL =
            JmsApi.builder(Serializable.class)
                  .name("RF Gateway Data (Internal)")
                  .description("Yukon Service Manager takes gateway data (which receives it first, from Network "
                          + "Manager) and passes it to Yukon webserver on a topic.")
                  .topic(true)
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<RfnMeterDisconnectRequest,RfnMeterDisconnectInitialReply,RfnMeterDisconnectConfirmationReply> RFN_METER_DISCONNECT_LEGACY =
            JmsApi.builder(RfnMeterDisconnectRequest.class, RfnMeterDisconnectInitialReply.class, RfnMeterDisconnectConfirmationReply.class)
                  .name("RFN Meter Disconnect (Legacy)")
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
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<RfnMeterDisconnectRequest,RfnMeterDisconnectInitialReply,RfnMeterDisconnectConfirmationReply> RFN_METER_DISCONNECT =
            JmsApi.builder(RfnMeterDisconnectRequest.class, RfnMeterDisconnectInitialReply.class, RfnMeterDisconnectConfirmationReply.class)
                  .name("RFN Meter Disconnect")
                  .description("Sends a disconnect request to an RFN meter via E2E. The initial reply "
                          + "indicates either that the command will be sent, or that there was an error sending. The "
                          + "final response indicates the ultimate success or failure of the operation.")
                  .communicationPattern(REQUEST_ACK_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.RfnMeterDisconnectRequest"))
                  .ackQueue(JmsQueue.TEMP_QUEUE)
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnMeterDisconnectRequest.class)
                  .ackMessage(RfnMeterDisconnectInitialReply.class)
                  .responseMessage(RfnMeterDisconnectConfirmationReply.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_PORTER)
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<RfnMeterReadRequest,RfnMeterReadReply,RfnMeterReadDataReply> RFN_METER_READ_LEGACY =
            JmsApi.builder(RfnMeterReadRequest.class, RfnMeterReadReply.class, RfnMeterReadDataReply.class)
                  .name("Rfn Meter Read (Legacy)")
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
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<RfnMeterReadRequest,RfnMeterReadReply,RfnMeterReadDataReply> RFN_METER_READ =
            JmsApi.builder(RfnMeterReadRequest.class, RfnMeterReadReply.class, RfnMeterReadDataReply.class)
                  .name("Rfn Meter Read")
                  .description("Attempts to send a read request for an RFN meter via E2E. The first response is a status "
                          + "message indicating this is a known meter and a read will be tried, or a read is not "
                          + "possible for this meter. This response should come back within seconds. The second "
                          + "response is the actual read data. This response is only expected if the first response "
                          + "was OK. This response can take anywhere from seconds to minutes depending on network "
                          + "performance.")
                  .communicationPattern(REQUEST_ACK_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.RfnMeterReadRequest"))
                  .ackQueue(JmsQueue.TEMP_QUEUE)
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnMeterReadRequest.class)
                  .ackMessage(RfnMeterReadReply.class)
                  .responseMessage(RfnMeterReadDataReply.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_PORTER)
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<SmartNotificationEventMulti,?,?> SMART_NOTIFICATION_EVENT= 
            JmsApi.builder(SmartNotificationEventMulti.class)
                  .name("Smart Notifications Event")
                  .description("Sent to Service Manager for processing")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.smartNotifEvent.event"))
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    public static final JmsApi<RfnDeviceDataRequest,?,RfnDeviceDataResponse> DYNAMIC_RFN_DEVICE_DATA_COLLECTION =
            JmsApi.builder(RfnDeviceDataRequest.class, RfnDeviceDataResponse.class)
                  .name("Dynamic rfn device data collection")
                  .description("Sends message to SM to initiate dynamic rfn device data collection")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.DynamicRfnDeviceDataRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(RfnDeviceDataRequest.class)
                  .responseMessage(RfnDeviceDataResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();

    public static final JmsApi<ZeusEcobeeAuthTokenRequest, ?, ZeusEcobeeAuthTokenResponse> ZEUS_ECOBEE_AUTH_TOKEN = 
            JmsApi.builder(ZeusEcobeeAuthTokenRequest.class, ZeusEcobeeAuthTokenResponse.class)
            .name("Zeus Ecobee Auth Token")
            .description("Sent from Service Manager and Webserver and received by Service manager to generate Zeus Ecobee Auth Token")
            .communicationPattern(REQUEST_RESPONSE)
            .queue(new JmsQueue("yukon.ecobee.auth.token.ZeusEcobeeAuthTokenRequest"))
            .responseQueue(JmsQueue.TEMP_QUEUE)
            .requestMessage(ZeusEcobeeAuthTokenRequest.class)
            .responseMessage(ZeusEcobeeAuthTokenResponse.class)
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
            .logger(YukonLogManager.getRfnLogger())
            .build();
    
    public static final JmsApi<Serializable,?,?> NEW_ALERT_CREATION =
            JmsApi.builder(Serializable.class)
                  .name("New Alert Creation")
                  .description("Yukon Service Manager passes alerts to Webserver to be created")
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
                  .logger(YukonLogManager.getRfnLogger())
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
                  .logger(YukonLogManager.getRfnLogger())
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

    public static final JmsApi<OptOutOptInJmsMessage,?,?> OPTOUTIN_NOTIFICATION = 
            JmsApi.builder(OptOutOptInJmsMessage.class)
                  .name("DR OptOut/OptIn Notification")
                  .description("Send Demand Response Notification related to OptOut/OptIn to other Integrated systems")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.dr.DRNotificationMessage"))
                  .requestMessage(OptOutOptInJmsMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_WEBSERVER)
                  .build();
    
    public static JmsApi<RfnNodeWiFiCommArchiveRequest,?,RfnNodeWiFiCommArchiveResponse> RFN_NODE_WIFI_COMM_ARCHIVE =
            JmsApi.builder(RfnNodeWiFiCommArchiveRequest.class, RfnNodeWiFiCommArchiveResponse.class)
                  .name("RFN Node WiFi Comm Archive")
                  .description("A notification from Network Manager to Yukon to archive the Super Meter's Wi-Fi connection status.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnNodeWiFiCommArchiveRequest"))
                  .responseQueue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnNodeWiFiCommArchiveResponse"))
                  .requestMessage(RfnNodeWiFiCommArchiveRequest.class)
                  .responseMessage(RfnNodeWiFiCommArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .logger(YukonLogManager.getRfnLogger())
                  .build();

    public static final JmsApi<DrProgramStatusJmsMessage,?,?> PROGRAM_STATUS_NOTIFICATION = 
            JmsApi.builder(DrProgramStatusJmsMessage.class)
                  .name("DR Program Status Notification")
                  .description("Send Program Status Notification to other Integrated systems")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.dr.DRNotificationMessage"))
                  .requestMessage(DrProgramStatusJmsMessage.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_WEBSERVER)
                  .build();

    public static final JmsApi<ConfigurationSettings,?,?> CLOUD_CONFIGURATION_SETTINGS =
            JmsApi.builder(ConfigurationSettings.class)
                  .name("Cloud Configuration Settings")
                  .description("Yukon Service Manager takes Cloud Configuration Settings and passes it to Yukon Message Broker on a queue.")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("com.eaton.eas.cloud.ConfigurationSettingsResponse"))
                  .requestMessage(ConfigurationSettings.class)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();

    public static final JmsApi<DynamicPaoInfoRequest,?,DynamicPaoInfoResponse> PORTER_DYNAMIC_PAOINFO =
            JmsApi.builder(DynamicPaoInfoRequest.class, DynamicPaoInfoResponse.class)
                  .name("Porter Dynamic Pao Info")
                  .description("Requests to Porter for Dynamic Pao Info, such as MCT load profile configuration")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.porter.dynamicPaoInfoRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(DynamicPaoInfoRequest.class)
                  .responseMessage(DynamicPaoInfoResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_PORTER)
                  .build();

    public static final JmsApi<MeterProgramValidationRequest,?,MeterProgramValidationResponse> METER_PROGRAM_VALIDATION =
            JmsApi.builder(MeterProgramValidationRequest.class, MeterProgramValidationResponse.class)
                  .name("Meter Program Validation")
                  .description("Requests to Porter to validate Meter Program entries")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.porter.meterProgramValidationRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(MeterProgramValidationRequest.class)
                  .responseMessage(MeterProgramValidationResponse.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_PORTER)
                  .build();

    public static final JmsApi<FieldSimulatorStatusRequest,?,FieldSimulatorStatusResponse> FIELD_SIMULATOR_STATUS =
            JmsApi.builder(FieldSimulatorStatusRequest.class, FieldSimulatorStatusResponse.class)
                  .name("Field Simulator Status")
                  .description("Requests current status from Field Simulator for UI display")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.fieldSimulator.statusRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(FieldSimulatorStatusRequest.class)
                  .responseMessage(FieldSimulatorStatusResponse.class)
                  .sender(YUKON_SIMULATORS)
                  .receiver(YUKON_FIELD_SIMULATOR)
                  .build();

    public static final JmsApi<ModifyFieldSimulatorRequest,?,ModifyFieldSimulatorResponse> FIELD_SIMULATOR_CONFIGURATION =
            JmsApi.builder(ModifyFieldSimulatorRequest.class, ModifyFieldSimulatorResponse.class)
                  .name("Field Simulator Configuration")
                  .description("Changes settings in Field Simulator")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.fieldSimulator.modifyConfiguration"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(ModifyFieldSimulatorRequest.class)
                  .responseMessage(ModifyFieldSimulatorResponse.class)
                  .sender(YUKON_SIMULATORS)
                  .receiver(YUKON_FIELD_SIMULATOR)
                  .build();
    
    public static final JmsApi<EatonCloudAuthTokenRequestV1, ?, EatonCloudAuthTokenResponseV1> EATON_CLOUD_AUTH_TOKEN = 
            JmsApi.builder(EatonCloudAuthTokenRequestV1.class, EatonCloudAuthTokenResponseV1.class)
                  .name("Eaton Cloud Auth Token")
                  .description("Generates an Eaton Cloud Auth Token")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.cannontech.dr.eatonCloud.message.v1.EatonCloudAuthTokenRequestV1"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(EatonCloudAuthTokenRequestV1.class)
                  .responseMessage(EatonCloudAuthTokenResponseV1.class)
                  .sender(YUKON_WEBSERVER)
                  .sender(YUKON_SERVICE_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<LMEatonCloudScheduledCycleCommand, ?, ?> LM_EATON_CLOUD_SCHEDULED_CYCLE_COMMAND = 
            JmsApi.builder(LMEatonCloudScheduledCycleCommand.class)
                  .name("Eaton Cloud Scheduled Cycle Command")
                  .description("Send an Eaton Cloud cycle command to Yukon")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.stream.dr.EatonCloudScheduledCyclingRequest"))
                  .requestMessage(LMEatonCloudScheduledCycleCommand.class)
                  .sender(YUKON_LOAD_MANAGEMENT)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<LMEatonCloudStopCommand, ?, ?> LM_EATON_CLOUD_STOP_COMMAND =
            JmsApi.builder(LMEatonCloudStopCommand.class)
                  .name("Eaton Cloud Stop Command)")
                  .description("Send an Eaton Cloud Stop Command")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.stream.dr.EatonCloudStopRequest"))
                  .requestMessage(LMEatonCloudStopCommand.class)
                  .sender(YUKON_LOAD_MANAGEMENT)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();

    public static final JmsApi<RfnSupportBundleRequest,RfnSupportBundleResponse ,RfnSupportBundleResponse> RF_SUPPORT_BUNDLE =
            JmsApi.builder(RfnSupportBundleRequest.class, RfnSupportBundleResponse.class, RfnSupportBundleResponse.class)
            .name("RF Support Bundle")
            .description("Sends a support bundle request from Yukon to Network Manager, specifying file "
            + "name and parameters. Response is sent by Network Manager when the support bundle "
            + "is generated, processed on a different queue.")
            .communicationPattern(REQUEST_RESPONSE)
            .queue(new JmsQueue("yukon.qr.obj.support.rfn.RfnSupportBundleRequest"))
            .responseQueue(JmsQueue.TEMP_QUEUE)
            .ackQueue(JmsQueue.TEMP_QUEUE)
            .requestMessage(RfnSupportBundleRequest.class)
            .ackMessage(RfnSupportBundleResponse.class)
            .responseMessage(RfnSupportBundleResponse.class)
            .sender(YUKON_WEBSERVER)
            .receiver(NETWORK_MANAGER)
            .logger(YukonLogManager.getRfnLogger())
            .build();

    public static final JmsApi<NetworkManagerHeartbeatRequest,?,NetworkManagerHeartbeatResponse> NM_HEARTBEAT =
            JmsApi.builder(NetworkManagerHeartbeatRequest.class, NetworkManagerHeartbeatResponse.class)
                  .name("Network Manager heartbeat")
                  .description("Sends a heartbeat message and collects response from Network Manager to confirm "
                          + "its communication with sender.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.heartbeat"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(NetworkManagerHeartbeatRequest.class)
                  .responseMessage(NetworkManagerHeartbeatResponse.class)
                  .sender(YUKON_WATCHDOG)
                  .receiver(NETWORK_MANAGER)
                  .timeToLive(Duration.standardMinutes(5))
                  .build();
     
    public static final JmsApi<EatonCloudDataRetrievalSimulatonRequest,?,?> EATON_CLOUD_SIM_DEVICE_DATA_RETRIEVAL_REQUEST = 
            JmsApi.builder(EatonCloudDataRetrievalSimulatonRequest.class)
                  .name("Eaton Cloud Device Auto Creation Simulation Request")
                  .description("WS sends request to SM start auto creation for simulated devices")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.simulator.EatonCloudDataRetrievalSimulatonRequest"))
                  .requestMessage(EatonCloudDataRetrievalSimulatonRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<EatonCloudSecretRotationSimulationRequest,?,?> EATON_CLOUD_SIM_SECRET_ROTATION_REQUEST = 
            JmsApi.builder(EatonCloudSecretRotationSimulationRequest.class)
                  .name("Eaton Cloud Secret Rotation Simulation Request")
                  .description("WS sends request to SM rotate secret1 and secret2")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.simulator.EatonCloudSecretRotationSimulationRequest"))
                  .requestMessage(EatonCloudSecretRotationSimulationRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<EatonCloudRuntimeCalcSimulatonRequest,?,?> EATON_CLOUD_SIM_RUNTIME_CALC_START_REQUEST = 
            JmsApi.builder(EatonCloudRuntimeCalcSimulatonRequest.class)
                  .name("Eaton Cloud Runtime Calculation Simulation Request")
                  .description("WS sends request to SM start runtime calculation")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.simulator.EatonCloudRuntimeCalcSimulatonRequest"))
                  .requestMessage(EatonCloudRuntimeCalcSimulatonRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();
    
    public static final JmsApi<DatabaseChangeEvent,?,?> DATABASE_CHANGE_EVENT_REQUEST = 
            JmsApi.builder(DatabaseChangeEvent.class)
                  .name("Database change event request")
                  .description("Webserver sends DB change events to Message broker")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("com.eaton.eas.yukon.dbchange.event"))
                  .requestMessage(DatabaseChangeEvent.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_MESSAGE_BROKER)
                  .build();

    public static JmsApi<RfnRelayCellularCommArchiveRequest,?,RfnRelayCellularCommArchiveResponse> RFN_RELAY_CELL_COMM_ARCHIVE =
            JmsApi.builder(RfnRelayCellularCommArchiveRequest.class, RfnRelayCellularCommArchiveResponse.class)
                  .name("RFN Relay Cellular Comm Archive")
                  .description("A notification from Network Manager to Yukon to archive the Cellular IPLink Relay's cellular connection status.")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnRelayCellularCommArchiveRequest"))
                  .responseQueue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnRelayCellularCommArchiveResponse"))
                  .requestMessage(RfnRelayCellularCommArchiveRequest.class)
                  .responseMessage(RfnRelayCellularCommArchiveResponse.class)
                  .sender(NETWORK_MANAGER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .logger(YukonLogManager.getRfnLogger())
                  .build();
    
    
    public static final JmsApi<ItronRuntimeCalcSimulatonRequest,?,?> ITRON_SIM_RUNTIME_CALC_START_REQUEST = 
            JmsApi.builder(ItronRuntimeCalcSimulatonRequest.class)
                  .name("Itron Calculation Simulation Request")
                  .description("WS sends request to SM start runtime calculation")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.simulator.ItronRuntimeCalcSimulatonRequest"))
                  .requestMessage(ItronRuntimeCalcSimulatonRequest.class)
                  .sender(YUKON_WEBSERVER)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();

    public static final JmsApi<RfnDeviceDeleteRequest, RfnDeviceDeleteInitialReply, RfnDeviceDeleteConfirmationReply> RFN_DEVICE_DELETE = JmsApi
            .builder(RfnDeviceDeleteRequest.class, RfnDeviceDeleteInitialReply.class, RfnDeviceDeleteConfirmationReply.class)
            .name("Device Delete Request")
            .description("Request is sent from Yukon to NM to delete a RFN device."
                    + "NM send a an acknowledgement confirmimg the presence/absence of the device in the NM Db."
                    + "NM will then send a second response confirmimg the deletion failure or success.")
            .communicationPattern(REQUEST_ACK_RESPONSE)
            .queue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnDeviceDeleteRequest"))
            .ackQueue(JmsQueue.TEMP_QUEUE)
            .responseQueue(new JmsQueue("com.eaton.eas.yukon.networkmanager.RfnDeviceDeleteConfirmationReply"))
            .requestMessage(RfnDeviceDeleteRequest.class)
            .ackMessage(RfnDeviceDeleteInitialReply.class)
            .responseMessage(RfnDeviceDeleteConfirmationReply.class)
            .sender(YUKON_WEBSERVER)
            .receiver(NETWORK_MANAGER)
            .receiver(YUKON_SERVICE_MANAGER)
            .sender(YUKON_SIMULATORS)
            .build();

    public static final JmsApi<YukonMetric, ?, ?> YUKON_METRIC = JmsApi.builder(YukonMetric.class)
            .name("Yukon Metric")
            .description("Multiple services publish system metrics data to this topic. Different consumers "
                    + "will process data as per their requirment.")
            .topic(true)
            .communicationPattern(NOTIFICATION)
            .queue(new JmsQueue("com.eaton.eas.yukon.metric"))
            .requestMessage(YukonMetric.class)
            .sender(YUKON_WEBSERVER)
            .sender(YUKON_SERVICE_MANAGER)
            .receiver(YUKON_SERVICE_MANAGER)
            .receiver(YUKON_CLOUD_SERVICE)
            .build();
    
    public static final JmsApi<EatonCloudHeartbeatRequest, ?, EatonCloudHeartbeatResponse> EATON_CLOUD_HEARTBEAT = 
            JmsApi.builder(EatonCloudHeartbeatRequest.class, EatonCloudHeartbeatResponse.class)
                  .name("Eaton Cloud Heartbeat")
                  .description("Verifies connection from Service Manager to Eaton Cloud")
                  .communicationPattern(REQUEST_RESPONSE)
                  .queue(new JmsQueue("com.cannontech.dr.eatonCloud.message.EatonCloudHeartbeatRequest"))
                  .responseQueue(JmsQueue.TEMP_QUEUE)
                  .requestMessage(EatonCloudHeartbeatRequest.class)
                  .responseMessage(EatonCloudHeartbeatResponse.class)
                  .sender(YUKON_WATCHDOG)
                  .receiver(YUKON_SERVICE_MANAGER)
                  .build();

    public static final JmsApi<AssetAvailArchiveSimulatorRequest,?,?> ASSET_AVAILABILITY_ARCHIVE_SIM_REQUEST = 
            JmsApi.builder(AssetAvailArchiveSimulatorRequest.class)
                  .name("Asset Availability Archive Simulator Request Simulation Request")
                  .description("WS sends request to SM to start archiving the point data")
                  .communicationPattern(NOTIFICATION)
                  .queue(new JmsQueue("yukon.notif.obj.simulator.AssetAvailArchiveSimulatorRequest"))
                  .requestMessage(AssetAvailArchiveSimulatorRequest.class)
                  .sender(YUKON_WEBSERVER)
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
                CLOUD_CONFIGURATION_SETTINGS,
                EATON_CLOUD_AUTH_TOKEN,
                LM_ADDRESS_NOTIFICATION,
                LM_EATON_CLOUD_SCHEDULED_CYCLE_COMMAND,
                LM_EATON_CLOUD_STOP_COMMAND,
                LOCATION,
                NM_HEARTBEAT,
                PORTER_DYNAMIC_PAOINFO,
                RF_SUPPORT_BUNDLE,
                NEW_ALERT_CREATION,
                ZEUS_ECOBEE_AUTH_TOKEN,
                DATABASE_CHANGE_EVENT_REQUEST,
                YUKON_METRIC,
                EATON_CLOUD_HEARTBEAT);
        
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
                RFN_METER_DISCONNECT_LEGACY,
                RFN_METER_READ,
                RFN_METER_READ_LEGACY,
                RFN_METER_READ_ARCHIVE,
                METER_PROGRAM_STATUS_ARCHIVE,
                METER_PROGRAM_VALIDATION);
        
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
                NETWORK_TREE_UPDATE_REQUEST,
                NETWORK_TREE_UPDATE_RESPONSE);
        
        addApis(jmsApis, RF_MISC, 
                RF_METADATA_MULTI,
                RF_ALARM_ARCHIVE,
                RF_EVENT_ARCHIVE,
                RFN_DEVICE_ARCHIVE,
                RFN_DEVICE_DELETE,
                RFN_STATUS_ARCHIVE,
                RFN_NODE_WIFI_COMM_ARCHIVE,
                RFN_RELAY_CELL_COMM_ARCHIVE,
                DYNAMIC_RFN_DEVICE_DATA_COLLECTION);
        
        addApis(jmsApis, SIMULATOR_MANAGEMENT,
                FIELD_SIMULATOR_CONFIGURATION,
                FIELD_SIMULATOR_STATUS,
                SIMULATORS);

        addApis(jmsApis, SMART_NOTIFICATION,
                SMART_NOTIFICATION_EVENT,
                SMART_NOTIFICATION_MESSAGE_PARAMETERS,
                SMART_NOTIFICATION_DAILY_DIGEST_TEST);
        
        addApis(jmsApis, WIDGET_REFRESH,
                DATA_COLLECTION,
                DATA_COLLECTION_RECALCULATION,
                INFRASTRUCTURE_WARNINGS,
                INFRASTRUCTURE_WARNINGS_CACHE_REFRESH);
        
        addApis(jmsApis, DR_NOTIFICATION,
                         DATA_NOTIFICATION,
                         ENROLLMENT_NOTIFICATION, 
                         OPTOUTIN_NOTIFICATION,
                         PROGRAM_STATUS_NOTIFICATION);
        
        addApis(jmsApis, 
                SIMULATOR, 
                EATON_CLOUD_SIM_DEVICE_DATA_RETRIEVAL_REQUEST, 
                EATON_CLOUD_SIM_RUNTIME_CALC_START_REQUEST,
                ITRON_SIM_RUNTIME_CALC_START_REQUEST,
                EATON_CLOUD_SIM_SECRET_ROTATION_REQUEST,
                ASSET_AVAILABILITY_ARCHIVE_SIM_REQUEST);

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