#include "precompiled.h"

#include "LMMeterDisconnectMessages.h"
#include "proton_encoder_proxy.h"


namespace Cti::Messaging::LoadManagement
{

LMMeterDisconnectControlMessage::LMMeterDisconnectControlMessage( int groupId,
                                                                  long long startTime,
                                                                  int controlDuration )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration )
{
    // empty
}

void LMMeterDisconnectControlMessage::streamInto( Proton::EncoderProxy & message ) const
{
    message.writeInt( _groupId );
    message.writeLong( _startTime );
    message.writeLong( _stopTime );
}

LMMeterDisconnectRestoreMessage::LMMeterDisconnectRestoreMessage( int groupId,
                                                                  long long restoreTime )
    :   _groupId( groupId ),
        _restoreTime( restoreTime )
{
    // empty
}

void LMMeterDisconnectRestoreMessage::streamInto( Proton::EncoderProxy & message ) const
{
    message.writeInt( _groupId );
    message.writeLong( _restoreTime );
}

}

