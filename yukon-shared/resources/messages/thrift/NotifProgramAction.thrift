include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifProgramAction {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _programId;
    3: required     string                          _eventDisplayName;
    4: required     string                          _action;
    5: required     Types.Timestamp                 _startTime;
    6: required     Types.Timestamp                 _stopTime;
    7: required     Types.Timestamp                 _notificationTime;
    8: required     list<i32>                       _customerIds;
}
