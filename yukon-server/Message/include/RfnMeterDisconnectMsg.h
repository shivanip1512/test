#pragma once

#include "rfn_identifier.h"

namespace Cti::Messaging::Rfn {

enum class RfnMeterDisconnectCmdType 
{
    ARM,
    RESUME,
    TERMINATE,
    QUERY,
};

struct RfnMeterDisconnectRequestMsg 
{
    RfnIdentifier rfnIdentifier;
    RfnMeterDisconnectCmdType action;
};

enum class RfnMeterDisconnectInitialReplyType
{
    OK,
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    TIMEOUT, // Yukon specific timeout
};

struct RfnMeterDisconnectInitialReplyMsg
{
    RfnMeterDisconnectInitialReplyType replyType;
};

enum class RfnMeterDisconnectConfirmationReplyType 
{
    SUCCESS,
    FAILURE,
    FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD,
    FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT,
    FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT,
    FAILED_UNEXPECTED_STATUS,
    NOT_SUPPORTED,
    NETWORK_TIMEOUT,
    TIMEOUT, // Yukon specific timeout
};

enum class RfnMeterDisconnectState
{
    UNKNOWN,
    CONNECTED,
    DISCONNECTED,
    ARMED,
    DISCONNECTED_DEMAND_THRESHOLD_ACTIVE,
    CONNECTED_DEMAND_THRESHOLD_ACTIVE,
    DISCONNECTED_CYCLING_ACTIVE,
    CONNECTED_CYCLING_ACTIVE,
};

struct RfnMeterDisconnectConfirmationReplyMsg
{
    RfnMeterDisconnectConfirmationReplyType replyType;
    RfnMeterDisconnectState state;
};

}