#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace LoadManagement {

class IM_EX_MSG LMSepControlMessage : public StreamableMessage
{
public:
    using MsgPtr = std::unique_ptr<LMSepControlMessage>;

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

    LMSepControlMessage();

    // Helper method to fill out the standard fields.
    static MsgPtr createMessage( int groupId,
                                 unsigned int utcStartTime,
                                 unsigned short controlMinutes,
                                 unsigned char criticality,
                                 bool useRampIn,
                                 bool useRampOut );

public:
    // Creates a new LMSepControLMessage properly configured for cycling control.
    // Ownership of the created message is given to the caller.
    static MsgPtr createCycleMessage( int            groupId,
                                      unsigned int   utcStartTime,
                                      unsigned short controlMinutes,
                                      unsigned char  criticality,
                                      char           cyclePercent,
                                      bool           isTrueCycle,
                                      bool           useRampIn,
                                      bool           useRampOut );

    // Creates a new LMSepControLMessage properly configured for temp offset control.
    // Ownership of the created message is given to the caller.
    static MsgPtr createTempOffsetMessage( int            groupId,
                                           unsigned int   utcStartTime,
                                           unsigned short controlMinutes,
                                           unsigned char  criticality,
                                           unsigned char  tempOffset,
                                           bool           isCoolOffset,
                                           bool           useRampIn,
                                           bool           useRampOut );

    static MsgPtr createSimpleShedMessage( int            groupId,
                                           unsigned int   utcStartTime,
                                           unsigned short controlMinutes );

    void streamInto(cms::StreamMessage &message) const;
};


}
}
}
