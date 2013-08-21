#pragma once

#include "test_serialization.h"

#include "mc_msg.h"
#include "mc_sched.h"
#include "mc_script.h"

/*-----------------------------------------------------------------------------
    CtiMCSchedule
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiMCSchedule> : public TestCase<CtiMessage>
{
    long            _scheduleId;
    std::string     _scheduleName;
    std::string     _categoryName;
    std::string     _scheduleType;
    long            _holidayScheduleId;
    std::string     _commandFile;
    std::string     _currentState;
    std::string     _startPolicy;
    std::string     _stopPolicy;
    CtiTime         _lastRunTime;
    std::string     _lastRunStatus;
    int             _startDay;
    int             _startMonth;
    int             _startYear;
    std::string     _startTime;
    std::string     _stopTime;
    std::string     _validWeekDays;
    int             _duration;
    long            _targetPaoId;
    std::string     _startCommand;
    std::string     _stopCommand;
    long            _repeatInterval;
    CtiTime         _current_start_time;
    CtiTime         _current_stop_time;
    int             _templateType;

    void Create()
    {
        _imsg.reset( new CtiMCSchedule );
        _omsg.reset( new CtiMCSchedule );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCSchedule &imsg = dynamic_cast<CtiMCSchedule&>(*_imsg);

        // items permitted

        const std::string scheduleType_items[]  = { CtiMCSchedule::Simple,
                                                    CtiMCSchedule::Scripted };

        const std::string currentState_items[]  = { CtiMCSchedule::Waiting,
                                                    CtiMCSchedule::Pending,
                                                    CtiMCSchedule::Running,
                                                    CtiMCSchedule::Disabled };

        const std::string startPolicy_items[]   = { CtiMCSchedule::DateTimeStartPolicy,
                                                    CtiMCSchedule::DayOfMonthStartPolicy,
                                                    CtiMCSchedule::WeekDayTimeStartPolicy,
                                                    CtiMCSchedule::ManualStartPolicy };

        const std::string stopPolicy_items[]    = { CtiMCSchedule::AbsoluteTimeStopPolicy,
                                                    CtiMCSchedule::DurationStopPolicy,
                                                    CtiMCSchedule::UntilCompleteStopPolicy,
                                                    CtiMCSchedule::ManualStopPolicy };

        const std::string lastRunStatus_items[] = { CtiMCSchedule::None,
                                                    CtiMCSchedule::Error,
                                                    CtiMCSchedule::Finished };

        imsg.setScheduleID        ( GenerateRandom( _scheduleId                         ));
        imsg.setScheduleName      ( GenerateRandom( _scheduleName                       ));
        imsg.setCategoryName      ( GenerateRandom( _categoryName                       ));
        imsg.setScheduleType      ( GenerateRandom( _scheduleType,  scheduleType_items  ));
        imsg.setHolidayScheduleID ( GenerateRandom( _holidayScheduleId                  ));
        imsg.setCommandFile       ( GenerateRandom( _commandFile                        ));
        imsg.setCurrentState      ( GenerateRandom( _currentState,  currentState_items  ));
        imsg.setStartPolicy       ( GenerateRandom( _startPolicy,   startPolicy_items   ));
        imsg.setStopPolicy        ( GenerateRandom( _stopPolicy,    stopPolicy_items    ));
        imsg.setLastRunTime       ( GenerateRandom( _lastRunTime                        ));
        imsg.setLastRunStatus     ( GenerateRandom( _lastRunStatus, lastRunStatus_items ));
        imsg.setStartDay          ( GenerateRandom( _startDay,      0, 31               ));
        imsg.setStartMonth        ( GenerateRandom( _startMonth                         ));
        imsg.setStartYear         ( GenerateRandom( _startYear                          ));
        imsg.setStartTime         ( GenerateRandom( _startTime,     8                   ));
        imsg.setStopTime          ( GenerateRandom( _stopTime,      8                   ));
        imsg.setValidWeekDays     ( GenerateRandom( _validWeekDays, 8                   ));
        imsg.setDuration          ( GenerateRandom( _duration                           ));
        imsg.setTargetPaoID       ( GenerateRandom( _targetPaoId                        ));
        imsg.setStartCommand      ( GenerateRandom( _startCommand                       ));
        imsg.setStopCommand       ( GenerateRandom( _stopCommand                        ));
        imsg.setRepeatInterval    ( GenerateRandom( _repeatInterval                     ));
        imsg.setCurrentStartTime  ( GenerateRandom( _current_start_time                 ));
        imsg.setCurrentStopTime   ( GenerateRandom( _current_stop_time                  ));
        imsg.setTemplateType      ( GenerateRandom( _templateType                       ));
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCSchedule &omsg = dynamic_cast<CtiMCSchedule&>(*_omsg);

        CompareMember( "_scheduleId",           _scheduleId,                omsg.getScheduleID() );
        CompareMember( "_scheduleName",         _scheduleName,              omsg.getScheduleName() );
        CompareMember( "_categoryName",         _categoryName,              omsg.getCategoryName() );
        CompareMember( "_scheduleType",         _scheduleType,              omsg.getScheduleType() );
        CompareMember( "_holidayScheduleId",    _holidayScheduleId,         omsg.getHolidayScheduleID() );
        CompareMember( "_commandFile",          _commandFile,               omsg.getCommandFile() );
        CompareMember( "_currentState",         _currentState,              omsg.getCurrentState() );
        CompareMember( "_startPolicy",          _startPolicy,               omsg.getStartPolicy() );
        CompareMember( "_stopPolicy",           _stopPolicy,                omsg.getStopPolicy() );
        CompareMember( "_lastRunTime",          _lastRunTime,               omsg.getLastRunTime() );
        CompareMember( "_lastRunStatus",        _lastRunStatus,             omsg.getLastRunStatus() );
        CompareMember( "_startDay",             _startDay,                  omsg.getStartDay() );
        CompareMember( "_startMonth",           _startMonth,                omsg.getStartMonth() );
        CompareMember( "_startYear",            _startYear,                 omsg.getStartYear() );
        CompareMember( "_startTime",            _startTime,                 omsg.getStartTime() );
        CompareMember( "_stopTime",             _stopTime,                  omsg.getStopTime() );
        CompareMember( "_validWeekDays",        _validWeekDays,             omsg.getValidWeekDays() );
        CompareMember( "_duration",             _duration,                  omsg.getDuration() );

        if( omsg.isSimpleSchedule() )
        {
            //  These fields are only copied by CtiMCSchedule::operator= if _scheduleType == "simple"

            CompareMember( "_targetPaoId",          _targetPaoId,               omsg.getTargetPaoId() );
            CompareMember( "_startCommand",         _startCommand,              omsg.getStartCommand() );
            CompareMember( "_stopCommand",          _stopCommand,               omsg.getStopCommand() );
            CompareMember( "_repeatInterval",       _repeatInterval,            omsg.getRepeatInterval() );
        }
        /*else
        {
            CompareMember<long>  ( "_targetPaoId",          0,         omsg.getTargetPaoId() );
            CompareMember<string>( "_startCommand",         "",        omsg.getStartCommand() );
            CompareMember<string>( "_stopCommand",          "",        omsg.getStopCommand() );
            CompareMember<long>  ( "_repeatInterval",       0,         omsg.getRepeatInterval() );
        }*/

        CompareMember( "_templateType",         _templateType,              omsg.getTemplateType() );
    }
};

/*-----------------------------------------------------------------------------
    CtiMCUpdateSchedule
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCUpdateSchedule> : public TestCase<CtiMessage>
{
    TestCaseItem<CtiMCSchedule> _tc_schedule;

    void Create()
    {
        _imsg.reset( new CtiMCUpdateSchedule );
        _omsg.reset( new CtiMCUpdateSchedule );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCUpdateSchedule &imsg = dynamic_cast<CtiMCUpdateSchedule&>(*_imsg);

        GenerateRandom( imsg._script );

        _tc_schedule.Populate( &imsg._schedule );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCUpdateSchedule &imsg = dynamic_cast<CtiMCUpdateSchedule&>(*_imsg),
                            &omsg = dynamic_cast<CtiMCUpdateSchedule&>(*_omsg);

        CompareMember( "_script",               imsg._script,               omsg._script );

        // PATCH: the operator = of the CtiMCSchedule does not copy the CtiMessage content
        static_cast<CtiMessage&>(omsg._schedule) = static_cast<CtiMessage&>(imsg._schedule);

        if( !_tc_schedule.Compare( &omsg._schedule ))
        {
            reportMismatch( "_schedule", _tc_schedule._failures );
        }
    }
};

/*-----------------------------------------------------------------------------
    CtiMCAddSchedule
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCAddSchedule> : public TestCase<CtiMessage>
{
    TestCaseItem<CtiMCSchedule> _tc_schedule;

    void Create()
    {
        _imsg.reset( new CtiMCAddSchedule );
        _omsg.reset( new CtiMCAddSchedule );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCAddSchedule &imsg = dynamic_cast<CtiMCAddSchedule&>(*_imsg);

        GenerateRandom( imsg._script );

        _tc_schedule.Populate( &imsg._schedule );

    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCAddSchedule &imsg = dynamic_cast<CtiMCAddSchedule&>(*_imsg),
                         &omsg = dynamic_cast<CtiMCAddSchedule&>(*_omsg);

        CompareMember( "_script",               imsg._script,               omsg._script );

        // PATCH: the operator = of the CtiMCSchedule does not copy the CtiMessage content
        static_cast<CtiMessage&>(omsg._schedule) = static_cast<CtiMessage&>(imsg._schedule);

        if( !_tc_schedule.Compare( &omsg._schedule ))
        {
            reportMismatch( "_schedule", _tc_schedule._failures );
        }
    }
};

/*-----------------------------------------------------------------------------
    CtiMCDeleteSchedule
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCDeleteSchedule> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiMCDeleteSchedule );
        _omsg.reset( new CtiMCDeleteSchedule );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCDeleteSchedule &imsg = dynamic_cast<CtiMCDeleteSchedule&>(*_imsg);

        GenerateRandom( imsg._id );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCDeleteSchedule &imsg = dynamic_cast<CtiMCDeleteSchedule&>(*_imsg),
                            &omsg = dynamic_cast<CtiMCDeleteSchedule&>(*_omsg);

        CompareMember( "_id",                   imsg._id,                   omsg._id );
    }
};

/*-----------------------------------------------------------------------------
    CtiMCRetrieveSchedule
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCRetrieveSchedule> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiMCRetrieveSchedule );
        _omsg.reset( new CtiMCRetrieveSchedule );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCRetrieveSchedule &imsg = dynamic_cast<CtiMCRetrieveSchedule&>(*_imsg);

        GenerateRandom( imsg._id );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCRetrieveSchedule &imsg = dynamic_cast<CtiMCRetrieveSchedule&>(*_imsg),
                              &omsg = dynamic_cast<CtiMCRetrieveSchedule&>(*_omsg);

        CompareMember( "_id",                   imsg._id,                   omsg._id );
    }
};

/*-----------------------------------------------------------------------------
    CtiMCRetrieveScript
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCRetrieveScript> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiMCRetrieveScript );
        _omsg.reset( new CtiMCRetrieveScript );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCRetrieveScript &imsg = dynamic_cast<CtiMCRetrieveScript&>(*_imsg);

        GenerateRandom( imsg._name );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCRetrieveScript &imsg = dynamic_cast<CtiMCRetrieveScript&>(*_imsg),
                            &omsg = dynamic_cast<CtiMCRetrieveScript&>(*_omsg);

        CompareMember( "_name",                 imsg._name,                 omsg._name );
    }
};

/*-----------------------------------------------------------------------------
    CtiMCOverrideRequest
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCOverrideRequest> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiMCOverrideRequest );
        _omsg.reset( new CtiMCOverrideRequest );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCOverrideRequest &imsg = dynamic_cast<CtiMCOverrideRequest&>(*_imsg);

        GenerateRandom( (int&)imsg._action, 0, 5 );
        GenerateRandom( imsg._id );
        GenerateRandom( imsg._start_time );
        GenerateRandom( imsg._stop_time );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCOverrideRequest &imsg = dynamic_cast<CtiMCOverrideRequest&>(*_imsg),
                             &omsg = dynamic_cast<CtiMCOverrideRequest&>(*_omsg);

        CompareMember( "_action",               (int&)imsg._action,         (int&)omsg._action );
        CompareMember( "_id",                   imsg._id,                   omsg._id );
        CompareMember( "_start_time",           imsg._start_time,           omsg._start_time );
        CompareMember( "_stop_time",            imsg._stop_time,            omsg._stop_time );
    }
};

/*-----------------------------------------------------------------------------
    CtiMCInfo
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCInfo> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiMCInfo );
        _omsg.reset( new CtiMCInfo );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCInfo &imsg = dynamic_cast<CtiMCInfo&>(*_imsg);

        GenerateRandom( imsg._id );
        GenerateRandom( imsg._info );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCInfo &imsg = dynamic_cast<CtiMCInfo&>(*_imsg),
                  &omsg = dynamic_cast<CtiMCInfo&>(*_omsg);

        CompareMember( "_id",                   imsg._id,                   omsg._id );
        CompareMember( "_info",                 imsg._info,                 omsg._info );
    }
};

/*-----------------------------------------------------------------------------
    CtiMCScript
-----------------------------------------------------------------------------*/

template<>
struct TestCase<CtiMCScript> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiMCScript );
        _omsg.reset( new CtiMCScript );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiMCScript &imsg = dynamic_cast<CtiMCScript&>(*_imsg);

        GenerateRandom( imsg._name );
        GenerateRandom( imsg._contents );
    }

    void Compare()
    {
        TestCase<CtiMessage>::Compare();

        CtiMCScript &imsg = dynamic_cast<CtiMCScript&>(*_imsg),
                    &omsg = dynamic_cast<CtiMCScript&>(*_omsg);

        CompareMember( "_name",                 imsg._name,                 omsg._name );
        CompareMember( "_contents",             imsg._contents,             omsg._contents );
    }
};
