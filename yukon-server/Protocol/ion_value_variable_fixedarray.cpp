/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_variable_fixedarray.cpp
 *
 * Class:  CtiIONFixedArray
 * Date:   07/13/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "logger.h"

#include "dllbase.h"
#include "ctidbgmem.h" // defines CTIDBG_new

#include "ion_value_variable_fixedarray.h"


CtiIONFixedArray::CtiIONFixedArray( ) :
    CtiIONValueVariable(Variable_FixedArray)
{

}


CtiIONFixedArray::~CtiIONFixedArray( )
{
    clear( );
}


void CtiIONFixedArray::clear( void )
{
    while( !_array.empty() )
    {
        delete _array.back();

        _array.pop_back();
    }
}


int CtiIONFixedArray::size( void )
{
    return _array.size( );
}


void CtiIONFixedArray::setFixedArrayType( FixedArrayTypes type )
{
    _arrayType = type;
}


CtiIONFixedArray::FixedArrayTypes CtiIONFixedArray::getFixedArrayType( void ) const
{
    return _arrayType;
}


bool CtiIONFixedArray::isFixedArrayType( CtiIONValue *toCheck, FixedArrayTypes arrayType )
{
    bool retVal = false;

    CtiIONFixedArray *tmpArray;

    if( Variable::isVariableType(toCheck, Variable_FixedArray) )
    {
        tmpArray = (CtiIONFixedArray *)toCheck;

        retVal = tmpArray->isFixedArrayType(arrayType);
    }

    return retVal;
}


bool CtiIONFixedArray::isFixedArrayType( FixedArrayTypes arrayType ) const
{
    return arrayType == _arrayType;
}


bool CtiIONFixedArray::isNumericArray( void ) const
{
    bool retVal;

    switch( getFixedArrayType( ) )
    {
        case FixedArray_Boolean:
        case FixedArray_Float:
        case FixedArray_UnsignedInt:
        case FixedArray_SignedInt:
        {
            retVal = true;

            break;
        }

        default:
        {
            retVal = false;

            break;
        }
    }

    return retVal;
}


bool CtiIONFixedArray::isNumericArray( CtiIONValue *toCheck )
{
    bool retVal = false;
    CtiIONFixedArray *tmpFixedArray;

    if( Variable::isVariableType(toCheck, Variable_FixedArray) )
    {
        tmpFixedArray = (CtiIONFixedArray *)toCheck;

        retVal = tmpFixedArray->isNumericArray();
    }

    return retVal;
}


unsigned int CtiIONFixedArray::getSerializedValueLength( void ) const
{
    unsigned int retVal = 0;

    retVal += getArrayHeaderLength();
    retVal += getArrayElementsLength();

    return retVal;
}


void CtiIONFixedArray::putSerializedValue( unsigned char *buf ) const
{
    unsigned int offset = 0;

    putArrayHeader(buf + offset);
    offset += getArrayHeaderLength();

    putArrayElements(buf + offset);
    offset += getArrayElementsLength();
}


unsigned int CtiIONFixedArray::getArrayHeaderLength( void ) const
{
    unsigned int tmpHeaderLength;
    unsigned long tmpLength = getArrayElementsLength( );
    unsigned long tmpItems  = _array.size( );

    tmpHeaderLength = 1;

    if( tmpItems > 255 )
    {
        tmpHeaderLength += 4;
    }
    else if( tmpItems > ItemsDescriptor_ItemsNibbleMax )
    {
        tmpHeaderLength++;
    }

    if( tmpLength > 255 )
    {
        tmpHeaderLength += 4;
    }
    else if( tmpLength > LengthDescriptor_LengthNibbleMax )
    {
        tmpHeaderLength++;
    }

    return tmpHeaderLength;
}


void CtiIONFixedArray::putArrayHeader( unsigned char *buf ) const
{
    unsigned long tmpLength, pos;
    unsigned long tmpItems;

    unsigned char tmpbuf, tmp8b, tmp32b[4];

    tmpLength = getArrayElementsLength( );
    tmpItems  = _array.size( );

    pos = 0;

    //  notation following - (nibble 1, nibble 0)

    memcpy( tmp32b, &tmpItems, 4 );

    if( tmpItems <= ItemsDescriptor_ItemsNibbleMax )
    {
        tmp8b = (tmp32b[0] & 0x0F) << 4;   //  n1 = item count
        //  note: tmp8b == ([item count],[0x0])
    }
    else if( tmpItems <= 255 )
    {
        tmp8b  = ItemsDescriptor_ItemsNextByte << 4;                 //  n1 = header key
        tmp8b |= (tmp32b[0] & 0xF0) >> 4;  //  n0 = item count (bits 7-4)
        buf[pos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[0] & 0x0F) << 4;  //  n1 = item count (bits 3-0)
        //  note: tmp8b == ([item count bits 3-0],[0x0])
    }
    else
    {
        tmp8b  = ItemsDescriptor_ItemsNext4Bytes << 4;                 //  n1 = header key
        tmp8b |= (tmp32b[3] & 0xF0) >> 4;  //  n0 = item count (bits 31-28)
        buf[pos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[3] & 0x0F) << 4;  //  n1 = item count (bits 27-24)
        tmp8b |= (tmp32b[2] & 0xF0) >> 4;  //  n0 = item count (bits 23-20)
        buf[pos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[2] & 0x0F) << 4;  //  n1 = item count (bits 19-16)
        tmp8b |= (tmp32b[1] & 0xF0) >> 4;  //  n0 = item count (bits 15-12)
        buf[pos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[1] & 0x0F) << 4;  //  n1 = item count (bits 11- 8)
        tmp8b |= (tmp32b[0] & 0xF0) >> 4;  //  n0 = item count (bits  7- 4)
        buf[pos++] = tmp8b;          //  ------ (n1,n0) written

        tmp8b  = (tmp32b[0] & 0x0F) << 4;  //  n1 = item count (bits  3- 0)
        //  note:  tmp8b == ([item count bits 3-0],[0x0])
    }


    memcpy( tmp32b, &tmpLength, 4 );

    if( tmpLength <= LengthDescriptor_LengthNibbleMax )
    {
        tmp8b |= tmp32b[0] & 0x0F;  //  n0 = length
        buf[pos++] = tmp8b;   //  ------ (n1,n0) written
    }
    else if( tmpLength <= 255 )
    {
        tmp8b |= LengthDescriptor_LengthNextByte;              //  n0 = header key
        buf[pos++] = tmp8b;   //  ------ (n1,n0) written

        buf[pos++] = tmp32b[0];  //  byte 0 written
    }
    else
    {
        tmp8b |= LengthDescriptor_LengthNext4Bytes;              //  n0 = header key
        buf[pos++] = tmp8b;   //  ------ (n1,n0) written

        buf[pos++] = tmp32b[3];  //  byte 3 written
        buf[pos++] = tmp32b[2];  //  byte 2 written
        buf[pos++] = tmp32b[1];  //  byte 1 written
        buf[pos++] = tmp32b[0];  //  byte 0 written
    }
}


unsigned int CtiIONFixedArray::getArrayElementsLength( void ) const
{
    int retVal = 0;

    //  to protect against accessing an invalid element
    if( _array.size() > 0 )
    {
        retVal += _array.size() * _array[0]->getElementLength();
    }

    return retVal;
}


void CtiIONFixedArray::putArrayElements( unsigned char *buf ) const
{
    unsigned int pos = 0;

    for( int i = 0; i < _array.size(); i++ )
    {
        _array[i]->putElement(buf + pos);
        pos += _array[i]->getElementLength();
    }
}

unsigned char CtiIONFixedArray::getVariableClassDescriptor( void ) const
{
    unsigned char desc;

    switch( getFixedArrayType() )
    {
        case FixedArray_Boolean:        desc = ClassDescriptor_FixedArray_Boolean;       break;
        case FixedArray_Char:           desc = ClassDescriptor_FixedArray_Char;          break;
        case FixedArray_Float:          desc = ClassDescriptor_FixedArray_Float;         break;
        case FixedArray_SignedInt:      desc = ClassDescriptor_FixedArray_SignedInt;     break;
        case FixedArray_UnsignedInt:    desc = ClassDescriptor_FixedArray_UnsignedInt;   break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            desc = ClassDescriptor_FixedArray_Char;

            break;
        }
    }

    return desc;
}


CtiIONValueVariable *CtiIONFixedArray::restoreFixedArray( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned char tmp4b,   //  temp 4 bit value
                  tmp8b;   //  temp 8 bit value
    unsigned long tmp32b;  //  temp 32 bit value

    unsigned long pos, itemCount, arrayLength, itemLength, totalUsed;

    CtiIONValueVariable *newArray = NULL;

    pos = 0;

    tmp8b = buf[pos++];
    tmp4b = (tmp8b & 0xF0) >> 4;  //  high nibble of the byte

/*    //  ACH FIXME YADDA YADDA:  this is a bigtime workaround for a bug in the ION's firmware.
    //                            it sends an incorrect itemlength/arraysize descriptor for
    //                            arrays of 13 items and 13 bytes long.
    //                            it should be 0xdd 0x0d, but they screwed up and crammed it all
    //                            into one byte.
    //    WARNING:  this WILL break if they fix the firmware.
    if( classDescriptor == ClassDescriptor_FixedArray_Char &&
        tmp8b == 0xdd )
    {
        itemCount   = 0x0d;
        arrayLength = 0x0d;
    }
    else*/
    {
        if( tmp4b <= ItemsDescriptor_ItemsNibbleMax )
        {
            //  item count = value
            itemCount = tmp4b;
        }
        else if( tmp4b == ItemsDescriptor_ItemsNextByte )
        {
            //  item count = next 8 bits
            itemCount   = tmp8b & 0x0F;         //  4 bits written
            tmp8b       = buf[pos++];
            itemCount <<= 4;
            itemCount  |= (tmp8b & 0xF0) >> 4;    //  8 bits written
        }
        else  //  if( tmp4b == ItemsDescriptor_ItemsNext4Bytes )
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

        if( tmp4b <= LengthDescriptor_LengthNibbleMax )
        {
            //  array length = value
            arrayLength = tmp4b;
        }
        else if( tmp4b == LengthDescriptor_LengthNextByte )
        {
            //  array length = next 8 bits
            arrayLength   = buf[pos++];
        }
        else if( tmp4b == LengthDescriptor_LengthNext4Bytes )
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
        else  //  tmp4b == LengthDescriptor_Reserved
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "arrayLength = 0xf, reserved" << endl;
            }

            itemCount   = 0;
            arrayLength = 0;
        }
    }

    totalUsed = 0;

    //  careful, '%' can cause a divide-by-zero.
    if( (itemCount != 0) && ((arrayLength % itemCount) == 0) )
    {
        itemLength = arrayLength / itemCount;

        switch( classDescriptor )
        {
            case ClassDescriptor_FixedArray_Char:          newArray = CTIDBG_new CtiIONCharArray       (itemCount, itemLength, (buf + pos), (len - pos), &totalUsed);  break;
            case ClassDescriptor_FixedArray_Boolean:       newArray = CTIDBG_new CtiIONBooleanArray    (itemCount, itemLength, (buf + pos), (len - pos), &totalUsed);  break;
            case ClassDescriptor_FixedArray_Float:         newArray = CTIDBG_new CtiIONFloatArray      (itemCount, itemLength, (buf + pos), (len - pos), &totalUsed);  break;
            case ClassDescriptor_FixedArray_SignedInt:     newArray = CTIDBG_new CtiIONSignedIntArray  (itemCount, itemLength, (buf + pos), (len - pos), &totalUsed);  break;
            case ClassDescriptor_FixedArray_UnsignedInt:   newArray = CTIDBG_new CtiIONUnsignedIntArray(itemCount, itemLength, (buf + pos), (len - pos), &totalUsed);  break;

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
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    //  was never assigned
    if( newArray == NULL )
    {
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //  we shouldn't do this - the stream isn't necessarily corrupt.  if the array is zero-length, we should
        //    return a zero-length array, not a NULL.
        //    however, to make this change, we'd have to ensure that all instances that use *Arrays would check for
        //    zero length...  that isn't something i'm willing to do in a branch (or when i can't explicitly direct
        //    the QA person to test it).
        totalUsed = len - pos;
    }

    pos += totalUsed;

    *bytesUsed = pos;

    return newArray;
}





