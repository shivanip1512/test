/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_float.h
 *
 * Class:  CtiIONFloat
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __ION_VALUE_FIXED_FLOAT_H__
#define __ION_VALUE_FIXED_FLOAT_H__
#pragma warning( disable : 4786 )


#include "ion_value_fixed.h"
#include "ion_value_numeric.h"
#include "ion_value_variable_fixedarray_element.h"

class CtiIONFloat : public CtiIONValueFixed, /*public CtiIONNumeric, */public CtiIONFixedArrayElement
{
private:

    float _float;
    char  _string[20];

protected:

    unsigned int getElementLength( void ) const;
    void putElement( unsigned char *buf ) const;

    friend CtiIONValue *CtiIONValueFixed::restoreFixed( unsigned char ionClass, unsigned char classDescriptor,
                                                        unsigned char *buf, unsigned long len, unsigned long *bytesUsed );
    friend class CtiIONFixedArrayTemplate<CtiIONFloat>;

    CtiIONFloat( unsigned char *byteStream, unsigned long streamLength );

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

public:

    CtiIONFloat( float initialValue );
    virtual ~CtiIONFloat( );

    CtiIONFloat &setValue( float value );
    float        getValue( void ) const;

    const char *toString( void );

    virtual bool isNumeric( void ) const  {  return true;  };
    double getNumericValue( void ) const;
};


#endif  //  #ifndef __ION_VALUE_FIXED_FLOAT_H__

