include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCServerResponse {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _messageId;
    3: required     i32                             _responseType;
    4: required     string                          _response;
}
