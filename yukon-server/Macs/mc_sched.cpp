#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_sched.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
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
#include "mc_sched.h"
#include <rw/collstr.h>

RWDEFINE_COLLECTABLE( CtiMCSchedule, MSG_MC_SCHEDULE );

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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        ostrm << " Target Select:  " << sched.getTargetSelect() << endl;
        ostrm << " Start Command:  " << sched.getStartCommand() << endl;
        ostrm << " Stop Command:   " << sched.getStopCommand() << endl;
        ostrm << " Repeat Int:     " << sched.getRepeatInterval() << endl;
    }


    if( !sched.checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return ostrm;
}

CtiMCSchedule::CtiMCSchedule()
:
  _current_start_time(RWTime((unsigned long)0)),
  _current_stop_time(RWTime((unsigned long)0))
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
    setManualStartTime(RWTime((unsigned long)0));
    setManualStopTime(RWTime((unsigned long)0));

    setValid(true);
    setUpdatedFlag(false);
    setDirty(true);
}

CtiMCSchedule::CtiMCSchedule(const CtiMCSchedule& sched)
{
    *this = sched;
}

CtiMCSchedule::~CtiMCSchedule()
{

}

void CtiMCSchedule::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    CtiTableMCSchedule::getSQL(db,keyTable,selector);
}

bool CtiMCSchedule::DecodeDatabaseReader(RWDBReader &rdr)
{
    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return ret;
}

bool CtiMCSchedule::Update()
{
    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    bool ret = ( _pao_table.Update().errorCode() == RWDBStatus::ok );
    
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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return ret;
}

bool CtiMCSchedule::Insert()
{   if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    bool ret = ( _pao_table.Insert().errorCode() == RWDBStatus::ok );
    
    if( ret )
    {
        ret = _schedule_table.Insert();
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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return ret;
}

bool CtiMCSchedule::Delete()
{
    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        ret = ( _pao_table.Delete().errorCode() == RWDBStatus::ok );
    }

    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( !ref.checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return (getScheduleID() == ref.getScheduleID());
}

CtiMCSchedule& CtiMCSchedule::operator=(const CtiMCSchedule& ref)
{
    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( !ref.checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( !ref.checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}

CtiMessage* CtiMCSchedule::replicateMessage() const
{
    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    CtiMCSchedule* copy = new CtiMCSchedule();
    *copy = *this;

    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( !copy->checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return copy;
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

const RWTime& CtiMCSchedule::getLastRunTime() const
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

const RWTime& CtiMCSchedule::getManualStartTime() const
{
    return _schedule_table.getManualStartTime();
}

const RWTime& CtiMCSchedule::getManualStopTime() const
{
    return _schedule_table.getManualStopTime();
}

const RWTime& CtiMCSchedule::getCurrentStartTime() const
{
    return _current_start_time;
}

const RWTime& CtiMCSchedule::getCurrentStopTime() const
{
    return _current_stop_time;
}

int CtiMCSchedule::getTemplateType() const
{
    return _schedule_table.getTemplateType();
}

const string& CtiMCSchedule::getTargetSelect() const
{
    return _simple_schedule_table.getTargetSelect();
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
        _pao_table.setName(RWCString( (char*) schedule_name.data() ));

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
        _pao_table.setTypeStr( RWCString( (char*) sched_type.data() ) );

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

CtiMCSchedule& CtiMCSchedule::setLastRunTime(const RWTime& last_run_time)
{
    if( last_run_time != getLastRunTime() )
    {
        if( isValidTime(last_run_time) )
            _schedule_table.setLastRunTime(last_run_time);
        else
            _schedule_table.setLastRunTime( RWTime( (unsigned long) 0 ));

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
    const RWTime& manual_start_time )
{
    if( manual_start_time != getManualStartTime() )
    {
        if( isValidTime(manual_start_time ) )
            _schedule_table.setManualStartTime( manual_start_time );
        else
            _schedule_table.setManualStartTime( RWTime( (unsigned long) 0) );

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setManualStopTime(
    const RWTime& manual_stop_time )
{
    if( manual_stop_time != getManualStopTime() )
    {
        if( isValidTime(manual_stop_time) )
            _schedule_table.setManualStopTime(manual_stop_time);
        else
            _schedule_table.setManualStopTime( RWTime( (unsigned long) 0) );

        setDirty(true);
    }

    return *this;
}

CtiMCSchedule& CtiMCSchedule::setCurrentStartTime(const RWTime& start_time)
{
    !isValidTime(start_time) ?
        (_current_start_time = RWTime((unsigned long)0)) :
        (_current_start_time = start_time );
    return *this;
}

CtiMCSchedule& CtiMCSchedule::setCurrentStopTime(const RWTime& stop_time)
{
    !isValidTime(stop_time) ?
        (_current_stop_time = RWTime((unsigned long)0) ) :
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

CtiMCSchedule& CtiMCSchedule::setTargetSelect(const string& target_select)
{
    if( target_select != getTargetSelect() )
    {
        _simple_schedule_table.setTargetSelect(target_select);
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

void CtiMCSchedule::saveGuts(RWvostream &aStream) const
{
    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // conversions before sending
    // string -> RWString
    CtiMessage::saveGuts(aStream);
    aStream     <<  getScheduleID() //long
                <<  RWCString( (char*) getScheduleName().data() )
                <<  RWCString( (char*) getCategoryName().data() )
                <<  RWCString( (char*) getScheduleType().data() )
                <<  getHolidayScheduleID() //long
                <<  RWCString( (char*) getCommandFile().data() )
                <<  RWCString( (char*) getCurrentState().data() )
                <<  RWCString( (char*) getStartPolicy().data() )
                <<  RWCString( (char*) getStopPolicy().data() )
                <<  getLastRunTime() //RWTime
                <<  RWCString( (char*) getLastRunStatus().data() )
                <<  getStartDay()  //int
                <<  getStartMonth() //int
                <<  getStartYear()  //int
                <<  RWCString( (char*) getStartTime().data() )
                <<  RWCString( (char*) getStopTime().data() )
                <<  RWCString( (char*) getValidWeekDays().data() )
                <<  getDuration()   //int
                <<  RWTime((unsigned long)0) //getManualStartTime() //RWTime
                <<  RWTime((unsigned long)0) //getManualStopTime()  //RWTime
                <<  RWCString( (char*) getTargetSelect().data() )
                <<  RWCString( (char*) getStartCommand().data() )
                <<  RWCString( (char*) getStopCommand().data() )
                <<  getRepeatInterval()  //int
                <<  getCurrentStartTime() //RWTime
                <<  getCurrentStopTime() //RWTime
                <<  getTemplateType();  //int

    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiMCSchedule::restoreGuts(RWvistream& aStream)
{
    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // bring the info back into temporaries
    // to use the set functions
    long temp_long;
    int temp_int;
    RWCString temp_str;
    RWTime temp_time;

    CtiMessage::restoreGuts(aStream);
    aStream >> temp_long;
    setScheduleID(temp_long);

    aStream >> temp_str;
    setScheduleName( (const char*) temp_str.data() );

    aStream >> temp_str;
    setCategoryName( (const char*) temp_str.data() );

    aStream >> temp_str;
    setScheduleType( (const char*) temp_str.data());

    aStream >> temp_long;
    setHolidayScheduleID(temp_long);

    aStream >> temp_str;
    setCommandFile( (const char*) temp_str.data() );

    aStream >> temp_str;
    setCurrentState( (const char*) temp_str.data() );

    aStream >> temp_str;
    setStartPolicy( (const char*) temp_str.data() );

    aStream >> temp_str;
    setStopPolicy( (const char*) temp_str.data() );

    aStream >> temp_time;
    setLastRunTime( temp_time );

    aStream >> temp_str;
    setLastRunStatus( (const char*) temp_str.data() );

    aStream >> temp_int;
    setStartDay(temp_int);

    aStream >> temp_int;
    setStartMonth(temp_int);

    aStream >> temp_int;
    setStartYear(temp_int);

    aStream >> temp_str;
    setStartTime( (const char*) temp_str.data() );

    aStream >> temp_str;
    setStopTime( (const char*) temp_str.data() );

    aStream >> temp_str;
    setValidWeekDays( (const char*) temp_str.data() );

    aStream >> temp_int;
    setDuration(temp_int);

    // Dont set the man start/stop time remove this in the future
    aStream >> temp_time;
    //setManualStartTime( temp_time );

    aStream >> temp_time;
    //setManualStopTime( temp_time );

    aStream >> temp_str;
    setTargetSelect( (const char*) temp_str.data() );

    aStream >> temp_str;
    setStartCommand( (const char*) temp_str.data() );

    aStream >> temp_str;
    setStopCommand( (const char*) temp_str.data() );

    aStream >> temp_int;
    setRepeatInterval( temp_int);

    aStream >> temp_int;
    setTemplateType(temp_int);

    if( !checkSchedule() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

/*== Private Functions == */

bool CtiMCSchedule::isValidTime(const RWTime& t) const
{
    return ( t > RWTime( RWDate( 1, 1990 ) ));
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
        if( fld[i] <= 0 || fld[i] >= 128 )
        {                       
            CtiLockGuard< CtiLogger > guard(dout); 
            dout << RWTime() << "CORRUPT FIELD: " << fld << " len: " << fld.length() << " value is: " << val << endl;         
            return false;
        }
    
    return true;
}
