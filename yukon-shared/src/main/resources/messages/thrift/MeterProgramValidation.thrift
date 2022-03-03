namespace cpp Cti.Messaging.Serialization.Thrift.Porter
namespace java com.cannontech.messaging.serialization.thrift.generated.porter

struct MeterProgramValidationRequest {
    1: required  string _meterProgramGuid;
}

struct MeterProgramValidationResponse {
    1: required  string _meterProgramGuid;
    2: required  i32    _status;
}