include "LMMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMCommand {
    1: required     LMMessage.LMMessage             _baseMessage;
    2: required     i32                             _command;
    3: required     i32                             _paoId;
    4: required     i32                             _number;
    5: required     double                          _value;
    6: required     i32                             _count;
    7: required     i32                             _auxId;   
}
