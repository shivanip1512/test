include "CCCommand.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCDynamicCommand {
    1: required     CCCommand.CCCommand             _baseMessage;
    2: required     i32                             _commandType;
    3: required     map<i32, i32>                   _longParameters;
    4: required     map<i32, double>                _doubleParameters;
}
