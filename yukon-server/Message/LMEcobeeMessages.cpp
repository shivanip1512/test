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
                                                              bool rampIn,
                                                              bool rampOut,
                                                              bool mandatory )
    :   _groupId( groupId ),
        _rampingOption( 0x00 ),
        _dutyCycle( dutyCycle ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _mandatory( mandatory )
{
    // set specified flags 
    _rampingOption |= rampIn  ? RampIn  : 0 ;
    _rampingOption |= rampOut ? RampOut : 0 ;
}

void LMEcobeeCyclingControlMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt ( _groupId );
    message.writeByte( _dutyCycle );
    message.writeByte( _rampingOption );
    message.writeByte( _mandatory );
    message.writeInt ( _startTime );
    message.writeInt ( _stopTime );
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

