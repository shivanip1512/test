include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct RequestCancel {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _RequestId;
    3: required     i32                             _RequestIdCount;
    4: required     Types.Timestamp                 _Time;
    5: required     i32                             _UserMessageId;
}
