namespace cpp Cti.Messaging.Serialization.Thrift.Porter
namespace java com.cannontech.messaging.serialization.thrift.generated.porter

enum DynamicPaoInfoDurationKeys {
    RFN_VOLTAGE_PROFILE_INTERVAL,
    MCT_IED_LOAD_PROFILE_INTERVAL
}

enum DynamicPaoInfoTimestampKeys {
    RFN_VOLTAGE_PROFILE_ENABLED_UNTIL
}

struct DynamicPaoInfoRequest {
    1: required  i32 _deviceId;
    2: required  set<DynamicPaoInfoDurationKeys> _durationKeys;
    3: required  set<DynamicPaoInfoTimestampKeys> _timestampKeys;
}

struct DynamicPaoInfoResponse {
    1: required  i32 _deviceId;
    2: required  map<DynamicPaoInfoDurationKeys, i64> _durationValues;
    3: required  map<DynamicPaoInfoTimestampKeys, i64> _timestampValues;
}