/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_struct.cpp
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

#include "ion_value_struct.h"
#include "ion_value_struct_types.h"


CtiIONStruct::CtiIONStruct( StructTypes structType, unsigned long numElements ) :
    CtiIONValue(ValueType_Struct),
    _structType(structType),
    _numElements(numElements)
{

}

CtiIONStruct::~CtiIONStruct( )
{

}


CtiIONStruct::StructTypes CtiIONStruct::getStructType( void ) const
{
    return _structType;
}


bool CtiIONStruct::isStructType( StructTypes structType ) const
{
    return _structType == structType;
}


bool CtiIONStruct::isStructType( CtiIONValue *toCheck, StructTypes structType )
{
    bool retVal = false;
    CtiIONStruct *tmpStruct;

    if( CtiIONValue::isStruct(toCheck) )
    {
        tmpStruct = (CtiIONStruct *)toCheck;

        retVal = tmpStruct->isStructType(structType);
    }

    return retVal;
}


void CtiIONStruct::init( vector< CtiIONValue * > &structValues )
{
    if( _numElements > 0 )
    {
        if( structValues.size() != _numElements )
        {
            setValid(false);
        }
    }

    for( int i = 0; i < structValues.size() && isValid(); i++ )
    {
        if( structValues[i]->isValid( ) )
        {
            _structElements.push_back(structValues[i]);
        }
        else
        {
            setValid(false);
        }
    }
}


unsigned char CtiIONStruct::getStructClassDescriptor( void ) const
{
    unsigned char retVal;

    switch( getStructType() )
    {
        case StructType_LogRecord:      retVal = ClassDescriptor_Struct_LogRecord;      break;
        case StructType_Alarm:          retVal = ClassDescriptor_Struct_Alarm;          break;
        case StructType_Event:          retVal = ClassDescriptor_Struct_Event;          break;
        case StructType_Range:          retVal = ClassDescriptor_Struct_Range;          break;
        case StructType_List:           retVal = ClassDescriptor_Struct_List;           break;
        case StructType_Exception:      retVal = ClassDescriptor_Struct_Exception;      break;
        case StructType_Waveform:       retVal = ClassDescriptor_Struct_Waveform;       break;
        case StructType_Date:           retVal = ClassDescriptor_Struct_Date;           break;
        case StructType_Calendar:       retVal = ClassDescriptor_Struct_Calendar;       break;
        case StructType_Profile:        retVal = ClassDescriptor_Struct_Profile;        break;
        case StructType_StringArray:    retVal = ClassDescriptor_Struct_StringArray;    break;

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


unsigned int CtiIONStruct::getSerializedLength( void ) const
{
    unsigned int length = 0;

    length++;  //  header byte

    length += getElementsLength();

    return length;
}


unsigned int CtiIONStruct::getElementsLength( void ) const
{
    unsigned int length = 0;
    ion_value_vector::const_iterator itr;

    for( itr = _structElements.begin(); itr != _structElements.end(); itr++ )
    {
        length += (*itr)->getSerializedLength();
    }

    length++;  //  struct end byte

    return length;
}


void CtiIONStruct::putSerialized( unsigned char *buf ) const
{
    unsigned int pos = 0;

    buf[pos++] = make_byte(IONClass_Struct, getStructClassDescriptor());

    putElements(buf + pos);

    pos += getElementsLength();
}


void CtiIONStruct::putElements( unsigned char *buf ) const
{
    unsigned int pos = 0;
    ion_value_vector::const_iterator itr;

    CtiIONStructEnd structEnd;

    for( itr = _structElements.begin(); itr != _structElements.end(); itr++ )
    {
        (*itr)->putSerialized(buf + pos);

        pos += (*itr)->getSerializedLength();
    }

    structEnd.putSerialized(buf + pos);

    pos += structEnd.getSerializedLength();
}


CtiIONValue *CtiIONStruct::at( int index )
{
    CtiIONValue *retVal = NULL;

    if( index < _structElements.size() )
    {
        retVal = _structElements[index];
    }

    return retVal;
}


CtiIONValue *CtiIONStruct::operator[]( int index )
{
    return at(index);
}


CtiIONValue *CtiIONStruct::restoreStruct( unsigned char ionClass, unsigned char classDescriptor,
                                          unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos, itemLength;

    CtiIONValue  *newValue  = NULL;
    CtiIONStruct *newStruct = NULL;

    vector< CtiIONValue * > structValues;

    pos = 0;

    while( (pos < len) && !Variable::isVariableType(newValue, Variable::Variable_StructEnd) )
    {
        newValue = Value::restoreObject((buf + pos), (len - pos), &itemLength);

        if( newValue != NULL && newValue->isValid() )
        {
            if( !Variable::isVariableType(newValue, Variable::Variable_StructEnd) )
            {
                structValues.push_back(newValue);
            }

            pos += itemLength;
        }
        else
        {
            //  on error, we assume the rest of the stream is corrupt
            pos = len;
        }
    }

    //  ACH: perhaps i shouldn't try to populate a structure on error... ?
    //         although it will fail if the elements are incorrect...
    switch( classDescriptor )
    {
        case ClassDescriptor_Struct_LogRecord:      newStruct = CTIDBG_new CtiIONLogRecord  ( structValues );  break;
        case ClassDescriptor_Struct_Alarm:          newStruct = CTIDBG_new CtiIONAlarm      ( structValues );  break;
        case ClassDescriptor_Struct_Event:          newStruct = CTIDBG_new CtiIONEvent      ( structValues );  break;
        case ClassDescriptor_Struct_Range:          newStruct = CTIDBG_new CtiIONRange      ( structValues );  break;
        case ClassDescriptor_Struct_List:           newStruct = CTIDBG_new CtiIONList       ( structValues );  break;
        case ClassDescriptor_Struct_Exception:      newStruct = CTIDBG_new CtiIONException  ( structValues );  break;
        case ClassDescriptor_Struct_Waveform:       newStruct = CTIDBG_new CtiIONWaveform   ( structValues );  break;
        case ClassDescriptor_Struct_Date:           newStruct = CTIDBG_new CtiIONDate       ( structValues );  break;
        case ClassDescriptor_Struct_Calendar:       newStruct = CTIDBG_new CtiIONCalendar   ( structValues );  break;
        case ClassDescriptor_Struct_Profile:        newStruct = CTIDBG_new CtiIONProfile    ( structValues );  break;
        case ClassDescriptor_Struct_StringArray:    newStruct = CTIDBG_new CtiIONStringArray( structValues );  break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            newStruct = NULL;
            pos = len;
        }
    }

    *bytesUsed = pos;

    return newStruct;
}

