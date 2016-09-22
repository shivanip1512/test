include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Message {
    1: required     Types.Timestamp                 _messageTime;
    2: required     i32                             _messagePriority;
    3: required     i32                             _soe;
    4: required     string                          _usr;
    5: required     string                          _src;
}

struct GenericMessage {
    1: required     string                          _messageType;
    2: required     binary                          _payload;
}
