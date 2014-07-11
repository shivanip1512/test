include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifCurtailmentEventDelete {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _curtailmentEventId;
    3: required     bool                            _deleteStart;
    4: required     bool                            _deleteStop;
}
