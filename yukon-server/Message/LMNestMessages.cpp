#include "precompiled.h"

#include "LMNestMessages.h"
//#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>


namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

LMNestCyclingControlMessage::LMNestCyclingControlMessage( int groupId,
                                                          int startTime,
                                                          int controlDuration )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration )
{
    // empty
}

void LMNestCyclingControlMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _groupId );
    message.writeInt( _startTime );
    message.writeInt( _stopTime );
}

///

LMNestRestoreMessage::LMNestRestoreMessage( int groupId,
                                            int restoreTime )
    :   _groupId( groupId ),
        _restoreTime( restoreTime )
{
    // empty
}

void LMNestRestoreMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _groupId );
    message.writeInt( _restoreTime );
}

}
}
}

