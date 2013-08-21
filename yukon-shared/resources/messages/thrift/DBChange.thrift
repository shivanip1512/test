include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct DBChange {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _id;
    3: required     i32                             _database;
    4: required     string                          _category;
    5: required     string                          _objecttype;
    6: required     i32                             _typeofchange;
}
