
#include "precompiled.h"

#include "LMSepControlMessage.h"

#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>

namespace Cti {
namespace Messaging {
namespace LoadManagement {


static const char  SEPAverageCycleUnused  = 0x80;
static const char  SEPStandardCycleUnused = 0xFF;
static const short SEPSetPointUnused      = 0x8000;
static const char  SEPTempOffsetUnused    = 0xFF;
// Events are a bitfield.
static const char  SEPEventRandomizeStart = 0x01;
static const char  SEPEventRandomizeStop  = 0x02;

LMSepControlMessage::LMSepControlMessage() :
_groupId(0),
_utcStartTime(0),
_controlMinutes(0),
_criticality(0),
_coolTempOffset(SEPTempOffsetUnused),
_heatTempOffset(SEPTempOffsetUnused),
_eventFlags(0),
_coolTempSetpoint(SEPSetPointUnused),
_heatTempSetpoint(SEPSetPointUnused),
_averageCyclePercent(SEPAverageCycleUnused),
_standardCyclePercent(SEPStandardCycleUnused)
{}

auto LMSepControlMessage::createMessage(int groupId, unsigned int utcStartTime, unsigned short controlMinutes, unsigned char criticality, bool useRampIn, bool useRampOut) -> MsgPtr
{
    std::unique_ptr<LMSepControlMessage> retVal { new LMSepControlMessage() };
    
    retVal->_groupId = groupId;
    retVal->_utcStartTime = utcStartTime;
    retVal->_controlMinutes = controlMinutes;
    retVal->_criticality = criticality;
    retVal->_eventFlags = (useRampIn ? SEPEventRandomizeStart : 0) | (useRampOut ? SEPEventRandomizeStop : 0);

    return retVal;
}

auto LMSepControlMessage::createTempOffsetMessage(int            groupId,
                                                  unsigned int   utcStartTime,
                                                  unsigned short controlMinutes,
                                                  unsigned char  criticality,
                                                  unsigned char  tempOffset,
                                                  bool           isCoolOffset,
                                                  bool           useRampIn,
                                                  bool           useRampOut) -> MsgPtr
{
    auto retVal = createMessage(groupId, utcStartTime, controlMinutes, criticality, useRampIn, useRampOut);

    if(isCoolOffset)
    {
        retVal->_coolTempOffset = tempOffset;
    }
    else
    {
        retVal->_heatTempOffset = tempOffset;
    }

    return retVal;
}

auto LMSepControlMessage::createSimpleShedMessage(int            groupId,
                                                  unsigned int   utcStartTime,
                                                  unsigned short controlMinutes) -> MsgPtr
{
    auto retVal = createMessage(groupId, utcStartTime, controlMinutes, 6, true, true);

    retVal->_standardCyclePercent = 100;

    return retVal;
}

auto LMSepControlMessage::createCycleMessage(int            groupId,
                                             unsigned int   utcStartTime,
                                             unsigned short controlMinutes,
                                             unsigned char  criticality,
                                             char           cyclePercent,
                                             bool           isTrueCycle,
                                             bool           useRampIn,
                                             bool           useRampOut) -> MsgPtr
{
    auto retVal = createMessage(groupId, utcStartTime, controlMinutes, criticality, useRampIn, useRampOut);

    if(isTrueCycle)
    {
        retVal->_averageCyclePercent = -1*cyclePercent;
    }
    else
    {
        retVal->_standardCyclePercent = cyclePercent;
    }

    return retVal;
}

void LMSepControlMessage::streamInto(cms::StreamMessage &message) const
{
    // To make the java conversion work properly, we are sending all unsigned fields using
    // a larger element. Bytes are sent as shorts, shorts as ints, etc.
    message.writeInt   (_groupId);
    message.writeLong  (_utcStartTime);
    message.writeInt   (_controlMinutes);
    message.writeShort (_criticality);
    message.writeShort (_coolTempOffset);
    message.writeShort (_heatTempOffset);
    message.writeShort (_coolTempSetpoint);
    message.writeShort (_heatTempSetpoint);
    message.writeByte  (_averageCyclePercent);
    message.writeShort (_standardCyclePercent);
    message.writeShort (_eventFlags);
}

}
}
}
