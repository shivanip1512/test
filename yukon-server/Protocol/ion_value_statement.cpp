#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_rootclasses.cpp
 *
 * Class:  CtiIONSerializable, CtiIONValue, CtiIONClass, CtiIONDataStream
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 *         Implementation for ION root classes
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new

#include "guard.h"
#include "logger.h"

#include "ion_rootclasses.h"
#include "ion_value_statement.h"
#include "ion_value_basic_array.h"
#include "ion_value_basic_boolean.h"


CtiIONStatement::CtiIONStatement( )
{
}

CtiIONStatement::CtiIONStatement( unsigned int handle, CtiIONMethod *method ) :
    _handle(handle),
    _method(method)
{
}


CtiIONStatement::~CtiIONStatement( )
{
}


unsigned int CtiIONStatement::getSerializedValueLength( void ) const
{
    int length;

    length = 2 + _method->getSerializedValueLength( );

    return length;
}


void CtiIONStatement::putSerializedValue( unsigned char *buf ) const
{
    int offset = 0;

    buf[offset++] = (_handle >> 8) & 0xFF;
    buf[offset++] =  _handle       & 0xFF;

    _method->putSerializedValue( buf + offset );
}



CtiIONMethod::CtiIONMethod( )
{
}


CtiIONMethod::CtiIONMethod( IONSimpleMethods method, CtiIONValue *parameter )
{
    switch( method )
    {
        case WriteRegisterValue:
        case WriteValue:
        {
            _parameter = parameter;
            break;
        }

        default:
        {
            _parameter = NULL;
            break;
        }
    }

    _extendedMethodNum = NoExtendedMethod;
    _methodNum = method;

    setValid(true);
}


CtiIONMethod::CtiIONMethod( IONExtendedMethods method, CtiIONValue *parameter )
{
    _methodNum = ExtendedMethod;
    _extendedMethodNum = method;

    _parameter = parameter;

    if( _parameter != NULL )
    {
        _extendedMethodNum |= 0x8000;
    }

    setValid(true);
}


CtiIONMethod::~CtiIONMethod( )
{
}


void CtiIONMethod::setValid( bool valid )
{
    _valid = valid;
}


int CtiIONMethod::isValid( void )
{
    return _valid;
}


CtiIONMethod::CtiIONMethod( unsigned char *buf, unsigned long len )
{
    unsigned long pos, bytesUsed;

    pos = 0;

    setValid(false);

    //  make sure there's data to read
    //    ACH:  is this necessary?
    if( len > 0 )
    {
        _methodNum = buf[pos++];
        setValid(true);

        //  if it's an extended method and there's enough data to read
        if( _methodNum == ExtendedMethod )
        {
            //  ACH:  necessary?
            if( (pos + 2) <= len )
            {
                _extendedMethodNum  = buf[pos++];
                _extendedMethodNum |= buf[pos++];
            }
            else
            {
                setValid(false);
            }
        }
        else
        {
            _extendedMethodNum = NoExtendedMethod;
        }

        //  if there's a value included (highest bit set to 1)
        if( _extendedMethodNum & 0x8000 || _methodNum == ReadValue || _methodNum == WriteValue )
        {
            //  just making sure data's available to read...
            //    ACH:  necessary?
            if( pos < len )
            {
                _parameter = CtiIONValue::restoreObject((buf + pos), (len - pos), &bytesUsed);

                //  make sure we read the parameter value correctly
                if( _parameter != NULL && _parameter->isValid() )
                {
                    pos += bytesUsed;
                }
                else
                {
                    setValid(false);
                }
            }
            else
            {
                setValid(false);
            }
        }
    }
    else
    {
        setValid(false);
    }
}


unsigned int CtiIONMethod::getSerializedValueLength( void ) const
{
    int size = 1;

    if( _methodNum == ExtendedMethod )
    {
        size += 2;
    }

    if( _parameter != NULL )
    {
        size += _parameter->getSerializedValueLength( );
    }

    return size;
}


void CtiIONMethod::putSerializedValue( unsigned char *buf ) const
{
    int offset = 0;

    buf[offset++] = _methodNum;

    if( _methodNum == ExtendedMethod )
    {
        //  who loves Big Endian?  nobody!  ... but we gotta use it anyway.

        buf[offset++] = (_extendedMethodNum >> 8) & 0xFF;
        buf[offset++] =  _extendedMethodNum       & 0xFF;
    }

    if( _parameter != NULL )
    {
        _parameter->putSerialized( buf + offset );
    }
}

