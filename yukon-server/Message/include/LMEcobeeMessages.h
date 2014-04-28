#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti            {
namespace Messaging      {
namespace LoadManagement {

class IM_EX_MSG LMEcobeeCyclingControlMessage : public StreamableMessage
{
public:

    LMEcobeeCyclingControlMessage( int  groupId,
                                   int  dutyCycle,
                                   int  startTime,
                                   int  controlDuration,
                                   bool rampIn,
                                   bool rampOut );

    void streamInto( cms::StreamMessage & message ) const;

private:

    enum RampOptionFlags
    {
        RampOut = ( 1 << 0 ),
        RampIn  = ( 1 << 1 )
    };

    int  _groupId;
    char _rampingOption;
    char _dutyCycle;
    int  _startTime;
    int  _stopTime;
};

///

class IM_EX_MSG LMEcobeeRestoreMessage : public StreamableMessage
{
public:

    LMEcobeeRestoreMessage( int groupId,
                            int restoreTime );

    void streamInto( cms::StreamMessage & message ) const;

private:

    int _groupId;
    int _restoreTime;
};

}
}
}

