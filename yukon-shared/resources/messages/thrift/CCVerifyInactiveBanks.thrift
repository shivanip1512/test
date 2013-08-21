include "CCVerifyBanks.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCVerifyInactiveBanks {
    1: required     CCVerifyBanks.CCVerifyBanks     _baseMessage;
    2: required     i32                             _bankInactiveTime;
}



