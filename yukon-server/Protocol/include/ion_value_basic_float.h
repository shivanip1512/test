#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_BASIC_FLOAT_H__
#define __ION_VALUE_BASIC_FLOAT_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_float.h
 *
 * Class:  CtiIONFloat
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_basic_numeric.h"

class IM_EX_PROT CtiIONFloat : public CtiIONNumeric
{
public:

    CtiIONFloat( float initialValue );
    CtiIONFloat( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONFloat( );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

    CtiIONFloat &setValue( float value );
    float        getValue( void );

private:

    float _float;
};


#endif  //  #ifndef __ION_VALUE_BASIC_FLOAT_H__

