#pragma warning( disable : 4786 )

#ifndef __ION_STRUCTTYPES_H__
#define __ION_STRUCTTYPES_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_structtypes.h
 *
 * Class:  CtiIONLogRecord, CtiIONAlarm, CtiIONEvent, CtiIONRange, CtiIONList, 
 *           CtiIONException, CtiIONWaveform, CtiIONDate, CtiIONCalendar, 
 *           CtiIONProfile, CtiIONStringArray, CtiIONMultiArray, CtiIONStructArray
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         Structure type classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
 

#include "ion_valuearraytypes.h"


class CtiIONLogRecord : public CtiIONStruct
{
public:

    CtiIONLogRecord( vector< CtiIONValue * > structValues );
    CtiIONLogRecord( CtiIONUnsignedInt *logPosition, 
                     CtiIONTime        *timestamp, 
                     CtiIONStruct      *logValues );

    enum Elements
    {
        LogPosition,
        Timestamp,
        LogValues
    };

private:

};



class CtiIONAlarm : public CtiIONStruct
{
public:

    CtiIONAlarm( vector< CtiIONValue * > structValues );
    CtiIONAlarm( CtiIONUnsignedInt *effectHandle, 
                 CtiIONUnsignedInt *transitions, 
                 CtiIONUnsignedInt *priority );

    enum Elements
    {
        EffectHandle,
        Transitions,
        Priority
    };

private:

};



class CtiIONEvent : public CtiIONStruct
{
public:

    CtiIONEvent( vector< CtiIONValue * > structValues );
    CtiIONEvent( CtiIONUnsignedInt *priority, 
                 CtiIONUnsignedInt *eventState, 
                 CtiIONUnsignedInt *causeHandle, 
                 CtiIONValue       *causeValue,
                 CtiIONUnsignedInt *effectHandle,
                 CtiIONValue       *effectValue );

    enum Elements
    {
        Priority,
        EventState,
        CauseHandle,
        CauseValue,
        EffectHandle,
        EffectValue
    };

private:

};



class CtiIONRange : public CtiIONStruct
{
public:

    CtiIONRange( vector< CtiIONValue * > structValues );
    CtiIONRange( CtiIONUnsignedInt *rangeStart, 
                 CtiIONUnsignedInt *rangeEnd );
    
    enum Elements
    {
        RangeStart,
        RangeEnd
    };

private:

};



class CtiIONList : public CtiIONStruct
{
public:

    CtiIONList( vector< CtiIONValue * > structValues );

    CtiIONValue *operator[]( unsigned long index )  { return getArrayElement( index ); };

private:

};



class CtiIONException : public CtiIONStruct
{
public:

    CtiIONException( vector< CtiIONValue * > structValues );
    CtiIONException( CtiIONUnsignedInt *exceptionCode,
                     CtiIONValue       *exceptionValue,
                     CtiIONCharArray   *reason );
    
    enum Elements
    {
        ExceptionCode,
        ExceptionValue,
        Reason
    };

private:

};



class CtiIONWaveform : public CtiIONStruct
{
public:

    CtiIONWaveform( vector< CtiIONValue * > structValues );
    CtiIONWaveform( CtiIONNumeric *samplingFrequency,
                    CtiIONNumeric *offset,
                    CtiIONNumeric *scale );
        
    enum Elements
    {
        SamplingFrequency,
        Offset,
        Scale,
        TimeOfFirstPoint,
        SamplePoints
    };

private:

};



class CtiIONDate : public CtiIONStruct
{
public:

    CtiIONDate( vector< CtiIONValue * > structValues );
    CtiIONDate( CtiIONUnsignedInt *year,
                CtiIONUnsignedInt *month,
                CtiIONUnsignedInt *dayOfTheMonth );
        
    enum Elements
    {
        Year,
        Month,
        DayOfTheMonth
    };
    
private:

};



class CtiIONCalendar : public CtiIONStruct
{
public:

    CtiIONCalendar( vector< CtiIONValue * > structValues );
    CtiIONCalendar( CtiIONDate             *startDate,
                    CtiIONUnsignedIntArray *listOfDays );
        
    enum Elements
    {
        StartDate,
        ListOfDays
    };
    
private:

};



class CtiIONProfile : public CtiIONStruct
{
public:

    CtiIONProfile( vector< CtiIONValue * > structValues );
    CtiIONProfile( CtiIONUnsignedIntArray *indexTable,
                   CtiIONUnsignedIntArray *activityList );
        
    enum Elements
    {
        IndexTable,
        ActivityList
    };

private:

};

#endif  //  #ifndef __ION_STRUCTTYPES_H__

