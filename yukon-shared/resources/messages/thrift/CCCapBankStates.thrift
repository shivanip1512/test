include "CCMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCState {
    1: required     string                          _text;
    2: required     i32                             _foregroundColor;
    3: required     i32                             _backgroundColor;
}

struct CCCapBankStates {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     list<CCState>                   _ccCapBankStates;
}
