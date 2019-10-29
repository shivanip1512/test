include "RfnAddressing.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift.MeterProgramming
namespace java com.cannontech.messaging.serialization.thrift.generated.meterProgramming

enum Source {
    PORTER,
    WS_COLLECTION_ACTION,
    SM_STATUS_ARCHIVE,
    SM_CONFIG_FAILURE
}

enum ProgrammingStatus {
    IDLE,
    UPLOADING, 
    CONFIRMING, 
    FAILED, 
    INITIATING, 
    CANCELED, 
    MISMATCHED
}

struct MeterProgramStatusArchiveRequest {
    1: required  RfnAddressing.RfnIdentifier rfnIdentifier;
    2: optional  string configurationId;
    3: required  ProgrammingStatus status;
    4: required  i32 error;
    5: required  i64 timeStamp;
    6: required  Source source;
}