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
#include "yukon.h"


#include "ctidbgmem.h" // defines CTIDBG_new

#include "guard.h"
#include "logger.h"

#include "ion_value.h"

//  included so CtiIONValue can call the class-specific functions in restoreObject
#include "ion_value_fixed.h"
#include "ion_value_struct.h"
#include "ion_value_structarray.h"
#include "ion_value_variable.h"


CtiIONValue::CtiIONValue( ValueTypes valueType ) :
    _valueType(valueType)
{
}


CtiIONValue::~CtiIONValue( )
{
}


CtiIONValue::ValueTypes CtiIONValue::getValueType( void ) const
{
    return _valueType;
}


bool CtiIONValue::isValueType( CtiIONValue *toCheck, ValueTypes type )
{
    bool retVal = false;

    if( toCheck != NULL )
    {
        retVal = toCheck->isValueType(type);
    }

    return retVal;
}


bool CtiIONValue::isValueType( ValueTypes type ) const
{
    return _valueType == type;
}


bool CtiIONValue::isNumeric( void ) const
{
    return false;
}


double CtiIONValue::getNumericValue( void ) const
{
    return 0;
}


const char *CtiIONValue::toString( void )
{
    return "(value)";
}


bool CtiIONValue::isFixed( void ) const
{
    return getValueType() == ValueType_Fixed;
}


bool CtiIONValue::isFixed( CtiIONValue *toCheck )
{
    bool retVal = false;

    if( toCheck != NULL )
    {
        retVal = toCheck->isFixed();
    }

    return retVal;
}


bool CtiIONValue::isStruct( void ) const
{
    return getValueType() == ValueType_Struct;
}


bool CtiIONValue::isStruct( CtiIONValue *toCheck )
{
    bool retVal = false;

    if( toCheck != NULL )
    {
        retVal = toCheck->isStruct();
    }

    return retVal;
}


bool CtiIONValue::isStructArray( void ) const
{
    return getValueType() == ValueType_StructArray;
}


bool CtiIONValue::isStructArray( CtiIONValue *toCheck )
{
    bool retVal = false;

    if( toCheck != NULL )
    {
        retVal = toCheck->isStructArray();
    }

    return retVal;
}


bool CtiIONValue::isVariable( void ) const
{
    return getValueType() == ValueType_Variable;
}


bool CtiIONValue::isVariable( CtiIONValue *toCheck )
{
    bool retVal = false;

    if( toCheck != NULL )
    {
        retVal = toCheck->isVariable();
    }

    return retVal;
}


bool CtiIONValue::isValid( void ) const
{
    return _valid;
}


CtiIONValue &CtiIONValue::setValid( bool valid )
{
    _valid = valid;

    return *this;
}


CtiIONValue *CtiIONValue::restoreObject( unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos = 0;

    unsigned char ionClass, classDescriptor;
    unsigned long tmpLength;

    //  zero it out to begin with
    CtiIONValue *newObject = NULL;


    //  ACH:  eventually make this a robust reader that won't progress beyond _len_ bytes...  otherwise could tromp a mangled buffer
    //          or do we need to worry about this?  if we've gotten a good datastream in, no CRC errors, is it safe to read blindly?
    ionClass        = (buf[pos] & 0xf0) >> 4;
    classDescriptor = (buf[pos] & 0x0f);

    pos++;

    switch( ionClass )
    {
        case IONClass_Fixed0:
        case IONClass_Fixed1:
        case IONClass_Fixed2:
        case IONClass_Fixed3:
        case IONClass_Fixed4:
        {
            newObject = Fixed::restoreFixed(ionClass, classDescriptor, (buf + pos), (len - pos), &tmpLength);

            pos += tmpLength;

            break;
        }

        case IONClass_VariableSize:
        {
            newObject = Variable::restoreVariable(ionClass, classDescriptor, (buf + pos), (len - pos), &tmpLength);

            pos += tmpLength;

            break;
        }

        case IONClass_Struct:
        {
            newObject = Struct::restoreStruct(ionClass, classDescriptor, (buf + pos), (len - pos), &tmpLength);

            pos += tmpLength;

            break;
        }

        case IONClass_StructArray:
        {
            newObject = StructArray::restoreStructArray(ionClass, classDescriptor, (buf + pos), (len - pos), &tmpLength);

            pos += tmpLength;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            pos = len;
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

