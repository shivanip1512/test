/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_float.cpp
 *
 * Class:  CtiIONFloat
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"

#include "ion_value_fixed_float.h"


CtiIONFloat::CtiIONFloat( float initialValue=0.0 ) :
    CtiIONValueFixed(Fixed_Float)
{
    _float  = initialValue;
}


CtiIONFloat::CtiIONFloat( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValueFixed(Fixed_Float)
{
    //  all i know about is 4-byte floats
    if( streamLength == 4 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_float)[0] = byteStream[3];
        ((unsigned char *)&_float)[1] = byteStream[2];
        ((unsigned char *)&_float)[2] = byteStream[1];
        ((unsigned char *)&_float)[3] = byteStream[0];

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


CtiIONFloat::~CtiIONFloat( )
{

}


unsigned int CtiIONFloat::getSerializedValueLength( void ) const
{
    return 4;
}


void CtiIONFloat::putSerializedValue( unsigned char *buf ) const
{
    buf[0] = ((unsigned char *)&_float)[3];
    buf[1] = ((unsigned char *)&_float)[2];
    buf[2] = ((unsigned char *)&_float)[1];
    buf[3] = ((unsigned char *)&_float)[0];
}


void CtiIONFloat::putElement( unsigned char *buf ) const
{
    putSerializedValue(buf);
}


unsigned int CtiIONFloat::getElementLength( void ) const
{
    return getSerializedValueLength();
}


CtiIONFloat &CtiIONFloat::setValue( float value )
{
    _float = value;

    return *this;
}


float CtiIONFloat::getValue( void ) const
{
    return _float;
}


const char *CtiIONFloat::toString( void )
{
    _snprintf(_string, 15, "%.3f", _float);

    return _string;
}


double CtiIONFloat::getNumericValue( void ) const
{
    return getValue();
}

