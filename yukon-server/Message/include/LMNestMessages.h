
#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

class IM_EX_MSG LMNestCyclingControlMessage : public StreamableMessage
{
public:

    LMNestCyclingControlMessage( int groupId,
                                 int startTime,
                                 int controlDuration );

    void streamInto( cms::StreamMessage & message ) const;

private:

    int _groupId;
    int _startTime;
    int _stopTime;
};

/// 

class IM_EX_MSG LMNestRestoreMessage : public StreamableMessage
{
public:

    LMNestRestoreMessage( int groupId,
                          int restoreTime );

    void streamInto( cms::StreamMessage & message ) const;

private:

    int _groupId;
    int _restoreTime;
};

}
}
}

