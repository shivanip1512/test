/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_float.cpp
 *
 * Class:  CtiIONFloat
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_value_basic_float.h"

#include "logger.h"


CtiIONFloat::CtiIONFloat( float initialValue=0.0 ) :
    CtiIONNumeric(IONFloat),
    _float(initialValue)
{
}


CtiIONFloat::CtiIONFloat( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONFloat)
{
    //  all i know about is 4-byte floats
    if( streamLength == 4 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_float)[0] = byteStream[3];
        ((unsigned char *)&_float)[1] = byteStream[2];
        ((unsigned char *)&_float)[2] = byteStream[1];
        ((unsigned char *)&_float)[3] = byteStream[0];

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


CtiIONFloat::~CtiIONFloat( )
{
}


unsigned int CtiIONFloat::getSerializedValueLength( void ) const
{
    return 4;
}


void CtiIONFloat::putSerializedValue( unsigned char *buf ) const
{
    memcpy( buf, &_float, getSerializedValueLength( ) );
}


CtiIONFloat &CtiIONFloat::setValue( float value )
{
    _float = value;  return *this;
}


float CtiIONFloat::getValue( void )
{
    return _float;
}

