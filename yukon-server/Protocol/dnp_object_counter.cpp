#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_counter
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/16 13:57:43 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_object_counter.h"
#include "logger.h"

CtiDNPCounter::CtiDNPCounter(int variation) : CtiDNPObject(Group, variation)
{

}


int CtiDNPCounter::restore(unsigned char *buf, int len)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int CtiDNPCounter::serialize(unsigned char *buf)
{
    return 0;
}


int CtiDNPCounter::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case Binary32Bit:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << " Single-bit packed binary output not supported yet " << endl;
            }

            retVal = 0;

            break;
        }

        case Binary32BitNoFlag:
        {
            retVal = 1;

            break;
        }
    }

    return retVal;
}

CtiDNPCounterChange::CtiDNPCounterChange(int variation) : CtiDNPObject(Group, variation)
{

}

CtiDNPCounterFrozen::CtiDNPCounterFrozen(int variation) : CtiDNPObject(Group, variation)
{

}

CtiDNPCounterFrozenEvent::CtiDNPCounterFrozenEvent(int variation) : CtiDNPObject(Group, variation)
{

}

