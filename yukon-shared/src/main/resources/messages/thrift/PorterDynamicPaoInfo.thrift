namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum DynamicPaoInfoKeys {
    RFN_VOLTAGE_PROFILE_ENABLED_UNTIL,
    RFN_VOLTAGE_PROFILE_INTERVAL
}

struct PorterDynamicPaoInfoRequest {
    1: required  i32                     _deviceId;
    2: required  set<DynamicPaoInfoKeys> _keys;
}

union DynamicPaoInfoTypes {
    1: i64    _integer;
    2: i64    _time;
    3: string _string;
}

struct PorterDynamicPaoInfoResponse {
    1: required  i32                 _deviceId;
    2: required  map<DynamicPaoInfoKeys, DynamicPaoInfoTypes> _values;
}