/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_variable.cpp
 *
 * Class:  CtiIONValueVariable
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

#include "ion_value_variable.h"
#include "ion_value_variable_boolean.h"
#include "ion_value_variable_program.h"
#include "ion_value_variable_fixedarray.h"


CtiIONValueVariable::CtiIONValueVariable( VariableTypes variableType ) :
    CtiIONValue(ValueType_Variable)
{
    _variableType = variableType;
}


CtiIONValueVariable::CtiIONValueVariable( const CtiIONValueVariable &aRef ) :
    CtiIONValue(ValueType_Variable)
{
    *this = aRef;
}


CtiIONValueVariable::~CtiIONValueVariable()
{
}


CtiIONValueVariable &CtiIONValueVariable::operator=( const CtiIONValueVariable &aRef )
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _variableType = aRef._variableType;
    }

    return *this;
}


CtiIONValueVariable::VariableTypes CtiIONValueVariable::getVariableType( void ) const
{
    return _variableType;
}


bool CtiIONValueVariable::isVariableType( VariableTypes type ) const
{
    return _variableType == type;
}


bool CtiIONValueVariable::isVariableType( CtiIONValue *toCheck, VariableTypes type )
{
    bool retVal = false;
    CtiIONValueVariable *tmpVariable;

    if( Value::isVariable(toCheck) )
    {
        tmpVariable = (CtiIONValueVariable *)toCheck;

        retVal = tmpVariable->isVariableType(type);
    }

    return retVal;
}


void CtiIONValueVariable::putSerialized( unsigned char *buf ) const
{
    unsigned int offset = 0;

    putSerializedHeader(buf + offset);

    offset += getSerializedHeaderLength();

    putSerializedValue(buf + offset);

    offset += getSerializedValueLength();
}


unsigned int CtiIONValueVariable::getSerializedLength( void ) const
{
    unsigned int length = 0;

    length += getSerializedHeaderLength();

    length += getSerializedValueLength();

    return length;
}


void CtiIONValueVariable::putSerializedHeader( unsigned char *buf ) const
{
    *buf++ = make_byte(IONClass_Variable, getVariableClassDescriptor());
}


unsigned int CtiIONValueVariable::getSerializedHeaderLength( void ) const
{
    return 1;
}


CtiIONValue *CtiIONValueVariable::restoreVariable( unsigned char ionClass, unsigned char classDescriptor,
                                                   unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos = 0;

    unsigned long itemLength;

    //  zero it out to begin with
    CtiIONValue *newObject = NULL;

    switch( classDescriptor )
    {
        //  these two cases don't use any more bytes, so pos doesn't need to be incremented
        case ClassDescriptor_Variable_BooleanFalse: newObject = CTIDBG_new CtiIONBoolean(false);    break;
        case ClassDescriptor_Variable_BooleanTrue:  newObject = CTIDBG_new CtiIONBoolean(true);     break;

        case ClassDescriptor_Variable_Program:
        {
            //  by definition, this uses the rest of the bytes
            newObject = CTIDBG_new CtiIONProgram((buf + pos), (len - pos));

            pos = len;

            break;
        }

        case ClassDescriptor_Variable_FixedArray0:
        case ClassDescriptor_Variable_FixedArray1:
        case ClassDescriptor_Variable_FixedArray2:
        case ClassDescriptor_Variable_FixedArray3:
        case ClassDescriptor_Variable_FixedArray4:
        {
            newObject = FixedArray::restoreFixedArray(classDescriptor, (buf + pos), (len - pos), &itemLength);

            pos += itemLength;

            break;
        }

        case ClassDescriptor_Variable_StructEnd:        newObject = CTIDBG_new CtiIONStructEnd();       break;
        case ClassDescriptor_Variable_StructArrayEnd:   newObject = CTIDBG_new CtiIONStructArrayEnd();  break;

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

