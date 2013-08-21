include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct MCScript {
    1: required     Message.Message                 _baseMessage;
    2: required     string                          _name;
    3: required     string                          _contents;
}
