include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct RfnDataStreamingUpdate {
    1: required     i32         paoId;
    2: required     string      json;
}

struct RfnDataStreamingUpdateReply {
    1: required     bool        success;
}