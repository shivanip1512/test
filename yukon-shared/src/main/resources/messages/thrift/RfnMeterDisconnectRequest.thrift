include "NetworkManagerMessaging.thrift"
include "RfnAddressing.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum RfnMeterDisconnectCmdType {
    ARM,
    RESUME,
    TERMINATE,
    QUERY
}

struct RfnMeterDisconnectRequest {
    1: required     RfnAddressing.RfnIdentifier rfnIdentifier;
    2: required     RfnMeterDisconnectCmdType action;
}

enum RfnMeterDisconnectInitialReplyType {
    OK,
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    TIMEOUT // Yukon specific timeout
}

struct RfnMeterDisconnectInitialReply {
    1: required     RfnMeterDisconnectInitialReplyType replyType;
}

enum RfnMeterDisconnectConfirmationReplyType {
    SUCCESS,
    FAILURE,
    FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD,
    FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT,
    FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT,
    FAILED_UNEXPECTED_STATUS,
    NOT_SUPPORTED,
    NETWORK_TIMEOUT,
    TIMEOUT // Yukon specific timeout
}

enum RfnMeterDisconnectState {
    UNKNOWN, 
    CONNECTED, 
    DISCONNECTED, 
    ARMED,
    DISCONNECTED_DEMAND_THRESHOLD_ACTIVE,
    CONNECTED_DEMAND_THRESHOLD_ACTIVE,
    DISCONNECTED_CYCLING_ACTIVE,
    CONNECTED_CYCLING_ACTIVE
}

struct RfnMeterDisconnectConfirmationReply {
    1: required     RfnMeterDisconnectConfirmationReplyType replyType;
    2: required     RfnMeterDisconnectState   state;
}