
#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"


namespace Cti::Messaging::LoadManagement {

class IM_EX_MSG LMItronCyclingControlMessage : public StreamableMessage
{
public:

    LMItronCyclingControlMessage( int groupId,
                                  long long startTime,
                                  int controlDuration,
                                  bool rampInOption,
                                  bool rampOutOption,
                                  long dutyCyclePercent,
                                  long dutyCyclePeriod,
                                  long criticality );

    void streamInto(cms::StreamMessage & message) const;

private:

    int _groupId;
    long long _startTime;
    long long _stopTime;
    bool _rampIn;
    bool _rampOut;
    long _dutyCyclePercent;
    long _dutyCyclePeriod;
    long _criticality;
};

class IM_EX_MSG LMItronRestoreMessage : public StreamableMessage
{
public:

    LMItronRestoreMessage( int groupId,
                           long long restoreTime );

    void streamInto(cms::StreamMessage & message) const;

private:

    int _groupId;
    long long _restoreTime;
};

}

