include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Tag {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _instanceId;
    3: required     i32                             _pointId;
    4: required     i32                             _tagId;
    5: required     string                          _descriptionStr;
    6: required     i32                             _action;
    7: required     Types.Timestamp                 _tagTime;
    8: required     string                          _referenceStr;
    9: required     string                          _taggedForStr;
   10: required     i32                             _clientMsgId;
}
