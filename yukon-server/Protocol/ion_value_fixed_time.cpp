/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_time.cpp
 *
 * Class:  CtiIONTime
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_fixed_time.h"

#include "logger.h"


CtiIONTime::CtiIONTime( unsigned char *buf, unsigned long len ) :
    CtiIONValueFixed(Fixed_Time)
{
    //  all i know about is 8-byte times
    if( len == 8 )
    {
        //  doggoned MSB ordering

        //  _seconds is bits 62-31
        ((unsigned char *)&_seconds)[3] = buf[0] & 0x7f;
        ((unsigned char *)&_seconds)[2] = buf[1];
        ((unsigned char *)&_seconds)[1] = buf[2];
        ((unsigned char *)&_seconds)[0] = buf[3];
        _seconds <<= 1;
        _seconds |= (buf[4] & 0x80) >> 7;

        //  _fractionalSeconds is bits 30-0
        ((unsigned char *)&_fractionalSeconds)[3] = buf[4] & 0x7f;
        ((unsigned char *)&_fractionalSeconds)[2] = buf[5];
        ((unsigned char *)&_fractionalSeconds)[1] = buf[6];
        ((unsigned char *)&_fractionalSeconds)[0] = buf[7];

        setValid(true);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        setValid(false);
    }
}


CtiIONTime::CtiIONTime( unsigned long seconds, unsigned long fractionalSeconds ) :
    CtiIONValueFixed(Fixed_Time)
{
    setSeconds(seconds);
    setFractionalSeconds(fractionalSeconds);
}


CtiIONTime::~CtiIONTime( )
{

}


CtiIONTime &CtiIONTime::setSeconds( unsigned long value )
{
    _seconds = value;
    return *this;
}


unsigned long CtiIONTime::getSeconds( void ) const
{
    return _seconds;
}


CtiIONTime &CtiIONTime::setFractionalSeconds( unsigned long value )
{
    //  copy the bits that're less than 2^31 - we only have 31 bits
    _fractionalSeconds = value & 0x7FFFFFFF;
    return *this;
}


unsigned long CtiIONTime::getFractionalSeconds( void ) const
{
    return _fractionalSeconds;
}


unsigned long CtiIONTime::getMilliseconds( void ) const
{
    return (_fractionalSeconds / MillisecondsToFractionalMultiplier);
}


unsigned int CtiIONTime::getSerializedValueLength( void ) const
{
    return 8;
}


void CtiIONTime::putSerializedValue( unsigned char *buf ) const
{
    unsigned long tmpSeconds;

    //  shift bits down, high bit (31) defaulted to 0
    tmpSeconds = _seconds >> 1;

    buf[0] = ((unsigned char *)&tmpSeconds)[3];
    buf[1] = ((unsigned char *)&tmpSeconds)[2];
    buf[2] = ((unsigned char *)&tmpSeconds)[1];
    buf[3] = ((unsigned char *)&tmpSeconds)[0];

    //  note that the low bit of _seconds (shifted into the aether above) is tacked on as the high bit of _fractionalSeconds
    tmpSeconds = ((_seconds & 0x01) << 31) | (_fractionalSeconds & 0x7fffffff);

    buf[4] = ((unsigned char *)&tmpSeconds)[3];
    buf[5] = ((unsigned char *)&tmpSeconds)[2];
    buf[6] = ((unsigned char *)&tmpSeconds)[1];
    buf[7] = ((unsigned char *)&tmpSeconds)[0];
}

