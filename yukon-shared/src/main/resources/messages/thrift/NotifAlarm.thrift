include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifAlarm {
    1: required     Message.Message                 _baseMessage;
    2: required     list<i32>                       _notifGroupIds;
    3: required     i32                             _categoryId;
    4: required     i32                             _pointId;
    5: required     i32                             _condition;
    6: required     double                          _value;
    7: required     Types.Timestamp                 _alarmTimestamp;
    8: required     bool                            _acknowledged;
    9: required     bool                            _abnormal;
}
