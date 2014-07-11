include "CCMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCDeleteItem {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     i32                             _itemId;
}
