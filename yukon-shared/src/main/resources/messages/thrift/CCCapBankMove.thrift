include "CCItemCommand.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCCapBankMove {
    1: required     CCItemCommand.CCItemCommand     _baseMessage;
    2: required     i32                             _permanentFlag;
    3: required     i32                             _oldFeederId;
    4: required     i32                             _newFeederId;
    5: required     double                          _capSwitchingOrder;
    6: required     double                          _closeOrder;
    7: required     double                          _tripOrder;
}
