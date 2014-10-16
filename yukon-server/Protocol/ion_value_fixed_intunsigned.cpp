/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_intunsigned.cpp
 *
 * Class:  CtiIONUnsignedInt
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 *         Implementation for ION basic value types
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_fixed_intunsigned.h"

#include "logger.h"

using std::endl;

CtiIONUnsignedInt::CtiIONUnsignedInt( const unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValueFixed(Fixed_UnsignedInt),
    _unsignedInt(0)
{
    //  all i know about is 4-byte unsigned ints
    if( streamLength <= 4 )
    {
        for( int i = 0; i < streamLength; i++ )
        {
            //  doggoned MSB ordering
            //  streamlength 4 -
            //  i = 0,  ui[4-0-1=3] = bs[0];
            //  i = 1,  ui[4-1-1=2] = bs[1];
            //  i = 2,  ui[4-2-1=1] = bs[2];
            //  i = 3,  ui[4-3-1=0] = bs[3];

            //  i = 0, ui[2-0-1=1] = bs[0];
            //  i = 1, ui[2-1-1=0] = bs[1];
            ((unsigned char *)&_unsignedInt)[streamLength - i - 1] = byteStream[i];
        }

        setValid(true);
    }
    else
    {
        CTILOG_ERROR(dout, "streamLength > 4 ("<< streamLength <<")");

        setValid(false);
    }
    memset( &_string, 0, sizeof(_string) );
}



CtiIONUnsignedInt::CtiIONUnsignedInt( unsigned long initialValue ) :
    CtiIONValueFixed(Fixed_UnsignedInt),
    _unsignedInt(initialValue)
{
    setValid(true);

    memset( &_string, 0, sizeof(_string) );
}


CtiIONUnsignedInt::~CtiIONUnsignedInt( )
{

}


unsigned int CtiIONUnsignedInt::getSerializedValueLength( void ) const
{
    return 4;
}


void CtiIONUnsignedInt::putSerializedValue( unsigned char *buf ) const
{
    buf[0] = ((const unsigned char *)&_unsignedInt)[3];
    buf[1] = ((const unsigned char *)&_unsignedInt)[2];
    buf[2] = ((const unsigned char *)&_unsignedInt)[1];
    buf[3] = ((const unsigned char *)&_unsignedInt)[0];
}


void CtiIONUnsignedInt::putElement( unsigned char *buf ) const
{
    putSerializedValue(buf);
}


unsigned int CtiIONUnsignedInt::getElementLength( void ) const
{
    return getSerializedValueLength();
}


CtiIONUnsignedInt &CtiIONUnsignedInt::setValue( unsigned long value )
{
    _unsignedInt = value;
    return *this;
}


unsigned long CtiIONUnsignedInt::getValue( void ) const
{
    return _unsignedInt;
}


const char *CtiIONUnsignedInt::toString( void )
{
    _snprintf(_string, 15, "%lu", _unsignedInt);
    _string[15] = 0;

    return _string;
}


double CtiIONUnsignedInt::getNumericValue( void ) const
{
    return getValue();
}

