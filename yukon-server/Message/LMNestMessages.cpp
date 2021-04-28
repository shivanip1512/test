#include "precompiled.h"

#include "LMNestMessages.h"
//#include "msg_pcreturn.h"

#include "proton_encoder_proxy.h"


namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

LMNestCyclingControlMessage::LMNestCyclingControlMessage( int       groupId,
                                                          long long startTime,
                                                          int       controlDuration )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration )
{
    // empty
}

void LMNestCyclingControlMessage::streamInto( Proton::EncoderProxy & message ) const
{
    message.writeInt( _groupId );
    message.writeLong( _startTime );
    message.writeLong( _stopTime );
}

///

LMNestRestoreMessage::LMNestRestoreMessage( int       groupId,
                                            long long restoreTime )
    :   _groupId( groupId ),
        _restoreTime( restoreTime )
{
    // empty
}

void LMNestRestoreMessage::streamInto( Proton::EncoderProxy & message ) const
{
    message.writeInt( _groupId );
    message.writeLong( _restoreTime );
}

}
}
}

