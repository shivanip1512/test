include "Message.thrift"
include "Types.Thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct PointData {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _id;
    3: required     byte                            _type;
    4: required     byte                            _quality;
    5: required     i32                             _tags;
    6: required     double                          _value;
    7: required     string                          _str;
    8: required     Types.Timestamp                 _time;
    9: required     i16                             _millis;
}
