include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMEcobeeCycleControlCommand {

    1: required     i32                             _groupId;
    2: required     i32                             _dutyCycle;
    3: required     Types.Timestamp                 _controlStartDateTime;
    4: required     Types.Timestamp                 _controlEndDateTime;
    5: required     bool                            _isMandatory;
    6: required     bool                            _isRampInOut;
    
}

enum LMEcobeeTemperatureTypes {
    HEAT = 0x00,
    COOL = 0x01
}

struct LMEcobeeSetpointControlCommand {

    1: required     i32                             _groupId;
    2: required     Types.Timestamp                 _controlStartDateTime;
    3: required     Types.Timestamp                 _controlEndDateTime;
    4: required     LMEcobeeTemperatureTypes        _temperatureOption;
    5: required     bool                            _isMandatory;
    6: required     i32                             _temperatureOffset;
    
}

struct LMEcobeePlusControlCommand {

    1: required     i32                             _groupId;
    2: required     Types.Timestamp                 _controlStartDateTime;
    3: required     Types.Timestamp                 _controlEndDateTime;
    4: required     LMEcobeeTemperatureTypes        _temperatureOption;
    5: required     i32                             _randomTimeSeconds;
    
}

struct LMEcobeeRestore {

    1: required     i32                             _groupId;
    2: required     Types.Timestamp                 _restoreTime;
    
}

