include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct ServerResponse {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _id;
    3: required     i32                             _status;
    4: required     string                          _message;
    5: required     bool                            _hasPayload;
    6: required     Message.GenericMessage          _payload;
}
