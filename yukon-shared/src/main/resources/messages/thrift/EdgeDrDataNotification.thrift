include "EdgeDrError.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct EdgeDrDataNotification {
    1: required i32 paoId;
    2: optional binary payload;
    3: optional string e2eId;
    4: optional EdgeDrError.EdgeDrError error;
}