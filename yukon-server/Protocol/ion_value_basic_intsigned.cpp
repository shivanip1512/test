/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_intsigned.cpp
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

#include "ion_value_basic_intsigned.h"


CtiIONSignedInt::CtiIONSignedInt( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONSignedInt)
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


CtiIONSignedInt::CtiIONSignedInt( long initialValue=0 ) :
    CtiIONNumeric(IONSignedInt),
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


CtiIONSignedInt &CtiIONSignedInt::setValue( long value )
{
    _signedInt = value;
    return *this;
}


long CtiIONSignedInt::getValue( void )
{
    return _signedInt;
}

