/*-----------------------------------------------------------------------------*
*
* File:   mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_sched.cpp-arc  $
* REVISION     :  $Revision: 1.11.2.1 $
* DATE         :  $Date: 2008/11/21 20:56:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  mc_sched.cpp

        Programmer:  Aaron Lauinger

        Description:    Source file for CtiMCSchedule

        Initial Date:  4/7/99
                       1/11/01

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999, 2001
---------------------------------------------------------------------------*/
#include "precompiled.h"

#include "mc_sched.h"
#include "utility.h"
#include "ctidate.h"

using namespace std;

DEFINE_COLLECTABLE( CtiMCSchedule, MSG_MC_SCHEDULE );

// Schedule types
const char* CtiMCSchedule::Simple   = "Simple";
const char* CtiMCSchedule::Scripted = "Script";

// Schedule States
const char* CtiMCSchedule::Waiting = "Waiting";
const char* CtiMCSchedule::Pending = "Pending";
const char* CtiMCSchedule::Running = "Running";
const char* CtiMCSchedule::Disabled= "Disabled";

// Start policies
const char* CtiMCSchedule::DateTimeStartPolicy      = "DateTime";
const char* CtiMCSchedule::DayOfMonthStartPolicy    = "DayOfMonthTime";
const char* CtiMCSchedule::WeekDayTimeStartPolicy   ="WeekDayTime";
const char* CtiMCSchedule::ManualStartPolicy        = "Manual";

    // Stop policies
const char* CtiMCSchedule::AbsoluteTimeStopPolicy   = "AbsoluteTime";
const char* CtiMCSchedule::DurationStopPolicy       = "Duration";
const char* CtiMCSchedule::UntilCompleteStopPolicy  = "UntilComplete";
const char* CtiMCSchedule::ManualStopPolicy         = "Manual";

//Last run statuses
const char* CtiMCSchedule::None     = "None";
const char* CtiMCSchedule::Error    = "Error";
const char* CtiMCSchedule::Finished = "Finished";

// Defaults
const char* CtiMCSchedule::DefaultType  = CtiMCSchedule::Simple;
const char* CtiMCSchedule::DefaultState = CtiMCSchedule::Disabled;
const char* CtiMCSchedule::DefaultCategory = "Default";
const char* CtiMCSchedule::DefaultStartPolicy = CtiMCSchedule::ManualStartPolicy;
const char* CtiMCSchedule::DefaultStopPolicy = CtiMCSchedule::ManualStopPolicy;
const char* CtiMCSchedule::DefaultLastRunStatus = CtiMCSchedule::None;

ostream& operator<<( ostream& ostrm, CtiMCSchedule& sched )
{
    if( !sched.checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< sched.getScheduleID() <<")");
    }

    ostrm << " Schedule ID:       " << sched.getScheduleID() << endl;
    ostrm << " Schedule Name:     " << sched.getScheduleName() << endl;
    ostrm << " Schedule Category: " << sched.getCategoryName() << endl;
    ostrm << " Schedule Type:     " << sched.getScheduleType() << endl;
    ostrm << " Holiday Sched ID:  " << sched.getHolidayScheduleID() << endl;
    ostrm << " Command File:      " << sched.getCommandFile() << endl;
    ostrm << " Current State:     " << sched.getCurrentState() << endl;
    ostrm << " Start Policy:      " << sched.getStartPolicy() << endl;
    ostrm << " Stop Policy:       " << sched.getStopPolicy() << endl;

    ostrm << " Last Run Time:    ";
    if( sched.getLastRunTime().isValid() )
        ostrm << sched.getLastRunTime() << endl;
    else
        ostrm << "Invalid" << endl;

    ostrm << " Last Run Status:  " << sched.getLastRunStatus() << endl;
    ostrm << " Start Day:        " << sched.getStartDay() << endl;
    ostrm << " Start Month:      " << sched.getStartMonth() << endl;
    ostrm << " Start Year:       " << sched.getStartYear() << endl;
    ostrm << " Valid Week Days:  " << sched.getValidWeekDays() << endl;
    ostrm << " Manual Start Time: ";
    if( sched.getManualStartTime().isValid() )
        ostrm << sched.getManualStartTime() << endl;
    else
        ostrm << "Invalid" << endl;

    ostrm << " Manual Stop Time: ";
    if( sched.getManualStopTime().isValid() )
        ostrm << sched.getManualStopTime() << endl;
    else
        ostrm << "Invalid" << endl;

    if( sched.isSimpleSchedule() )
    {
        ostrm << " Target Pao ID:  " << sched.getTargetPaoId() << endl;
        ostrm << " Start Command:  " << sched.getStartCommand() << endl;
        ostrm << " Stop Command:   " << sched.getStopCommand() << endl;
        ostrm << " Repeat Int:     " << sched.getRepeatInterval() << endl;
    }


    if( !sched.checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< sched.getScheduleID() <<")");
    }

    return ostrm;
}

CtiMCSchedule::CtiMCSchedule()
:
  _current_start_time(CtiTime((unsigned long)0)),
  _current_stop_time(CtiTime((unsigned long)0))
{
    _pao_table.setCategory("Schedule");
    _pao_table.setClassStr("Schedule");
    _pao_table.setStatisticsStr("-");

    setScheduleType(DefaultType);
    setCategoryName(DefaultCategory);
    setCurrentState(DefaultState);
    setStartPolicy(DefaultStartPolicy);
    setStopPolicy(DefaultStopPolicy);
    setLastRunStatus(DefaultLastRunStatus);
    setManualStartTime(CtiTime((unsigned long)0));
    setManualStopTime(CtiTime((unsigned long)0));

    setValid(true);
    setUpdatedFlag(false);
    CtiMemDBObject::setDirty(true);     // setDirty() is virtual (why?): make explicit who we are calling
}

CtiMCSchedule::CtiMCSchedule(const CtiMCSchedule& sched)
{
    *this = sched;
}

CtiMCSchedule::~CtiMCSchedule()
{

}

bool CtiMCSchedule::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    bool ret = false;
    _pao_table.DecodeDatabaseReader(rdr);
    ret = _schedule_table.DecodeDatabaseReader(rdr);

    if( ret && isSimpleSchedule() )
    {
        ret = _simple_schedule_table.DecodeDatabaseReader(rdr);
    }

    // We just came from the database so we must exist there yes?
    setUpdatedFlag(true);
    setDirty(false);

    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    return ret;
}

bool CtiMCSchedule::Update()
{
    bool ret = false;
    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    if( getScheduleID() != 0 )
    {
        ret =  _pao_table.Update();
    }

    if( ret )
    {
        ret = _schedule_table.Update();
    }

    if( ret && isSimpleSchedule() )
    {
        ret = _simple_schedule_table.Update();
    }

    if( ret )
    {
        setUpdatedFlag(true);
        setDirty(false);
    }

    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    return ret;
}

bool CtiMCSchedule::Insert()
{
    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    if( getScheduleID() == 0 )
    {
        INT newId = PAOIdGen();

        if (newId != 0)
        {
            setScheduleID(newId);
        }
        else
        {
            CTILOG_ERROR(dout, "Invalid Connection to Database");

            return false;
        }
    }

    bool ret = _pao_table.Insert();

    if( ret )
    {
        ret = _schedule_table.Insert();
    }
    else
    {
        setScheduleID(0);//This guy is screwed up. Next time we will try to get him a new ID.

        CTILOG_WARN(dout, "There was probably an ID conflict, this is bad.");
    }

    if( ret && isSimpleSchedule() )
    {
        ret = _simple_schedule_table.Insert();
    }

    if( ret )
    {
        setUpdatedFlag(true);
        setDirty(false);
    }

    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    return ret;
}

bool CtiMCSchedule::Delete()
{
    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    bool ret = true;

    // Delete in opposite order as
    // insert or update
    if( isSimpleSchedule() )
    {
        ret = _simple_schedule_table.Delete();
    }

    if( ret )
    {
        ret = _schedule_table.Delete();
    }

    if( ret )
    {
        ret = _pao_table.Delete();
    }

    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    return ret;
}

bool CtiMCSchedule::isSimpleSchedule() const
{
    const string& type = getScheduleType();

    return ( type == "simple" ||
             type == "Simple" ||
             type == "SIMPLE" );
}

bool CtiMCSchedule::operator==(const CtiMCSchedule& ref) const
{
    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    if( !ref.checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule ref is invalid");
    }

    return (getScheduleID() == ref.getScheduleID());
}

CtiMCSchedule& CtiMCSchedule::operator=(const CtiMCSchedule& ref)
{
    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    if( !ref.checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule ref is invalid ("<< ref.getScheduleID() <<")");
    }

    _current_start_time = ref._current_start_time;
    _current_stop_time = ref._current_stop_time;

    _pao_table = ref._pao_table;
    _schedule_table = ref._schedule_table;

    if( isSimpleSchedule() )
    {
        _simple_schedule_table = ref._simple_schedule_table;
    }

    setDirty(true);

    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    if( !ref.checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule ref is invalid ("<< ref.getScheduleID() <<")");
    }

    return *this;
}

CtiMessage* CtiMCSchedule::replicateMessage() const
{
    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    CtiMCSchedule* aCopy = new CtiMCSchedule();
    *aCopy = *this;

    if( !checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule is invalid ("<< getScheduleID() <<")");
    }

    if( !aCopy->checkSchedule() )
    {
        CTILOG_ERROR(dout, "Schedule copy is invalid");
    }

    return aCopy;
}

/*========Setters and Getters Below========*/

long CtiMCSchedule::getScheduleID() const
{
    return _pao_table.getID();
}

const string CtiMCSchedule::getScheduleName() const
{
    return string( _pao_table.getName().data() );
}

const string& CtiMCSchedule::getCategoryName() const
{
    return _schedule_table.getCategoryName();
}

const string CtiMCSchedule::getScheduleType() const
{
    return string( _pao_table.getTypeStr().data() );
}

long CtiMCSchedule::getHolidayScheduleID() const
{
    return _schedule_table.getHolidayScheduleID();
}

const string& CtiMCSchedule::getCommandFile() const
{
    return _schedule_table.getCommandFile();
}

const string& CtiMCSchedule::getCurrentState() const
{
    return _schedule_table.getCurrentState();
}

const string& CtiMCSchedule::getStartPolicy() const
{
    return _schedule_table.getStartPolicy();
}

const string& CtiMCSchedule::getStopPolicy() const
{
    return _schedule_table.getStopPolicy();
}

const CtiTime& CtiMCSchedule::getLastRunTime() const
{
    return _schedule_table.getLastRunTime();
}

const string& CtiMCSchedule::getLastRunStatus() const
{
    return _schedule_table.getLastRunStatus();
}

int CtiMCSchedule::getStartDay() const
{
    return _schedule_table.getStartDay();
}

int CtiMCSchedule::getStartMonth() const
{
    return _schedule_table.getStartMonth();
}

int CtiMCSchedule::getStartYear() const
{
    return _schedule_table.getStartYear();
}

const string& CtiMCSchedule::getStartTime() const
{
    return _schedule_table.getStartTime();
}

const string& CtiMCSchedule::getStopTime() const
{
    return _schedule_table.getStopTime();
}

const string& CtiMCSchedule::getValidWeekDays() const
{
    return _schedule_table.getValidWeekDays();
}

int CtiMCSchedule::getDuration() const
{
    return _schedule_table.getDuration();
}

const CtiTime& CtiMCSchedule::getManualStartTime() const
{
    return _schedule_table.getManualStartTime();
}

const CtiTime& CtiMCSchedule::getManualStopTime() const
{
    return _schedule_table.getManualStopTime();
}

const CtiTime& CtiMCSchedule::getCurrentStartTime() const
{
    return _current_start_time;
}

const CtiTime& CtiMCSchedule::getCurrentStopTime() const
{
    return _current_stop_time;
}

int CtiMCSchedule::getTemplateType() const
{
    return _schedule_table.getTemplateType();
}

long CtiMCSchedule::getTargetPaoId() const
{
    return _simple_schedule_table.getTargetPaoId();
}

const string& CtiMCSchedule::getStartCommand() const
{
    return _simple_schedule_table.getStartCommand();
}

const string& CtiMCSchedule::getStopCommand() const
{
    return _simple_schedule_table.getStopCommand();
}

long CtiMCSchedule::getRepeatInterval() const
{
    return _simple_schedule_table.getRepeatInterval();
}


CtiMCSchedule& CtiMCSchedule::setScheduleID(long sched_id)
{
    if( sched_id != getScheduleID() )
    {
        _pao_table.setID(sched_id);
        _schedule_table.setScheduleID(sched_id);
        _simple_schedule_table.setScheduleID(sched_id);

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setScheduleName(const string& schedule_name)
{
    if( schedule_name != getScheduleName() )
    {
        _pao_table.setName(schedule_name.c_str() );

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setCategoryName(const string& category_name)
{
    if( category_name != getCategoryName() )
    {
        _schedule_table.setCategoryName(category_name);

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setScheduleType(const string& sched_type)
{
    assert( sched_type == CtiMCSchedule::Simple ||
            sched_type == CtiMCSchedule::Scripted );

    if( sched_type != getScheduleType() )
    {
        _pao_table.setTypeStr(  sched_type.c_str() );

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setHolidayScheduleID(long holiday_sched_id)
{
    if( holiday_sched_id != getHolidayScheduleID() )
    {
        _schedule_table.setHolidayScheduleID(holiday_sched_id);

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setCommandFile(const string& cmd_file)
{
    if( cmd_file != getCommandFile() )
    {
        _schedule_table.setCommandFile(cmd_file);

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setCurrentState(const string& state)
{
    assert( state == CtiMCSchedule::Waiting ||
            state == CtiMCSchedule::Pending ||
            state == CtiMCSchedule::Running ||
            state == CtiMCSchedule::Disabled  );

    if( state != getCurrentState() )
    {
        _schedule_table.setCurrentState(state);

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStartPolicy(const string& start_policy)
{
    assert( start_policy == CtiMCSchedule::DateTimeStartPolicy     ||
            start_policy == CtiMCSchedule::DayOfMonthStartPolicy   ||
            start_policy == CtiMCSchedule::WeekDayTimeStartPolicy  ||
            start_policy == CtiMCSchedule::ManualStartPolicy       );

    if( start_policy != getStartPolicy() )
    {
        _schedule_table.setStartPolicy(start_policy);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStopPolicy(const string& stop_policy)
{
    assert(  stop_policy == CtiMCSchedule::AbsoluteTimeStopPolicy   ||
             stop_policy == CtiMCSchedule::DurationStopPolicy       ||
             stop_policy == CtiMCSchedule::UntilCompleteStopPolicy  ||
             stop_policy == CtiMCSchedule::ManualStopPolicy         );

    if( stop_policy != getStopPolicy() )
    {
        _schedule_table.setStopPolicy(stop_policy);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setLastRunTime(const CtiTime& last_run_time)
{
    if( last_run_time != getLastRunTime() )
    {
        if( isValidTime(last_run_time) )
            _schedule_table.setLastRunTime(last_run_time);
        else
            _schedule_table.setLastRunTime( CtiTime( (unsigned long) 0 ));

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setLastRunStatus(const string& last_run_status)
{
    assert( last_run_status == CtiMCSchedule::None  ||
            last_run_status == CtiMCSchedule::Error ||
            last_run_status == CtiMCSchedule::Finished );

    if( last_run_status != getLastRunStatus() )
    {
        _schedule_table.setLastRunStatus(last_run_status);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStartDay(int start_day)
{
    assert( start_day >= 0 && start_day <= 31 );

    if( start_day != getStartDay() )
    {
        _schedule_table.setStartDay(start_day);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStartMonth(int start_month)
{
    if( start_month != getStartMonth() )
    {
        _schedule_table.setStartMonth(start_month);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStartYear(int start_year)
{
    if( start_year != getStartYear() )
    {
        _schedule_table.setStartYear(start_year);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStartTime(const string& start_time)
{
    assert( start_time.length() == 8 );

    if( start_time != getStartTime() )
    {
        _schedule_table.setStartTime(start_time);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStopTime(const string& stop_time)
{
    assert( stop_time.length() == 8 );

    if( stop_time != getStopTime() )
    {
        _schedule_table.setStopTime(stop_time);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setValidWeekDays(const string& valid_week_days)
{
    assert( valid_week_days.length() == 8 );

    if( valid_week_days != getValidWeekDays() )
    {
        _schedule_table.setValidWeekDays(valid_week_days);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setDuration(int duration)
{
    if( duration != getDuration() )
    {
        _schedule_table.setDuration(duration);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setManualStartTime(
    const CtiTime& manual_start_time )
{
    if( manual_start_time != getManualStartTime() )
    {
        if( isValidTime(manual_start_time ) )
            _schedule_table.setManualStartTime( manual_start_time );
        else
            _schedule_table.setManualStartTime( CtiTime( (unsigned long) 0) );

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setManualStopTime(
    const CtiTime& manual_stop_time )
{
    if( manual_stop_time != getManualStopTime() )
    {
        if( isValidTime(manual_stop_time) )
            _schedule_table.setManualStopTime(manual_stop_time);
        else
            _schedule_table.setManualStopTime( CtiTime( (unsigned long) 0) );

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setCurrentStartTime(const CtiTime& start_time)
{
    !isValidTime(start_time) ?
        (_current_start_time = CtiTime((unsigned long)0)) :
        (_current_start_time = start_time );
    return *this;
}

CtiMCSchedule& CtiMCSchedule::setCurrentStopTime(const CtiTime& stop_time)
{
    !isValidTime(stop_time) ?
        (_current_stop_time = CtiTime((unsigned long)0) ) :
        (_current_stop_time = stop_time );
    return *this;
}

CtiMCSchedule& CtiMCSchedule::setTemplateType(int template_type)
{
    if( template_type != getTemplateType() )
    {
        _schedule_table.setTemplateType(template_type);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setTargetPaoID(const int target_id)
{
    if( target_id != getTargetPaoId() )
    {
        _simple_schedule_table.setTargetPaoID(target_id);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStartCommand(const string& start_command)
{
    if( start_command != getStartCommand() )
    {
        _simple_schedule_table.setStartCommand(start_command);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setStopCommand(const string& stop_command)
{
    if( stop_command != getStopCommand() )
    {
        _simple_schedule_table.setStopCommand(stop_command);
        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setRepeatInterval(long repeat_interval)
{
    if( repeat_interval != getRepeatInterval() )
    {
        _simple_schedule_table.setRepeatInterval(repeat_interval);
        setDirty(true);
    }

    return *this;
}


/*== Private Functions == */

bool CtiMCSchedule::isValidTime(const CtiTime& t) const
{
    return ( t > CtiTime( CtiDate( 1, 1990 ) ));
}

bool CtiMCSchedule::checkSchedule() const
{
    if( !checkField( getCategoryName(), "category name") )
        return false;

    if( !checkField( getCommandFile(), "command file") )
        return false;

    if( !checkField( getCurrentState(),"current state") )
        return false;

    if( !checkField( getLastRunStatus(),"last run status") )
        return false;

    if ( !checkField( getScheduleName(), "schedule name") )
        return false;

    if( !checkField( getScheduleType(), "schedule type") )
        return false;

    if( !checkField( getStartCommand(), "start command") )
        return false;

    if( !checkField( getStartPolicy(), "start policy") )
        return false;

    if( !checkField( getStopPolicy(), "stop policy") )
        return false;

    if( !checkField( getStartTime(), "start time") )
        return false;

    if( !checkField( getStopTime(), "stop time") )
        return false;

    return true;
}

bool CtiMCSchedule::checkField(const string& fld, const string& val) const
{
    for( int i = 0; i < fld.length(); i++ )
    {
        if( fld[i] <= 0 || fld[i] >= 128 )
        {
            CTILOG_ERROR(dout, "CORRUPT FIELD: "<< fld <<" len: "<< fld.length() <<" value is: "<< val);

            return false;
        }
    }

    return true;
}
