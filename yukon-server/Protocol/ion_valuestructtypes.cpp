/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuestructtypes.cpp
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

#include "ion_valuestructtypes.h"


CtiIONLogRecord::CtiIONLogRecord( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONLogRecord, 3)
{
    //  verify that the elements are of the correct type
    if( structValues.size() == _numElements &&
        structValues[LogPosition]->getType() == IONUnsignedInt &&
        structValues[Timestamp]->getType()   == IONTime        &&
        structValues[LogValues]->getType()   == IONArray       &&
            ((CtiIONArray *)structValues[LogValues])->getArrayType() == IONStruct )
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
    CtiIONStruct(IONLogRecord, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(logPosition);
    values.push_back(timestamp);
    values.push_back(logValues);

    //  init calls setValid depending on the validity of the contained objects
    init(values);
}



CtiIONAlarm::CtiIONAlarm( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONAlarm, 3)
{
    //  verify that the elements are of the correct type
    if( structValues.size() == _numElements &&
        structValues[EffectHandle]->getType() == IONUnsignedInt &&
        structValues[Transitions]->getType()  == IONUnsignedInt &&
        structValues[Priority]->getType()     == IONUnsignedInt )
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
    CtiIONStruct(IONAlarm, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(effectHandle);
    values.push_back(transitions);
    values.push_back(priority);

    //  init calls setValid depending on the validity of the contained objects
    init(values);
}



CtiIONEvent::CtiIONEvent( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONEvent, 6)
{
    //  verify that the elements are the correct type
    if( structValues.size( ) == _numElements &&
        structValues[Priority]->getType( )     == IONUnsignedInt &&
        structValues[EventState]->getType( )   == IONUnsignedInt &&
        structValues[CauseHandle]->getType( )  == IONUnsignedInt &&
        structValues[EffectHandle]->getType( ) == IONUnsignedInt )
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
    CtiIONStruct(IONEvent, 6)
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
    CtiIONStruct(IONRange, 2)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        structValues[RangeStart]->getType() == IONUnsignedInt &&
        structValues[RangeEnd]->getType()   == IONUnsignedInt )
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
    CtiIONStruct(IONRange, 2)
{
    vector< CtiIONValue * > values;

    values.push_back(rangeStart);
    values.push_back(rangeEnd);

    init(values);
}



CtiIONList::CtiIONList( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONList)
{
    //  an IONList can hold any combination of types - init will check the validity, but the
    //    only validity constraint is the sum validity of the elements
    init(structValues);
}



CtiIONException::CtiIONException( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONException, 3)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        structValues[ExceptionCode]->getType() == IONUnsignedInt &&
        structValues[Reason]->getType()        == IONArray &&
             ((CtiIONArray *)structValues[Reason])->getArrayType() == IONCharArray )
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
    CtiIONStruct(IONException, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(exceptionCode);
    values.push_back(exceptionValue);
    values.push_back(reason);

    init(values);
}



CtiIONWaveform::CtiIONWaveform( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONWaveform, 5)
{
     //  verify that the elements are the correct type
    if( structValues.size( ) == _numElements &&
        structValues[SamplingFrequency]->isNumeric( ) &&
        structValues[Offset]->isNumeric( ) &&
        structValues[Scale]->isNumeric( ) &&
        structValues[TimeOfFirstPoint]->getType( ) == IONTime &&
        structValues[SamplePoints]->getType( )     == IONArray &&
             ((CtiIONArray *)structValues[SamplePoints])->isNumericArray( ) )
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
    CtiIONStruct(IONDate, 3)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        structValues[Year]->getType()          == IONUnsignedInt &&
        structValues[Month]->getType()         == IONUnsignedInt &&
        structValues[DayOfTheMonth]->getType() == IONUnsignedInt )
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
    CtiIONStruct(IONDate, 3)
{
    vector< CtiIONValue * > values;

    values.push_back(year);
    values.push_back(month);
    values.push_back(dayOfTheMonth);

    init(values);
}



CtiIONCalendar::CtiIONCalendar( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONCalendar, 2)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        structValues[StartDate]->getType()  == IONUnsignedInt &&
        structValues[ListOfDays]->getType() == IONArray &&
        ((CtiIONArray *)structValues[ListOfDays])->getArrayType() == IONUnsignedIntArray )
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
    CtiIONStruct(IONCalendar, 2)
{
    vector< CtiIONValue * > values;

    values.push_back(startDate);
    values.push_back(listOfDays);

    init(values);
}


CtiIONProfile::CtiIONProfile( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONProfile, 2)
{
    //  verify that the elements are the correct type
    if( structValues.size() == _numElements &&
        structValues[IndexTable]->getType()   == IONArray &&
        structValues[ActivityList]->getType() == IONArray &&
        ((CtiIONArray *)structValues[IndexTable])->getArrayType()   == IONUnsignedIntArray &&
        ((CtiIONArray *)structValues[ActivityList])->getArrayType() == IONUnsignedIntArray )
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
    CtiIONStruct(IONProfile, 2)
{
    vector< CtiIONValue * > values;

    values.push_back(indexTable);
    values.push_back(activityList);

    init(values);
}


CtiIONStringArray::CtiIONStringArray( vector< CtiIONValue * > structValues ) :
    CtiIONStruct(IONStringArray, 0)
{
    setValid(true);

    for( int i = 0; i < structValues.size() && isValid(); i++ )
    {
        if( (structValues[i]->getType() == IONArray) &&
            (((CtiIONArray *)structValues[i])->getArrayType() == IONCharArray) )
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
