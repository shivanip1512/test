#include "precompiled.h"

#include <iostream>
#include <iomanip>
using namespace std;

#include "dbaccess.h"
#include "logger.h"
#include "tbl_ptdispatch.h"
#include "database_reader.h"
#include "database_writer.h"
#include "ctidate.h"
#include "ctitime.h"

CtiTablePointDispatch::CtiTablePointDispatch() :
_pointID(0),
_timeStamp( CtiDate( (UINT)1, (UINT)1, (UINT)1990 )),
_quality(UnintializedQuality),
_value(0),
_tags(0),
//_staleCount(0),
//_lastAlarmLogID(0),
_nextArchiveTime(CtiTime(YUKONEOT+86400))
{
    setTimeStampMillis(0);
}

CtiTablePointDispatch::CtiTablePointDispatch(LONG pointid,
                                             DOUBLE value,
                                             UINT quality,
                                             const CtiTime& timestamp,
                                             UINT millis) :
_pointID(pointid),
_timeStamp(timestamp),
_quality(quality),
_value(value),
_tags(0),
//_staleCount(0),
//_lastAlarmLogID(0),
_nextArchiveTime(CtiTime(YUKONEOT - 86400))
{
    setTimeStampMillis(millis);

    setDirty(TRUE);
}

CtiTablePointDispatch::CtiTablePointDispatch(const CtiTablePointDispatch& ref) :
_tags(0)
{
    *this = ref;
}

CtiTablePointDispatch::~CtiTablePointDispatch()
{
}

CtiTablePointDispatch& CtiTablePointDispatch::operator=(const CtiTablePointDispatch& right)
{
    if(this != &right)
    {
        Inherited::operator=(right);
        setDirty(TRUE);
        setPointID( right.getPointID() );

        setTimeStamp( right.getTimeStamp() );
        setTimeStampMillis( right.getTimeStampMillis() );
        setQuality( right.getQuality() );
        setValue( right.getValue() );

        resetTags();
        setTags( right.getTags() );
        setNextArchiveTime( right.getNextArchiveTime() );
        //setStaleCount( right.getStaleCount() );
        //setLastAlarmLogID( right.getLastAlarmLogID() );
    }

    return *this;
}

bool CtiTablePointDispatch::operator==(const CtiTablePointDispatch& right) const
{
    return( getPointID() == right.getPointID() );
}

string CtiTablePointDispatch::getTableName()
{
    return "DynamicPointDispatch";
}

string CtiTablePointDispatch::getSQLCoreStatement(long id)
{
    static const string sqlNoID = "SELECT DPD.pointid, DPD.timestamp, DPD.quality, DPD.value, DPD.tags, DPD.nextarchive, "
                                      "DPD.millis "
                                  "FROM DynamicPointDispatch DPD";

    if( id )
    {
        return string(sqlNoID + " WHERE DPD.pointid = ?");
    }
    else
    {
        return sqlNoID;
    }
}

bool CtiTablePointDispatch::Restore()
{
    static const string sql =  "SELECT DD.pointid, DD.timestamp, DD.quality, DD.value, DD.tags, DD.nextarchive, DD.millis "
                               "FROM DynamicPointDispatch DD "
                               "WHERE DD.pointid = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getPointID();

    reader.execute();

    /*
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are sirty and need to be
     *  written into the database
     */
    if( reader() )
    {
        DecodeDatabaseReader( reader );
        return true;
    }
    else
    {
        setDirty( TRUE );
        return false;
    }
}

void CtiTablePointDispatch::DecodeDatabaseReader(Cti::RowReader& rdr )
{
    static const string pointid = "pointid";
    INT millis;

    rdr[pointid] >> _pointID;
    rdr >> _timeStamp;
    rdr >> _quality;
    rdr >> _value;
    rdr >> _tags;
    rdr >> _nextArchiveTime;
    rdr >> millis;

    setTimeStampMillis(millis);

    resetDirty(FALSE);
}

bool CtiTablePointDispatch::Update(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "timestamp = ?, "
                                        "quality = ?, "
                                        "value = ?, "
                                        "tags = ?, "
                                        "nextarchive = ?, "
                                        "stalecount = ?, "
                                        "millis = ?"
                                   " where "
                                        "pointid = ?";

    Cti::Database::DatabaseWriter   updater(conn, sql);

    updater
        << getTimeStamp()
        << getQuality()
        << getValue()
        << getTags()
        << getNextArchiveTime()
        << getStaleCount()
        << getTimeStampMillis()
        << getPointID();

    bool success = executeUpdater(updater);

    if( success )    // No error occured!
    {
        resetDirty(FALSE);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << updater.asString() << endl;
    }

    return success;
}


bool CtiTablePointDispatch::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter
        << getPointID()
        << getTimeStamp()
        << getQuality()
        << getValue()
        << getTags()
        << getNextArchiveTime()
        << getStaleCount()
        << getLastAlarmLogID()
        << getTimeStampMillis();

    bool success = inserter.execute();

    if( success )    // No error occured!
    {
        resetDirty(FALSE);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
    }

    return success;
}

LONG CtiTablePointDispatch::getPointID() const
{
    return _pointID;
}

CtiTablePointDispatch& CtiTablePointDispatch::setPointID(LONG pointid)
{
    setDirty(TRUE);
    _pointID = pointid;
    return *this;
}

const CtiTime& CtiTablePointDispatch::getTimeStamp() const
{
    return _timeStamp;
}

CtiTablePointDispatch& CtiTablePointDispatch::setTimeStamp(const CtiTime& timestamp)
{
    setDirty(TRUE);
    _timeStamp = timestamp;
    return *this;
}

INT CtiTablePointDispatch::getTimeStampMillis() const
{
    return _timeStampMillis;
}

CtiTablePointDispatch& CtiTablePointDispatch::setTimeStampMillis(INT millis)
{
    setDirty(TRUE);

    if( millis > 999 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setTimeStampMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setTimeStampMillis(), millis = " << millis << " < 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis = 0;
    }

    _timeStampMillis = millis;

    return *this;
}

UINT CtiTablePointDispatch::getQuality() const
{
    return _quality;
}

CtiTablePointDispatch& CtiTablePointDispatch::setQuality(UINT quality)
{
    setDirty(TRUE);
    _quality = quality;
    return *this;
}

DOUBLE CtiTablePointDispatch::getValue() const
{
    return _value;
}

CtiTablePointDispatch& CtiTablePointDispatch::setValue(DOUBLE value)
{
    setDirty(TRUE);
    _value = value;
    return *this;
}

UINT CtiTablePointDispatch::getTags() const
{
    return _tags;
}

UINT CtiTablePointDispatch::setTags(UINT tags)
{
    UINT oldTags = _tags;
    _tags |= tags;
    if( oldTags != _tags )
    {
        setDirty(TRUE);
    }
    return _tags;
}

UINT CtiTablePointDispatch::resetTags(UINT mask)
{
    UINT oldTags = _tags;
    _tags &= ~mask;
    if( oldTags != _tags )
    {
        setDirty(TRUE);
    }
    return _tags;
}

// getStaleCount always returns 0
UINT CtiTablePointDispatch::getStaleCount() const
{
    return 0; //_stalecount
}

const CtiTime& CtiTablePointDispatch::getNextArchiveTime() const
{
    return _nextArchiveTime;
}

CtiTablePointDispatch& CtiTablePointDispatch::setNextArchiveTime(const CtiTime& timestamp)
{
    setDirty(TRUE);
    _nextArchiveTime= timestamp;
    return *this;
}

CtiTablePointDispatch& CtiTablePointDispatch::applyNewReading(const CtiTime& timestamp,
                                                              UINT millis,
                                                              UINT quality,
                                                              DOUBLE value,
                                                              UINT tags,
                                                              const CtiTime& archivetime,
                                                              UINT num )
{


    if(timestamp < _timeStamp)    // The setting is backward in time...
    {
        setTimeStamp( timestamp );
        setTimeStampMillis(millis);
        setQuality( quality );
        setValue( value );

        setTags( tags );
        setNextArchiveTime( archivetime );

        setDirty(TRUE);
    }
    else if(timestamp >= _timeStamp)
    {
        setTimeStamp( timestamp );
        setTimeStampMillis(millis);
        setQuality( quality );
        setValue( value );

        setTags( tags );
        setNextArchiveTime( archivetime );

        setDirty(TRUE);
    }
    return *this;
}

void CtiTablePointDispatch::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        CHAR  oldFill = dout.fill();
        dout.fill('0');

        dout << endl;
        dout << " PointID                                  : " << _pointID << endl;
        dout << " Time Stamp                               : " << _timeStamp << ", " << _timeStampMillis << "ms" << endl;
        dout << " Value                                    : " << _value << endl;
        dout << " Quality                                  : " << _quality << endl;
        dout << " Next Archive Time                        : " << _nextArchiveTime << endl;
        dout << " Tags                                     : 0x" << hex << setw(8) << _tags << dec << endl;

        dout.fill(oldFill);
    }

}

// getLastAlarmLogID always returns 0
ULONG CtiTablePointDispatch::getLastAlarmLogID() const
{
    return 0;
}
