
#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"


namespace Cti::Messaging::LoadManagement {

class IM_EX_MSG LMMeterDisconnectControlMessage : public StreamableMessage
{
public:

    LMMeterDisconnectControlMessage( int groupId,
                                     long long startTime,
                                     int controlDuration );

    void streamInto(cms::StreamMessage & message) const;

private:

    int _groupId;
    long long _startTime;
    long long _stopTime;
};

class IM_EX_MSG LMMeterDisconnectRestoreMessage : public StreamableMessage
{
public:

    LMMeterDisconnectRestoreMessage( int groupId,
                                     long long restoreTime );

    void streamInto(cms::StreamMessage & message) const;

private:

    int _groupId;
    long long _restoreTime;
};

}

