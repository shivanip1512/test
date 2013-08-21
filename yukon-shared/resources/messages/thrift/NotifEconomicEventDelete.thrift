include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifEconomicEventDelete {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _economicEventId;
    3: required     bool                            _deleteStart;
    4: required     bool                            _deleteStop;
}
