/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed_char.cpp
 *
 * Class:  CtiIONChar
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "ctidbgmem.h" // defines CTIDBG_new

#include "logger.h"

#include "ion_value_fixed_char.h"


CtiIONChar::CtiIONChar( unsigned char initialValue='\0' ) :
    CtiIONValueFixed(Fixed_Char),
    _char(initialValue & 0x7F)
{

}


CtiIONChar::CtiIONChar( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValueFixed(Fixed_Char)
{
    //  all i know about is 1-byte chars
    if( streamLength <= 1 )
    {
        if( streamLength > 0 )
        {
            _char = byteStream[0];
        }
        else
        {
            _char = 0;
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


CtiIONChar::~CtiIONChar( )
{

}


CtiIONChar &CtiIONChar::setValue( unsigned char value )
{
    _char = value & 0x7F;
    return *this;
}


unsigned char CtiIONChar::getValue( void ) const
{
    return _char;
}


const char *CtiIONChar::toString( void )
{
    _string[0] = _char;
    _string[1] = '\0';

    return _string;
}


unsigned int CtiIONChar::getSerializedValueLength( void ) const
{
    return 1;
}


void CtiIONChar::putSerializedValue( unsigned char *buf ) const
{
    memcpy( buf, &_char, getSerializedValueLength( ) );
}


void CtiIONChar::putElement( unsigned char *buf ) const
{
    putSerializedValue(buf);
}


unsigned int CtiIONChar::getElementLength( void ) const
{
    return getSerializedValueLength();
}

