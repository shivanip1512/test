include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct PointRegistration {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _regFlags;
    3: required     list<i32>                       _pointList;
}
