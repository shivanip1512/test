include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Request {
    1: required     Message.Message                 _baseMessage;
    2: required     i32                             _deviceId;
    3: required     string                          _commandString;
    4: required     i32                             _routeId;
    5: required     i32                             _macroOffset;
    6: required     i32                             _attemptNum;
    7: required     i32                             _groupMessageId;
    8: required     i32                             _userMessageId;
    9: required     i32                             _optionsField;
}
