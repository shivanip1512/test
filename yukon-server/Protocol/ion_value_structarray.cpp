/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_structarray.cpp
 *
 * Class:
 * Date:   08/29/2001
 *
 * Author: Matthew Fisher
 *
 *         Implementation for ION struct value types
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "logger.h"

#include "ion_value_structarray.h"



CtiIONStructArray::CtiIONStructArray( StructArrayTypes structArrayType ) :
    CtiIONValue(ValueType_StructArray),
    _structArrayType(structArrayType)
{

}


CtiIONStructArray::~CtiIONStructArray( )
{

}


unsigned char CtiIONStructArray::getStructArrayKey( void ) const
{
    unsigned char key;

    key  = 0x90;
    key |= getStructArrayType();

    return key;
}


CtiIONStructArray::StructArrayTypes CtiIONStructArray::getStructArrayType( void ) const
{
    return _structArrayType;
}


bool CtiIONStructArray::isStructArrayType( StructArrayTypes structArrayType ) const
{
    return _structArrayType == structArrayType;
}


bool CtiIONStructArray::isStructArrayType( CtiIONValue *toCheck, StructArrayTypes structArrayType )
{
    bool retVal = false;
    CtiIONStructArray *tmpStructArray;

    if( CtiIONValue::isStructArray(toCheck) )
    {
        tmpStructArray = (CtiIONStructArray *)toCheck;

        retVal = tmpStructArray->isStructArrayType(structArrayType);
    }

    return retVal;
}


CtiIONValue *CtiIONStructArray::restoreStructArray( unsigned char ionClass, unsigned char classDescriptor,
                                                    unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos, itemLength;

    CtiIONValue       *newValue;
    CtiIONStructArray *newStructArray = NULL;

    vector< CtiIONValue * > structValues;

/*
    pos = 0;
    while( (buf[pos] != 0xF9) && (pos < len) )
    {
        newValue = Value::restoreObject((buf + pos), (len - pos), &itemLength);

        if( newValue != NULL && newValue->isValid() && (((CtiIONStruct *)newValue)->getStructType( ) == classDescriptor) )
        {
            structValues.push_back(newValue);
            pos += itemLength;
        }
        else
        {
            pos = len;
        }
    }

    if( buf[pos] == 0xF9 )
    {
        pos++;

        //  ACH:  define all struct array types

        switch( classDescriptor )
        {
            case IONLogArray:           newStructArray = CTIDBG_new CtiIONLogArray( structValues );         break;
            case IONAlarmArray:         newStructArray = CTIDBG_new CtiIONAlarmArray( structValues );       break;
            case IONStringArrayArray:   newStructArray = CTIDBG_new CtiIONStringArrayArray( structValues ); break;
            case IONMultiArray:         newStructArray = CTIDBG_new CtiIONMultiArray( structValues );       break;

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                newStructArray = NULL;
            }
        }
    }

    //*bytesUsed = pos;
*/

    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        *bytesUsed = len;
    }

    return newStructArray;
}

