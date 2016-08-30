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
_nextArchiveTime(CtiTime(YUKONEOT - 86400))
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

std::array<Cti::Database::ColumnDefinition, 7> CtiTablePointDispatch::getTempTableSchema()
{
    return { Cti::Database::ColumnDefinition
        { "POINTID",        "numeric",  "NUMBER"   },
        { "TIMESTAMP",      "datetime", "DATE"     },
        { "QUALITY",        "numeric",  "NUMBER"   },
        { "VALUE",          "float",    "FLOAT"    },
        { "TAGS",           "numeric",  "NUMBER"   },
        { "NEXTARCHIVE",    "datetime", "DATE"     },
        { "millis",         "smallint", "SMALLINT" }};
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
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are dirty and need to be
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

void CtiTablePointDispatch::fillRowWriter(Cti::RowWriter &writer) const
{
    writer
        << getPointID()
        << getTimeStamp()
        << getQuality()
        << getValue()
        << getTags()
        << getNextArchiveTime()
        << getTimeStampMillis();
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
    itemList.add("Time Stamp")        << _timeStamp <<", "<< _timeStampMillis <<"ms";
    itemList.add("Value")             << _value;
    itemList.add("Quality")           << _quality;
    itemList.add("Next Archive Time") << _nextArchiveTime;
    itemList.add("Tags")              << CtiNumStr(_tags).xhex().zpad(8);

    return itemList.toString();
}
