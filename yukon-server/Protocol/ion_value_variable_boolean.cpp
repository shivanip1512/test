/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_variable_boolean.cpp
 *
 * Class:  CtiIONBoolean
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "ctidbgmem.h" // defines CTIDBG_new

#include "logger.h"

#include "ion_value_variable_boolean.h"


CtiIONBoolean::CtiIONBoolean( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValueVariable(Variable_Boolean)
{
    //  all i know about is 1-byte booleans
    if( streamLength == 1 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_bool)[0] = byteStream[0];

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


CtiIONBoolean::CtiIONBoolean( bool initialValue=false ) :
    CtiIONValueVariable(Variable_Boolean),
    _bool(initialValue)
{
}


CtiIONBoolean::~CtiIONBoolean( )
{
}


unsigned char CtiIONBoolean::getVariableClassDescriptor( void ) const
{
    VariableClassDescriptor retVal;

    if( getValue() == true )
    {
        retVal = ClassDescriptor_Variable_BooleanTrue;
    }
    else
    {
        retVal = ClassDescriptor_Variable_BooleanFalse;
    }

    return retVal;
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


void CtiIONBoolean::putElement( unsigned char *buf ) const
{
    putSerialized(buf);
}


unsigned int CtiIONBoolean::getElementLength( void ) const
{
    return getSerializedLength();
}


CtiIONBoolean &CtiIONBoolean::setValue( bool value )
{
    _bool = value;
    return *this;
}


bool CtiIONBoolean::getValue( void ) const
{
    return _bool;
}


const char *CtiIONBoolean::toString( void )
{
    if( _bool )
    {
        _snprintf(_string, 6, "true");
    }
    else
    {
        _snprintf(_string, 6, "false");
    }

    return _string;
}


double CtiIONBoolean::getNumericValue( void ) const
{
    return getValue();
}

