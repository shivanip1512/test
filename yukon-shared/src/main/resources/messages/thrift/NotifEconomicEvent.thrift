include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifEconomicEvent {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _economicEventId;
    3: required     i32                             _revisionNumber;
    4: required     i32                             _action;
}
