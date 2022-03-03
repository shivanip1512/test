#include "precompiled.h"

#include "LMEcobeeMessages.h"
#include "msg_pcreturn.h"



namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

LMEcobeeCyclingControlMessage::LMEcobeeCyclingControlMessage( int  programId,
                                                              int  groupId,
                                                              int  dutyCycle,
                                                              long long  startTime,
                                                              int  controlDuration,
                                                              bool mandatory,
                                                              bool rampInOut )
    :   _programId( programId ),
        _groupId( groupId ),
        _dutyCycle( dutyCycle ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _mandatory( mandatory ),
        _rampingOption( rampInOut )
{
    // Empty
}


///

LMEcobeeSetpointControlMessage::LMEcobeeSetpointControlMessage( int  programId,
                                                                int  groupId,
                                                                long long startTime,
                                                                int  controlDuration,
                                                                TempOptionTypes temperatureOption,
                                                                bool mandatory,
                                                                int  temperatureOffset )
    :   _programId(programId),
        _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _temperatureOption( temperatureOption ),
        _mandatory( mandatory ),
        _temperatureOffset( temperatureOffset )
{
    // empty
}

///

LMEcobeePlusControlMessage::LMEcobeePlusControlMessage( int programId,
                                                        int groupId,
                                                        long long startTime,
                                                        int controlDuration,    
                                                        TempOptionTypes temperatureOption,
                                                        int randomTimeSeconds )

    :   _programId(programId),
        _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _temperatureOption( temperatureOption ),       
        _randomTimeSeconds( randomTimeSeconds )      
{
    //empty
}

/// 

LMEcobeeRestoreMessage::LMEcobeeRestoreMessage( int groupId,
                                                long long restoreTime )
    :   _groupId( groupId ),
        _restoreTime( restoreTime )
{
    // empty
}

}
}
}

