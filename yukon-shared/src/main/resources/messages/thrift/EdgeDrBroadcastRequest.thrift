namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum EdgeBroadcastMessagePriority {
    IMMEDIATE = 0x00,
    NON_REAL_TIME = 0x01,
    //...
}

struct EdgeDrBroadcastRequest {
    1: required string messageGuid;
    2: required binary payload;
}