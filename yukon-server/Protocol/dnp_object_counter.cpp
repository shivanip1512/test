/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_counter
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2003/12/26 17:27:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "dnp_object_counter.h"
#include "logger.h"

CtiDNPCounter::CtiDNPCounter(int variation) :
    CtiDNPObject(Group, variation)
{

}


CtiDNPCounter::CtiDNPCounter(int group, int variation) :
    CtiDNPObject(group, variation)
{

}


int CtiDNPCounter::restore(unsigned char *buf, int len)
{
    //  ACH:  check minimum length, like the others
    return restoreVariation(buf, len, getVariation());
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


int CtiDNPCounter::restoreVariation(unsigned char *buf, int len, int variation)
{
    int pos = 0;

    _valid = true;

    switch( variation )
    {
        case Binary32Bit:
        case Delta32Bit:
        {
            _flag = buf[pos++];

            //  fall through
        }
        case Binary32BitNoFlag:
        case Delta32BitNoFlag:
        {
            unsigned char *val = (unsigned char *)&_counter;

            val[0] = buf[pos++];
            val[1] = buf[pos++];
            val[2] = buf[pos++];
            val[3] = buf[pos++];

            break;
        }

        case Binary16Bit:
        case Delta16Bit:
        {
            _flag = buf[pos++];

            //  fall through
        }
        case Binary16BitNoFlag:
        case Delta16BitNoFlag:
        {
            unsigned char *val = (unsigned char *)&_counter;

            val[0] = buf[pos++];
            val[1] = buf[pos++];
            val[2] = 0;
            val[3] = 0;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            pos = len;

            break;
        }
    }

    return pos;
}


int CtiDNPCounter::serializeVariation(unsigned char *buf, int variation)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return 0;
}


CtiPointDataMsg *CtiDNPCounter::getPoint( const CtiDNPTimeCTO *cto )
{
    CtiPointDataMsg *tmpMsg;

    double val = 0;
    int quality;

    switch(getVariation())
    {
        case Binary32Bit:
        case Binary32BitNoFlag:
        case Binary16Bit:
        case Binary16BitNoFlag:
        case Delta32Bit:
        case Delta32BitNoFlag:
        case Delta16Bit:
        case Delta16BitNoFlag:
        {
            val = _counter;

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
    }

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

