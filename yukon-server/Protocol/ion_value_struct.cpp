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
        case StructType_LogRecord:      retVal = StructClassDescriptor_LogRecord;   break;
        case StructType_Alarm:          retVal = StructClassDescriptor_Alarm;       break;
        case StructType_Event:          retVal = StructClassDescriptor_Event;       break;
        case StructType_Range:          retVal = StructClassDescriptor_Range;       break;
        case StructType_List:           retVal = StructClassDescriptor_List;        break;
        case StructType_Exception:      retVal = StructClassDescriptor_Exception;   break;
        case StructType_Waveform:       retVal = StructClassDescriptor_Waveform;    break;
        case StructType_Date:           retVal = StructClassDescriptor_Date;        break;
        case StructType_Calendar:       retVal = StructClassDescriptor_Calendar;    break;
        case StructType_Profile:        retVal = StructClassDescriptor_Profile;     break;
        case StructType_StringArray:    retVal = StructClassDescriptor_StringArray; break;

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

    for( ionStructIterator_const itr = _structElements.begin(); itr != _structElements.end(); itr++ )
    {
        length += (*itr)->getSerializedLength();
    }

    length++;  //  struct end byte

    return length;
}


void CtiIONStruct::putSerialized( unsigned char *buf ) const
{
    unsigned int pos = 0;
    unsigned char tmp;

    CtiIONStructEnd structEnd;

    buf[pos++] = make_byte(IONClass_Struct, getStructClassDescriptor());

    for( ionStructIterator_const itr = _structElements.begin(); itr != _structElements.end(); itr++ )
    {
        (*itr)->putSerialized(buf + pos);

        pos += (*itr)->getSerializedLength();
    }

    structEnd.putSerialized(buf + pos);

    pos += structEnd.getSerializedLength();
}


CtiIONValue *CtiIONStruct::operator[]( Elements index )
{
    CtiIONValue *retVal = NULL;

    if( index < _structElements.size() )
    {
        retVal = _structElements[index];
    }

    return retVal;
}


CtiIONValue *CtiIONStruct::restoreStruct( unsigned char ionClass, unsigned char classDescriptor,
                                          unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos, itemLength;

    CtiIONValue  *newValue;
    CtiIONStruct *newStruct;

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
    switch( classDescriptor )
    {
        case StructClassDescriptor_LogRecord:      newStruct = CTIDBG_new CtiIONLogRecord( structValues );    break;
        case StructClassDescriptor_Alarm:          newStruct = CTIDBG_new CtiIONAlarm( structValues );        break;
        case StructClassDescriptor_Event:          newStruct = CTIDBG_new CtiIONEvent( structValues );        break;
        case StructClassDescriptor_Range:          newStruct = CTIDBG_new CtiIONRange( structValues );        break;
        case StructClassDescriptor_List:           newStruct = CTIDBG_new CtiIONList( structValues );         break;
        case StructClassDescriptor_Exception:      newStruct = CTIDBG_new CtiIONException( structValues );    break;
        case StructClassDescriptor_Waveform:       newStruct = CTIDBG_new CtiIONWaveform( structValues );     break;
        case StructClassDescriptor_Date:           newStruct = CTIDBG_new CtiIONDate( structValues );         break;
        case StructClassDescriptor_Calendar:       newStruct = CTIDBG_new CtiIONCalendar( structValues );     break;
        case StructClassDescriptor_Profile:        newStruct = CTIDBG_new CtiIONProfile( structValues );      break;
        case StructClassDescriptor_StringArray:    newStruct = CTIDBG_new CtiIONStringArray( structValues );  break;

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

