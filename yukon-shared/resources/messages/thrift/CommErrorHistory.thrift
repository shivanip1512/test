include "Message.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CommErrorHistory {
    1: required     Message.Message   	        _baseMessage;
    2: required     i32                         _commErrorId;
    3: required     i32                         _paoId;
    4: required     Types.Timestamp             _dateTime;
    5: required     i32                         _errorType;
    6: required     i32                         _errorNumber;
    7: required     string                      _command;
    8: required     string                      _outMessage;
    9: required     string                      _inMessage;
}
