namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum EdgeUnicastPriority {
    HIGH = 0x00,
    LOW = 0x01,
    //...
}

struct EdgeDrUnicastRequest {
    1: required string messageGuid;
    2: required list<i32> paoIds;
    3: required binary payload;
    4: required EdgeUnicastPriority queuePriority = EdgeUnicastPriority.HIGH;
    5: required EdgeUnicastPriority networkPriority = EdgeUnicastPriority.HIGH;
}