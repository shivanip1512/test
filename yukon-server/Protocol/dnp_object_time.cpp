/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_time
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_time.h"
#include "logger.h"

CtiDNPTime::CtiDNPTime(int variation) : CtiDNPObject(Group, variation)
{
    _seconds      = 0.0;
    _milliseconds = 0.0;
}


CtiDNPTime::CtiDNPTime(int group, int variation) : CtiDNPObject(group, variation)
{
    _seconds      = 0.0;
    _milliseconds = 0.0;
}


double CtiDNPTime::getSeconds() const
{
    return _seconds;
}


double CtiDNPTime::getMilliseconds() const
{
    return _milliseconds;
}


void CtiDNPTime::setSeconds( double seconds )
{
    _seconds = seconds;
}


void CtiDNPTime::setMilliseconds( double millis )
{
    _milliseconds = millis;
}


int CtiDNPTime::restore(unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    if( len >= getSerializedLen() )
    {
        pos = restoreVariation(buf, len, getVariation());
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _valid = false;
        pos = len;
    }

    return pos;
}


int CtiDNPTime::restoreVariation(unsigned char *buf, int len, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case TimeAndDate:
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

        case TimeAndDateWithInterval:
        {
            //  restore the time block first
            pos += restoreVariation(buf, len, CtiDNPTime::TimeAndDate);

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


int CtiDNPTime::serialize(unsigned char *buf)
{
    return serializeVariation(buf, getVariation());
}

int CtiDNPTime::serializeVariation(unsigned char *buf, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case TimeAndDate:
        {
            double tmp;
            long tmpHi, tmpLo;

            tmp  = _seconds * 1000.0;
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

        case TimeAndDateWithInterval:
        {
            long tmp;

            //  serialize the time block first
            pos += serializeVariation(buf, CtiDNPTime::TimeAndDate);

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


int CtiDNPTime::getSerializedLen(void)
{
    int retVal = 0;

    switch(getVariation())
    {
        case TimeAndDate:
        {
            retVal = 6;

            break;
        }

        case TimeAndDateWithInterval:
        {
            retVal = 10;

            break;
        }
    }

    return retVal;
}


CtiDNPTimeCTO::CtiDNPTimeCTO(int variation) : CtiDNPTime(Group, variation)
{

}

int CtiDNPTimeCTO::restore(unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    switch( getVariation() )
    {
        case TimeAndDateCTO:
        case TimeAndDateCTOUnsynchronized:
        {
            pos += restoreVariation(buf,len,CtiDNPTime::TimeAndDate);
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
        }
    }

    return pos;
}

int CtiDNPTimeCTO::serialize(unsigned char *buf)
{
    int pos = 0;

    switch( getVariation() )
    {
        case TimeAndDateCTO:
        case TimeAndDateCTOUnsynchronized:
        {
            pos += serializeVariation(buf, CtiDNPTime::TimeAndDate);
            break;
        }
    }

    return pos;
}

int CtiDNPTimeCTO::getSerializedLen(void)
{
    int retVal = 0;

    switch( getVariation() )
    {
        case TimeAndDateCTO:
        case TimeAndDateCTOUnsynchronized:
        {
            retVal = 6;

            break;
        }
    }

    return retVal;
}


CtiDNPTimeDelay::CtiDNPTimeDelay(int variation) : CtiDNPObject(Group, variation)
{

}


double CtiDNPTimeDelay::getSeconds() const
{
    double retVal;

    if( _variation == Fine )
    {
        retVal = _delay / 1000;
    }
    else
    {
        retVal = _delay;
    }

    return retVal;
}


double CtiDNPTimeDelay::getMilliseconds() const
{
    double retVal;

    if( _variation == Fine )
    {
        retVal = _delay % 1000;
    }
    else
    {
        retVal = 0;
    }

    return retVal;
}


int CtiDNPTimeDelay::restore(unsigned char *buf, int len)
{
    int pos = 0;

    if( len >= 2 )
    {
        _delay  = buf[pos++];
        _delay |= buf[pos++] << 8;
    }

    return pos;
}

int CtiDNPTimeDelay::serialize(unsigned char *buf)
{
    return 0;
}

int CtiDNPTimeDelay::getSerializedLen(void)
{
    return 0;
}

