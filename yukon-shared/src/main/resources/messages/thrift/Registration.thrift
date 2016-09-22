include "Message.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct Registration {
    1: required     Message.Message                 _baseMessage;
    2: required     string                          _appName;
    3: required     i32                             _appId;
    4: required     i32                             _appIsUnique;
    5: required     i32                             _appExpirationDelay;
}
