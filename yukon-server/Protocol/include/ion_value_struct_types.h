#pragma once

#include "ion_value_struct.h"

#include "ion_value_fixed_intunsigned.h"
#include "ion_value_fixed_time.h"
#include "ion_value_variable_fixedarray.h"

template < class T >
class CtiIONStructArrayTemplate;

class CtiIONLogRecord : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONLogRecord>;
    CtiIONLogRecord();

public:

    CtiIONLogRecord( std::vector< CtiIONValue * > structValues );
    CtiIONLogRecord( CtiIONUnsignedInt *logPosition,
                     CtiIONTime        *timestamp,
                     CtiIONStruct      *logValues );

    CtiIONUnsignedInt *getLogPosition( void );
    CtiIONTime        *getTimestamp( void );
    CtiIONStruct      *getLogValues( void );

    enum Elements
    {
        LogPosition,
        Timestamp,
        LogValues
    };
};

class CtiIONAlarm : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONAlarm>;
    CtiIONAlarm();

public:

    CtiIONAlarm( std::vector< CtiIONValue * > structValues );
    CtiIONAlarm( CtiIONUnsignedInt *effectHandle,
                 CtiIONUnsignedInt *transitions,
                 CtiIONUnsignedInt *priority );

    enum Elements
    {
        EffectHandle,
        Transitions,
        Priority
    };
};

class CtiIONEvent : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONEvent>;
    CtiIONEvent();

public:

    CtiIONEvent( std::vector< CtiIONValue * > structValues );
    CtiIONEvent( CtiIONUnsignedInt *priority,
                 CtiIONUnsignedInt *eventState,
                 CtiIONUnsignedInt *causeHandle,
                 CtiIONValue       *causeValue,
                 CtiIONUnsignedInt *effectHandle,
                 CtiIONValue       *effectValue );

    CtiIONUnsignedInt *getPriority( void );
    CtiIONUnsignedInt *getEventState( void );
    CtiIONUnsignedInt *getCauseHandle( void );
    CtiIONValue       *getCauseValue( void );
    CtiIONUnsignedInt *getEffectHandle( void );
    CtiIONValue       *getEffectValue( void );

    enum Elements
    {
        Priority,
        EventState,
        CauseHandle,
        CauseValue,
        EffectHandle,
        EffectValue
    };
};

class CtiIONRange : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONRange>;
    CtiIONRange();

public:

    CtiIONRange( std::vector< CtiIONValue * > structValues );
    CtiIONRange( CtiIONUnsignedInt *rangeStart,
                 CtiIONUnsignedInt *rangeEnd );

    enum Elements
    {
        RangeStart,
        RangeEnd
    };
};

class CtiIONList : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONList>;
    CtiIONList();

public:

    CtiIONList( std::vector< CtiIONValue * > structValues );

/*    CtiIONValue *operator[]( unsigned long index )  { return getArrayElement( index ); };*/
};

class CtiIONException : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONException>;
    CtiIONException();

public:

    CtiIONException( std::vector< CtiIONValue * > structValues );
    CtiIONException( CtiIONValue *v1, CtiIONValue *v2, CtiIONValue *v3 );

    CtiIONException( CtiIONUnsignedInt *exceptionCode,
                     CtiIONValue       *exceptionValue,
                     CtiIONCharArray   *reason );

    enum Elements
    {
        ExceptionCode,
        ExceptionValue,
        Reason
    };
};

class CtiIONWaveform : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONWaveform>;
    CtiIONWaveform();

public:

    CtiIONWaveform( std::vector< CtiIONValue * > structValues );
/*    CtiIONWaveform( CtiIONNumeric *samplingFrequency,
                    CtiIONNumeric *offset,
                    CtiIONNumeric *scale );
*/
    enum Elements
    {
        SamplingFrequency,
        Offset,
        Scale,
        TimeOfFirstPoint,
        SamplePoints
    };
};

class CtiIONDate : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONDate>;
    CtiIONDate();

public:

    CtiIONDate( std::vector< CtiIONValue * > structValues );
    CtiIONDate( CtiIONUnsignedInt *year,
                CtiIONUnsignedInt *month,
                CtiIONUnsignedInt *dayOfTheMonth );

    enum Elements
    {
        Year,
        Month,
        DayOfTheMonth
    };
};

class CtiIONCalendar : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONCalendar>;
    CtiIONCalendar();

public:

    CtiIONCalendar( std::vector< CtiIONValue * > structValues );
    CtiIONCalendar( CtiIONDate             *startDate,
                    CtiIONUnsignedIntArray *listOfDays );

    enum Elements
    {
        StartDate,
        ListOfDays
    };
};

class CtiIONProfile : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONProfile>;
    CtiIONProfile();

public:

    CtiIONProfile( std::vector< CtiIONValue * > structValues );
    CtiIONProfile( CtiIONUnsignedIntArray *indexTable,
                   CtiIONUnsignedIntArray *activityList );

    enum Elements
    {
        IndexTable,
        ActivityList
    };
};

class CtiIONStringArray : public CtiIONStruct
{
protected:

    friend class CtiIONStructArrayTemplate<CtiIONStringArray>;
    CtiIONStringArray();

public:

    CtiIONStringArray( std::vector< CtiIONValue * > structValues );

};
