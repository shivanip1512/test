#pragma once

#include "ctitime.h"
#include "pointtypes.h"  //  for SYS_PID_CAPCONTROL

#include <boost/optional.hpp>

#include <string>
#include <vector>

namespace Cti {
namespace CapControl {

struct EventLogEntry
{
    EventLogEntry(
        long logId_, long pointId_, long spAreaId_, long areaId_, long stationId_, long subId_, long feederId_,
        long eventType_, long seqId_, long value_, std::string text_, std::string userName_,
        double kvarBefore_ = 0, double kvarAfter_ = 0, double kvarChange_ = 0,
        std::string ipAddress_ = "(N/A)", long actionId_ = -1, std::string stateInfo_ = "(N/A)",
        double aVar_ = 0, double bVar_ = 0, double cVar_ = 0,  int regulatorId_ = 0) :
        pointId(pointId_), spAreaId(spAreaId_), areaId(areaId_),
        stationId(stationId_), subId(subId_), feederId(feederId_), eventType(eventType_), seqId(seqId_),
        value(value_), text(text_), userName(userName_), kvarBefore(kvarBefore_), kvarAfter(kvarAfter_), kvarChange(kvarChange_), ipAddress(ipAddress_),
        actionId(actionId_), stateInfo(stateInfo_), aVar(aVar_), bVar(bVar_), cVar(cVar_), regulatorId(regulatorId_)
    {
    }

    EventLogEntry(std::string text_, int regulatorId_ = 0, long eventType_ = -1) :
        userName("cap control"), text(text_), pointId(SYS_PID_CAPCONTROL),
        spAreaId(0), areaId(0), stationId(0), subId(0), feederId(0), eventType(eventType_),
        seqId(0), value(0), kvarBefore(0), kvarAfter(0), kvarChange(0), ipAddress("(N/A)"),
        actionId(0), stateInfo("(N/A)"), aVar(0), bVar(0), cVar(0), regulatorId(regulatorId_)
    {
    }

    void setActionId(long actionId_)
    {  actionId = actionId_;  }
    void setStateInfo(std::string stateInfo_)
    {  stateInfo = stateInfo_;  }

    void setEventSubtype(const long eventSubtype_)
    {
        eventSubtype.reset( eventSubtype_ );
    }

    CtiTime timeStamp;
    long pointId;
    long spAreaId;
    long areaId;
    long stationId;
    long subId;
    long feederId;
    long eventType;
    long seqId;
    long value;
    std::string text;
    std::string userName;
    double kvarBefore;
    double kvarAfter;
    double kvarChange;
    std::string ipAddress;
    long actionId;
    std::string stateInfo;
    int regulatorId;

    double aVar;
    double bVar;
    double cVar;

    boost::optional<long> eventSubtype;
    
};

typedef std::vector<EventLogEntry> EventLogEntries;

}
}
