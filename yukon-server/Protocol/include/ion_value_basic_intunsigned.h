#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_BASIC_INTUNSIGNED_H__
#define __ION_VALUE_BASIC_INTUNSIGNED_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_intunsigned.h
 *
 * Class:  CtiIONUnsignedInt
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_basic_numeric.h"

class IM_EX_PROT CtiIONUnsignedInt : public CtiIONNumeric
{
public:

    CtiIONUnsignedInt( unsigned int initialValue );
    CtiIONUnsignedInt( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONUnsignedInt( );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

    CtiIONUnsignedInt &setValue( unsigned int value );
    unsigned int       getValue( void );

private:

    unsigned int _unsignedInt;
};


#endif  //  #ifndef __ION_VALUE_BASIC_INTUNSIGNED_H__

