/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_intunsigned.cpp
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

#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_basic_intunsigned.h"

#include "logger.h"


CtiIONUnsignedInt::CtiIONUnsignedInt( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONUnsignedInt)
{
    //  all i know about is 4-byte unsigned ints
    if( streamLength <= 4 )
    {
        _unsignedInt = 0;

        for( int i = 0; i < streamLength; i++ )
        {
            //  doggoned MSB ordering
            ((unsigned char *)&_unsignedInt)[i] = byteStream[streamLength - i - 1];
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



CtiIONUnsignedInt::CtiIONUnsignedInt( unsigned int initialValue=0 ) :
    CtiIONNumeric(IONUnsignedInt),
    _unsignedInt(initialValue)
{
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
    memcpy( buf, &_unsignedInt, getSerializedValueLength( ) );
}


CtiIONUnsignedInt &CtiIONUnsignedInt::setValue( unsigned int value )
{
    _unsignedInt = value;
    return *this;
}


unsigned int CtiIONUnsignedInt::getValue( void )
{
    return _unsignedInt;
}

