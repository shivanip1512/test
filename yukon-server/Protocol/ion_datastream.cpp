/*-----------------------------------------------------------------------------*
 *
 * File:   ion_datastream.cpp
 *
 * Class:  CtiIONDataStream
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "ctidbgmem.h" // defines CTIDBG_new

#include "guard.h"
#include "logger.h"

#include "ion_datastream.h"


CtiIONDataStream::CtiIONDataStream( unsigned char *buf, unsigned long len )
{
    parseByteStream( buf, len );
}


CtiIONDataStream::CtiIONDataStream( )
{
}


CtiIONDataStream::~CtiIONDataStream( )
{
    clearAndDestroy();
}


void CtiIONDataStream::parseByteStream( unsigned char *buf, unsigned long len )
{
    unsigned long pos = 0;

    unsigned long itemLength;
    CtiIONValue *tmpValue;

    while( pos < len )
    {
        tmpValue = CtiIONValue::restoreObject((buf + pos), (len - pos), &itemLength);

        if( tmpValue != NULL && tmpValue->isValid() )
        {
            _streamValues.push_back( tmpValue );

            pos += itemLength;
        }
        else
        {
            pos = len;
        }
    }
}


CtiIONDataStream &CtiIONDataStream::initialize( unsigned char *buf, unsigned long len )
{
    clearAndDestroy();

    parseByteStream(buf, len);

    return *this;
}


bool CtiIONDataStream::isValid( void )
{
    bool result = true;
    DSIterator itr;

    for( itr = _streamValues.begin(); itr != _streamValues.end() && result; itr++ )
    {
        result &= (*itr)->isValid();
    }

    return result;
}


void CtiIONDataStream::clearAndDestroy( void )
{
    while( !_streamValues.empty( ) )
    {
        delete _streamValues.back();

        _streamValues.pop_back();
    }
}


CtiIONValue *CtiIONDataStream::at( int index ) const
{
    CtiIONValue *retVal;

    if( index >= 0 && index < _streamValues.size() )
    {
        retVal = _streamValues.at(index);
    }
    else
    {
        retVal = NULL;
    }

    return retVal;
}

/*
CtiIONValue *CtiIONDataStream::operator[]( int index ) const
{
    return at(index);
}
*/

CtiIONDataStream &CtiIONDataStream::push_back( CtiIONValue *toInsert )
{
    if( toInsert != NULL )
    {
        _streamValues.push_back(toInsert);
    }

    return *this;
}


CtiIONDataStream &CtiIONDataStream::erase( int index )
{
    if( index >= 0 && index < _streamValues.size() )
    {
        _streamValues.erase(_streamValues.begin() + index);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}

int CtiIONDataStream::size( void ) const
{
    return _streamValues.size();
}


bool CtiIONDataStream::empty( void ) const
{
    return _streamValues.empty();
}


bool CtiIONDataStream::itemIsType( int index, CtiIONValueFixed::FixedTypes type )
{
    bool result = false;

    if( index >= 0 && index < _streamValues.size() )
    {
        result = CtiIONValueFixed::isFixedType(_streamValues[index], type);
    }

    return result;
}


bool CtiIONDataStream::itemIsType( int index, CtiIONValueVariable::VariableTypes type )
{
    bool result = false;

    if( index >= 0 && index < _streamValues.size() )
    {
        result = CtiIONValueVariable::isVariableType(_streamValues[index], type);
    }

    return result;
}


bool CtiIONDataStream::itemIsType( int index, CtiIONFixedArray::FixedArrayTypes type )
{
    bool result = false;

    if( index >= 0 && index < _streamValues.size() )
    {
        result = CtiIONFixedArray::isFixedArrayType(_streamValues[index], type);
    }

    return result;
}


bool CtiIONDataStream::itemIsType( int index, CtiIONStruct::StructTypes type )
{
    bool result = false;

    if( index >= 0 && index < _streamValues.size() )
    {
        result = CtiIONStruct::isStructType(_streamValues[index], type);
    }

    return result;
}


bool CtiIONDataStream::itemIsType( int index, CtiIONStructArray::StructArrayTypes type )
{
    bool result = false;

    CtiIONStructArray *tmpStructArray;

    if( index >= 0 && index < _streamValues.size() )
    {
        result = CtiIONStructArray::isStructArrayType(_streamValues[index], type);
    }

    return result;
}


void CtiIONDataStream::putSerialized( unsigned char *buf ) const
{
    int offset;

    DSConstIterator itr;

    offset = 0;

    //  concatenate all items into the buffer
    for( itr = _streamValues.begin(); itr != _streamValues.end(); itr++ )
    {
        (*itr)->putSerialized(buf + offset);

        offset += (*itr)->getSerializedLength();
    }
}


unsigned int CtiIONDataStream::getSerializedLength( void ) const
{
    unsigned int length = 0;

    DSConstIterator itr;

    //  sum all of the data sizes
    for( itr = _streamValues.begin(); itr != _streamValues.end(); itr++ )
    {
        length += (*itr)->getSerializedLength();
    }

    return length;
}

