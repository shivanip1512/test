#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/tbl_mcsched.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/12/16 23:55:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*---------------------------------------------------------------------------

        Filename:  tbl_mcsched.h

        Programmer:  Aaron Lauinger

        Description:    Source file for CtiTableMCSchedule

        Initial Date:  4/7/99
                       1/11/01

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999, 2001
---------------------------------------------------------------------------*/
#include "tbl_mcsched.h"
#include "dbaccess.h"

//Name of the database table
const char* CtiTableMCSchedule::_table_name = "MACSchedule";

CtiTableMCSchedule::CtiTableMCSchedule()
{
}

CtiTableMCSchedule::CtiTableMCSchedule(
                                      long sched_id,
                                      const string& category_name,
                                      long h_sched_id,
                                      const string& cmd_file,
                                      const string& state,
                                      const string& start_policy,
                                      const string& stop_policy,
                                      const RWTime& last_run_time,
                                      const string& last_run_status,
                                      int start_day,
                                      int start_month,
                                      int start_year,
                                      const string& start_time,
                                      const string& stop_time,
                                      const string& valid_week_days,
                                      int duration,
                                      const RWTime& manual_start_time,
                                      const RWTime& manual_stop_time,
                                      int template_type )
:
_schedule_id(sched_id),
_category_name(category_name),
_holiday_schedule_id(h_sched_id),
_command_file(cmd_file),
_current_state(state),
_start_policy(start_policy),
_stop_policy(stop_policy),
_last_run_time(last_run_time),
_last_run_status(last_run_status),
_start_day(start_day),
_start_month(start_month),
_start_year(start_year),
_start_time(start_time),
_stop_time(stop_time),
_valid_week_days(valid_week_days),
_duration(duration),
_manual_start_time(manual_start_time),
_manual_stop_time(manual_stop_time),
_template_type(template_type)
{
}

CtiTableMCSchedule::~CtiTableMCSchedule()
{
}

void CtiTableMCSchedule::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(_table_name);

    selector                            <<
    keyTable["scheduleid"]          <<
    keyTable["categoryname"]        <<
    keyTable["holidayscheduleid"]   <<
    keyTable["commandfile"]         <<
    keyTable["currentstate"]        <<
    keyTable["startpolicy"]         <<
    keyTable["stoppolicy"]          <<
    keyTable["lastruntime"]         <<
    keyTable["lastrunstatus"]       <<
    keyTable["startday"]            <<
    keyTable["startmonth"]          <<
    keyTable["startyear"]           <<
    keyTable["starttime"]           <<
    keyTable["stoptime"]            <<
    keyTable["validweekdays"]       <<
    keyTable["duration"]            <<
    keyTable["manualstarttime"]     <<
    keyTable["manualstoptime"]      <<
    keyTable["template"];

    selector.from(keyTable);
}

bool CtiTableMCSchedule::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWDBNullIndicator isNull;

    // RWDBReader has no operator>>(string&) so use
    // a temporary RWCString and then copy it
    RWCString temp;

    rdr["scheduleid"]       >> _schedule_id;

    rdr["categoryname"]     >> temp;
    _category_name = temp;

    rdr["holidayscheduleid"] >> _holiday_schedule_id;

    rdr["commandfile"]      >> temp;
    _command_file = temp;

    rdr["currentstate"]     >> temp;
    _current_state = temp;

    rdr["startpolicy"]      >> temp;
    _start_policy = temp;

    rdr["stoppolicy"]       >> temp;
    _stop_policy = temp;

    rdr["lastruntime"]      >> _last_run_time;

    rdr["lastrunstatus"]    >> temp;
    _last_run_status = temp;

    rdr["startday"]         >> _start_day;
    rdr["startmonth"]       >> _start_month;
    rdr["startyear"]        >> _start_year;

    rdr["starttime"]        >> temp;
    _start_time = temp;

    rdr["stoptime"]         >> temp;
    _stop_time = temp;

    rdr["validweekdays"]    >> temp;
    _valid_week_days = temp;

    rdr["duration"]         >> _duration;

    rdr["manualstarttime"] >> isNull;

    // a null man start or stop time indicates there isn't one
    // so set them to invalid if that is what we find
    if( isNull )
        _manual_start_time = RWTime( (unsigned long) 0 );
    else
        rdr["manualstarttime"] >> _manual_start_time;

    rdr["manualstoptime"] >> isNull;

    if( isNull )
        _manual_stop_time = RWTime( (unsigned long) 0 );
    else
        rdr["manualstoptime"] >> _manual_stop_time;

    rdr["template"]         >> _template_type;

    return true;
}

bool CtiTableMCSchedule::Update()
{
    bool ret_val = false;
    string sql;

    try
    {
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable t = conn.database().table( _table_name );

            RWDBUpdater updater = t.updater();

            updater.where( t["ScheduleID"] == getScheduleID() );

            updater << t["CategoryName"].assign((const char*) getCategoryName().data());

            updater << t["HolidayScheduleID"].assign(getHolidayScheduleID());

            updater << t["CommandFile"].assign((const char*) getCommandFile().data());

            updater << t["CurrentState"].assign((const char*) getCurrentState().data());

            updater << t["StartPolicy"].assign((const char*) getStartPolicy().data());

            updater << t["StopPolicy"].assign((const char*) getStopPolicy().data());

            if( getLastRunTime().isValid() )
                updater << t["LastRunTime"].assign( getLastRunTime() );

            updater << t["LastRunStatus"].assign((const char*) getLastRunStatus().data());

            updater << t["StartDay"].assign( getStartDay() );
            updater << t["StartMonth"].assign( getStartMonth() );
            updater << t["StartYear"].assign( getStartYear() );

            updater << t["StartTime"].assign((const char*) getStartTime().data());

            updater << t["StopTime"].assign((const char*) getStopTime().data());

            updater << t["ValidWeekDays"].assign((const char*) getValidWeekDays().data());

            updater << t["Duration"].assign( getDuration() );

            /* manual start and stop times updating is disbled until
               a way is found to insert a null or invalid date */


            if( getManualStartTime().isValid() )
                updater << t["ManualStartTime"].assign( getManualStartTime() );
            else
                updater << t["ManualStartTime"].assign( RWDBValue() );

            if( getManualStopTime().isValid() )
                updater << t["ManualStopTime"].assign( getManualStopTime() );
            else
                updater << t["ManualStopTime"].assign( RWDBValue() );

            updater << t["Template"].assign( getTemplateType() );

            sql = (const char*) updater.asString().data();

            RWDBResult result = updater.execute(conn);

            ret_val = ( result.status().errorCode() == RWDBStatus::ok );
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()
        << " An exception occured updating table \""
        << _table_name
        << "\""
        << endl;
    }

    if( !ret_val )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()
        << " "
        << sql
        << endl;
    }

    return ret_val;
}

bool CtiTableMCSchedule::Insert()
{
    bool ret_val = false;
    string sql;

    try
    {
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable t = conn.database().table( _table_name );
            RWDBInserter inserter = t.inserter();

            inserter << getScheduleID();

            inserter << (const char*) getCategoryName().data();

            inserter << getHolidayScheduleID();

            inserter << (const char*) getCommandFile().data();

            inserter << (const char*) getCurrentState().data();

            inserter << (const char*) getStartPolicy().data();

            inserter << (const char*) getStopPolicy().data();

            if( getLastRunTime().isValid() )
                inserter << RWDBDateTime( getLastRunTime() );
            else
                inserter << RWDBValue(); //null

            inserter << (const char*) getLastRunStatus().data();

            inserter << getStartDay();
            inserter << getStartMonth();
            inserter << getStartYear();

            inserter << (const char*) getStartTime().data();

            inserter << (const char*) getStopTime().data();

            inserter << (const char*) getValidWeekDays().data();

            inserter << getDuration();

            if( getManualStartTime().isValid() )
                inserter << RWDBDateTime( getManualStartTime() );
            else
                inserter << RWDBValue(); //null

            if( getManualStopTime().isValid() )
                inserter << RWDBDateTime( getManualStopTime() );
            else
                inserter << RWDBValue(); //null

            inserter << getTemplateType();

            sql = (const char*) inserter.asString().data();

            RWDBResult result = inserter.execute(conn);

            ret_val = ( result.status().errorCode() == RWDBStatus::ok );

        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()
        << " An exception occured inserting into table \""
        << _table_name
        << "\""
        << endl;
    }

    if( !ret_val )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()
        << " "
        << sql
        << endl;
    }

    return ret_val;

}

bool CtiTableMCSchedule::Delete()
{
    bool ret_val = false;
    string sql;

    try
    {
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable t = conn.database().table( _table_name );
            RWDBDeleter deleter = t.deleter();

            deleter.where( t["ScheduleID"] == getScheduleID() );

            sql = (const char*) deleter.asString().data();

            RWDBResult result = deleter.execute();
            ret_val = ( result.status().errorCode() == RWDBStatus::ok );
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()
        << " An exception occured deleting from table \""
        << _table_name
        << "\""
        << endl;
    }

    if( !ret_val )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()
        << " "
        << sql
        << endl;
    }

    return ret_val;

}
/*========Setters and Getters Below========*/

long CtiTableMCSchedule::getScheduleID() const
{
    return _schedule_id;
}

const string& CtiTableMCSchedule::getCategoryName() const
{
    return _category_name;
}

long CtiTableMCSchedule::getHolidayScheduleID() const
{
    return _holiday_schedule_id;
}

const string& CtiTableMCSchedule::getCommandFile() const
{
    return _command_file;
}

const string& CtiTableMCSchedule::getCurrentState() const
{
    return _current_state;
}

const string& CtiTableMCSchedule::getStartPolicy() const
{
    return _start_policy;
}

const string& CtiTableMCSchedule::getStopPolicy() const
{
    return _stop_policy;
}

const RWTime& CtiTableMCSchedule::getLastRunTime() const
{
    return _last_run_time;
}

const string& CtiTableMCSchedule::getLastRunStatus() const
{
    return _last_run_status;
}

int CtiTableMCSchedule::getStartDay() const
{
    return _start_day;
}

int CtiTableMCSchedule::getStartMonth() const
{
    return _start_month;
}

int CtiTableMCSchedule::getStartYear() const
{
    return _start_year;
}

const string& CtiTableMCSchedule::getStartTime() const
{
    return _start_time;
}

const string& CtiTableMCSchedule::getStopTime() const
{
    return _stop_time;
}

const string& CtiTableMCSchedule::getValidWeekDays() const
{
    return _valid_week_days;
}

int CtiTableMCSchedule::getDuration() const
{
    return _duration;
}

const RWTime& CtiTableMCSchedule::getManualStartTime() const
{
    return _manual_start_time;
}

const RWTime& CtiTableMCSchedule::getManualStopTime() const
{
    return _manual_stop_time;
}

int CtiTableMCSchedule::getTemplateType() const
{
    return _template_type;
}

CtiTableMCSchedule& CtiTableMCSchedule::setScheduleID(long sched_id)
{
    _schedule_id = sched_id;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setCategoryName(const string& category_name)
{
    _category_name = category_name;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setHolidayScheduleID(long holiday_sched_id)
{
    _holiday_schedule_id = holiday_sched_id;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setCommandFile(const string& cmd_file)
{
    _command_file = cmd_file;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setCurrentState(const string& state)
{
    _current_state = state;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setStartPolicy(const string& start_policy)
{
    _start_policy = start_policy;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setStopPolicy(const string& stop_policy)
{
    _stop_policy = stop_policy;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setLastRunTime(const RWTime& last_run_time)
{
    _last_run_time = last_run_time;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setLastRunStatus(const string& last_run_status)
{
    _last_run_status = last_run_status;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setStartDay(int start_day)
{
    _start_day = start_day;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setStartMonth(int start_month)
{
    _start_month = start_month;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setStartYear(int start_year)
{
    _start_year = start_year;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setStartTime(const string& start_time)
{
    _start_time = start_time;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setStopTime(const string& stop_time)
{
    _stop_time = stop_time;
    return *this;
}


CtiTableMCSchedule& CtiTableMCSchedule::setValidWeekDays(
                                                        const string& valid_week_days)
{
    _valid_week_days = valid_week_days;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setDuration(int duration)
{
    _duration = duration;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setManualStartTime(
                                                          const RWTime& manual_start_time )
{
    _manual_start_time = manual_start_time;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setManualStopTime(
                                                         const RWTime& manual_stop_time )
{
    _manual_stop_time = manual_stop_time;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setTemplateType(int template_type)
{
    _template_type = template_type;
    return *this;
}

