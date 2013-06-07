include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct MCVerifyScript {
    1: required     Message.Message                 _baseMessage;
    2: required     string                          _name;
}
