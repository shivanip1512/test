
/*-----------------------------------------------------------------------------*
 *
 * File:   ion_basictypes.cpp
 *
 * Class:  CtiIONChar, CtiIONNumeric, CtiIONFloat, CtiIONSignedInt, CtiIONUnsignedInt,
 *           CtiIONBoolean, CtiIONTime, CtiIONArray
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 *         Implementation for ION basic value types
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_basic_time.h"

#include "logger.h"


CtiIONTime::CtiIONTime( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValue(IONTime)
{
    //  all i know about is 8-byte times
    if( streamLength == 8 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_seconds)[0] = byteStream[7];
        ((unsigned char *)&_seconds)[1] = byteStream[6];
        ((unsigned char *)&_seconds)[2] = byteStream[5];
        ((unsigned char *)&_seconds)[3] = byteStream[4];

        ((unsigned char *)&_fractionalSeconds)[0] = byteStream[3];
        ((unsigned char *)&_fractionalSeconds)[1] = byteStream[2];
        ((unsigned char *)&_fractionalSeconds)[2] = byteStream[1];
        ((unsigned char *)&_fractionalSeconds)[3] = byteStream[0];

        _seconds <<= 1;
        //  the high bit of _fractionalSeconds is actually the low bit of _seconds
        _seconds |= (_fractionalSeconds & 0x80000000) >> 31;
        _fractionalSeconds &= 0x7FFFFFFF;

        setValid( TRUE );
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        setValid( FALSE );
    }
}


CtiIONTime::CtiIONTime( unsigned long seconds = 0, unsigned long fractionalSeconds = 0 ) :
    CtiIONValue(IONTime)
{
    setSeconds( seconds );
    setFractionalSeconds( fractionalSeconds );
}


CtiIONTime::~CtiIONTime( )
{
}


CtiIONTime &CtiIONTime::setSeconds( unsigned long value )
{
    _seconds = value;
    return *this;
}


unsigned long CtiIONTime::getSeconds( void )
{
    return _seconds;
}


CtiIONTime &CtiIONTime::setFractionalSeconds( unsigned long value )
{
    //  copy the bits that're less than 2^31 - we only have 31 bits
    _fractionalSeconds = value & 0x7FFFFFFF;
    return *this;
}


unsigned long CtiIONTime::getFractionalSeconds( void )
{
    return _fractionalSeconds;
}


unsigned int CtiIONTime::getSerializedValueLength( void ) const
{
    return 8;
}

void CtiIONTime::putSerializedValue( unsigned char *buf ) const
{
    unsigned long tmpSeconds;

    tmpSeconds = _fractionalSeconds | ((_seconds & 0x01) << 31);

    buf[0] = ((unsigned char *)&tmpSeconds)[3];
    buf[1] = ((unsigned char *)&tmpSeconds)[2];
    buf[2] = ((unsigned char *)&tmpSeconds)[1];
    buf[3] = ((unsigned char *)&tmpSeconds)[0];

    tmpSeconds = _seconds >> 1;  //  shift bits down, high bit (31) defaulted to 0

    buf[4] = ((unsigned char *)&tmpSeconds)[3];
    buf[5] = ((unsigned char *)&tmpSeconds)[2];
    buf[6] = ((unsigned char *)&tmpSeconds)[1];
    buf[7] = ((unsigned char *)&tmpSeconds)[0];
}

