include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifCurtailmentEvent {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _curtailmentEventId;
    3: required     i32                             _action;    
}