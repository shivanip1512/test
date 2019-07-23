include "LMMessage.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMManualControlRequest {
    1: required     LMMessage.LMMessage             _baseMessage;
    2: required     i32                             _command;
    3: required     i32                             _paoId;
    4: required     Types.Timestamp                 _notifyTime;
    5: required     Types.Timestamp                 _startTime;
    6: required     Types.Timestamp                 _stopTime;
    7: required     i32                             _startGear;
    8: required     i32                             _startPriority;
    9: required     string                          _additionalInfo;
   10: required     i32                             _constraintCmd;
   11: required     string                          _originSource;
}
