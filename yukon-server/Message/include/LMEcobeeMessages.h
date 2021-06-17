#pragma once


#include "dlldefs.h"

namespace Cti            {
namespace Messaging      {
namespace LoadManagement {


struct IM_EX_MSG LMEcobeeCyclingControlMessage
{

    LMEcobeeCyclingControlMessage( int  groupId,
                                   int  dutyCycle,
                                   int  startTime,
                                   int  controlDuration,
                                   bool mandatory,
                                   bool rampInOut);

    int  _groupId;
    char _dutyCycle;
    int  _startTime;
    int  _stopTime;
    char _mandatory;
    bool _rampingOption;
};

///

struct IM_EX_MSG LMEcobeeSetpointControlMessage 
{

    enum class TempOptionTypes
    {
        Heat,
        Cool
    };

    LMEcobeeSetpointControlMessage( int  groupId,
                                    long long startTime,
                                    int  controlDuration,
                                    TempOptionTypes temperatureOption,
                                    bool mandatory,
                                    int  temperatureOffset );


    int  _groupId;
    TempOptionTypes _temperatureOption;
    char _mandatory;
    int  _temperatureOffset;
    long long _startTime;
    long long _stopTime;
};

/// 

struct IM_EX_MSG LMEcobeePlusControlMessage 
  
{
    enum class TempOptionTypes
    {
        Heat,
        Cool
    };

    LMEcobeePlusControlMessage( int groupId,
                                long long startTime,
                                int controlDuration,            
                                TempOptionTypes temperatureOption,
                                int randomTimeSeconds );

    int _groupId;
    long long _startTime;
    long long _stopTime;
    TempOptionTypes _temperatureOption;    
    int _randomTimeSeconds;
};

/// 

struct IM_EX_MSG LMEcobeeRestoreMessage 
    
{

    LMEcobeeRestoreMessage( int groupId,
                            int restoreTime );

    int _groupId;
    int _restoreTime;
};

}
}
}

