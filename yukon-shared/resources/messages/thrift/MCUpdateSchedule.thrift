include "Message.thrift"
include "MCSchedule.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct MCUpdateSchedule {
    1: required     Message.Message                 _baseMessage;
    2: required     MCSchedule.MCSchedule           _schedule;
    3: required     string                          _script;
}
