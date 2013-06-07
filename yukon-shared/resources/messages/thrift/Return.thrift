include "Multi.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Return {
    1: required     Multi.Multi                     _baseMessage;
    2: required     i32                             _deviceId;
    3: required     string                          _commandString;
    4: required     string                          _resultString;
    5: required     i32                             _status;
    6: required     i32                             _routeId;
    7: required     i32                             _macroOffset;
    8: required     i32                             _attemptNum;
    9: required     i32                             _groupMessageId;
   10: required     i32                             _userMessageId;
   11: required     bool                            _expectMore;
}
