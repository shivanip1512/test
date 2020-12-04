include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

enum LMEatonCloudCycleType {
    STANDARD = 0x00,
    TRUE_CYCLE = 0x01,
    SMART_CYCLE = 0x02,
}

struct LMEatonCloudCycleCommand {
    1: required     i32                             _groupId;
    2: required     i32                             _controlSeconds;
    3: required     bool                            _isRampIn;
    4: required     bool                            _isRampOut;
    5: required     LMEatonCloudCycleType           _cyclingOption;
    6: required     i32                             _dutyCyclePercentage;
    7: required     i32                             _dutyCyclePeriod;
    8: required     i32                             _criticality;
    9: required     Types.Timestamp                 _currentDateTime;
}

struct LMEatonCloudScheduledCycleCommand {
    1: required     i32                             _groupId;
    2: required     Types.Timestamp                 _controlStartDateTime;
    3: required     Types.Timestamp                 _controlEndDateTime;
    4: required     bool                            _isRampIn;
    5: required     bool                            _isRampOut;
    6: required     LMEatonCloudCycleType           _cyclingOption;
    7: required     i32                             _dutyCyclePercentage;
    8: required     i32                             _dutyCyclePeriod;
    9: required     i32                             _criticality;
}

enum LMEatonCloudStopType {
    RESTORE = 0x00,
    STOP_CYCLE = 0x01
}

struct LMEatonCloudStopCommand {
    1: required     i32                             _groupId;
    2: required     Types.Timestamp                 _restoreTime;
    3: required     LMEatonCloudStopType            _stopType;
}