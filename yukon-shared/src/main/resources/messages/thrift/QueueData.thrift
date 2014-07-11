include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct QueueData {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _id;
    3: required     i32                             _queueCount;
    4: required     i32                             _rate;
    5: required     i32                             _requestId;
    6: required     i32                             _requestIdCount;
    7: required     Types.Timestamp                 _aTime;
    8: required     i32                             _userMessageId;
}
