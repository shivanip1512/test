include "Types.thrift"
include "RfnE2eData.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated


struct RfnDeviceCreationRequest {
    1: required     RfnE2eData.RfnIdentifier    rfnIdentifier;
}

struct RfnDeviceCreationReply {
    1: required     i32                         paoId;
    2: required     string                      category;
    3: required     string                      deviceType;
}