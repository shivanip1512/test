#include "tbl_rawpthistory.h"
#include "dbaccess.h"
#include "logger.h"

void CtiTableRawPointHistory::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    Insert(conn);
}

void CtiTableRawPointHistory::Insert(RWDBConnection &conn)
{
    RWDBTable table = conn.database().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getChangeID() <<
    getPointID() <<
    RWDBDateTime(getTime()) <<
    getQuality() <<
    getValue() <<
    getMillis();

    RWDBStatus stat = inserter.execute( conn ).status();

    if( stat.errorCode() != RWDBStatus::ok )
    {
        LONG newcid = ChangeIdGen(true);

        if(newcid != getChangeID())
        {
            RWTime Now;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Insert collision occurred in table " << getTableName() << "." << endl;
                dout << Now << "   ChangeId has been re-initialized.  There may be two copies of dispatch inserting into this DB" << endl;
            }

            setChangeID( newcid );
            stat = inserter.execute( conn ).status();

            if( stat.errorCode() != RWDBStatus::ok )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unable to insert point change for point id " << getPointID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   " << inserter.asString() << endl;
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

RWCString CtiTableRawPointHistory::getTableName() const
{
    return RWCString("rawpointhistory");
}

LONG CtiTableRawPointHistory::getChangeID() const
{
    return _changeID;
}

LONG CtiTableRawPointHistory::getPointID() const
{
    return _pointID;
}

RWTime CtiTableRawPointHistory::getTime() const
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

CtiTableRawPointHistory& CtiTableRawPointHistory::setTime(const RWTime &rwt)
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
            dout << RWTime() << " **** Checkpoint - setMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - setMillis(), millis = " << millis << " < 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector << table["changeid"]
    << table["pointid"]
    << table["timestamp"]
    << table["quality"]
    << table["value"]
    << table["millis"];

    selector.where( table["pointid"] == getPointID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
}

void CtiTableRawPointHistory::RestoreMax()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    char temp[80];

    sprintf(temp, "select max(timestamp) from rawpointhistory where pointid=%ld", getPointID() );

    selector << table["changeid"]
    << table["pointid"]
    << table["timestamp"]
    << table["quality"]
    << table["value"]
    << table["millis"];

    selector.where( table["pointid"] == getPointID() &&  table["timestamp"] == temp);

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
}

void CtiTableRawPointHistory::DecodeDatabaseReader( RWDBReader& rdr )
{
    RWDBDateTime dt;
    INT millis;

    rdr["changeid"]     >> _changeID;
    rdr["pointid"]      >> _pointID;
    rdr["timestamp"]    >> dt;
    rdr["quality"]      >> _quality;
    rdr["value"]        >> _value;
    rdr["millis"]       >> millis;

    setTime( dt.rwtime() );             // Convert that thing back into a time.
    setMillis( millis );
}


