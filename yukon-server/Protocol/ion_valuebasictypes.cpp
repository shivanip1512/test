
/*-----------------------------------------------------------------------------*
 *
 * File:   ion_basictypes.cpp
 *
 * Class:  CtiIONChar, CtiIONNumeric, CtiIONFloat, CtiIONSignedInt, CtiIONUnsignedInt,
 *           CtiIONBoolean, CtiIONTime, CtiIONArray
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 *         Implementation for ION basic value types
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
 
#include "ion_valuebasictypes.h"
#include "ion_valuestructtypes.h"
 
 
CtiIONChar::CtiIONChar( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValue(IONChar)
{
    //  all i know about is 1-byte chars
    if( streamLength == 1 )
    {
        _char = byteStream[0];
        setValid( TRUE );
    }
    else
    {
        setValid( FALSE );
    }
}



CtiIONBoolean::CtiIONBoolean( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONBoolean)
{
    //  all i know about is 1-byte booleans... ?
    if( streamLength == 1 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_bool)[0] = byteStream[0];
        
        setValid( TRUE );
    }
    else
    {
        setValid( FALSE );
    }
}



CtiIONFloat::CtiIONFloat( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONFloat)
{
    //  all i know about is 4-byte floats
    if( streamLength == 4 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_float)[0] = byteStream[3];
        ((unsigned char *)&_float)[1] = byteStream[2];
        ((unsigned char *)&_float)[2] = byteStream[1];
        ((unsigned char *)&_float)[3] = byteStream[0];

        setValid( TRUE );
    }
    else
    {
        setValid( FALSE );
    }
}



CtiIONSignedInt::CtiIONSignedInt( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONSignedInt)
{
    //  all i know about is 4-byte signed ints
    if( streamLength == 4 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_signedInt)[0] = byteStream[3];
        ((unsigned char *)&_signedInt)[1] = byteStream[2];
        ((unsigned char *)&_signedInt)[2] = byteStream[1];
        ((unsigned char *)&_signedInt)[3] = byteStream[0];

        setValid( TRUE );
    }
    else
    {
        setValid( FALSE );
    }
}



CtiIONUnsignedInt::CtiIONUnsignedInt( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONNumeric(IONUnsignedInt)
{
    //  all i know about is 4-byte unsigned ints
    if( streamLength == 4 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_unsignedInt)[0] = byteStream[3];
        ((unsigned char *)&_unsignedInt)[1] = byteStream[2];
        ((unsigned char *)&_unsignedInt)[2] = byteStream[1];
        ((unsigned char *)&_unsignedInt)[3] = byteStream[0];

        setValid( TRUE );
    }
    else
    {
        setValid( FALSE );
    }
}



CtiIONTime::CtiIONTime( unsigned char *byteStream, unsigned long streamLength ) :
    CtiIONValue(IONTime)
{
    //  all i know about is 8-byte times
    if( streamLength == 8 )
    {
        //  doggoned MSB ordering
        ((unsigned char *)&_seconds)[0] = byteStream[7];
        ((unsigned char *)&_seconds)[1] = byteStream[6];
        ((unsigned char *)&_seconds)[2] = byteStream[5];
        ((unsigned char *)&_seconds)[3] = byteStream[4];

        ((unsigned char *)&_fractionalSeconds)[0] = byteStream[3];
        ((unsigned char *)&_fractionalSeconds)[1] = byteStream[2];
        ((unsigned char *)&_fractionalSeconds)[2] = byteStream[1];
        ((unsigned char *)&_fractionalSeconds)[3] = byteStream[0];

        _seconds <<= 1;
        //  the high bit of _fractionalSeconds is actually the low bit of _seconds
        _seconds |= (_fractionalSeconds & 0x80000000) >> 31;
        _fractionalSeconds &= 0x7FFFFFFF;

        setValid( TRUE );
    }
    else
    {
        setValid( FALSE );
    }
}


CtiIONTime &CtiIONTime::setFractionalSeconds( unsigned long value )  
{ 
    //  copy the bits that're less than 2^31 - we only have 31 bits 
    _fractionalSeconds = value & 0x7FFFFFFF;
    return *this; 
};


void CtiIONTime::putSerializedValue( unsigned char *buf )
{ 
    unsigned long tmpSeconds;
     
    tmpSeconds = _fractionalSeconds | ((_seconds & 0x01) << 31);
    
    buf[0] = ((unsigned char *)&tmpSeconds)[3];
    buf[1] = ((unsigned char *)&tmpSeconds)[2];
    buf[2] = ((unsigned char *)&tmpSeconds)[1];
    buf[3] = ((unsigned char *)&tmpSeconds)[0];

    tmpSeconds = _seconds >> 1;  //  shift bits down, high bit (31) defaulted to 0
    
    buf[4] = ((unsigned char *)&tmpSeconds)[3];
    buf[5] = ((unsigned char *)&tmpSeconds)[2];
    buf[6] = ((unsigned char *)&tmpSeconds)[1];
    buf[7] = ((unsigned char *)&tmpSeconds)[0];
}



int CtiIONArray::isNumericArray( void )
{
    int retVal;

    switch( getArrayType( ) )
    {
        case IONBooleanArray:
        case IONFloatArray:
        case IONUnsignedIntArray:
        case IONSignedIntArray:
           retVal = TRUE;

        default:
            retVal = FALSE;
    }

    return retVal;
}


CtiIONArray &CtiIONArray::appendArrayElement( CtiIONValue *toAppend )
{
    _array.push_back( toAppend );
    return *this;
}


CtiIONValue *CtiIONArray::getArrayElement( unsigned long index )
{ 
    CtiIONValue *tmp = NULL;
    
    if( index < _array.size( ) )
        tmp = _array[index];

    return tmp;
}


void CtiIONArray::clearArrayElements( void )
{
    CtiIONValue *tmp;

    while( _array.size( ) > 0 )
    {
        tmp = _array.back( );
        delete tmp;
        _array.pop_back( );
    }
}



void CtiIONArray::putSerializedValue( unsigned char *buf )
{
    int i,
        bytesWritten;
    
    bytesWritten = 0;
    for( i = 0; i < _array.size( ); i++ )
    {
        ((CtiIONValue *)_array[i])->putSerializedValue( buf + bytesWritten );
        bytesWritten += ((CtiIONValue *)_array[i])->getSerializedLength( );
    }
}


unsigned int CtiIONArray::getSerializedValueLength( void )
{
    int i,
        totalLength;
    
    totalLength = 0;
    for( i = 0; i < _array.size( ); i++ )
    {
        totalLength += ((CtiIONValue *)_array[i])->getSerializedLength( );
    }

    return totalLength;
}


unsigned char CtiIONArray::getArrayKey( void )
{
    unsigned char key;

    switch( getArrayType( ) )
    {
        case IONCharArray:          
        case IONBooleanArray:       
        case IONFloatArray:         
        case IONSignedIntArray:     
        case IONUnsignedIntArray:   
            key  = 0xF0;
            key |= getArrayType( );
            break;

        case IONStruct:             
            key = ((CtiIONStruct *)this)->getStructKey( );
            break;

        case IONStructArray:  
            key = ((CtiIONStructArray *)this)->getStructArrayKey( );
            break;
    }

    return key;
}


unsigned int CtiIONArray::getSerializedHeaderLength( void )
{
    unsigned int tmpHeaderLength;
    unsigned long tmpLength = getSerializedValueLength( );
    unsigned long tmpItems  = _array.size( );

    tmpHeaderLength = 1;
    
    if( getArrayType( ) != IONStruct )
    {
        if( tmpItems > 13 )
            tmpHeaderLength++;
        else if( tmpItems > 255 )
            tmpHeaderLength += 4;

        if( tmpLength > 13 )
            tmpHeaderLength++;
        else if( tmpLength > 255 )
            tmpHeaderLength += 4;
    }

    return tmpHeaderLength;
}


void CtiIONArray::putSerializedHeader( unsigned char *buf )
{
    unsigned long tmpLength = getSerializedValueLength( ),
                  streamPos;
    unsigned long tmpItems  = _array.size( );
    
    unsigned char tmpbuf, tmp8b, tmp32b[4];

    streamPos = 0;

    //  notation following - (nibble 1, nibble 0)
    
    memcpy( tmp32b, &tmpItems, 4 );
    
    if( tmpItems <= 0xD )
    {
        tmp8b = (tmp32b[0] & 0x0F) << 4;   //  n1 = item count
        //  note: tmp8b == ([item count],[0x0])
    }
    else if( tmpItems <= 255 )
    {
        tmp8b  = 0xE << 4;                 //  n1 = header key
        tmp8b |= (tmp32b[0] & 0xF0) >> 4;  //  n0 = item count (bits 7-4)
        buf[streamPos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[0] & 0x0F) << 4;  //  n1 = item count (bits 3-0)
        //  note: tmp8b == ([item count bits 3-0],[0x0])
    }
    else
    {
        tmp8b  = 0xF << 4;                 //  n1 = header key
        tmp8b |= (tmp32b[3] & 0xF0) >> 4;  //  n0 = item count (bits 31-28)
        buf[streamPos++] = tmp8b;          //  ------ (n1,n0) written
 
        tmp8b  = (tmp32b[3] & 0x0F) << 4;  //  n1 = item count (bits 27-24)
        tmp8b |= (tmp32b[2] & 0xF0) >> 4;  //  n0 = item count (bits 23-20)
        buf[streamPos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[2] & 0x0F) << 4;  //  n1 = item count (bits 19-16)
        tmp8b |= (tmp32b[1] & 0xF0) >> 4;  //  n0 = item count (bits 15-12)
        buf[streamPos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[1] & 0x0F) << 4;  //  n1 = item count (bits 11- 8)
        tmp8b |= (tmp32b[0] & 0xF0) >> 4;  //  n0 = item count (bits  7- 4)
        buf[streamPos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[0] & 0x0F) << 4;  //  n1 = item count (bits  3- 0)
        //  note:  tmp8b == ([item count bits 3-0],[0x0])
    }
        
    
    memcpy( tmp32b, &tmpLength, 4 );

    if( tmpLength <= 0xC )
    {
        tmp8b |= tmp32b[0] & 0x0F;  //  n0 = length
        buf[streamPos++] = tmp8b;   //  ------ (n1,n0) written
    }
    else if( tmpLength <= 255 )
    {
        tmp8b |= 0x0D;              //  n0 = header key
        buf[streamPos++] = tmp8b;   //  ------ (n1,n0) written
        
        buf[streamPos++] = tmp32b[0];  //  byte 0 written
    }
    else
    {
        tmp8b |= 0x0E;              //  n0 = header key
        buf[streamPos++] = tmp8b;   //  ------ (n1,n0) written
        
        buf[streamPos++] = tmp32b[3];  //  byte 3 written
        buf[streamPos++] = tmp32b[2];  //  byte 2 written
        buf[streamPos++] = tmp32b[1];  //  byte 1 written
        buf[streamPos++] = tmp32b[0];  //  byte 0 written
    }
}


CtiIONArray *CtiIONArray::restoreStruct( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength )
{
    CtiIONArray *newVal = NULL;
    
    newVal = CtiIONStruct::restoreObject( classDescriptor, byteStream, streamLength );

    return newVal;
}


CtiIONArray *CtiIONArray::restoreStructArray( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength )
{
    CtiIONArray *newVal = NULL;

    newVal = CtiIONStructArray::restoreObject( classDescriptor, byteStream, streamLength );

    return newVal;
}


CtiIONArray *CtiIONArray::restoreFixedArray( unsigned char classDescriptor, unsigned char *byteStream, unsigned long streamLength )
{
    CtiIONArray *newVal = NULL;
    unsigned char tmp8b,   //  temp 8 bit value
                  tmp4b;   //  temp 4 bit value
    unsigned long tmp32b,  //  temp 32 bit value
                  streamPos,
                  itemCount,
                  itemLength;

    streamPos = 0;

    tmp8b = byteStream[streamPos++];
    tmp4b = (tmp8b & 0xF0) >> 4;  //  high nibble of the byte
        
    if( tmp4b <= 0xD )
    {
        //  item count = value
        itemCount = tmp4b;
    }
    else if( tmp4b == 0xE )
    {
        //  item count = next 8 bits
        itemCount   = tmp8b & 0x0F;       //  4 bits written
        tmp8b       = byteStream[streamPos++];
        itemCount <<= 4;
        itemCount  |= tmp8b & 0xF0 >> 4;  //  8 bits written
    }
    else  //  tmp4b == 0xF
    {
        //  item count = next 32 bits
        itemCount   = tmp8b & 0x0F;       //  4 bits written
        tmp8b       = byteStream[streamPos++];
        itemCount <<= 8;
        itemCount  |= tmp8b;              //  12 bits written
        tmp8b       = byteStream[streamPos++];
        itemCount <<= 8;
        itemCount  |= tmp8b;              //  20 bits written
        tmp8b       = byteStream[streamPos++];
        itemCount <<= 8;
        itemCount  |= tmp8b;              //  28 bits written
        tmp8b       = byteStream[streamPos++];
        itemCount <<= 4;
        itemCount  |= tmp8b & 0xF0 >> 4;  //  32 bits written
    }

    tmp4b = tmp8b & 0x0F;

    if( tmp4b <= 0xC )
    {
        //  item length = value
        itemLength = tmp4b;
    }
    else if( tmp4b == 0xD )
    {
        //  item count = next 8 bits
        itemCount   = byteStream[streamPos++];
    }
    else if( tmp4b == 0xE )
    {
        //  item count = next 32 bits
        itemCount   = byteStream[streamPos++];  //  8 bits written
        itemCount <<= 8;
        itemCount  |= byteStream[streamPos++];  //  16 bits written
        itemCount <<= 8;
        itemCount  |= byteStream[streamPos++];  //  24 bits written
        itemCount <<= 8;
        itemCount  |= byteStream[streamPos++];  //  32 bits written
    }
    else  //  tmp4b == 0xF - currently reserved, i don't know how to read it
    {
        itemCount = 0;
    }

    if( itemCount > 0 )
    {
        switch( classDescriptor )
        {
            case IONCharArray:
                newVal = new CtiIONCharArray( itemCount, itemLength, byteStream );
                break;
            case IONBooleanArray:
                newVal = new CtiIONBooleanArray( itemCount, itemLength, byteStream );
                break;
            case IONFloatArray:
                newVal = new CtiIONFloatArray( itemCount, itemLength, byteStream );
                break;
            case IONSignedIntArray:
                newVal = new CtiIONSignedIntArray( itemCount, itemLength, byteStream );
                break;
            case IONUnsignedIntArray:
                newVal = new CtiIONUnsignedIntArray( itemCount, itemLength, byteStream );
                break;
        }
        
    }
    
    return newVal;
}


unsigned char *CtiIONArray::putClassSize( unsigned char key, unsigned char *buf )
{
    unsigned long tmpLength = getSerializedValueLength( );
    unsigned long tmpItems  = _array.size( );

    //  if it's a fixed size array
    if( getArrayType( ) != IONStruct )
    {
        if( tmpItems <= 13 )
        {
            key |= (tmpItems << 4) & 0xF0;
        }
        else if( tmpItems <= 255 )
        {
            key |= 0xE0;
            key |= (tmpItems >> 4) & 0x0F;
            *(buf++) = key;
            key  = (tmpItems << 4) & 0xF0;
        }
        else
        {
            key |= 0xF0;
            key |= (tmpItems) & 0x0F;
            *(buf++) = key;
            tmpItems >>= 4;
            *(buf++) = tmpItems & 0xFF;
            tmpItems >>= 8;
            *(buf++) = tmpItems & 0xFF;
            tmpItems >>= 8;
            *(buf++) = tmpItems & 0xFF;
            tmpItems >>= 4;
            key  = tmpItems & 0xF0;
        }

        if( tmpLength <= 12 )
        {
            key |= tmpLength;
            *(buf++) = key;
        }
        else if( tmpLength <= 255 )
        {
            key |= 0x0D;
            *(buf++) = key;
            *(buf++) = tmpLength & 0xFF;
        }
        else
        {
            key |= 0x0E;
            *(buf++) = key;
            *(buf++) = tmpLength & 0xFF;
            tmpLength >>= 8;
            *(buf++) = tmpLength & 0xFF;
            tmpLength >>= 8;
            *(buf++) = tmpLength & 0xFF;
            tmpLength >>= 8;
            *(buf++) = tmpLength & 0xFF;
        }
    }
    
    return buf;
}



CtiIONProgram::CtiIONProgram( CtiIONStatement *initial ) :
    CtiIONValue(IONProgram)
{ 
    addStatement( initial ); 
};


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

        tmpMethod = new CtiIONMethod( byteStream, streamLength - streamPos );
        
        //  make sure we read the method out okay
        if( tmpMethod != NULL && tmpMethod->isValid( ) )
        {
            streamPos += tmpMethod->getSerializedValueLength( );

            tmpStatement = new CtiIONStatement( handle, tmpMethod );
            if( tmpStatement != NULL )
            {
                _statements.push_back( tmpStatement );
            }
        }
        else
        {
            //  read an invalid CtiIONMethod - rest of stream is likely corrupt.
            streamPos = streamLength;
            setValid( FALSE );
        }
    }
}


unsigned int CtiIONProgram::getSerializedValueLength( void )
{
    unsigned int length;
    
    for( int i = 0; i < _statements.size( ); i++ )
        length += _statements[i]->getSerializedValueLength( );
    
    return length;
}


void CtiIONProgram::putSerializedValue( unsigned char *buf )
{
    unsigned int pos = 0;
    
    for( int i = 0; i < _statements.size( ); i++ )
    {
        _statements[i]->putSerializedValue( buf + pos );
        pos += _statements[i]->getSerializedValueLength( );
    }
}



