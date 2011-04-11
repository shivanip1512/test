#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace LoadManagement {

class IM_EX_MSG LMSepControlMessage : public StreamableMessage
{
    int             _groupId;
    unsigned int    _utcStartTime;
    unsigned short  _controlMinutes;
    unsigned char   _criticality;
    unsigned char   _coolTempOffset;
    unsigned char   _heatTempOffset;
    short           _coolTempSetpoint;
    short           _heatTempSetpoint;
    char            _averageCyclePercent;
    unsigned char   _standardCyclePercent;
    unsigned char   _eventFlags;

public:

    LMSepControlMessage(int            groupId,
                        unsigned int   utcStartTime,
                        unsigned short controlMinutes,
                        unsigned char  criticality,
                        unsigned char  coolTempOffset,
                        unsigned char  heatTempOffset,
                        short          coolTempSetpoint,
                        short          heatTempSetpoint,
                        char           averageCyclePercent,
                        unsigned char  standardCyclePercent,
                        unsigned char  eventFlags);

    void streamInto(cms::StreamMessage &message) const;
};


}
}
}
