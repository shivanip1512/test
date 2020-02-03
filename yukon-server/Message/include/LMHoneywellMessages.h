#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti::Messaging::LoadManagement
{

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

class IM_EX_MSG LMHoneywellSetpointControlMessage : public StreamableMessage
{
public:

    LMHoneywellSetpointControlMessage( const int  programId,
                                       const int  groupId,
                                       const bool temperatureOption,
                                       const bool mandatory,
                                       const int  temperatureOffset,
                                       const long long startTime,
                                       const int  controlDuration );

    void streamInto(cms::StreamMessage & message) const;

private:

    int  _programId;
    int  _groupId;
    char _temperatureOption;
    char _mandatory;
    int  _temperatureOffset;
    long long _startTime;
    long long _stopTime;
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

