include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Trace {
    1: required     Message.Message                 _baseMessage;
    2: required     bool                            _end; 
    3: required     i32                             _attributes;
    4: required     string                          _trace;
}
