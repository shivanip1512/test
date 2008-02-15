/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_counter
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2008/02/15 21:08:15 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_counter.h"
#include "logger.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

Counter::Counter(int variation) :
    Object(Group, variation)
{

}


Counter::Counter(int group, int variation) :
    Object(group, variation)
{

}


int Counter::restore(const unsigned char *buf, int len)
{
    //  ACH:  check minimum length, like the others
    return restoreVariation(buf, len, getVariation());
}


int Counter::serialize(unsigned char *buf) const
{
    return 0;
}


int Counter::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0;

            break;
        }
    }

    return retVal;
}


int Counter::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    _valid = true;

    switch( variation )
    {
        case C_Binary32Bit:
        case C_Delta32Bit:
        {
            _flag = buf[pos++];

            //  fall through
        }
        case C_Binary32BitNoFlag:
        case C_Delta32BitNoFlag:
        {
            unsigned char *val = (unsigned char *)&_counter;

            val[0] = buf[pos++];
            val[1] = buf[pos++];
            val[2] = buf[pos++];
            val[3] = buf[pos++];

            break;
        }

        case C_Binary16Bit:
        case C_Delta16Bit:
        {
            _flag = buf[pos++];

            //  fall through
        }
        case C_Binary16BitNoFlag:
        case C_Delta16BitNoFlag:
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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            pos = len;

            break;
        }
    }

    return pos;
}


int Counter::serializeVariation(unsigned char *buf, int variation) const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return 0;
}


CtiPointDataMsg *Counter::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val = 0;
    int quality;

    //  this used to be chosen or excluded by the variation...  but every
    //    variation returns the same data, so there's no need to differentiate...
    //    at least not for counters
    //  the child classes add to this, but the values concerned are still the same
    val = _counter;

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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Counter object, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, PulseAccumulatorPointType);

    return tmpMsg;
}



CounterEvent::CounterEvent(int variation) :
    Counter(Group, variation),
    _toc(Time::T_TimeAndDate)
{

}

int CounterEvent::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    switch(getVariation())
    {
        case CE_Binary32BitNoTime:
        case CE_Delta32BitNoTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary32Bit);
            break;
        }
        case CE_Binary16BitNoTime:
        case CE_Delta16BitNoTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            break;
        }
        case CE_Binary32BitWithTime:
        case CE_Delta32BitWithTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            pos += _toc.restore(buf + pos, len - pos);
            break;
        }
        case CE_Binary16BitWithTime:
        case CE_Delta16BitWithTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            pos += _toc.restore(buf + pos, len - pos);
            break;
        }
    }

        //  ACH:  check minimum length, like the others
    return pos;
}


int CounterEvent::serialize(unsigned char *buf) const
{
    return 0;
}


int CounterEvent::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - in CounterEvent::getSerializedLen(), function unimplemented **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0;

            break;
        }
    }

    return retVal;
}


CtiPointDataMsg *CounterEvent::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val = 0;
    int quality;

    //  inherited types just add on to the parent's values
    tmpMsg = Counter::getPoint(cto);

    switch( getVariation() )
    {
        case CE_Binary16BitWithTime:
        case CE_Delta16BitWithTime:
        case CE_Binary32BitWithTime:
        case CE_Delta32BitWithTime:
        {
            tmpMsg->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);
            tmpMsg->setTime(_toc.getSeconds());
            tmpMsg->setMillis(_toc.getMilliseconds());

            break;
        }
    }

    if( gDNPVerbose )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Counter Event object, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    //tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, PulseAccumulatorPointType);

    return tmpMsg;
}


CounterFrozen::CounterFrozen(int variation) : Counter(Group, variation)
{

}

int CounterFrozen::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    switch(getVariation())
    {
        /*
        CF_Delta32Bit                  =  3,
        CF_Delta16Bit                  =  4,
        CF_Binary32BitWithTimeOfFreeze =  5,
        CF_Binary16BitWithTimeOfFreeze =  6,
        CF_Delta32BitWithTimeOfFreeze  =  7,
        CF_Delta16BitWithTimeOfFreeze  =  8,
        CF_Delta32BitNoFlag            = 11,
        CF_Delta16BitNoFlag            = 12,
        */

        case CF_Binary32Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary32Bit);
            break;
        }
        case CF_Binary32BitNoFlag:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary32BitNoFlag);
            break;
        }
        case CF_Binary16Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            break;
        }
        case CF_Binary16BitNoFlag:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16BitNoFlag);
            break;
        }
    }

        //  ACH:  check minimum length, like the others
    return pos;
}


int CounterFrozen::serialize(unsigned char *buf) const
{
    return 0;
}


int CounterFrozen::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - in CounterFrozen::getSerializedLen(), function unimplemented **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0;

            break;
        }
    }

    return retVal;
}


CtiPointDataMsg *CounterFrozen::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val = 0;
    int quality;

    //  inherited types just add on to the parent's values
    tmpMsg = Counter::getPoint(cto);

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

    if( gDNPVerbose && tmpMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Frozen counter object, value " << tmpMsg->getValue() << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    //tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, PulseAccumulatorPointType);

    return tmpMsg;
}


CounterFrozenEvent::CounterFrozenEvent(int variation) : Object(Group, variation)
{

}

}
}
}

