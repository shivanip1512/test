#include "precompiled.h"

#include "tbl_devicereadrequestlog.h"
#include "logger.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;

CtiTblDeviceReadRequestLog::CtiTblDeviceReadRequestLog( long requestLogId, long requestId, const string &cmd_line, CtiTime& start, CtiTime& stop, long jobId) :
_requestLogId(requestLogId),
_requestId(requestId),
_command(cmd_line),
_startTime(start),
_stopTime(stop),
_readJobId(jobId)
{}

CtiTblDeviceReadRequestLog::~CtiTblDeviceReadRequestLog()
{
}

long  CtiTblDeviceReadRequestLog::getRequestLogId() const
{
    return _requestLogId;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setRequestLogId( long requestLogId )
{
    _requestLogId = requestLogId;
    return *this;
}

long  CtiTblDeviceReadRequestLog::getRequestId() const
{
    return _requestId;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setRequestId( long requestId )
{
    _requestId = requestId;
    return *this;
}

long  CtiTblDeviceReadRequestLog::getReadJobId() const
{
    return _readJobId;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setReadJobId( long readJobId )
{
    _readJobId = readJobId;
    return *this;
}

const CtiTime&  CtiTblDeviceReadRequestLog::getStartTime() const
{
    return _startTime;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setStartTime( CtiTime& startTime )
{
    _startTime = startTime;
    return *this;
}

const CtiTime&  CtiTblDeviceReadRequestLog::getStopTime() const
{
    return _stopTime;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setStopTime( CtiTime& stopTime )
{
    _stopTime = stopTime;
    return *this;
}

const string& CtiTblDeviceReadRequestLog::getCommand() const
{
    return _command;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setCommand( string& cmd )
{
    _command = cmd;
    return *this;
}

string CtiTblDeviceReadRequestLog::getTableName()
{
    return "DeviceReadRequestLog";
}

bool CtiTblDeviceReadRequestLog::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getRequestLogId()
        << getRequestId()
        << getCommand()
        << getStartTime()
        << getStopTime()
        << getReadJobId();

    return Cti::Database::executeCommand( inserter, __FILE__, __LINE__ );
}

bool CtiTblDeviceReadRequestLog::Update()
{
    static const string sql = "update " + getTableName() +
                                   " set "
                                        "requestid = ?, "
                                        "command = ?, "
                                        "starttime = ?, "
                                        "stoptime = ?, "
                                        "devicereadjoblogid = ?"
                                   " where "
                                        "devicereadrequestlogid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getRequestId()
        << getCommand()
        << getStartTime()
        << getStopTime()
        << getReadJobId()
        << getRequestLogId();

    return Cti::Database::executeUpdater( updater, __FILE__, __LINE__ );
}

