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

CtiTableRawPointHistory::CtiTableRawPointHistory(long pid, int qual, double val, const CtiTime tme, int millis, std::string id) :
    pointId(pid),
    quality(qual),
    value(val),
    time(tme),
    millis(validateMillis(millis)),
    trackingId(id)  //  just for tracking, not written to the database
{
}


std::array<Cti::Database::ColumnDefinition, 5> CtiTableRawPointHistory::getTempTableSchema()
{
    return { Cti::Database::ColumnDefinition
        { "POINTID",   "numeric",  "NUMBER" },
        { "TIMESTAMP", "datetime", "DATE"   },
        { "QUALITY",   "numeric",  "NUMBER" },
        { "VALUE",     "float",    "FLOAT"  },
        { "millis",    "smallint", "SMALLINT" }};
}
            

void CtiTableRawPointHistory::fillRowWriter(Cti::RowWriter &inserter) const
{
    inserter
        << pointId
        << time
        << quality
        << value
        << millis;
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
    std::string delimitedTrackingId =
        trackingId.empty()
            ? ""
            : " " + trackingId;

    return Cti::StreamBuffer{}
        << pointId
        << "," << time
        << "," << quality
        << "," << value
        << "," << millis
        << delimitedTrackingId;
}
