#include "precompiled.h"

#include "LMEcobeeMessages.h"
#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>


namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

LMEcobeeCyclingControlMessage::LMEcobeeCyclingControlMessage( int  groupId,
                                                              int  dutyCycle,
                                                              int  startTime,
                                                              int  controlDuration,
                                                              bool mandatory,
                                                              bool rampInOut )
    :   _groupId( groupId ),
        _dutyCycle( dutyCycle ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _mandatory( mandatory ),
        _rampingOption( rampInOut )
{
    // Empty
}

void LMEcobeeCyclingControlMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt ( _groupId );
    message.writeByte( _dutyCycle );
    message.writeByte( _mandatory );
    message.writeInt ( _startTime );
    message.writeInt ( _stopTime );
    message.writeByte( _rampingOption );
}

///

LMEcobeeSetpointControlMessage::LMEcobeeSetpointControlMessage( int  groupId,
                                                                long long startTime,
                                                                int  controlDuration,
                                                                bool temperatureOption,
                                                                bool mandatory,
                                                                int  temperatureOffset )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _temperatureOption( temperatureOption ),
        _mandatory( mandatory ),
        _temperatureOffset( temperatureOffset )
{
    // empty
}

void LMEcobeeSetpointControlMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _groupId );
    message.writeByte( _temperatureOption );
    message.writeByte( _mandatory );
    message.writeInt( _temperatureOffset );
    message.writeLong( _startTime );
    message.writeLong( _stopTime );
}

///

LMEcobeePlusControlMessage::LMEcobeePlusControlMessage( int groupId,
                                                        long long startTime,
                                                        int controlDuration,    
                                                        bool temperatureOption,  
                                                        int randomTimeSeconds )

    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _temperatureOption( temperatureOption ),       
        _randomTimeSeconds( randomTimeSeconds )      
{
    //empty
}

void LMEcobeePlusControlMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _groupId );
    message.writeLong( _startTime );
    message.writeLong( _stopTime );
    message.writeByte( _temperatureOption );
    message.writeInt( _randomTimeSeconds );
}

/// 

LMEcobeeRestoreMessage::LMEcobeeRestoreMessage( int groupId,
                                                int restoreTime )
    :   _groupId( groupId ),
        _restoreTime( restoreTime )
{
    // empty
}

void LMEcobeeRestoreMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _groupId );
    message.writeInt( _restoreTime );
}

}
}
}

