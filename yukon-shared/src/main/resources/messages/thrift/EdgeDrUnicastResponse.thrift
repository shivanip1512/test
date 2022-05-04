include "EdgeDrError.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct EdgeDrUnicastResponse {
    1: required string messageGuid;
    2: required map<i32,i16> paoToE2eId;
    3: optional EdgeDrError.EdgeDrError error;
}