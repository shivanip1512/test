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
#include "ion_rootclasses.h"
#include "ion_valuebasictypes.h"
#include "guard.h"
#include "logger.h"

void CtiIONSerializable::putSerialized( unsigned char *buf ) const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

unsigned int CtiIONSerializable::getSerializedLength( void ) const
{
    return 0;
}


unsigned char *CtiIONSerializable::getSerialized( void ) const
{
    unsigned char *retData;
    int            retDataLength;

    retDataLength = getSerializedLength( );

    retData = CTIDBG_new unsigned char[retDataLength];

    if( retData != NULL )
    {
        putSerialized( retData );
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime( ) << " (" << __FILE__ << ":" <<__LINE__ << ") unable to allocate " << retDataLength << " bytes in ::getSerialized( ), returning NULL" << endl;
    }

    return retData;
}



int CtiIONValue::isNumeric( void )
{
    int retVal;

    switch( getType( ) )
    {
        case IONFloat:
        case IONBoolean:
        case IONUnsignedInt:
        case IONSignedInt:
            retVal = TRUE;

        default:
            retVal = FALSE;
    }

    return retVal;
}

void CtiIONValue::putSerialized( unsigned char *buf ) const
{
    putSerializedHeader( buf );
    putSerializedValue( buf + getSerializedHeaderLength( ) );
}


unsigned int CtiIONValue::getSerializedLength( void ) const
{
    return getSerializedHeaderLength( ) + getSerializedValueLength( );
}


void CtiIONValue::putSerializedHeader( unsigned char *buf ) const
{
    unsigned char key = 0, extendedValue;

    switch( getType( ) )
    {
        case IONChar:
            key  = 0x1 << 4;
            break;
        case IONFloat:
            key = 0x3;
            break;
        case IONSignedInt:
            key = 0x4;
            break;
        case IONTime:
            key = 0x5;
            break;
        case IONUnsignedInt:
            key = 0x6;
            break;

        case IONBoolean:
            if( ((CtiIONBoolean *)this)->getValue( ) )
                key = 0xF2;  //  boolean true
            else
                key = 0xF1;  //  boolean false
            break;

        case IONProgram:
            key = 0xF6;
            break;

        case IONArray:
            key = ((CtiIONArray *)this)->getArrayKey( );

//        default:
    }

    buf = putClassSize( key, buf );
}


unsigned int CtiIONValue::getSerializedHeaderLength( void ) const
{
    unsigned long tmpLength = getSerializedValueLength( );
    unsigned int tmpHeaderLength;

    if( tmpLength <= 12 )
        tmpHeaderLength = 1;
    else if( tmpLength <= 255 )
        tmpHeaderLength = 2;
    else
        tmpHeaderLength = 5;

    return tmpHeaderLength;
}


unsigned char *CtiIONValue::putClassSize( unsigned char key, unsigned char *buf ) const
{
    unsigned long tmpLength = getSerializedValueLength( );

    if( tmpLength <= 12 )
    {
        key |= tmpLength & 0x0F;
        *buf++ = key;
    }
    else if( tmpLength <= 255 )
    {
        key |= 0x0D;
        *buf++ = key;
        *buf++ = tmpLength & 0xFF;
    }
    else
    {
        key |= 0x0E;
        *buf++ = key;
        *buf++ = tmpLength & 0xFF;
        tmpLength >>= 8;
        *buf++ = tmpLength & 0xFF;
        tmpLength >>= 8;
        *buf++ = tmpLength & 0xFF;
        tmpLength >>= 8;
        *buf++ = tmpLength & 0xFF;
    }

    return buf;
}


CtiIONValue *CtiIONValue::restoreObject( unsigned char *&byteStream, unsigned long &streamLength )
{
    unsigned char ionClass, classDescriptor;
    unsigned long itemLength, streamPos;
    CtiIONValue *CTIDBG_newVal = NULL;

    ionClass = (byteStream[0] & 0xF0) >> 4;
    classDescriptor = (byteStream[0] & 0x0F);

    streamPos = 1;

    if( ionClass < 0x07 )
    {
        if( classDescriptor <= 0x0C )
        {
            itemLength = classDescriptor;
        }
        else if( classDescriptor == 0x0D )
        {
            itemLength = byteStream[streamPos++];
        }
        else
        {
            itemLength  = byteStream[streamPos++] << 24;
            itemLength |= byteStream[streamPos++] << 16;
            itemLength |= byteStream[streamPos++] <<  8;
            itemLength |= byteStream[streamPos++];
        }

        switch( ionClass )
        {
            case 0x1:  CTIDBG_newVal = CTIDBG_new CtiIONChar( byteStream + streamPos, itemLength );
                break;
            case 0x3:  CTIDBG_newVal = CTIDBG_new CtiIONFloat( byteStream + streamPos, itemLength );
                break;
            case 0x4:  CTIDBG_newVal = CTIDBG_new CtiIONSignedInt( byteStream + streamPos, itemLength );
                break;
            case 0x5:  CTIDBG_newVal = CTIDBG_new CtiIONTime( byteStream + streamPos, itemLength );
                break;
            case 0x6:  CTIDBG_newVal = CTIDBG_new CtiIONUnsignedInt( byteStream + streamPos, itemLength );
                break;
        }
    }
    else if( ionClass == 0x7 )
    {
        CTIDBG_newVal = CtiIONArray::restoreStruct( classDescriptor, byteStream + streamPos, streamLength - streamPos );
    }
    else if( ionClass == 0x8 )
    {
        CTIDBG_newVal = CtiIONArray::restoreStructArray( classDescriptor, byteStream + streamPos, streamLength - streamPos );
    }
    else if( ionClass == 0xF )
    {
        switch( classDescriptor )
        {
            case 0x1:
                CTIDBG_newVal = CTIDBG_new CtiIONBoolean( FALSE );
                break;
            case 0x2:
                CTIDBG_newVal = CTIDBG_new CtiIONBoolean( TRUE );
                break;
            case 0x6:
                CTIDBG_newVal = CTIDBG_new CtiIONProgram( byteStream + streamPos, streamLength - streamPos );
                break;
            case 0xA:
            case 0xB:
            case 0xC:
            case 0xD:
            case 0xE:
                //  fixed size arrays
                CTIDBG_newVal = CtiIONArray::restoreFixedArray( classDescriptor, byteStream, streamLength );
                break;
        }
    }

    //  move the pointer past the object we just retored
    if( CTIDBG_newVal != NULL && CTIDBG_newVal->isValid( ) )
    {
        itemLength    = CTIDBG_newVal->getSerializedLength( );
        byteStream   += itemLength;
        streamLength -= itemLength;
    }

    return CTIDBG_newVal;
}



CtiIONDataStream::CtiIONDataStream( unsigned char *byteStream, unsigned long streamLength )
{
    parseByteStream( byteStream, streamLength );
}


CtiIONDataStream::~CtiIONDataStream( )
{
    for( int i = 0; i < _streamValues.size( ); i++ )
        delete _streamValues[i];
}


void CtiIONDataStream::parseByteStream( unsigned char *byteStream, unsigned long streamLength )
{
    int bytePos = 0;
    CtiIONValue *tmpValue;

    while( bytePos < streamLength )
    {
        tmpValue = CtiIONValue::restoreObject( byteStream, streamLength );
        _streamValues.push_back( tmpValue );
        bytePos += tmpValue->getSerializedLength( );
    }
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


const CtiIONValue &CtiIONDataStream::getItem( int index ) const
{
    return *(_streamValues[index]);
}


CtiIONDataStream &CtiIONDataStream::appendItem( CtiIONValue *toInsert )
{
    _streamValues.push_back( toInsert );
    return *this;
}


int CtiIONDataStream::getItemCount( void ) const
{
    return _streamValues.size( );
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
        getItem(itemNum).putSerialized(buf + dataOffset);

        dataOffset += getItem(itemNum).getSerializedLength( );
    }
}


unsigned int CtiIONDataStream::getSerializedLength( void ) const
{
    int itemNum;
    unsigned int totalDataLength = 0;

    //  sum all of the data sizes
    for( itemNum = 0; itemNum < getItemCount(); itemNum++ )
    {
        totalDataLength += getItem(itemNum).getSerializedLength( );
    }

    return totalDataLength;
}



unsigned int CtiIONStatement::getSerializedValueLength( void ) const
{
    return sizeof(short) + _method->getSerializedValueLength( );  //  sizeof(short) == 2
}


void CtiIONStatement::putSerializedValue( unsigned char *buf ) const
{
    *((short *)buf) = (short)_handle;
    _method->putSerializedValue( buf + sizeof(short) );  //  sizeof(short) == 2
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

    _valid = TRUE;
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

    _valid = TRUE;
}


CtiIONMethod::CtiIONMethod( unsigned char *byteStream, unsigned long streamLength )
{
    unsigned long streamPos;

    streamPos = 0;

    _valid = FALSE;

    //  make sure there's data to read
    if( streamLength > 0 )
    {
        _methodNum = byteStream[streamPos++];
        _valid = TRUE;

        //  if it's an extended method and there's enough data to read
        if( _methodNum == ExtendedMethod )
        {
            if( (streamPos + 2) <= streamLength )
            {
                _extendedMethodNum  = byteStream[streamPos++];
                _extendedMethodNum |= byteStream[streamPos++];
            }
            else
            {
                _valid = FALSE;
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
            if( streamPos < streamLength )
            {
                _parameter = CtiIONValue::restoreObject( byteStream, streamLength );

                //  make sure we read the parameter value correctly
                if( _parameter != NULL && _parameter->isValid( ) )
                {
                    streamPos += _parameter->getSerializedLength( );
                }
                else
                {
                    _valid = FALSE;
                }
            }
            else
            {
                _valid = FALSE;
            }
        }
    }
    else
    {
        _valid = FALSE;
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

    memcpy( buf + offset, &_methodNum, 1 );
    offset += 1;

    if( _methodNum == ExtendedMethod )
    {
        memcpy( buf + offset, &_extendedMethodNum, 2 );
        offset += 2;
    }

    if( _parameter != NULL )
    {
        _parameter->putSerialized( buf + offset );
    }
}

