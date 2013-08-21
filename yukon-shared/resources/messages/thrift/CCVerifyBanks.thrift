include "CCItemCommand.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCVerifyBanks {
    1: required     CCItemCommand.CCItemCommand     _baseMessage;
    2: required     bool                            _disableOvUv;
}
