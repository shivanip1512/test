
#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

class IM_EX_MSG LMNestCriticalCyclingControlMessage : public StreamableMessage
{
public:

    LMNestCriticalCyclingControlMessage( int groupId,
                                         int startTime,
                                         int controlDuration );

    void streamInto( cms::StreamMessage & message ) const;

private:

    int _groupId;
    int _startTime;
    int _stopTime;
};

///

class IM_EX_MSG LMNestStandardCyclingControlMessage : public StreamableMessage
{
public:

    LMNestStandardCyclingControlMessage( int groupId,
                                         int startTime,
                                         int controlDuration,
                                         int prepOption,
                                         int peakOption,
                                         int postOption );

    void streamInto( cms::StreamMessage & message ) const;

private:

    int _groupId;
    int _startTime;
    int _stopTime;
    int _prepOption;
    int _peakOption;
    int _postOption;
};

}
}
}

