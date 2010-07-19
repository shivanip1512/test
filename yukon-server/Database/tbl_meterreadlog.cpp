
/*-----------------------------------------------------------------------------*
*
* File:   tbl_meterreadlog
*
* Date:   1/24/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/Database/tbl_meterreadlog.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2007/03/08 21:56:14 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "tbl_meterreadlog.h"
#include "dbaccess.h"
#include "logger.h"
#include "ctitime.h"
#include "database_writer.h"


void CtiTableMeterReadLog::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    Insert(conn);
}

void CtiTableMeterReadLog::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter <<
        getLogID() <<
        getDeviceID() <<
        getTime() <<
        getStatusCode() <<
        getRequestLogID();

    if( ! inserter.execute() )
    {
        LONG newcid = SynchronizedIdGen("DeviceReadLog", 1);

        if(newcid != getLogID() && newcid != 0)
        {
            CtiTime Now;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Insert collision occurred in table " << getTableName() << "." << endl;
                dout << Now << "   ChangeId has been re-initialized." << endl;
            }

            setLogID( newcid );

            Cti::Database::DatabaseWriter   inserter(conn, sql);

            inserter <<
                getLogID() <<
                getDeviceID() <<
                getTime() <<
                getStatusCode() <<
                getRequestLogID();

            if( ! inserter.execute() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Unable to insert log for device id " << getDeviceID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << inserter.asString() << endl;
            }
        }
    }
}


CtiTableMeterReadLog& CtiTableMeterReadLog::operator=(const CtiTableMeterReadLog& aRef)
{
    if(this != &aRef)
    {
        setLogID(aRef.getLogID());
        setDeviceID(aRef.getDeviceID());
        setRequestLogID(aRef.getRequestLogID());
        setStatusCode(aRef.getStatusCode());
        setTime(aRef.getTime());
    }
    return *this;
}

RWBoolean CtiTableMeterReadLog::operator<(const CtiTableMeterReadLog& aRef) const
{
    bool retVal;
    if( getTime() < aRef.getTime() )
    {
        retVal = true;
    }
    else if( getTime() == aRef.getTime() && getDeviceID() < aRef.getDeviceID() )
    {
        retVal = true;
    }
    else
    {
        retVal = false;
    }
    return(retVal);
}

LONG CtiTableMeterReadLog::getLogID() const
{
    return _logID;
}

LONG CtiTableMeterReadLog::getDeviceID() const
{
    return _deviceID;
}

LONG CtiTableMeterReadLog::getRequestLogID() const
{
    return _requestID;
}

LONG CtiTableMeterReadLog::getStatusCode() const
{
    return _statusCode;
}

CtiTime CtiTableMeterReadLog::getTime() const
{
    return _time;
}

string CtiTableMeterReadLog::getTableName() const
{
    return string("devicereadlog");
}

CtiTableMeterReadLog&   CtiTableMeterReadLog::setLogID(LONG id)
{
    _logID = id;
    return *this;
}

CtiTableMeterReadLog&   CtiTableMeterReadLog::setDeviceID(LONG id)
{
    _deviceID = id;
    return *this;
}

CtiTableMeterReadLog&   CtiTableMeterReadLog::setRequestLogID(LONG id)
{
    _requestID = id;
    return *this;
}

CtiTableMeterReadLog&   CtiTableMeterReadLog::setStatusCode(LONG code)
{
    _statusCode = code;
    return *this;
}

CtiTableMeterReadLog& CtiTableMeterReadLog::setTime(const CtiTime &rwt)
{
    _time = rwt;
    return *this;
}

void CtiTableMeterReadLog::DecodeDatabaseReader( Cti::RowReader& rdr )
{
    rdr["deviceid"]     >> _deviceID;
    rdr["foreignid"]    >> _requestID;
    rdr["statuscode"]   >> _statusCode;
    rdr["timestamp"]    >> _time;
}

