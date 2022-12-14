#include "precompiled.h"

#include "tbl_mcsimpsched.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;

//Name of the database table
const char* CtiTableMCSimpleSchedule::_table_name = "MACSimpleSchedule";

CtiTableMCSimpleSchedule::CtiTableMCSimpleSchedule(
                                                  long schedule_id,
                                                  long target_id,
                                                  const string& start_command,
                                                  const string& stop_command,
                                                  long repeat_interval )
:
_schedule_id(schedule_id),
_target_id(target_id),
_start_command(start_command),
_stop_command(stop_command),
_repeat_interval(repeat_interval)
{

}

long CtiTableMCSimpleSchedule::getScheduleID() const
{
    return _schedule_id;
}

long CtiTableMCSimpleSchedule::getTargetPaoId() const
{
    return _target_id;
}

const string& CtiTableMCSimpleSchedule::getStartCommand() const
{
    return _start_command;
}

const string& CtiTableMCSimpleSchedule::getStopCommand() const
{
    return _stop_command;
}

long CtiTableMCSimpleSchedule::getRepeatInterval() const
{
    return _repeat_interval;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setScheduleID(long schedule_id)
{
    _schedule_id = schedule_id;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setTargetPaoID(const int target_id)
{
    _target_id = target_id;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setStartCommand(const string& start_command)
{
    _start_command = start_command;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setStopCommand(const string& stop_command)
{
    _stop_command = stop_command;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setRepeatInterval(long repeat_interval)
{
    _repeat_interval = repeat_interval;
    return *this;
}

bool CtiTableMCSimpleSchedule::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    rdr["scheduleid"]       >> _schedule_id;

    rdr["targetpaobjectid"] >> _target_id;

    rdr["startcommand"]     >> _start_command;
    rdr["stopcommand"]      >> _stop_command;

    rdr["repeatinterval"]   >> _repeat_interval;

    return true;
}

bool CtiTableMCSimpleSchedule::Update()
{
    static const string sql =  "update " + std::string(_table_name) +
                                " set "
                                    "targetpaobjectid = ?, "
                                    "StartCommand = ?, "
                                    "StopCommand = ?, "
                                    "RepeatInterval = ?"
                                " where "
                                    "ScheduleID = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getTargetPaoId()
        << getStartCommand()
        << getStopCommand()
        << getRepeatInterval()
        << getScheduleID();

    return Cti::Database::executeUpdater( updater, CALLSITE );
}

bool CtiTableMCSimpleSchedule::Insert()
{
    static const std::string sql = "insert into " + std::string(_table_name) + " values (?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getScheduleID()
        << getTargetPaoId()
        << getStartCommand()
        << getStopCommand()
        << getRepeatInterval();

    return Cti::Database::executeCommand( inserter, CALLSITE );
}

bool CtiTableMCSimpleSchedule::Delete()
{
    static const std::string sql = "delete from " + std::string(_table_name) + " where ScheduleID = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getScheduleID();

    return Cti::Database::executeCommand( deleter, CALLSITE );
}

CtiTableMCSimpleSchedule::~CtiTableMCSimpleSchedule()
{};

