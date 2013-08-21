include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifVoiceCompleted {
    1: required     Message.Message                 _baseMessage;
    2: required     string                          _callToken;
    4: required     i32                             _callStatus;
}
