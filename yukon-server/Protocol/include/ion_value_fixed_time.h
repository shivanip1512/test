#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_FIXED_TIME_H__
#define __ION_VALUE_FIXED_TIME_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_time.h
 *
 * Class:  CtiION*
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_fixed.h"

class CtiIONTime : public CtiIONValueFixed
{
private:

    unsigned long _seconds;
    unsigned long _fractionalSeconds;

protected:

    friend CtiIONValueFixed;

    CtiIONTime( unsigned char *byteStream, unsigned long streamLength );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

public:

    CtiIONTime( unsigned long seconds, unsigned long fractionalSeconds );
    ~CtiIONTime( );

    CtiIONTime    &setSeconds( unsigned long value );
    unsigned long  getSeconds( void );
    CtiIONTime    &setFractionalSeconds( unsigned long value );
    unsigned long  getFractionalSeconds( void );
};


#endif  //  #ifndef __ION_VALUE_FIXED_TIME_H__


