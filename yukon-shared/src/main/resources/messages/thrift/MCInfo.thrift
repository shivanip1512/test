include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct MCInfo {
    1: required     Message.Message                 _baseMessage;  
    2: required     i32                             _id;
    3: required     string                          _info;
}
