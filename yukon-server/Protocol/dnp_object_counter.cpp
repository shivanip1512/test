#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_counter
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/12/21 17:20:45 $
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
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0;
            break;
        }
    }

    return retVal;
}


CtiPointDataMsg *CtiDNPCounter::getPoint( void )
{
    CtiPointDataMsg *tmpMsg;

    double val = 0;
    int quality;

/*    switch(getVariation())
    {
        case WithStatus:
        {
            //  fall through
        }
        case SingleBitPacked:
        {
            val = _bi.flags.state;
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }*/

/*    UnintializedQuality = 0,
    InitDefaultQuality,
    InitLastKnownQuality,
    NonUpdatedQuality,
    ManualQuality,
    NormalQuality,
    ExceedsLowQuality,
    ExceedsHighQuality,
    AbnormalQuality,
    UnknownQuality,
    InvalidQuality,
    PartialIntervalQuality,
    DeviceFillerQuality,
    QuestionableQuality,
    OverflowQuality,
    PowerfailQuality,
    UnreasonableQuality

    if( _flags.aiflags.remoteforced )
    {

    }*/

    if( gDNPVerbose )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Counter object, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, PulseAccumulatorPointType);

    return tmpMsg;
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

