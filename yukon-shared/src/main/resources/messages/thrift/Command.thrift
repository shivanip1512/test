include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Command {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _operation;
    3: required     string                          _opString;
    4: required     i32                             _opArgCount;
    5: required     list<i32>                       _opArgList;
}
