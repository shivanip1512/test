include "RfnE2eData.thrift"
include "NetworkManagerMessaging.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated


enum RfnBroadcastDeliveryType {
    IMMEDIATE = 0x00,
    NON_REAL_TIME = 0x01,
}


struct RfnBroadcastRequest {
    1: required     byte                        sourceId = 1;
    2: required     i16                         messageId;
    3: required     byte                        broadcastApplicationId;
    4: required     RfnBroadcastDeliveryType    deliveryType;
    5: required     binary                      payload;
    6: optional     NetworkManagerMessaging.NetworkManagerRequestHeader     header;
}


struct RfnBroadcastReply {
    1: required     i32                         replyType;
    2: optional     string                      failureReason;
    3: required     map<RfnE2eData.RfnIdentifier, string>   gatewayErrors;
    4: optional     NetworkManagerMessaging.NetworkManagerRequestHeader     header;
}

