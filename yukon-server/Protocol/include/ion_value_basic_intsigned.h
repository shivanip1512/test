#pragma warning( disable : 4786 )

#ifndef __ION_VALUE_BASIC_INTSIGNED_H__
#define __ION_VALUE_BASIC_INTSIGNED_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasictypes.h
 *
 * Class:  CtiIONSignedInt
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_basic_numeric.h"
#include "ctitypes.h"

class IM_EX_PROT CtiIONSignedInt : public CtiIONNumeric
{
public:

    CtiIONSignedInt( long initialValue );
    CtiIONSignedInt( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONSignedInt( );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

    CtiIONSignedInt &setValue( long value );
    long             getValue( void );

    double           getNumericValue( void );

private:

    long _signedInt;
};

#endif  //  #ifndef __ION_VALUE_BASIC_INTSIGNED_H__

