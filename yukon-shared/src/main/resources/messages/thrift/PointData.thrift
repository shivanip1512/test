include "Message.thrift"
include "Types.Thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct PointData {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _id;
    3: required     i32                             _type;
    4: required     i32                             _quality;
    5: required     i32                             _tags;
    6: required     i32                             _limit;
    7: required     double                          _value;
    8: required     i32                             _exemptionStatus;
    9: required     string                          _str;
   10: required     Types.Timestamp                 _time;
   11: required     i32                             _millis;
}
