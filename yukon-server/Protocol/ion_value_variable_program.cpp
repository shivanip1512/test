/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_variable_program.cpp
 *
 * Class:  CtiIONProgram
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "ctidbgmem.h" // defines CTIDBG_new

#include "logger.h"

#include "ion_value_variable_program.h"



CtiIONProgram::CtiIONProgram( ) :
    CtiIONValueVariable(Variable_Program)
{
}

CtiIONProgram::CtiIONProgram( CtiIONStatement *initial ) :
    CtiIONValueVariable(Variable_Program)
{
    addStatement(initial);
}

CtiIONProgram::CtiIONProgram( unsigned char *buf, unsigned long len ) :
    CtiIONValueVariable(Variable_Program)
{
    unsigned long pos, bytesUsed;
    unsigned short handle;
    CtiIONStatement *tmpStatement;

    pos = 0;

    //  make sure there's enough data for a statement (3 bytes minimum)
    while( (pos + 3) <= len )
    {
        tmpStatement = CTIDBG_new CtiIONStatement((buf + pos), (len - pos), &bytesUsed);

        if( tmpStatement != NULL )
        {
            addStatement(tmpStatement);

            pos += bytesUsed;
        }
        else
        {
            setValid(false);

            //  read an invalid CtiIONMethod - rest of stream is likely corrupt.
            pos = len;
        }
    }
}


CtiIONProgram::~CtiIONProgram( )
{
    clearAndDestroy();
}


CtiIONProgram &CtiIONProgram::addStatement( CtiIONStatement *toAdd )
{
    _statements.push_back(toAdd);
    return *this;
}


CtiIONProgram &CtiIONProgram::clearAndDestroy( void )
{
    while( !_statements.empty() )
    {
        delete _statements.back();

        _statements.pop_back();
    }

    return *this;
}


unsigned char CtiIONProgram::getVariableClassDescriptor( void ) const
{
    return ClassDescriptor_Variable_Program;
}


unsigned int CtiIONProgram::getSerializedValueLength( void ) const
{
    unsigned int length = 0;

    for( int i = 0; i < _statements.size( ); i++ )
    {
        length += _statements[i]->getLength( );
    }

    return length;
}


void CtiIONProgram::putSerializedValue( unsigned char *buf ) const
{
    unsigned int pos = 0;

    for( int i = 0; i < _statements.size( ); i++ )
    {
        _statements[i]->write( buf + pos );
        pos += _statements[i]->getLength( );
    }
}


CtiIONStatement::CtiIONStatement( )
{
}

CtiIONStatement::CtiIONStatement( unsigned int handle, CtiIONMethod *method ) :
    _handle(handle),
    _method(method)
{
}

CtiIONStatement::CtiIONStatement( unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos;
    unsigned short tmpHandle;

    pos = 0;

    _handle  = buf[pos++];
    _handle |= buf[pos++] << 8;

    _method = CTIDBG_new CtiIONMethod((buf + pos), (len - pos), bytesUsed);

    //  make sure we read the method out okay
    if( _method != NULL && _method->isValid( ) )
    {
        setValid(true);

        pos += *bytesUsed;
    }
    else
    {
        delete _method;

        setValid(false);

        //  read an invalid CtiIONMethod - rest of stream is likely corrupt.
        pos = len;
    }

    *bytesUsed = pos;
}

CtiIONStatement::~CtiIONStatement( )
{
    delete _method;
}


void CtiIONStatement::setValid( bool valid )
{
    _valid = valid;
}


bool CtiIONStatement::isValid( void )
{
    return _valid;
}


unsigned int CtiIONStatement::getLength( void ) const
{
    int length;

    length = 2 + _method->getLength( );

    return length;
}


void CtiIONStatement::write( unsigned char *buf ) const
{
    int offset = 0;

    buf[offset++] = (_handle >> 8) & 0xFF;
    buf[offset++] =  _handle       & 0xFF;

    _method->write( buf + offset );
}



CtiIONMethod::CtiIONMethod( )
{
}


CtiIONMethod::CtiIONMethod( IONSimpleMethods method, CtiIONValue *parameter )
{
    setValid(true);

    switch( method )
    {
        case ReadValue:
        case WriteRegisterValue:  //  er?  is this right?
        case WriteValue:
        {
            if( parameter != NULL )
            {
                _parameter = parameter;
            }
            else
            {
                _parameter = NULL;

                setValid(false);
            }

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
}


CtiIONMethod::CtiIONMethod( IONExtendedMethods method, CtiIONValue *parameter )
{
    setValid(true);

    _methodNum = ExtendedMethod;
    _extendedMethodNum = method;

    _parameter = parameter;

    if( _parameter != NULL )
    {
        _extendedMethodNum |= 0x8000;
    }
}


CtiIONMethod::~CtiIONMethod( )
{
    delete _parameter;
}


void CtiIONMethod::setValid( bool valid )
{
    _valid = valid;
}


int CtiIONMethod::isValid( void )
{
    return _valid;
}


CtiIONMethod::CtiIONMethod( unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos;

    pos = 0;

    setValid(false);

    //  ACH:  make sure there's data to read... ?
    //          is this necessary?
    if( len > 0 )
    {
        _methodNum = buf[pos++];
        setValid(true);

        //  if it's an extended method and there's enough data to read
        if( _methodNum == ExtendedMethod )
        {
            _extendedMethodNum  = buf[pos++];
            _extendedMethodNum |= buf[pos++];
        }
        else
        {
            _extendedMethodNum = NoExtendedMethod;
        }

        //  if there's a value included (highest bit set to 1)
        if( _extendedMethodNum & 0x8000 || _methodNum == ReadValue || _methodNum == WriteValue )
        {
            _parameter = CtiIONValue::restoreObject((buf + pos), (len - pos), bytesUsed);

            //  make sure we read the parameter value correctly
            if( _parameter != NULL && _parameter->isValid() )
            {
                pos += *bytesUsed;
            }
            else
            {
                delete _parameter;

                setValid(false);
            }
        }
    }
    else
    {
        setValid(false);
    }

    *bytesUsed = pos;
}


unsigned int CtiIONMethod::getLength( void ) const
{
    int size = 1;

    if( _methodNum == ExtendedMethod )
    {
        size += 2;
    }

    if( _parameter != NULL )
    {
        size += _parameter->getSerializedLength( );
    }

    return size;
}


void CtiIONMethod::write( unsigned char *buf ) const
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

