include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifLMControl {
    1: required     Message.Message                 _baseMessage;
    2: required     list<i32>                       _notifGroupIds;
    3: required     i32                             _notifType;
    4: required     i32                             _programId;    
    5: required     Types.Timestamp                 _startTime;
    6: required     Types.Timestamp                 _stopTime;
}
