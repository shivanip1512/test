/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_program.cpp
 *
 * Class:  CtiIONProgram
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_basic_program.h"

#include "logger.h"


CtiIONProgram::CtiIONProgram( ) :
    CtiIONValue(IONProgram)
{
}

CtiIONProgram::CtiIONProgram( CtiIONStatement *initial ) :
    CtiIONValue(IONProgram)
{
    addStatement( initial );
}

CtiIONProgram::CtiIONProgram( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValue(IONProgram)
{
    unsigned long streamPos;
    unsigned short handle;
    CtiIONStatement *tmpStatement;
    CtiIONMethod *tmpMethod;

    streamPos = 0;

    //  make sure there's enough data for a statement (3 bytes minimum)
    while( (streamPos + 3) <= streamLength )
    {
        handle  = byteStream[streamPos++];
        handle |= byteStream[streamPos++] << 8;

        tmpMethod = CTIDBG_new CtiIONMethod( byteStream, streamLength - streamPos );

        //  make sure we read the method out okay
        if( tmpMethod != NULL && tmpMethod->isValid( ) )
        {
            setValid(true);

            streamPos += tmpMethod->getSerializedValueLength( );

            tmpStatement = CTIDBG_new CtiIONStatement( handle, tmpMethod );
            if( tmpStatement != NULL )
            {
                _statements.push_back( tmpStatement );
            }
        }
        else
        {
            setValid(false);

            //  read an invalid CtiIONMethod - rest of stream is likely corrupt.
            streamPos = streamLength;
        }
    }
}


CtiIONProgram::~CtiIONProgram( )
{
    clearAndDestroy();
}


CtiIONProgram &CtiIONProgram::addStatement( CtiIONStatement *toAdd )
{
    _statements.push_back( toAdd );
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


unsigned int CtiIONProgram::getSerializedValueLength( void ) const
{
    unsigned int length = 0;

    for( int i = 0; i < _statements.size( ); i++ )
    {
        length += _statements[i]->getSerializedValueLength( );
    }

    return length;
}


void CtiIONProgram::putSerializedValue( unsigned char *buf ) const
{
    unsigned int pos = 0;

    for( int i = 0; i < _statements.size( ); i++ )
    {
        _statements[i]->putSerializedValue( buf + pos );
        pos += _statements[i]->getSerializedValueLength( );
    }
}



