#include "precompiled.h"

#include "LMHoneywellMessages.h"

#include <cms/StreamMessage.h>


namespace Cti::Messaging::LoadManagement
{

LMHoneywellCyclingControlMessage::LMHoneywellCyclingControlMessage( const int  programId,
                                                                    const int  groupId,
                                                                    const int  dutyCycle,
                                                                    const int  startTime,
                                                                    const int  controlDuration,
                                                                    const bool rampInOut )
    :   _programId( programId ),
        _groupId(groupId),
        _rampingOption(rampInOut),
        _dutyCycle(dutyCycle),
        _startTime(startTime),
        _stopTime(startTime + controlDuration)
{
    // empty
}

void LMHoneywellCyclingControlMessage::streamInto(cms::StreamMessage & message) const
{
    message.writeInt( _programId );
    message.writeInt(_groupId);
    message.writeByte(_dutyCycle);
    message.writeByte(_rampingOption);
    message.writeInt(_startTime);
    message.writeInt(_stopTime);
}

LMHoneywellSetpointControlMessage::LMHoneywellSetpointControlMessage( const int  programId,
                                                                      const int  groupId,
                                                                      const bool temperatureOption,
                                                                      const bool mandatory,
                                                                      const int  temperatureOffset,
                                                                      const long long startTime,
                                                                      const int  controlDuration )
    :   _programId( programId ),
        _groupId(groupId),
        _temperatureOption( temperatureOption ),
        _mandatory( mandatory ),
        _temperatureOffset( temperatureOffset ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration )
{
    // empty
}

void LMHoneywellSetpointControlMessage::streamInto(cms::StreamMessage & message) const
{
    message.writeInt( _programId );
    message.writeInt( _groupId );
    message.writeByte( _temperatureOption );
    message.writeByte( _mandatory );
    message.writeInt( _temperatureOffset );
    message.writeLong( _startTime );
    message.writeLong( _stopTime );
}

LMHoneywellRestoreMessage::LMHoneywellRestoreMessage( const int groupId,
                                                      const int restoreTime )
    :   _groupId(groupId),
        _restoreTime(restoreTime)
{
    // empty
}

void LMHoneywellRestoreMessage::streamInto(cms::StreamMessage & message) const
{
    message.writeInt(_groupId);
    message.writeInt(_restoreTime);
}

}

