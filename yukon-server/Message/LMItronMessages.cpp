
#include "precompiled.h"

#include "LMItronMessages.h"
#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>


namespace Cti::Messaging::LoadManagement {

LMItronCyclingControlMessage::LMItronCyclingControlMessage( int groupId,
                                                            long long startTime,
                                                            int controlDuration,
                                                            bool rampInOption,
                                                            bool rampOutOption,
                                                            long dutyCyclePercent,
                                                            long dutyCyclePeriod,
                                                            long criticality )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _rampIn( rampInOption ),
        _rampOut( rampOutOption ),
        _dutyCyclePercent( dutyCyclePercent ),
        _dutyCyclePeriod( dutyCyclePeriod ),
        _criticality( criticality )
{
    // empty
}

void LMItronCyclingControlMessage::streamInto(cms::StreamMessage & message) const
{
    message.writeInt( _groupId );
    message.writeLong( _startTime );
    message.writeLong( _stopTime );
    message.writeByte( _rampIn );
    message.writeByte( _rampOut );
    message.writeByte( _dutyCyclePercent );
    message.writeInt( _dutyCyclePeriod );
    message.writeByte( _criticality );
}

LMItronRestoreMessage::LMItronRestoreMessage( int groupId,
                                              long long restoreTime )
    :   _groupId( groupId ),
        _restoreTime( restoreTime )
{
    // empty
}

void LMItronRestoreMessage::streamInto(cms::StreamMessage & message) const
{
    message.writeInt( _groupId );
    message.writeLong( _restoreTime );
}

}

