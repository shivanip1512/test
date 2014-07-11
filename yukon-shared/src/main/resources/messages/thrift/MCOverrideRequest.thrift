include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct MCOverrideRequest {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _action;
    3: required     i32                             _id;
    4: required     Types.Timestamp                 _startTime;
    5: required     Types.Timestamp                 _stopTime;
}
