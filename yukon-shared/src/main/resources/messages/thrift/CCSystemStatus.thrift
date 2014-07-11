include "CCMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCSystemStatus {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     bool                            _systemState;
}
