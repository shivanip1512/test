/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuebasic_array.cpp
 *
 * Class:  CtiIONArray
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_basic_array.h"
#include "ion_valuestructtypes.h"

#include "logger.h"


CtiIONArray::CtiIONArray( IONArrayTypes arrayType ) :
    CtiIONValue(IONArray),
    _arrayType(arrayType)
{
}


CtiIONArray::~CtiIONArray( )
{
    clearArrayElements( );
}


CtiIONArray::IONArrayTypes CtiIONArray::getArrayType( void ) const
{
    return _arrayType;
}


bool CtiIONArray::isNumericArray( void )
{
    int retVal;

    switch( getArrayType( ) )
    {
        case IONBooleanArray:
        case IONFloatArray:
        case IONUnsignedIntArray:
        case IONSignedIntArray:
           retVal = true;

        default:
            retVal = false;
    }

    return retVal;
}


int CtiIONArray::size( void )
{
    return _array.size( );
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



void CtiIONArray::putSerializedValue( unsigned char *buf ) const
{
    int i, bytesWritten;

    bytesWritten = 0;
    for( i = 0; i < _array.size( ); i++ )
    {
        ((CtiIONValue *)_array[i])->putSerializedValue( buf + bytesWritten );
        bytesWritten += ((CtiIONValue *)_array[i])->getSerializedLength( );
    }
}


unsigned int CtiIONArray::getSerializedValueLength( void ) const
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


unsigned int CtiIONArray::getSerializedHeaderLength( void ) const
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


void CtiIONArray::putSerializedHeader( unsigned char *buf ) const
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


CtiIONArray *CtiIONArray::restoreStruct( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    CtiIONArray *newVal = NULL;

    newVal = CtiIONStruct::restoreObject( classDescriptor, buf, len, bytesUsed );

    return newVal;
}


CtiIONArray *CtiIONArray::restoreStructArray( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    CtiIONArray *newVal = NULL;

    newVal = CtiIONStructArray::restoreObject( classDescriptor, buf, len, bytesUsed );

    return newVal;
}


CtiIONArray *CtiIONArray::restoreFixedArray( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned char tmp4b,   //  temp 4 bit value
                  tmp8b;   //  temp 8 bit value
    unsigned long tmp32b;  //  temp 32 bit value

    unsigned long pos, itemCount, arrayLength, itemLength, totalUsed;

    CtiIONArray *newArray = NULL;

    pos = 0;

    tmp8b = buf[pos++];
    tmp4b = (tmp8b & 0xF0) >> 4;  //  high nibble of the byte

    if( tmp4b <= 0xD )
    {
        //  item count = value
        itemCount = tmp4b;
    }
    else if( tmp4b == 0xE )
    {
        //  item count = next 8 bits
        itemCount   = tmp8b & 0x0F;         //  4 bits written
        tmp8b       = buf[pos++];
        itemCount <<= 4;
        itemCount  |= (tmp8b & 0xF0) >> 4;    //  8 bits written
    }
    else  //  tmp4b == 0xF
    {
        //  item count = next 32 bits
        itemCount   = tmp8b & 0x0F;         //  4 bits written
        tmp8b       = buf[pos++];
        itemCount <<= 8;
        itemCount  |= tmp8b;                //  12 bits written
        tmp8b       = buf[pos++];
        itemCount <<= 8;
        itemCount  |= tmp8b;                //  20 bits written
        tmp8b       = buf[pos++];
        itemCount <<= 8;
        itemCount  |= tmp8b;                //  28 bits written
        tmp8b       = buf[pos++];
        itemCount <<= 4;
        itemCount  |= (tmp8b & 0xF0) >> 4;  //  32 bits written
    }

    tmp4b = tmp8b & 0x0F;

    if( tmp4b <= 0xC )
    {
        //  array length = value
        arrayLength = tmp4b;
    }
    else if( tmp4b == 0xD )
    {
        //  array length = next 8 bits
        arrayLength   = buf[pos++];
    }
    else if( tmp4b == 0xE )
    {
        //  array length = next 32 bits
        arrayLength   = buf[pos++];  //  8 bits written
        arrayLength <<= 8;
        arrayLength  |= buf[pos++];  //  16 bits written
        arrayLength <<= 8;
        arrayLength  |= buf[pos++];  //  24 bits written
        arrayLength <<= 8;
        arrayLength  |= buf[pos++];  //  32 bits written
    }
    else  //  tmp4b == 0xF - currently reserved, i don't know how to read it
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        arrayLength = 0;
        itemCount   = 0;
    }

    totalUsed = 0;

    //  careful, '%' can cause a divide-by-zero.
    if( (itemCount != 0) && ((arrayLength % itemCount) == 0) )
    {
        itemLength = arrayLength / itemCount;

        switch( classDescriptor )
        {
            case IONCharArray:          newArray = CTIDBG_new CtiIONCharArray       ((buf + pos), (len - pos), itemCount, itemLength, &totalUsed);  break;
            case IONBooleanArray:       newArray = CTIDBG_new CtiIONBooleanArray    ((buf + pos), (len - pos), itemCount, itemLength, &totalUsed);  break;
            case IONFloatArray:         newArray = CTIDBG_new CtiIONFloatArray      ((buf + pos), (len - pos), itemCount, itemLength, &totalUsed);  break;
            case IONSignedIntArray:     newArray = CTIDBG_new CtiIONSignedIntArray  ((buf + pos), (len - pos), itemCount, itemLength, &totalUsed);  break;
            case IONUnsignedIntArray:   newArray = CTIDBG_new CtiIONUnsignedIntArray((buf + pos), (len - pos), itemCount, itemLength, &totalUsed);  break;

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    //  was never assigned
    if( newArray == NULL )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        totalUsed = len - pos;
    }

    pos += totalUsed;

    *bytesUsed = pos;

    return newArray;
}


unsigned char *CtiIONArray::putClassSize( unsigned char key, unsigned char *buf ) const
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

