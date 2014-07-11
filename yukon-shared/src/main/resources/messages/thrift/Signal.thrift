include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Signal {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _id;
    3: required     i32                             _logType;
    4: required     i32                             _signalCategory;
    5: required     string                          _text;
    6: required     string                          _additionalInfo;
    7: required     i32                             _tags;
    8: required     i32                             _condition;
    9: required     i32                             _signalMillis;
   10: required     double                          _pointValue;
}
