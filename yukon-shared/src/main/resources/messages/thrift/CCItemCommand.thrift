include "CCCommand.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCItemCommand {
    1: required     CCCommand.CCCommand             _baseMessage;
    2: required     i32                             _itemId;
}
