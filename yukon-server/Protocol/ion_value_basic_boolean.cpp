/*-----------------------------------------------------------------------------*
 *
 * File:   ion_basictypes.cpp
 *
 * Class:  CtiIONBoolean
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_basic_boolean.h"

#include "logger.h"


CtiIONBoolean::CtiIONBoolean( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONBoolean)
{
    //  all i know about is 1-byte booleans
    if( streamLength == 1 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_bool)[0] = byteStream[0];

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


CtiIONBoolean::CtiIONBoolean( bool initialValue=false ) :
    CtiIONNumeric(IONBoolean),
    _bool(initialValue)
{
}


CtiIONBoolean::~CtiIONBoolean( )
{
}


unsigned int CtiIONBoolean::getSerializedValueLength( void ) const
{
    //  header contains the boolean value
    return 0;
}


void CtiIONBoolean::putSerializedValue( unsigned char *buf ) const
{
    //  header contains the boolean value, nothing to add
}


CtiIONBoolean &CtiIONBoolean::setValue( bool value )
{
    _bool = value;
    return *this;
}


bool CtiIONBoolean::getValue( void )
{
    return _bool;
}

