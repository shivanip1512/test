
#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

class IM_EX_MSG LMNestCyclingControlMessage : public StreamableMessage
{
public:

    LMNestCyclingControlMessage( int       groupId,
                                 long long startTime,
                                 int       controlDuration );

    void streamInto( cms::StreamMessage & message ) const;

private:

    int       _groupId;
    long long _startTime;
    long long _stopTime;
};

/// 

class IM_EX_MSG LMNestRestoreMessage : public StreamableMessage
{
public:

    LMNestRestoreMessage( int       groupId,
                          long long restoreTime );

    void streamInto( cms::StreamMessage & message ) const;

private:

    int       _groupId;
    long long _restoreTime;
};

}
}
}

