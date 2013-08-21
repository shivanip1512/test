include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMControlHistory {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _paoId;
    3: required     i32                             _pointId;
    4: required     i32                             _rawState;
    5: required     Types.Timestamp                 _startDateTime;
    6: required     i32                             _controlDuration;
    7: required     i32                             _reductionRatio; 
    8: required     string                          _controlType;
    9: required     string                          _activeRestore;
   10: required     double                          _reductionValue;
   11: required     i32                             _controlPriority;
   12: required     i32                             _associationKey;
}
