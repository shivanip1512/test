/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_fixed.cpp
 *
 * Class:  CtiIONValueFixed
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

#include "ion_value_fixed.h"
#include "ion_value_fixed_char.h"
#include "ion_value_fixed_float.h"
#include "ion_value_fixed_intsigned.h"
#include "ion_value_fixed_intunsigned.h"
#include "ion_value_fixed_time.h"


CtiIONValueFixed::CtiIONValueFixed( FixedTypes fixedType ) :
    CtiIONValue(ValueType_Fixed)
{
    _fixedType = fixedType;
}


CtiIONValueFixed::CtiIONValueFixed( const CtiIONValueFixed &aRef ) :
    CtiIONValue(ValueType_Fixed)
{
    *this = aRef;
}


CtiIONValueFixed::~CtiIONValueFixed()
{
}


CtiIONValueFixed &CtiIONValueFixed::operator=( const CtiIONValueFixed &aRef )
{
    if(this != &aRef)
    {
        _fixedType = aRef._fixedType;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}


bool CtiIONValueFixed::isFixedType( CtiIONValue *toCheck, FixedTypes type )
{
    bool retVal = false;
    CtiIONValueFixed *tmpFixed;

    if( toCheck != NULL && toCheck->isFixed() )
    {
        tmpFixed = (CtiIONValueFixed *)toCheck;

        retVal   = tmpFixed->isFixedType(type);
    }

    return retVal;
}


bool CtiIONValueFixed::isFixedType( FixedTypes type ) const
{
    return getFixedType() == type;
}


CtiIONValueFixed::FixedTypes CtiIONValueFixed::getFixedType( void ) const
{
    return _fixedType;
}


unsigned char CtiIONValueFixed::getFixedIONClassType( void ) const
{
    unsigned char retVal;

    switch( getFixedType() )
    {
        case Fixed_Char:        retVal = IONClass_Char;         break;
        case Fixed_Time:        retVal = IONClass_Time;         break;
        case Fixed_Float:       retVal = IONClass_Float;        break;
        case Fixed_SignedInt:   retVal = IONClass_SignedInt;    break;
        case Fixed_UnsignedInt: retVal = IONClass_UnsignedInt;  break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0xff;

            break;
        }
    }

    return retVal;
}


void CtiIONValueFixed::putSerialized( unsigned char *buf ) const
{
    unsigned int offset = 0;

    putSerializedHeader(buf + offset);

    offset += getSerializedHeaderLength();

    putSerializedValue(buf + offset);

    offset += getSerializedValueLength();
}


unsigned int CtiIONValueFixed::getSerializedLength( void ) const
{
    unsigned int length = 0;

    length += getSerializedHeaderLength();

    length += getSerializedValueLength();

    return length;
}


void CtiIONValueFixed::putSerializedHeader( unsigned char *buf ) const
{
    unsigned char classDescriptor, tmpLength;

    tmpLength = getSerializedValueLength( );

    if( tmpLength <= 12 )
    {
        *buf++ = make_byte(getFixedIONClassType(), tmpLength);
    }
    else if( tmpLength <= 255 )
    {
        *buf++ = make_byte(getFixedIONClassType(), ClassDescriptor_Fixed_LengthNextByte);

        *buf++ = tmpLength & 0xFF;
    }
    else
    {
        *buf++ = make_byte(getFixedIONClassType(), ClassDescriptor_Fixed_LengthNext4Bytes);

        *buf++ = tmpLength & 0xFF;

        tmpLength >>= 8;
        *buf++ = tmpLength & 0xFF;

        tmpLength >>= 8;
        *buf++ = tmpLength & 0xFF;

        tmpLength >>= 8;
        *buf++ = tmpLength & 0xFF;
    }
}


unsigned int CtiIONValueFixed::getSerializedHeaderLength( void ) const
{
    unsigned long tmpLength;
    unsigned int  tmpHeaderLength;

    tmpLength = getSerializedValueLength( );

    if( tmpLength <= 12 )
    {
        tmpHeaderLength = 1;
    }
    else if( tmpLength <= 255 )
    {
        tmpHeaderLength = 2;
    }
    else
    {
        tmpHeaderLength = 5;
    }

    return tmpHeaderLength;
}


CtiIONValue *CtiIONValueFixed::restoreFixed( unsigned char ionClass, unsigned char classDescriptor,
                                             unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos = 0;
    unsigned long itemLength;

    //  zero it out to begin with
    CtiIONValue *newObject = NULL;


    if( classDescriptor <= ClassDescriptor_Fixed_LengthNibbleMax )
    {
        itemLength = classDescriptor;
    }
    else if( classDescriptor == ClassDescriptor_Fixed_LengthNextByte )
    {
        itemLength = buf[pos++];
    }
    else  //  if( classDescriptor == ClassDescriptor_Fixed_LengthNext4Bytes )
    {
        itemLength  = buf[pos++] << 24;
        itemLength |= buf[pos++] << 16;
        itemLength |= buf[pos++] <<  8;
        itemLength |= buf[pos++];
    }

    switch( ionClass )
    {
        case IONClass_Char:           newObject = CTIDBG_new CtiIONChar       ((buf + pos), itemLength);    break;
        case IONClass_Float:          newObject = CTIDBG_new CtiIONFloat      ((buf + pos), itemLength);    break;
        case IONClass_SignedInt:      newObject = CTIDBG_new CtiIONSignedInt  ((buf + pos), itemLength);    break;
        case IONClass_UnsignedInt:    newObject = CTIDBG_new CtiIONUnsignedInt((buf + pos), itemLength);    break;
        case IONClass_Time:           newObject = CTIDBG_new CtiIONTime       ((buf + pos), itemLength);    break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            itemLength = len - pos;
        }
    }

    pos += itemLength;

    //  if we've successfully read something off the bytestream
    if( newObject != NULL && newObject->isValid( ) )
    {
        //  set us to the end of the successfully read bytes
        *bytesUsed = pos;
    }
    else
    {
        //  set us to the end of the input, we're apparently corrupt (so the following bytes would be, too)
        *bytesUsed = len;
    }

    return newObject;
}

