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

#include "ion_rootclasses.h"
#include "ion_value_basic_array.h"
#include "ion_value_basic_boolean.h"
#include "ion_value_basic_char.h"
#include "ion_value_basic_float.h"
#include "ion_value_basic_intsigned.h"
#include "ion_value_basic_intunsigned.h"
#include "ion_value_basic_program.h"
#include "ion_value_basic_time.h"


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


CtiIONValue::IONValueTypes CtiIONValue::getType( void ) const
{
    return _valueType;
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


int CtiIONValue::isValid( void )
{
    return _valid;
}


void CtiIONValue::setValid( bool valid )
{
    _valid = valid;
}


void CtiIONValue::putSerialized( unsigned char *buf ) const
{
    int offset = 0;

    putSerializedHeader( buf + offset );

    offset += getSerializedHeaderLength( );

    putSerializedValue ( buf + offset );
}


unsigned int CtiIONValue::getSerializedLength( void ) const
{
    int length = 0;

    length += getSerializedHeaderLength( );
    length += getSerializedValueLength( );

    return length;
}


void CtiIONValue::putSerializedHeader( unsigned char *buf ) const
{
    unsigned char key = 0, extendedValue;

    switch( getType( ) )
    {
        case IONChar:           key  = 0x10;    break;
        case IONFloat:          key  = 0x30;    break;
        case IONSignedInt:      key  = 0x40;    break;
        case IONTime:           key  = 0x50;    break;
        case IONUnsignedInt:    key  = 0x60;    break;

        case IONProgram:        key  = 0xF6;    break;

        case IONBoolean:
        {
            if( ((CtiIONBoolean *)this)->getValue( ) )
            {
                key = 0xf2;  //  boolean true
            }
            else
            {
                key = 0xf1;  //  boolean false
            }

            break;
        }

        case IONArray:
        {
            key = ((CtiIONArray *)this)->getArrayKey( );
            break;
        }

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    buf = putClassSize( key, buf );
}


unsigned int CtiIONValue::getSerializedHeaderLength( void ) const
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


unsigned char *CtiIONValue::putClassSize( unsigned char key, unsigned char *buf ) const
{
    unsigned long tmpLength;

    switch( key & 0xf0 )
    {
        case 0x10:  //  ACH:  replace with enums...
        case 0x30:
        case 0x40:
        case 0x50:
        case 0x60:
        {
            tmpLength = getSerializedValueLength( );

            if( tmpLength <= 12 )
            {
                key = (key & 0xF0) | (tmpLength & 0x0F);
                *buf++ = key;
            }
            else if( tmpLength <= 255 )
            {
                key = (key & 0xF0) | 0x0D;
                *buf++ = key;

                *buf++ = tmpLength & 0xFF;
            }
            else
            {
                key = (key & 0xF0) | 0x0E;
                *buf++ = key;

                *buf++ = tmpLength & 0xFF;

                tmpLength >>= 8;
                *buf++ = tmpLength & 0xFF;

                tmpLength >>= 8;
                *buf++ = tmpLength & 0xFF;

                tmpLength >>= 8;
                *buf++ = tmpLength & 0xFF;
            }

            break;
        }

        case 0xf0:
        {
            *buf++ = key;
        }


    }

    return buf;
}


CtiIONValue *CtiIONValue::restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos = 0;

    unsigned char ionClass, classDescriptor;
    unsigned long itemLength;

    //  zero it out to begin with
    CtiIONValue *newObject = NULL;


    //  ACH:  eventually make this a robust reader that won't progress beyond _len_ bytes...  otherwise could tromp a mangled buffer
    //          or do we need to worry about this?  if we've gotten a good datastream in, no CRC errors, is it safe to read blindly?
    ionClass        = (buf[pos] & 0xF0) >> 4;
    classDescriptor = (buf[pos] & 0x0F);

    pos++;

    if( ionClass < 0x07 )
    {
        if( classDescriptor <= 0x0C )
        {
            itemLength = classDescriptor;
        }
        else if( classDescriptor == 0x0D )
        {
            itemLength = buf[pos++];
        }
        else
        {
            itemLength  = buf[pos++] << 24;
            itemLength |= buf[pos++] << 16;
            itemLength |= buf[pos++] <<  8;
            itemLength |= buf[pos++];
        }

        switch( ionClass )
        {
            case 0x1:  newObject = CTIDBG_new CtiIONChar       ((buf + pos), itemLength);    break;
            case 0x3:  newObject = CTIDBG_new CtiIONFloat      ((buf + pos), itemLength);    break;
            case 0x4:  newObject = CTIDBG_new CtiIONSignedInt  ((buf + pos), itemLength);    break;
            case 0x5:  newObject = CTIDBG_new CtiIONTime       ((buf + pos), itemLength);    break;
            case 0x6:  newObject = CTIDBG_new CtiIONUnsignedInt((buf + pos), itemLength);    break;

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
    }
    else if( ionClass == 0x7 )
    {
        newObject = CtiIONArray::restoreStruct     (classDescriptor, (buf + pos), (len - pos), &itemLength);

        pos += itemLength;
    }
    else if( ionClass == 0x8 )
    {
        newObject = CtiIONArray::restoreStructArray(classDescriptor, (buf + pos), (len - pos), &itemLength);

        pos += itemLength;
    }
    else if( ionClass == 0xF )
    {
        switch( classDescriptor )
        {
            case 0x1:   newObject = CTIDBG_new CtiIONBoolean(false);   break;
            case 0x2:   newObject = CTIDBG_new CtiIONBoolean(true);    break;

            case 0x6:
            {
                //  by definition, this uses the rest of the bytes
                newObject = CTIDBG_new CtiIONProgram((buf + pos), (len - pos));

                pos = len;

                break;
            }

            case 0xA:
            case 0xB:
            case 0xC:
            case 0xD:
            case 0xE:
            {
                //  fixed size arrays
                newObject = CtiIONArray::restoreFixedArray(classDescriptor, (buf + pos), (len - pos), &itemLength);

                pos += itemLength;

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

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

