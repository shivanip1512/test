namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum RfnExpressComBroadcastReplyType {
    SUCCESS = 0x00,
    FAILURE = 0x01,
    NETWORK_TIMEOUT = 0x02,
    TIMEOUT = 0x03,
    //...
}

struct RfnExpressComBroadcastReply {
    1: required    map<i64, RfnExpressComBroadcastReplyType>    status;
}