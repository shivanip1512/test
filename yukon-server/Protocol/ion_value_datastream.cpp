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
    for( int i = 0; i < _streamValues.size( ); i++ )
        delete _streamValues[i];
}


void CtiIONDataStream::parseByteStream( unsigned char *buf, unsigned long len )
{
    unsigned long pos = 0;

    unsigned long itemLength;
    CtiIONValue *tmpValue;

    while( pos < len )
    {
        tmpValue = CtiIONValue::restoreObject(buf, len, &itemLength);

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
    clear();

    parseByteStream(buf, len);

    return *this;
}


void CtiIONDataStream::clear( void )
{
    CtiIONValue *tmp;

    while( !_streamValues.empty() )
    {
        tmp = _streamValues.back();

        if( tmp != NULL )
        {
            delete tmp;
        }

        _streamValues.pop_back();
    }
}


CtiIONValue *CtiIONDataStream::getItem( int index ) const
{
    return _streamValues[index];
}


CtiIONValue *CtiIONDataStream::operator[]( int index ) const
{
    return getItem(index);
}


CtiIONDataStream &CtiIONDataStream::appendItem( CtiIONValue *toInsert )
{
    _streamValues.push_back(toInsert);
    return *this;
}


int CtiIONDataStream::getItemCount( void ) const
{
    return _streamValues.size();
}


bool CtiIONDataStream::empty( void ) const
{
    return _streamValues.empty();
}


bool CtiIONDataStream::contains( CtiIONValue::IONValueTypes type )
{
    bool result = false;
    streamIterator itr;

    for( itr = _streamValues.begin(); (itr != _streamValues.end()) && !result; itr++ )
    {
        result = itemIs(*itr, type);
    }

    return result;
}


bool CtiIONDataStream::contains( CtiIONArray::IONArrayTypes type )
{
    bool result = false;
    streamIterator itr;

    for( itr = _streamValues.begin(); (itr != _streamValues.end()) && !result; itr++ )
    {
        result = itemIs(*itr, type);
    }

    return result;
}


bool CtiIONDataStream::contains( CtiIONStruct::IONStructTypes type )
{
    bool result = false;
    streamIterator itr;

    for( itr = _streamValues.begin(); (itr != _streamValues.end()) && !result; itr++ )
    {
        result = itemIs(*itr, type);
    }

    return result;
}


bool CtiIONDataStream::itemIs( CtiIONValue *toCheck, CtiIONValue::IONValueTypes type )
{
    bool result = false;

    if( toCheck->getType() == type )
    {
        result = true;
    }

    return result;
}


bool CtiIONDataStream::itemIs( CtiIONValue *toCheck, CtiIONArray::IONArrayTypes type )
{
    bool result;
    CtiIONArray *tmpArray;

    if( itemIs(toCheck, CtiIONValue::IONArray) )
    {
        tmpArray = (CtiIONArray *)toCheck;

        if( tmpArray->getArrayType() == type )
        {
            result = true;
        }
    }

    return result;
}


bool CtiIONDataStream::itemIs( CtiIONValue *toCheck, CtiIONStruct::IONStructTypes type )
{
    bool result = false;

    CtiIONStruct *tmpStruct;

    if( itemIs(toCheck, CtiIONValue::IONArray) )
    {
        if( itemIs(toCheck, CtiIONArray::IONStruct) )
        {
            tmpStruct = (CtiIONStruct *)toCheck;

            if( tmpStruct->getStructType() == type )
            {
                result = true;
            }
        }
    }

    return result;
}


void CtiIONDataStream::putSerialized( unsigned char *buf ) const
{
    int            itemNum,
                   dataOffset;
    unsigned char *retData;
    CtiIONValue   *tmpItem;

    dataOffset = 0;

    //  concatenate all items into the buffer
    for( itemNum = 0; itemNum < getItemCount(); itemNum++ )
    {
        getItem(itemNum)->putSerialized(buf + dataOffset);

        dataOffset += getItem(itemNum)->getSerializedLength();
    }
}


unsigned int CtiIONDataStream::getSerializedLength( void ) const
{
    int itemNum;
    unsigned int totalDataLength = 0;

    //  sum all of the data sizes
    for( itemNum = 0; itemNum < getItemCount(); itemNum++ )
    {
        totalDataLength += getItem(itemNum)->getSerializedLength();
    }

    return totalDataLength;
}

