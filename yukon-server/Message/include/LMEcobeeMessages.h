#pragma once


#include "dlldefs.h"

namespace Cti            {
namespace Messaging      {
namespace LoadManagement {


struct IM_EX_MSG LMEcobeeCyclingControlMessage
{

    LMEcobeeCyclingControlMessage( int  programId,
                                   int  groupId,
                                   int  dutyCycle,
                                   long long startTime,
                                   int  controlDuration,
                                   bool mandatory,
                                   bool rampInOut);

    int  _programId;
    int  _groupId;
    char _dutyCycle;
    long long  _startTime;
    long long  _stopTime;
    char _mandatory;
    bool _rampingOption;
};

///

enum class TempOptionTypes
{
    Heat,
    Cool
};

/// 

struct IM_EX_MSG LMEcobeeSetpointControlMessage 
{

    LMEcobeeSetpointControlMessage( int  programId,
                                    int  groupId,
                                    long long startTime,
                                    int  controlDuration,
                                    TempOptionTypes temperatureOption,
                                    bool mandatory,
                                    int  temperatureOffset );


    int  _programId;
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
    LMEcobeePlusControlMessage( int programId,
                                int groupId,
                                long long startTime,
                                int controlDuration,            
                                TempOptionTypes temperatureOption,
                                int randomTimeSeconds );

    int _programId;
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
                            long long restoreTime );

    int _groupId;
    long long _restoreTime;
};

}
}
}

