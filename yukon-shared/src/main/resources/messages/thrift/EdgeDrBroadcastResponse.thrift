include "EdgeDrError.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct EdgeDrBroadcastResponse {
    1: required string messageGuid;
    2: optional EdgeDrError.EdgeDrError error;
}