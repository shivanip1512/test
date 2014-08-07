#include "precompiled.h"

#include "tbl_devicereadjoblog.h"
#include "logger.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;

CtiTblDeviceReadJobLog::CtiTblDeviceReadJobLog( long jobLogId, long scheduleId, CtiTime& start, CtiTime& stop) :
_jobLogId(jobLogId),
_scheduleId(scheduleId),
_startTime(start),
_stopTime(stop)
{}

CtiTblDeviceReadJobLog::~CtiTblDeviceReadJobLog()
{
}

long  CtiTblDeviceReadJobLog::getJobLogId() const
{
    return _jobLogId;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setJobLogId( long jobLogId )
{
    _jobLogId = jobLogId;
    return *this;
}

long  CtiTblDeviceReadJobLog::getScheduleId() const
{
    return _scheduleId;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setScheduleId( long scheduleId )
{
    _scheduleId = scheduleId;
    return *this;
}

const CtiTime&  CtiTblDeviceReadJobLog::getStartTime() const
{
    return _startTime;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setStartTime( CtiTime& startTime )
{
    _startTime = startTime;
    return *this;
}

const CtiTime&  CtiTblDeviceReadJobLog::getStopTime() const
{
    return _stopTime;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setStopTime( CtiTime& stopTime )
{
    _stopTime = stopTime;
    return *this;
}

string CtiTblDeviceReadJobLog::getTableName()
{
    return "DeviceReadJobLog";
}

bool CtiTblDeviceReadJobLog::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getJobLogId()
        << getScheduleId()
        << getStartTime()
        << getStopTime();

    return Cti::Database::executeCommand( inserter, __FILE__, __LINE__ );
}

bool CtiTblDeviceReadJobLog::UpdateStopTime()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "stoptime = ?"
                                    " where "
                                        "devicereadjoblogid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getStopTime()
        << getJobLogId();

    return Cti::Database::executeUpdater( updater, __FILE__, __LINE__ );
}

