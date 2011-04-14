#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace LoadManagement {

class IM_EX_MSG LMSepControlMessage : public StreamableMessage
{
private:
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

    // Constructor for any control
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

    // Constructor for temp offset control
    LMSepControlMessage(int            groupId,
                        unsigned int   utcStartTime,
                        unsigned short controlMinutes,
                        unsigned char  criticality,
                        unsigned char  coolTempOffset,
                        unsigned char  heatTempOffset,
                        unsigned char  eventFlags);

    void streamInto(cms::StreamMessage &message) const;

    static const char  SEPAverageCycleUnused  = 0x80;
    static const char  SEPStandardCycleUnused = 0xFF;
    static const short SEPSetPointUnused      = 0x8000;
    static const char  SEPTempOffsetUnused    = 0xFF;
    // Events are a bitfield.
    static const char  SEPEventRandomizeStart = 0x01;
    static const char  SEPEventRandomizeStop  = 0x02;
};


}
}
}
