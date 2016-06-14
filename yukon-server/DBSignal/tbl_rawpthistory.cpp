#include "precompiled.h"

#include "tbl_rawpthistory.h"
#include "logger.h"
#include "database_connection.h"
#include "database_exceptions.h"

#include <boost/algorithm/string/join.hpp>

using std::endl;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseException;

using DbClientType = DatabaseConnection::ClientType;

CtiTableRawPointHistory::CtiTableRawPointHistory(long pid, int qual, double val, const CtiTime tme, int millis) :
    _pointId(pid),
    _quality(qual),
    _value(val),
    _time(tme),
    _millis(validateMillis(millis))
{
}


std::string CtiTableRawPointHistory::getTempTableCreationSql(const DbClientType clientType)
{
    switch( clientType )
    {
        case DbClientType::Oracle:
        {
            return 
                "create global temporary table Temp_RPH ("
                "POINTID   NUMBER   not null,"
                "TIMESTAMP DATE     not null,"
                "QUALITY   NUMBER   not null,"
                "VALUE     FLOAT    not null,"
                "millis    SMALLINT not null)"
                " on commit preserve rows";
        }
        case DbClientType::SqlServer:
        {
            return 
                "create table ##rph ("
                "POINTID   numeric  not null,"
                "TIMESTAMP datetime not null,"
                "QUALITY   numeric  not null,"
                "VALUE     float    not null,"
                "millis    smallint not null)";
        }
    }

    throw DatabaseException("Unknown client type " + std::to_string(static_cast<unsigned>(clientType)));
}


std::string CtiTableRawPointHistory::getTempTableTruncationSql(const DbClientType clientType)
{
    switch( clientType )
    {
        case DbClientType::Oracle:
            return "truncate table Temp_RPH";
        case DbClientType::SqlServer:
            return "truncate table ##rph";
    }

    throw Cti::Database::DatabaseException("Unknown client type " + std::to_string(static_cast<unsigned>(clientType)));
}


std::string CtiTableRawPointHistory::getInsertSql(const DbClientType clientType, size_t rows)
{
    const std::string oraclePrefix = "INSERT ALL";
    const std::string oracleInfix  = " INTO Temp_RPH VALUES(?,?,?,?,?)";
    const std::string oracleSuffix = " SELECT 1 FROM DUAL;";

    const std::string sqlPrefix = "INSERT INTO ##rph (pointid,timestamp,quality,value,millis) VALUES";
    const std::string sqlInfix = " (?,?,?,?,?)";  //  joined by commas

    switch( clientType )
    {
        case DbClientType::SqlServer:
            return sqlPrefix 
                    + boost::algorithm::join(std::vector<std::string> { rows, sqlInfix }, ",");

        case DbClientType::Oracle:
            return oraclePrefix 
                    + boost::algorithm::join(std::vector<std::string> { rows, oracleInfix }, ",") 
                    + oracleSuffix;
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(clientType)) };
}

std::string CtiTableRawPointHistory::getFinalizeSql(const DbClientType clientType)
{
    switch( clientType )
    {
        case DbClientType::SqlServer:
        {
            return
                "declare @maxChangeId numeric;"
                "select @maxChangeId = max(changeid) from rawpointhistory;"
                "insert into RAWPOINTHISTORY (changeid, pointid, timestamp, quality, value, millis)"
				" select @maxChangeId + ROW_NUMBER() OVER (ORDER BY (SELECT 1)), pointid, timestamp, quality, value, millis"
                " FROM ##rph;";
        }
        case DbClientType::Oracle:
        {
            return
                "DECLARE maxChangeId number;"
                    "BEGIN"
                    " SELECT MAX(changeid) INTO maxChangeId"
                    " FROM RAWPOINTHISTORY;"
                "INSERT INTO RAWPOINTHISTORY"
					" SELECT maxChangeId + ROW_NUMBER() OVER(ORDER BY (SELECT 1 FROM DUAL)), pointid, timestamp, quality, value, millis"
                    " FROM Temp_RPH; "
                "END;";
        }
    }

    throw DatabaseException{ "Invalid client type " + std::to_string(static_cast<unsigned>(clientType)) };
}
            

void CtiTableRawPointHistory::fillInserter(Cti::RowWriter &inserter)
{
    inserter
        << _pointId
        << _time
        << _quality
        << _value
        << _millis;
}

int CtiTableRawPointHistory::validateMillis(int millis)
{
    if( millis < 0 )
    {
        CTILOG_ERROR(dout, "millis = "<< millis <<" < 0 - returning 0");

        return 0;
    }

    if( millis > 999 )
    {
        CTILOG_ERROR(dout, "millis = "<< millis <<" > 999 - returning % 1000");

        return millis % 1000;
    }

    return millis;
}


std::string CtiTableRawPointHistory::toString() const
{
    return 
        Cti::StreamBuffer{} 
            << _pointId 
            << "," << _time
            << "," << _quality
            << "," << _value
            << "," << _millis;
}
