#pragma warning( disable : 4786 )

#ifndef __ION_VALUEBASIC_TIME_H__
#define __ION_VALUEBASIC_TIME_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasictypes.h
 *
 * Class:  CtiION*
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         Basic value type classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_rootclasses.h"
#include "ctitypes.h"

class IM_EX_PROT CtiIONTime : public CtiIONValue
{
public:

    CtiIONTime( unsigned long seconds, unsigned long fractionalSeconds );
    CtiIONTime( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONTime( );

    CtiIONTime    &setSeconds( unsigned long value );
    unsigned long  getSeconds( void );
    CtiIONTime    &setFractionalSeconds( unsigned long value );
    unsigned long  getFractionalSeconds( void );

protected:

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

private:

    unsigned long _seconds;
    unsigned long _fractionalSeconds;
};


#endif  //  #ifndef __ION_VALUEBASIC_TIME_H__


