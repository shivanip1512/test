#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace LoadManagement {

class IM_EX_MSG LMHoneywellCyclingControlMessage : public StreamableMessage
{
public:

    LMHoneywellCyclingControlMessage( const int  programId,
                                      const int  groupId,
                                      const int  dutyCycle,
                                      const int  startTime,
                                      const int  controlDuration,
                                      const bool rampInOut );

    void streamInto(cms::StreamMessage & message) const;

private:

    int  _programId;
    int  _groupId;
    bool _rampingOption;
    char _dutyCycle;
    int  _startTime;
    int  _stopTime;
};

class IM_EX_MSG LMHoneywellRestoreMessage : public StreamableMessage
{
public:

    LMHoneywellRestoreMessage( const int groupId,
                               const int restoreTime );

    void streamInto(cms::StreamMessage & message) const;

private:

    int _groupId;
    int _restoreTime;
};

}
}
}

