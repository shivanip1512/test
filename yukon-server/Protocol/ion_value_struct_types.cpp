/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_struct_types.cpp
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

#include "ion_value_struct_types.h"


CtiIONLogRecord::CtiIONLogRecord( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_LogRecord, 3)
{
    //  verify that the elements are of the correct type
    if( structValues.size() == _numElements &&
        Fixed::isFixedType(structValues[LogPosition], Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[Timestamp],   Fixed::Fixed_Time)        &&
        Value::isValueType(structValues[LogValues],   Value::ValueType_Struct) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}


CtiIONLogRecord::CtiIONLogRecord( CtiIONUnsignedInt *logPosition,
                                  CtiIONTime        *timestamp,
                                  CtiIONStruct      *logValues ) :
    CtiIONStruct(StructType_LogRecord, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(logPosition);
    values.push_back(timestamp);
    values.push_back(logValues);

    //  init calls setValid depending on the validity of the contained objects
    init(values);
}



CtiIONAlarm::CtiIONAlarm( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Alarm, 3)
{
    //  verify that the elements are of the correct type
    if( structValues.size() == _numElements &&
        Fixed::isFixedType(structValues[EffectHandle], Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[Transitions],  Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[Priority],     Fixed::Fixed_UnsignedInt) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}


CtiIONAlarm::CtiIONAlarm( CtiIONUnsignedInt *effectHandle,
                          CtiIONUnsignedInt *transitions,
                          CtiIONUnsignedInt *priority ) :
    CtiIONStruct(StructType_Alarm, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(effectHandle);
    values.push_back(transitions);
    values.push_back(priority);

    //  init calls setValid depending on the validity of the contained objects
    init(values);
}



CtiIONEvent::CtiIONEvent( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Event, 6)
{
    //  verify that the elements are the correct type
    if( structValues.size( ) == _numElements &&
        Fixed::isFixedType(structValues[Priority],     Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[EventState],   Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[CauseHandle],  Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[EffectHandle], Fixed::Fixed_UnsignedInt) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init( structValues );
    }
    else
    {
        setValid(false);
    }
}


CtiIONEvent::CtiIONEvent( CtiIONUnsignedInt *priority,
                          CtiIONUnsignedInt *eventState,
                          CtiIONUnsignedInt *causeHandle,
                          CtiIONValue       *causeValue,
                          CtiIONUnsignedInt *effectHandle,
                          CtiIONValue       *effectValue ) :
    CtiIONStruct(StructType_Event, 6)
{
    vector< CtiIONValue * > values;

    values.push_back(priority);
    values.push_back(eventState);
    values.push_back(causeHandle);
    values.push_back(causeValue);
    values.push_back(effectHandle);
    values.push_back(effectValue);

    init(values);
}



CtiIONRange::CtiIONRange( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Range, 2)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        Fixed::isFixedType(structValues[RangeStart], Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[RangeEnd],   Fixed::Fixed_UnsignedInt) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}


CtiIONRange::CtiIONRange( CtiIONUnsignedInt *rangeStart,
                          CtiIONUnsignedInt *rangeEnd ) :
    CtiIONStruct(StructType_Range, 2)
{
    vector< CtiIONValue * > values;

    values.push_back(rangeStart);
    values.push_back(rangeEnd);

    init(values);
}



CtiIONList::CtiIONList( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_List, 0)
{
    //  an IONList can hold any combination of types - init will check the validity, but the
    //    only validity constraint is the sum validity of the elements
    init(structValues);
}



CtiIONException::CtiIONException( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Exception, 3)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        Fixed::isFixedType(structValues[ExceptionCode],    Fixed::Fixed_UnsignedInt) &&
        FixedArray::isFixedArrayType(structValues[Reason], FixedArray::FixedArray_Char) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}


CtiIONException::CtiIONException( CtiIONUnsignedInt *exceptionCode,
                                  CtiIONValue       *exceptionValue,
                                  CtiIONCharArray   *reason ) :
    CtiIONStruct(StructType_Exception, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(exceptionCode);
    values.push_back(exceptionValue);
    values.push_back(reason);

    init(values);
}


CtiIONWaveform::CtiIONWaveform( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Waveform, 5)
{
     //  verify that the elements are the correct type
    if( structValues.size( ) == _numElements &&
        structValues[SamplingFrequency]->isNumeric( ) &&
        structValues[Offset]->isNumeric( ) &&
        structValues[Scale]->isNumeric( ) &&
        Fixed::isFixedType(structValues[TimeOfFirstPoint], Fixed::Fixed_Time) &&
        FixedArray::isNumericArray(structValues[SamplePoints]) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}


CtiIONDate::CtiIONDate( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Date, 3)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        Fixed::isFixedType(structValues[Year],          Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[Month],         Fixed::Fixed_UnsignedInt) &&
        Fixed::isFixedType(structValues[DayOfTheMonth], Fixed::Fixed_UnsignedInt) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}

CtiIONDate::CtiIONDate( CtiIONUnsignedInt *year,
                        CtiIONUnsignedInt *month,
                        CtiIONUnsignedInt *dayOfTheMonth ) :
    CtiIONStruct(StructType_Date, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(year);
    values.push_back(month);
    values.push_back(dayOfTheMonth);

    init(values);
}



CtiIONCalendar::CtiIONCalendar( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Calendar, 2)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        Fixed::isFixedType(structValues[StartDate],            Fixed::Fixed_UnsignedInt) &&
        FixedArray::isFixedArrayType(structValues[ListOfDays], FixedArray::FixedArray_UnsignedInt) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}


CtiIONCalendar::CtiIONCalendar( CtiIONDate             *startDate,
                                CtiIONUnsignedIntArray *listOfDays ) :
    CtiIONStruct(StructType_Calendar, 2)
{
    vector< CtiIONValue * > values;

    values.push_back(startDate);
    values.push_back(listOfDays);

    init(values);
}


CtiIONProfile::CtiIONProfile( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_Profile, 2)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        FixedArray::isFixedArrayType(structValues[IndexTable],   FixedArray::FixedArray_UnsignedInt) &&
        FixedArray::isFixedArrayType(structValues[ActivityList], FixedArray::FixedArray_UnsignedInt) )
    {
        //  init calls setValid depending on the validity of the contained objects
        init(structValues);
    }
    else
    {
        setValid(false);
    }
}


CtiIONProfile::CtiIONProfile( CtiIONUnsignedIntArray *indexTable,
                              CtiIONUnsignedIntArray *activityList ) :
    CtiIONStruct(StructType_Profile, 2)
{
    vector< CtiIONValue * > values;

    values.push_back(indexTable);
    values.push_back(activityList);

    init(values);
}


CtiIONStringArray::CtiIONStringArray( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(StructType_StringArray, 0)
{
    setValid(true);

    for( int i = 0; i < structValues.size() && isValid(); i++ )
    {
        if( FixedArray::isFixedArrayType(structValues[i], FixedArray::FixedArray_Char) )
        {
            _numElements++;
        }
        else
        {
            setValid(false);
        }
    }

    if( isValid() )
    {
        init(structValues);
    }
}
