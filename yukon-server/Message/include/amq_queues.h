#pragma once

#include "dlldefs.h"

#include <string>

namespace Cti::Messaging::ActiveMQ::Queues {

class IM_EX_MSG OutboundQueue
{
public:
    std::string name;

    static const OutboundQueue PorterResponses;
    static const OutboundQueue SmartEnergyProfileControl;
    static const OutboundQueue SmartEnergyProfileRestore;
    static const OutboundQueue EcobeeCyclingControl;
    static const OutboundQueue EcobeeSetpointControl;
    static const OutboundQueue EcobeeRestore;
    static const OutboundQueue HoneywellCyclingControl;
    static const OutboundQueue HoneywellRestore;
    static const OutboundQueue NestCyclingControl;
    static const OutboundQueue NestRestore;
    static const OutboundQueue ItronCyclingControl;
    static const OutboundQueue ItronRestore;
    static const OutboundQueue MeterDisconnectControl;
    static const OutboundQueue MeterDisconnectRestore;
    static const OutboundQueue HistoryRowAssociationResponse;
    static const OutboundQueue IvvcAnalysisMessage;
    static const OutboundQueue CapControlOperationMessage;
    static const OutboundQueue RfnBroadcast;
    static const OutboundQueue NetworkManagerRequest;
    static const OutboundQueue NetworkManagerE2eDataRequest;
    static const OutboundQueue ScannerOutMessages;
    static const OutboundQueue ScannerInMessages;
    static const OutboundQueue GetBatteryNodeChannelConfigRequest;
    static const OutboundQueue SetBatteryNodeChannelConfigRequest;
    static const OutboundQueue DeviceCreationRequest;
    static const OutboundQueue RfnDataStreamingUpdate;
    static const OutboundQueue MeterProgramStatusArchiveRequest;
};


class IM_EX_MSG InboundQueue
{
public:
    std::string name;

    static const InboundQueue NetworkManagerResponse;
    static const InboundQueue NetworkManagerE2eDataConfirm;
    static const InboundQueue NetworkManagerE2eDataIndication;
    static const InboundQueue ScannerOutMessages;
    static const InboundQueue ScannerInMessages;
    static const InboundQueue PorterDynamicPaoInfoRequest;
};

}