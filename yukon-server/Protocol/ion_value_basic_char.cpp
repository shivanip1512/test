
/*-----------------------------------------------------------------------------*
 *
 * File:   ion_basictypes.cpp
 *
 * Class:  CtiIONChar
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new
#include "ion_value_basic_char.h"

#include "logger.h"


CtiIONChar::CtiIONChar( unsigned char initialValue='\0' ) :
    CtiIONValue(IONChar),
    _char(initialValue & 0x7F)
{
}


CtiIONChar::CtiIONChar( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValue(IONChar)
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

unsigned char CtiIONChar::getValue( void )
{
    return _char;
}

unsigned int CtiIONChar::getSerializedValueLength( void ) const
{
    return 1;
}

void CtiIONChar::putSerializedValue( unsigned char *buf ) const
{
    memcpy( buf, &_char, getSerializedValueLength( ) );
}

