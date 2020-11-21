namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum LMEatonCloudCycleType {
    STANDARD = 0x00,
    TRUE_CYCLE = 0x01,
    SMART_CYCLE = 0x02,
}

struct RfnExpressComBroadcastReply {
    1: required     i32                             _groupId;
    2: required     i32                             _controlSeconds;
    3: required     bool                            _isRampIn;
    4: required     bool                            _isRampOut;
    5: required     LMEatonCloudCycleType           _cyclingOption;
    6: required     i32                             _dutyCyclePercentage;
    7: required     i32                             _dutyCyclePeriod;
    8: required     i32                             _criticality;
    9: required     i32                             _controlResendRate;
   10: required     Types.Timestamp                 _currentDateTime;
}