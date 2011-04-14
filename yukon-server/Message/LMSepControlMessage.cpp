
#include "yukon.h"

#include "LMSepControlMessage.h"

#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>

namespace Cti {
namespace Messaging {
namespace LoadManagement {

LMSepControlMessage::LMSepControlMessage(int            groupId,
                                         unsigned int   utcStartTime,
                                         unsigned short controlMinutes,
                                         unsigned char  criticality,
                                         unsigned char  coolTempOffset,
                                         unsigned char  heatTempOffset,
                                         short          coolTempSetpoint,
                                         short          heatTempSetpoint,
                                         char           averageCyclePercent,
                                         unsigned char  standardCyclePercent,
                                         unsigned char  eventFlags) :
_groupId(groupId),
_utcStartTime(utcStartTime),
_controlMinutes(controlMinutes),
_criticality(criticality),
_coolTempOffset(coolTempOffset),
_heatTempOffset(heatTempOffset),
_coolTempSetpoint(coolTempSetpoint),
_heatTempSetpoint(heatTempSetpoint),
_averageCyclePercent(averageCyclePercent),
_standardCyclePercent(standardCyclePercent),
_eventFlags(eventFlags)
{
}

LMSepControlMessage::LMSepControlMessage(int            groupId,
                                         unsigned int   utcStartTime,
                                         unsigned short controlMinutes,
                                         unsigned char  criticality,
                                         unsigned char  coolTempOffset,
                                         unsigned char  heatTempOffset,
                                         unsigned char  eventFlags) :
_groupId(groupId),
_utcStartTime(utcStartTime),
_controlMinutes(controlMinutes),
_criticality(criticality),
_coolTempOffset(coolTempOffset),
_heatTempOffset(heatTempOffset),
_eventFlags(eventFlags),

//The below are not needed
_coolTempSetpoint(SEPSetPointUnused),
_heatTempSetpoint(SEPSetPointUnused),
_averageCyclePercent(SEPAverageCycleUnused),
_standardCyclePercent(SEPStandardCycleUnused)
{
}

void LMSepControlMessage::streamInto(cms::StreamMessage &message) const
{
    message.writeInt  (_groupId);
    message.writeInt  (_utcStartTime);
    message.writeShort(_controlMinutes);
    message.writeByte (_criticality);
    message.writeByte (_coolTempOffset);
    message.writeByte (_heatTempOffset);
    message.writeShort(_coolTempSetpoint);
    message.writeShort(_heatTempSetpoint);
    message.writeByte (_averageCyclePercent);
    message.writeByte (_standardCyclePercent);
    message.writeByte (_eventFlags);
}

}
}
}
