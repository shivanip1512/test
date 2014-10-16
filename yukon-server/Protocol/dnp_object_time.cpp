/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_time
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2006/06/23 19:59:00 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "dnp_object_time.h"
#include "logger.h"
#include "cparms.h"

using std::endl;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

Time::Time(int variation) :
    Object(Group, variation),
    _seconds(0.0),
    _milliseconds(0.0),
    _interval(0.0)
{
}


Time::Time(int group, int variation) :
    Object(group, variation),
    _seconds(0.0),
    _milliseconds(0.0),
    _interval(0.0)
{
}


double Time::getSeconds() const
{
    return _seconds;
}


double Time::getMilliseconds() const
{
    return _milliseconds;
}


void Time::setSeconds( double seconds )
{
    _seconds = seconds;
}


void Time::setMilliseconds( double millis )
{
    _milliseconds = millis;
}


int Time::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    const int serializedLen = getSerializedLen();

    if( len >= serializedLen )
    {
        pos = restoreVariation(buf, len, getVariation());
    }
    else
    {
        CTILOG_ERROR(dout, "len < serializedLen ("<< len <<" < "<< serializedLen <<")");

        _valid = false;
        pos = len;
    }

    return pos;
}


int Time::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case T_TimeAndDate:
        {
            double tmp = 0.0;

            for( int i = 5; i >= 0; i-- )
            {
                tmp *= 256.0;
                tmp += (double)(buf[pos+i]);
            }

            pos += 6;

            _seconds      = floor(tmp / 1000.0);
            _milliseconds = tmp - (_seconds * 1000.0);

            break;
        }

        case T_TimeAndDateWithInterval:
        {
            //  restore the time block first
            pos += restoreVariation(buf, len, T_TimeAndDate);

            //  then restore the interval
            _interval = 0.0;

            for( int i = 3; i >= 0; i-- )
            {
                _interval *= 256.0;
                _interval += (double)(buf[pos+i]);
            }

            pos += 4;

            break;
        }
    }

    return pos;
}


int Time::serialize(unsigned char *buf) const
{
    return serializeVariation(buf, getVariation());
}

int Time::serializeVariation(unsigned char *buf, int variation) const
{
    int pos = 0;

    switch(variation)
    {
        case T_TimeAndDate:
        {
            double tmp;
            long tmpHi, tmpLo;

            tmp = _seconds;
            tmp *= 1000.0;
            tmp += _milliseconds;

            //  tmp is a 48-bit value...  i must crop it into two 24-bit values for ease
            //    (because double has a 53-bit mantissa, it holds these values losslessly and with ease.)

            tmpHi = tmp / (256.0 * 256.0 * 256.0);
            tmpLo = tmp - (tmpHi * (256.0 * 256.0 * 256.0));

            buf[pos++] =  tmpLo        & 0xff;
            buf[pos++] = (tmpLo >>  8) & 0xff;
            buf[pos++] = (tmpLo >> 16) & 0xff;

            buf[pos++] =  tmpHi        & 0xff;
            buf[pos++] = (tmpHi >>  8) & 0xff;
            buf[pos++] = (tmpHi >> 16) & 0xff;

            break;
        }

        case T_TimeAndDateWithInterval:
        {
            long tmp;

            //  serialize the time block first
            pos += serializeVariation(buf, T_TimeAndDate);

            tmp = _interval;

            buf[pos++] =  tmp        & 0xff;
            buf[pos++] = (tmp >>  8) & 0xff;
            buf[pos++] = (tmp >> 16) & 0xff;
            buf[pos++] = (tmp >> 24) & 0xff;

            break;
        }
    }

    return pos;
}


int Time::getSerializedLen(void) const
{
    int retVal = 0;

    switch(getVariation())
    {
        case T_TimeAndDate:
        {
            retVal = 6;

            break;
        }

        case T_TimeAndDateWithInterval:
        {
            retVal = 10;

            break;
        }
    }

    return retVal;
}


TimeCTO::TimeCTO(int variation) : Time(Group, variation)
{

}


TimeCTO::TimeCTO(const TimeCTO &src) : Time(Group, src.getVariation())
{
    setSeconds(src.getSeconds());
    setMilliseconds(src.getMilliseconds());
}


int TimeCTO::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    const int variation = getVariation();

    switch(variation)
    {
        case TC_TimeAndDateCTO:
        case TC_TimeAndDateCTOUnsynchronized:
        {
            pos += restoreVariation(buf, len, T_TimeAndDate);
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");

            _valid = false;
            pos = len;
        }
    }

    return pos;
}

int TimeCTO::serialize(unsigned char *buf) const
{
    int pos = 0;

    switch( getVariation() )
    {
        case TC_TimeAndDateCTO:
        case TC_TimeAndDateCTOUnsynchronized:
        {
            pos += serializeVariation(buf, T_TimeAndDate);
            break;
        }
    }

    return pos;
}

int TimeCTO::getSerializedLen(void) const
{
    int retVal = 0;

    switch( getVariation() )
    {
        case TC_TimeAndDateCTO:
        case TC_TimeAndDateCTOUnsynchronized:
        {
            retVal = 6;

            break;
        }
    }

    return retVal;
}


TimeDelay::TimeDelay(int variation) :
    Object(Group, variation),
    _delay(0.0)
{

}


double TimeDelay::getSeconds() const
{
    double retVal;

    if( _variation == TD_Fine )
    {
        retVal = _delay / 1000.0;
    }
    else
    {
        retVal = _delay;
    }

    return retVal;
}


double TimeDelay::getMilliseconds() const
{
    double retVal;

    if( _variation == TD_Fine )
    {
        retVal = _delay % 1000;
    }
    else
    {
        retVal = 0;
    }

    return retVal;
}


int TimeDelay::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    if( len >= 2 )
    {
        _delay  = buf[pos++];
        _delay |= buf[pos++] << 8;
    }

    return pos;
}

int TimeDelay::serialize(unsigned char *buf) const
{
    return 0;
}

int TimeDelay::getSerializedLen(void) const
{
    return 0;
}

}
}
}

