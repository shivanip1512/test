include "Types.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct LMEcobeeCycleControlCommand {

    1: required     i32                             _programId;
    2: required     i32                             _groupId;
    3: required     i32                             _dutyCycle;
    4: required     Types.Timestamp                 _controlStartDateTime;
    5: required     Types.Timestamp                 _controlEndDateTime;
    6: required     bool                            _isMandatory;
    7: required     bool                            _isRampInOut;
    
}

enum LMEcobeeTemperatureTypes {
    HEAT = 0x00,
    COOL = 0x01
}

struct LMEcobeeSetpointControlCommand {

    1: required     i32                             _programId;
    2: required     i32                             _groupId;
    3: required     Types.Timestamp                 _controlStartDateTime;
    4: required     Types.Timestamp                 _controlEndDateTime;
    5: required     LMEcobeeTemperatureTypes        _temperatureOption;
    6: required     bool                            _isMandatory;
    7: required     i32                             _temperatureOffset;
    
}

struct LMEcobeePlusControlCommand {

    1: required     i32                             _programId;
    2: required     i32                             _groupId;
    3: required     Types.Timestamp                 _controlStartDateTime;
    4: required     Types.Timestamp                 _controlEndDateTime;
    5: required     LMEcobeeTemperatureTypes        _temperatureOption;
    6: required     i32                             _randomTimeSeconds;
    
}

struct LMEcobeeRestore {

    1: required     i32                             _groupId;
    2: required     Types.Timestamp                 _restoreTime;
    
}

