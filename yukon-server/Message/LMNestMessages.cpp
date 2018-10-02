#include "precompiled.h"

#include "LMNestMessages.h"
//#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>


namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

LMNestCriticalCyclingControlMessage::LMNestCriticalCyclingControlMessage( int groupId,
                                                                          int startTime,
                                                                          int controlDuration )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration )
{
    // empty
}

void LMNestCriticalCyclingControlMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _groupId );
    message.writeInt( _startTime );
    message.writeInt( _stopTime );
}

///

LMNestStandardCyclingControlMessage::LMNestStandardCyclingControlMessage( int groupId,
                                                                          int startTime,
                                                                          int controlDuration,
                                                                          int prepOption,
                                                                          int peakOption,
                                                                          int postOption )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( startTime + controlDuration ),
        _prepOption( prepOption ),
        _peakOption( peakOption ),
        _postOption( postOption )
{
    // empty
}

void LMNestStandardCyclingControlMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeInt( _groupId );
    message.writeInt( _startTime );
    message.writeInt( _stopTime );
    message.writeByte( _prepOption );
    message.writeByte( _peakOption );
    message.writeByte( _postOption );
}

}
}
}

