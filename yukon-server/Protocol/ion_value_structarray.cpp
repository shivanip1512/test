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
#include "yukon.h"

#include "ctidbgmem.h" // defines CTIDBG_new
#include "logger.h"

#include "ion_value_structarray.h"
#include "ion_value_struct.h"



CtiIONStructArray::CtiIONStructArray( ) :
    CtiIONValue(ValueType_StructArray)
{

}


CtiIONStructArray::~CtiIONStructArray( )
{

}


void CtiIONStructArray::push_back( CtiIONStruct *toAppend )
{
    if( toAppend != NULL )
    {
        _structArrayElements.push_back(toAppend);
    }
}


void CtiIONStructArray::clear( void )
{
    while( !_structArrayElements.empty() )
    {
        delete _structArrayElements.back();

        _structArrayElements.pop_back();
    }
}


int CtiIONStructArray::size( void )
{
    return _structArrayElements.size();
}


CtiIONStructArray::const_iterator CtiIONStructArray::begin( void )
{
    return _structArrayElements.begin();
}


CtiIONStructArray::const_iterator CtiIONStructArray::end( void )
{
    return _structArrayElements.end();
}


CtiIONStructArray::StructArrayTypes CtiIONStructArray::getStructArrayType( void ) const
{
    return _structArrayType;
}


void CtiIONStructArray::setStructArrayType( StructArrayTypes structArrayType )
{
    _structArrayType = structArrayType;
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


unsigned char CtiIONStructArray::getStructArrayClassDescriptor( void ) const
{
    unsigned char retVal;

    switch( getStructArrayType() )
    {
        case StructArrayType_AlarmArray:    retVal = ClassDescriptor_StructArray_Alarm;         break;
        case StructArrayType_LogArray:      retVal = ClassDescriptor_StructArray_LogRecord;     break;

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


unsigned int CtiIONStructArray::getSerializedLength( void ) const
{
    unsigned int length = 0;
    ion_struct_vector::const_iterator itr;

    length++;  //  header byte

    for( itr = _structArrayElements.begin(); itr != _structArrayElements.end(); itr++ )
    {
        length += (*itr)->getElementsLength();
    }

    length++;  //  structarray end byte

    return length;
}


void CtiIONStructArray::putSerialized( unsigned char *buf ) const
{
    unsigned int pos = 0;
    ion_struct_vector::const_iterator itr;

    CtiIONStructArrayEnd structArrayEnd;

    buf[pos++] = make_byte(IONClass_StructArray, getStructArrayClassDescriptor());

    for( itr = _structArrayElements.begin(); itr != _structArrayElements.end(); itr++ )
    {
        (*itr)->putElements(buf + pos);

        pos += (*itr)->getElementsLength();
    }

    structArrayEnd.putSerialized(buf + pos);

    pos += structArrayEnd.getSerializedLength();
}


CtiIONValue *CtiIONStructArray::restoreStructArray( unsigned char ionClass, unsigned char classDescriptor,
                                                    unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos, itemLength;

    CtiIONValue       *newValue;
    CtiIONStructArray *newStructArray = NULL;

    vector< CtiIONStruct * > structArrayElements;

    pos = 0;

    while( (buf[pos] != StructArrayEnd) && (pos < len) )
    {
        newValue = Struct::restoreStruct(Struct::IONClass_Struct, classDescriptor, (buf + pos), (len - pos), &itemLength);

        if( Struct::isStructType(newValue, (Struct::StructTypes)classDescriptor) )
        {
            structArrayElements.push_back((CtiIONStruct *)newValue);
            pos += itemLength;
        }
        else
        {
            pos = len;
        }
    }

    if( pos < len && buf[pos] == StructArrayEnd )
    {
        pos++;

        //  ACH:  define all struct array types

        switch( classDescriptor )
        {
            case ClassDescriptor_StructArray_Alarm:     newStructArray = CTIDBG_new CtiIONAlarmArray(); break;
            case ClassDescriptor_StructArray_LogRecord: newStructArray = CTIDBG_new CtiIONLogArray();   break;

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                newStructArray = NULL;
            }
        }

        if( newStructArray != NULL )
        {
            newStructArray->init(structArrayElements);
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        while( !structArrayElements.empty() )
        {
            delete structArrayElements.back();

            structArrayElements.pop_back();
        }
    }

    *bytesUsed = pos;

    return newStructArray;
}


void CtiIONStructArray::init( ion_struct_vector structElements )
{
    ion_struct_vector::const_iterator itr;

    for( itr = structElements.begin(); itr != structElements.end(); itr++ )
    {
        push_back(*itr);
    }
}

