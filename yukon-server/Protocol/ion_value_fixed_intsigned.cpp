/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_intsigned.cpp
 *
 * Class:  CtiIONSignedInt
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new

#include "logger.h"

#include "ion_value_fixed_intsigned.h"


CtiIONSignedInt::CtiIONSignedInt( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValueFixed(Fixed_SignedInt)
{
    //  all i know about is 4-byte signed ints
    if( streamLength <= 4 )
    {
        //  check for sign extension
        if( byteStream[0] & 0x80 )
        {
            _signedInt = -1;
        }
        else
        {
            _signedInt = 0;
        }

        for( int i = 0; i < streamLength; i++ )
        {
            //  doggoned MSB ordering
            ((unsigned char *)&_signedInt)[i] = byteStream[streamLength - i - 1];
        }

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


CtiIONSignedInt::CtiIONSignedInt( long initialValue ) :
    CtiIONValueFixed(Fixed_SignedInt),
    _signedInt(initialValue)
{

}


CtiIONSignedInt::~CtiIONSignedInt( )
{

}


unsigned int CtiIONSignedInt::getSerializedValueLength( void ) const
{
    return 4;
}


void CtiIONSignedInt::putSerializedValue( unsigned char *buf ) const
{
    memcpy( buf, &_signedInt, getSerializedValueLength( ) );
}


void CtiIONSignedInt::putElement( unsigned char *buf ) const
{
    putSerializedValue(buf);
}


unsigned int CtiIONSignedInt::getElementLength( void ) const
{
    return getSerializedValueLength();
}


CtiIONSignedInt &CtiIONSignedInt::setValue( long value )
{
    _signedInt = value;
    return *this;
}


long CtiIONSignedInt::getValue( void ) const
{
    return _signedInt;
}


double CtiIONSignedInt::getNumericValue( void ) const
{
    return getValue();
}

