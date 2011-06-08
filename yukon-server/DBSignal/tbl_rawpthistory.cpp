#include "yukon.h"
#include "tbl_rawpthistory.h"
#include "dbaccess.h"
#include "logger.h"
#include "ctitime.h"
#include "database_writer.h"
#include "database_reader.h"


using std::string;
using std::endl;

void CtiTableRawPointHistory::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    Insert(conn);
}

void CtiTableRawPointHistory::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter 
        << getChangeID()
        << getPointID()
        << getTime()
        << getQuality()
        << getValue()
        << getMillis();

    if( ! inserter.execute() )
    {
        LONG newcid = ChangeIdGen(true);

        if(newcid != getChangeID())
        {
            CtiTime Now;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Insert collision occurred in table " << getTableName() << "." << endl;
                dout << Now << "   ChangeId has been re-initialized.  There may be two copies of dispatch inserting into this DB" << endl;
            }

            setChangeID( newcid );

            // Try again with new ChangeID

            Cti::Database::DatabaseWriter   inserter(conn, sql);

            inserter 
                << getChangeID()
                << getPointID()
                << getTime()
                << getQuality()
                << getValue()
                << getMillis();

            if( ! inserter.execute() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Unable to insert point change for point id " << getPointID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << inserter.asString() << endl;
            }
        }
    }
}


CtiTableRawPointHistory& CtiTableRawPointHistory::operator=(const CtiTableRawPointHistory& aRef)
{
    if(this != &aRef)
    {
        setChangeID(aRef.getChangeID());
        setPointID(aRef.getPointID());
        setTime(aRef.getTime());
        setQuality(aRef.getQuality());
        setValue(aRef.getValue());
        setMillis(aRef.getMillis());
    }
    return *this;
}

RWBoolean CtiTableRawPointHistory::operator<(const CtiTableRawPointHistory& aRef) const
{
    return(getTime() < aRef.getTime());
}


INT CtiTableRawPointHistory::getQuality() const
{
    return _quality;
}

DOUBLE CtiTableRawPointHistory::getValue() const
{
    return _value;
}

string CtiTableRawPointHistory::getTableName() const
{
    return string("rawpointhistory");
}

LONG CtiTableRawPointHistory::getChangeID() const
{
    return _changeID;
}

LONG CtiTableRawPointHistory::getPointID() const
{
    return _pointID;
}

CtiTime CtiTableRawPointHistory::getTime() const
{
    return _time;
}

INT CtiTableRawPointHistory::getMillis() const
{
    return _millis;
}

CtiTableRawPointHistory& CtiTableRawPointHistory::setChangeID(LONG id)
{
    _changeID = id;
    return *this;
}

CtiTableRawPointHistory& CtiTableRawPointHistory::setPointID(LONG id)
{
    _pointID = id;
    return *this;
}

CtiTableRawPointHistory& CtiTableRawPointHistory::setTime(const CtiTime &rwt)
{
    _time = rwt;
    return *this;
}

CtiTableRawPointHistory& CtiTableRawPointHistory::setMillis(INT millis)
{
    if( millis > 999 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setMillis(), millis = " << millis << " < 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis = 0;
    }

    _millis = millis;

    return *this;
}

CtiTableRawPointHistory& CtiTableRawPointHistory::setQuality(const INT &qual)
{
    _quality = qual;
    return *this;
}

CtiTableRawPointHistory& CtiTableRawPointHistory::setValue(const DOUBLE &val)
{
    _value = val;
    return *this;
}

void CtiTableRawPointHistory::Restore()
{
    static const string sql =  "SELECT RPH.changeid, RPH.pointid, RPH.timestamp, RPH.quality, RPH.value, RPH.millis "
                               "FROM rawpointhistory RPH "
                               "WHERE RPH.pointid = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getPointID();

    reader.execute();

    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
}

void CtiTableRawPointHistory::RestoreMax()
{
    static const string sql =  "SELECT RPH.changeid, RPH.pointid, RPH.timestamp, RPH.quality, RPH.value, RPH.millis "
                               "FROM rawpointhistory RPH "
                               "WHERE RPH.pointid = ? AND RPH.timestamp = (select max(timestamp) "
                                                                          "from rawpointhistory "
                                                                          "where pointid = ?)";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getPointID()
           << getPointID();

    reader.execute();

    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
}

void CtiTableRawPointHistory::DecodeDatabaseReader( Cti::RowReader& rdr )
{
    CtiTime dt;
    INT millis;

    rdr["changeid"]     >> _changeID;
    rdr["pointid"]      >> _pointID;
    rdr["timestamp"]    >> dt;
    rdr["quality"]      >> _quality;
    rdr["value"]        >> _value;
    rdr["millis"]       >> millis;

    setTime( dt );             // Convert that thing back into a time.
    setMillis( millis );
}


