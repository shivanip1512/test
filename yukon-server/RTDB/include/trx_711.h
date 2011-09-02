#pragma once

#include "trx_info.h"

class CtiTransmitter711Info : public CtiTransmitterInfo
{
public:

    HCTIQUEUE        QueueHandle;
    HCTIQUEUE        ActinQueueHandle;

    USHORT           PortQueueEnts;
    USHORT           PortQueueConts;
    USHORT           RContInLength;
    USHORT           ReadyN;
    USHORT           NCsets;
    USHORT           NCOcts;
    LONG             FreeSlots;
    USHORT           RColQMin;
    QUEENT           QueTable[MAXQUEENTRIES];
    unsigned long    LastColdStartTime;

private:

public:

    CtiTransmitter711Info(int type) :
        CtiTransmitterInfo(type),
        QueueHandle(NULL),
        ActinQueueHandle(NULL),
        PortQueueEnts(0),
        PortQueueConts(0),
        RContInLength(0),
        ReadyN(32),
        NCsets(0),
        NCOcts(0),
        FreeSlots(MAXQUEENTRIES),
        RColQMin(0),
        LastColdStartTime(0)
    {
        for(int i = 0; i < MAXQUEENTRIES; i++)
        {
            QueTable[i].InUse = 0;                // Not in use.
            QueTable[i].TimeSent = -1L;           // Yes, this is odd, but sets it always to MAX_ULONG.  Whatever that is bitwise.
        }
    }

    CtiTransmitter711Info(const CtiTransmitter711Info& aRef)
    {
       *this = aRef;
    }

    virtual ~CtiTransmitter711Info()
    {
        CloseQueue(QueueHandle);
        CloseQueue(ActinQueueHandle);
    }


    CtiTransmitter711Info& operator=(const CtiTransmitter711Info& aRef)
    {
        if( this != &aRef )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
        }

        return *this;
    }

    CtiTransmitter711Info& reduceEntsConts(BOOL rcontstoo)
    {
        //  Decrease the queue entry count
        if( PortQueueEnts > 0 )
        {
            PortQueueEnts--;
        }

        //  If this is an RCONT compatible command decrease said count
        if( rcontstoo && PortQueueConts > 0 )
        {
            PortQueueConts--;
        }

        return *this;
    }

    unsigned long getLastColdStartTime()
    {
        return LastColdStartTime;
    }

    void setLastColdStartTime(unsigned long time)
    {
        LastColdStartTime = time;
    }

};
