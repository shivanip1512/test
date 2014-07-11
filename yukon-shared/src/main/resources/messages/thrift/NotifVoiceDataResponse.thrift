include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct NotifVoiceDataResponse {
    1: required     Message.Message                 _baseMessage;
    2: required     string                          _callToken;
    3: required     string                          _xmlData;
    4: required     i32                             _contactId;
}
