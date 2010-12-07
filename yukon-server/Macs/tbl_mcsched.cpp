

#include "yukon.h"

#include "tbl_mcsched.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_writer.h"

//Name of the database table
const char* CtiTableMCSchedule::_table_name = "MACSchedule";

CtiTableMCSchedule::CtiTableMCSchedule()
    : _schedule_id(0), _holiday_schedule_id(0),
      _start_day(0), _start_month(0), _start_year(0),
      _duration(0), _template_type(0)
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
                                      const CtiTime& last_run_time,
                                      const string& last_run_status,
                                      int start_day,
                                      int start_month,
                                      int start_year,
                                      const string& start_time,
                                      const string& stop_time,
                                      const string& valid_week_days,
                                      int duration,
                                      const CtiTime& manual_start_time,
                                      const CtiTime& manual_stop_time,
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

bool CtiTableMCSchedule::DecodeDatabaseReader(Cti::RowReader &rdr)
{

    string temp;

    rdr["scheduleid"]       >> _schedule_id;

    rdr["categoryname"]     >> _category_name;

    rdr["holidayscheduleid"] >> _holiday_schedule_id;

    rdr["commandfile"]      >> _command_file;

    rdr["currentstate"]     >> _current_state;

    rdr["startpolicy"]      >> _start_policy;

    rdr["stoppolicy"]       >> _stop_policy;

    rdr["lastruntime"]      >> _last_run_time;

    rdr["lastrunstatus"]    >> _last_run_status;

    rdr["startday"]         >> _start_day;
    rdr["startmonth"]       >> _start_month;
    rdr["startyear"]        >> _start_year;

    rdr["starttime"]        >> _start_time;

    rdr["stoptime"]         >> _stop_time;

    rdr["validweekdays"]    >> _valid_week_days;

    rdr["duration"]         >> _duration;

    // a null man start or stop time indicates there isn't one
    // so set them to invalid if that is what we find
    if( rdr["manualstarttime"].isNull() )
        _manual_start_time = CtiTime( (unsigned long) 0 );
    else
        rdr["manualstarttime"] >> _manual_start_time;

    if( rdr["manualstoptime"].isNull() )
        _manual_stop_time = CtiTime( (unsigned long) 0 );
    else
        rdr["manualstoptime"] >> _manual_stop_time;

    rdr["template"]         >> _template_type;

    return true;
}

bool CtiTableMCSchedule::Update()
{
    static const string sql =  "update " + std::string(_table_name) + 
                                " set "
                                    "CategoryName = ?, "
                                    "HolidayScheduleID = ?, "
                                    "CommandFile = ?, "
                                    "CurrentState = ?, "
                                    "StartPolicy = ?, "
                                    "StopPolicy = ?, "
                                    "LastRunTime = ?, "
                                    "LastRunStatus = ?, "
                                    "StartDay = ?, "
                                    "StartMonth = ?, "
                                    "StartYear = ?, "
                                    "StartTime = ?, "
                                    "StopTime = ?, "
                                    "ValidWeekDays = ?, "
                                    "Duration = ?, "
                                    "ManualStartTime = ?, "
                                    "ManualStopTime = ?, "
                                    "Template = ?"
                                " where "
                                    "ScheduleID = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getCategoryName()
        << getHolidayScheduleID()
        << getCommandFile()
        << getCurrentState()
        << getStartPolicy()
        << getStopPolicy();

    if( getLastRunTime().isValid() )
    {    
        updater << getLastRunTime();
    }
    else
    {
        updater << Cti::Database::DatabaseWriter::Null;
    }

    updater
        << getLastRunStatus()
        << getStartDay()
        << getStartMonth()
        << getStartYear()
        << getStartTime()
        << getStopTime()
        << getValidWeekDays()
        << getDuration();

    if( getManualStartTime().isValid() )
    {    
        updater << getManualStartTime();
    }
    else
    {
        updater << Cti::Database::DatabaseWriter::Null;
    }

    if( getManualStopTime().isValid() )
    {    
        updater << getManualStopTime();
    }
    else
    {
        updater << Cti::Database::DatabaseWriter::Null;
    }

    updater
        << getTemplateType()
        << getScheduleID();

    bool success = updater.execute();
    success &= ( updater.rowsAffected() > 0 );

    if( ! success )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << CtiTime() << updater.asString() << endl;
    }

    return success;
}

bool CtiTableMCSchedule::Insert()
{
    static const std::string sql = "insert into " + std::string(_table_name) +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getScheduleID()
        << getCategoryName()
        << getHolidayScheduleID()
        << getCommandFile()
        << getCurrentState()
        << getStartPolicy()
        << getStopPolicy();

    if( getLastRunTime().isValid() )
    {    
        inserter << CtiTime( getLastRunTime() );
    }
    else
    {    
        inserter << Cti::Database::DatabaseWriter::Null;
    }

    inserter
        << getLastRunStatus()
        << getStartDay()
        << getStartMonth()
        << getStartYear()
        << getStartTime()
        << getStopTime()
        << getValidWeekDays()
        << getDuration();

    if( getManualStartTime().isValid() )
    {    
        inserter << CtiTime( getManualStartTime() );
    }
    else
    {    
        inserter << Cti::Database::DatabaseWriter::Null;
    }

    if( getManualStopTime().isValid() )
    {    
        inserter << CtiTime( getManualStopTime() );
    }
    else
    {    
        inserter << Cti::Database::DatabaseWriter::Null;
    }

    inserter << getTemplateType();

    bool success = inserter.execute();

    if( ! success )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << CtiTime() << inserter.asString() << endl;
    }

    return success;
}

bool CtiTableMCSchedule::Delete()
{
    static const std::string sql = "delete from " + std::string(_table_name) + " where ScheduleID = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getScheduleID();

    bool ret_val = deleter.execute();

    if( !ret_val )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << CtiTime() << deleter.asString() << endl;
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

const CtiTime& CtiTableMCSchedule::getLastRunTime() const
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

const CtiTime& CtiTableMCSchedule::getManualStartTime() const
{
    return _manual_start_time;
}

const CtiTime& CtiTableMCSchedule::getManualStopTime() const
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

CtiTableMCSchedule& CtiTableMCSchedule::setLastRunTime(const CtiTime& last_run_time)
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
                                                          const CtiTime& manual_start_time )
{
    _manual_start_time = manual_start_time;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setManualStopTime(
                                                         const CtiTime& manual_stop_time )
{
    _manual_stop_time = manual_stop_time;
    return *this;
}

CtiTableMCSchedule& CtiTableMCSchedule::setTemplateType(int template_type)
{
    _template_type = template_type;
    return *this;
}

