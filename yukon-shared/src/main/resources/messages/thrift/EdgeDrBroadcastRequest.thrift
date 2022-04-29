namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct EdgeDrBroadcastRequest {
    1: required string messageGuid;
    2: required binary payload;
}