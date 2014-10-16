#include "precompiled.h"

#include <iostream>
#include <iomanip>
using namespace std;

#include "dbaccess.h"
#include "logger.h"
#include "tbl_ptdispatch.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"
#include "database_exceptions.h"
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
_nextArchiveTime(CtiTime(YUKONEOT+86400)),
_pointIdInvalid(false)
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
_nextArchiveTime(CtiTime(YUKONEOT - 86400)),
_pointIdInvalid(false)
{
    setTimeStampMillis(millis);

    setDirty(TRUE);
}

CtiTablePointDispatch::~CtiTablePointDispatch()
{
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
        return string(sqlNoID + " WHERE DPD.pointid = " + CtiNumStr(id));
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

bool CtiTablePointDispatch::writeToDB(Cti::Database::DatabaseConnection &conn)
{
    using namespace Cti::Database;

    try
    {
        const TryInsertFirst tryInsertFirst = ! getUpdatedFlag();

        executeUpsert(
                conn,
                boost::bind(&CtiTablePointDispatch::initInserter, this, _1),
                boost::bind(&CtiTablePointDispatch::initUpdater,  this, _1),
                tryInsertFirst,
                __FILE__, __LINE__, LogDebug::Disable );

        setUpdatedFlag(true);
        setDirty(false);

        return true;
    }
    catch( Cti::Database::ForeignKeyViolationException& )
    {
        _pointIdInvalid = true;
    }
    catch( Cti::Database::DatabaseException& )
    {
        // logging is done inside executeUpsert()
    }

    return false;
}


void CtiTablePointDispatch::initUpdater(Cti::Database::DatabaseWriter &updater) const
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

    updater.setCommandText(sql);

    updater
        << getTimeStamp()
        << getQuality()
        << getValue()
        << getTags()
        << getNextArchiveTime()
        << getStaleCount()
        << getTimeStampMillis()
        << getPointID();
}

void CtiTablePointDispatch::initInserter(Cti::Database::DatabaseWriter &inserter) const
{
    static const std::string sql = "insert into " + getTableName() +
                                       " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    inserter.setCommandText(sql);

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
}

bool CtiTablePointDispatch::isPointIdInvalid() const
{
    return _pointIdInvalid;
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
        CTILOG_ERROR(dout, "millis = "<< millis <<" > 999");

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        CTILOG_ERROR(dout, "millis = "<< millis <<" < 0");

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

std::string CtiTablePointDispatch::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTablePointDispatch";
    itemList.add("PointID")           << _pointID;
    itemList.add("Time Stamp")        << _timeStamp <<") "<< _timeStampMillis <<"ms";
    itemList.add("Value")             << _value;
    itemList.add("Quality")           << _quality;
    itemList.add("Next Archive Time") << _nextArchiveTime;
    itemList.add("Tags")              << CtiNumStr(_tags).xhex().zpad(8);

    return itemList.toString();
}

// getLastAlarmLogID always returns 0
ULONG CtiTablePointDispatch::getLastAlarmLogID() const
{
    return 0;
}
