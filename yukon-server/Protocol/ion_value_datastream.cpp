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

#include "ion_value_datastream.h"


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
        tmpValue = CtiIONValue::restoreObject(buf + pos, len, &itemLength);

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

    if( index < _streamValues.size() )
    {
        retVal = _streamValues.at(index);
    }
    else
    {
        retVal = NULL;
    }

    return retVal;
}


CtiIONValue *CtiIONDataStream::operator[]( int index ) const
{
    return at(index);
}


CtiIONDataStream &CtiIONDataStream::push_back( CtiIONValue *toInsert )
{
    _streamValues.push_back(toInsert);
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


bool CtiIONDataStream::itemIs( int index, CtiIONValue::IONValueTypes type )
{
    bool result = false;

    if( _streamValues[index]->getType() == type )
    {
        result = true;
    }

    return result;
}


bool CtiIONDataStream::itemIs( int index, CtiIONArray::IONArrayTypes type )
{
    bool result = false;

    if( itemIs(index, CtiIONValue::IONArray) )
    {
        if( ((CtiIONArray *)_streamValues[index])->getArrayType() == type )
        {
            result = true;
        }
    }

    return result;
}


bool CtiIONDataStream::itemIs( int index, CtiIONStruct::IONStructTypes type )
{
    bool result = false;

    CtiIONStruct *tmpStruct;

    if( itemIs(index, CtiIONValue::IONArray) )
    {
        if( itemIs(index, CtiIONArray::IONStruct) )
        {
            if( ((CtiIONStruct *)_streamValues[index])->getStructType() == type )
            {
                result = true;
            }
        }
    }

    return result;
}


bool CtiIONDataStream::itemsAre( CtiIONValue::IONValueTypes type )
{
    bool result = true;

    for( int i = 0; (i < _streamValues.size()) && result; i++ )
    {
        result &= itemIs(i, type);
    }

    return result;
}


bool CtiIONDataStream::itemsAre( CtiIONArray::IONArrayTypes type )
{
    bool result = true;

    for( int i = 0; (i < _streamValues.size()) && result; i++ )
    {
        result &= itemIs(i, type);
    }

    return result;
}


bool CtiIONDataStream::itemsAre( CtiIONStruct::IONStructTypes type )
{
    bool result = true;

    for( int i = 0; (i < _streamValues.size()) && result; i++ )
    {
        result &= itemIs(i, type);
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

