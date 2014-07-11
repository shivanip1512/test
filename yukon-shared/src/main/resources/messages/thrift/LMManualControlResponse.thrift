include "LMMessage.thrift"
include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMConstraintViolation {
    1: required     i32                             _errorCode;
    2: required     list<double>                    _doubleParams;
    3: required     list<i32>                       _integerParams;
    4: required     list<string>                    _stringParams;
    5: required     list<Types.Timestamp>           _datetimeParams;
}

struct LMManualControlResponse {
    1: required     LMMessage.LMMessage             _baseMessage;
    2: required     i32                             _paoId;
    3: required     list<LMConstraintViolation>     _constraintViolations;
    4: required     string                          _bestFitAction;
}
