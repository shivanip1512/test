#include "precompiled.h"

#include "amq_queues.h"

namespace Cti::Messaging::ActiveMQ::Queues {

const IM_EX_MSG OutboundQueue
    OutboundQueue::PorterResponses
    {"yukon.notif.stream.amr.PorterResponseMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::SmartEnergyProfileControl
    {"yukon.notif.stream.dr.SmartEnergyProfileControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::SmartEnergyProfileRestore
    {"yukon.notif.stream.dr.SmartEnergyProfileRestoreMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::EcobeeCyclingControl
    {"yukon.notif.stream.dr.EcobeeCyclingControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::EcobeeSetpointControl
    {"yukon.notif.stream.dr.EcobeeSetpointControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::EcobeeRestore
    {"yukon.notif.stream.dr.EcobeeRestoreMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::EcobeePlusControl
    {"yukon.notif.stream.dr.EcobeePlusControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::HoneywellCyclingControl
    {"yukon.notif.stream.dr.HoneywellCyclingControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::HoneywellSetpointControl
    {"yukon.notif.stream.dr.HoneywellSetpointControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::HoneywellRestore
    {"yukon.notif.stream.dr.HoneywellRestoreMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::NestCyclingControl
    {"yukon.notif.stream.dr.NestCyclingControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::NestRestore
    {"yukon.notif.stream.dr.NestRestoreMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::ItronCyclingControl
    {"yukon.notif.stream.dr.ItronCyclingControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::ItronRestore
    {"yukon.notif.stream.dr.ItronRestoreMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::MeterDisconnectControl
    {"yukon.notif.stream.dr.MeterDisconnectControlMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::MeterDisconnectRestore
    {"yukon.notif.stream.dr.MeterDisconnectRestoreMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::EatonCloudScheduledCyclingRequest
    {"yukon.notif.stream.dr.EatonCloudScheduledCyclingRequest"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::EatonCloudStopRequest
    {"yukon.notif.stream.dr.EatonCloudStopRequest"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::HistoryRowAssociationResponse
    {"yukon.notif.stream.dr.HistoryRowAssociationResponse"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::IvvcAnalysisMessage
    {"yukon.notif.stream.cc.IvvcAnalysisMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::CapControlOperationMessage
    {"yukon.notif.stream.cc.CapControlOperationMessage"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::RfnBroadcast
    {"yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::NetworkManagerRequest
    {"com.eaton.eas.yukon.networkmanager.request"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::NetworkManagerE2eDataRequest
    {"com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataRequest"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::ScannerOutMessages
    {"com.eaton.eas.yukon.scanner.outmessages"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::ScannerInMessages
    {"com.eaton.eas.yukon.scanner.inmessages"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::GetBatteryNodeChannelConfigRequest
    {"com.eaton.eas.yukon.networkmanager.batterynode.GetChannelConfiguration"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::SetBatteryNodeChannelConfigRequest
    {"com.eaton.eas.yukon.networkmanager.batterynode.SetChannelConfiguration"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::DeviceCreationRequest
    {"com.eaton.eas.yukon.deviceCreation"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::RfnDataStreamingUpdate
    {"com.eaton.eas.yukon.rfnDataStreamingUpdate"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::MeterProgramStatusArchiveRequest
    {"com.eaton.eas.yukon.MeterProgramStatusArchiveRequest"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::RfnEdgeDrUnicastResponse
    {"com.eaton.eas.yukon.porter.edgeDr.unicast.response"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::RfnEdgeDrBroadcastResponse
    {"com.eaton.eas.yukon.porter.edgeDr.broadcast.response"};
const IM_EX_MSG OutboundQueue
    OutboundQueue::NetworkManagerRfnBroadcastRequest
    {"com.eaton.eas.yukon.networkmanager.rfn.broadcast.request"};

const IM_EX_MSG InboundQueue
    InboundQueue::MeterProgramValidationRequest
    { "com.eaton.eas.yukon.porter.meterProgramValidationRequest" };
const IM_EX_MSG InboundQueue
    InboundQueue::NetworkManagerResponse
    {"com.eaton.eas.yukon.networkmanager.response"};
const IM_EX_MSG InboundQueue
    InboundQueue::NetworkManagerE2eDataConfirm
    {"com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataConfirm"};
const IM_EX_MSG InboundQueue
    InboundQueue::NetworkManagerE2eDataIndication
    {"com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataIndication"};
const IM_EX_MSG InboundQueue
    InboundQueue::ScannerOutMessages
    {"com.eaton.eas.yukon.scanner.outmessages"};
const IM_EX_MSG InboundQueue
    InboundQueue::ScannerInMessages
    {"com.eaton.eas.yukon.scanner.inmessages"};
const IM_EX_MSG InboundQueue
    InboundQueue::PorterDynamicPaoInfoRequest
    {"com.eaton.eas.yukon.porter.dynamicPaoInfoRequest"};

const IM_EX_MSG InboundQueue
    InboundQueue::FieldSimulatorStatusRequest
    { "com.eaton.eas.yukon.fieldSimulator.statusRequest" };
const IM_EX_MSG InboundQueue
    InboundQueue::FieldSimulatorModifyConfiguration
    { "com.eaton.eas.yukon.fieldSimulator.modifyConfiguration" };

const IM_EX_MSG InboundQueue
    InboundQueue::RfnMeterDisconnectRequest
    { "com.eaton.eas.yukon.RfnMeterDisconnectRequest" };
const IM_EX_MSG InboundQueue
    InboundQueue::RfnMeterReadRequest
    { "com.eaton.eas.yukon.RfnMeterReadRequest" };

const IM_EX_MSG InboundQueue
    InboundQueue::RfnEdgeDrUnicastRequest
    { "com.eaton.eas.yukon.porter.edgeDr.unicast.request" };
const IM_EX_MSG InboundQueue
    InboundQueue::RfnEdgeDrBroadcastRequest
    { "com.eaton.eas.yukon.porter.edgeDr.broadcast.request" };
const IM_EX_MSG InboundQueue
    InboundQueue::NetworkManagerRfnBroadcastResponse
    { "com.eaton.eas.yukon.networkmanager.rfn.broadcast.response" };
}
