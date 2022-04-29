namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct EdgeDrUnicastRequest {
    1: required string messageGuid;
    2: required list<i32> paoIds;
    3: required binary payload;
}