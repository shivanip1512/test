

#include "yukon.h"

#include "tbl_devicereadjoblog.h"
#include "logger.h"
#include "dbaccess.h"

#include "database_connection.h"
#include "database_writer.h"

CtiTblDeviceReadJobLog::CtiTblDeviceReadJobLog( long jobLogId, long scheduleId, CtiTime& start, CtiTime& stop) :
_jobLogId(jobLogId),
_scheduleId(scheduleId),
_startTime(start),
_stopTime(stop)
{}

CtiTblDeviceReadJobLog::CtiTblDeviceReadJobLog(const CtiTblDeviceReadJobLog& aRef)
{
    *this = aRef;
}

CtiTblDeviceReadJobLog::~CtiTblDeviceReadJobLog()
{
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::operator=(const CtiTblDeviceReadJobLog& aRef)
{
    if(this != &aRef)
    {
        _jobLogId = aRef.getJobLogId();
        _scheduleId = aRef.getScheduleId();
        _startTime = aRef.getStartTime();
        _stopTime = aRef.getStopTime();
    }
    return *this;
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

    return inserter.execute();
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

    bool success = updater.execute();

    return success && ( updater.rowsAffected() > 0 );
}

