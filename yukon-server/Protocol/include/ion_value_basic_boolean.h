#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_BASIC_BOOLEAN_H__
#define __ION_VALUE_BASIC_BOOLEAN_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_boolean.h
 *
 * Class:  CtiIONBoolean
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         Basic value type classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_basic_numeric.h"

class IM_EX_PROT CtiIONBoolean : public CtiIONNumeric
{
public:

    CtiIONBoolean( bool initialValue );
    CtiIONBoolean( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONBoolean( );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

    CtiIONBoolean &setValue( bool value );
    bool           getValue( void );

    double         getNumericValue( void );

private:

    bool _bool;
};


#endif  //  #ifndef __ION_VALUE_BASIC_BOOLEAN_H__

