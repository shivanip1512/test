#include "precompiled.h"

#include "row_reader.h"

#include "dbaccess.h"
#include "logger.h"
#include "tbl_pt_alarm.h"
#include "database_util.h"

using namespace std;

CtiTablePointAlarming& CtiTablePointAlarming::operator=(const CtiTablePointAlarming& aRef)
{
    if(this != &aRef)
    {
        _pointID = aRef._pointID;

        for(int i = 0; i < ALARM_STATE_SIZE; i++)
        {
            setAlarmCategory( i, aRef.getAlarmCategory(i) );
        }

        setAutoAckStates( aRef.getAutoAckStates() );
        setExcludeNotifyStates( aRef.getExcludeNotifyStates() );
        setNotifyOnAcknowledge( aRef.getNotifyOnAcknowledge() );
        setNotifyOnClear(aRef.getNotifyOnClear());
        setNotificationGroupID( aRef.getNotificationGroupID() );
    }
    return *this;
}

UINT CtiTablePointAlarming::resolveExcludeStates( string &str )
{
    int i;
    UINT states = 0;

    if( !str.empty() )
    {
        std::transform(str.begin(), str.end(), str.begin(), tolower);

        for(i = 0 ; i < str.length() - 1 && i < 32; i++ )
        {
            if(str[i] == 'y' || str[i] == 'e' || str[i] == 'b')
            {
                states |= ( 0x00000001 << i );      // Put the bit in there
            }
        }
    }

    return states;
}

UINT CtiTablePointAlarming::resolveAutoAcknowledgeStates( string &str )
{
    int i;
    UINT states = 0;

    if( !str.empty() )
    {
        std::transform(str.begin(), str.end(), str.begin(), tolower);

        for(i = 0 ; i < str.length() - 1 && i < 32; i++ )
        {
            if(str[i] == 'a' || str[i] == 'b')
            {
                states |= ( 0x00000001 << i );      // Put the bit in there
            }
        }
    }

    return states;
}

LONG CtiTablePointAlarming::getPointID() const
{
    return _pointID;
}

UINT CtiTablePointAlarming::getAlarmCategory(const INT offset) const
{
    return _alarmCategory[offset];
}
UINT CtiTablePointAlarming::getExcludeNotifyStates() const
{
    return _excludeNotifyStates;
}
UINT CtiTablePointAlarming::getAutoAckStates() const
{
    return _autoAckStates;
}
UINT CtiTablePointAlarming::getNotificationGroupID() const
{
    return _notificationGroupID;
}

BOOL CtiTablePointAlarming::getNotifyOnAcknowledge() const
{
    return _notifyOnAcknowledge;
}

BOOL CtiTablePointAlarming::getNotifyOnClear() const
{
    return _notifyOnClear;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAlarmCategory( const INT offset, const UINT &aInt )
{
    _alarmCategory[offset] = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAlarmCategory( const string str )
{
    if( str.length() < ALARM_STATE_SIZE )
    {
        CTILOG_WARN(dout, "Alarm category string less than ALARM_STATE_SIZE, defaulting remaining categories to 1" + Cti::FormattedList::of(
            "Point ID", _pointID,
            "str.length()", str.length()));

        auto db_end = std::copy(str.cbegin(), str.cend(), std::begin(_alarmCategory));
        std::fill(db_end, std::end(_alarmCategory), 1);
    }
    else
    {
        std::copy_n(str.cbegin(), ALARM_STATE_SIZE, std::begin(_alarmCategory));
    }

    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setExcludeNotifyStates( const UINT &aInt )
{
    _excludeNotifyStates = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAutoAckStates( const UINT &aInt )
{
    _autoAckStates = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotifyOnAcknowledge( const BOOL &aBool )
{
    _notifyOnAcknowledge = aBool;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotifyOnClear( const BOOL &aBool )
{
    _notifyOnClear = aBool;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotificationGroupID( const UINT &aInt )
{
    _notificationGroupID = aInt;
    return *this;
}

string CtiTablePointAlarming::getTableName()
{
    return string("PointAlarming");
}


bool CtiTablePointAlarming::operator<(const CtiTablePointAlarming &rhs) const
{
    return _pointID < rhs._pointID;
}

static const auto baseSql = 
    "select"
        " PA.pointid"
        ", PA.alarmstates"
        ", PA.excludenotifystates"
        ", PA.notifyonacknowledge"
        ", PA.notificationgroupid"
    " from"
        " pointalarming PA"s;

//This SQL is only proper when we assume some things about alarmstates
static const auto whereSql =
    " where"
        " PA.alarmstates != '\001\001\001\001\001\001\001\001"  //  32 \001 chars, 4 rows of 8
                            "\001\001\001\001\001\001\001\001"
                            "\001\001\001\001\001\001\001\001"
                            "\001\001\001\001\001\001\001\001'"s;

std::string CtiTablePointAlarming::getSqlForFullLoad()
{
    return baseSql + whereSql;
}

std::string CtiTablePointAlarming::getSqlForPaoId()
{
    return baseSql 
        + " join point P on P.pointid = PA.pointid " 
        + whereSql
        + " AND " + Cti::Database::createIdEqualClause("P", "paobjectid");
}

std::string CtiTablePointAlarming::getSqlForPaoIdAndPointId()
{
    return getSqlForPaoId()
        + " AND " + Cti::Database::createIdEqualClause("PA", "pointid");
}

std::string CtiTablePointAlarming::getSqlForPointId()
{
    return getSqlForFullLoad()
        + " AND " + Cti::Database::createIdEqualClause("PA", "pointid");
}

std::string CtiTablePointAlarming::getSqlForPointIds(const size_t count)
{
    return getSqlForFullLoad()
        + " AND " + Cti::Database::createIdInClause("PA", "pointid", count);
}

CtiTablePointAlarming::~CtiTablePointAlarming()
{
}

bool CtiTablePointAlarming::alarmOn( int alarm ) const
{
    return( _alarmCategory[alarm] > 0 );
}

INT CtiTablePointAlarming::alarmPriority( int alarm ) const
{
    return _alarmCategory[alarm];
}

bool CtiTablePointAlarming::isNotifyExcluded( int alarm) const
{
    return(_excludeNotifyStates & (0x00000001 << alarm));
}

bool CtiTablePointAlarming::isAutoAcked( int alarm) const
{
    return(_autoAckStates & (0x00000001 << alarm));
}


CtiTablePointAlarming::CtiTablePointAlarming(Cti::RowReader &rdr)
{
    static const string pointid = "pointid";
    string temp;

    rdr[pointid] >> _pointID;
    rdr >> temp;
    setAlarmCategory( temp );

    rdr >> temp;
    setExcludeNotifyStates( resolveExcludeStates( temp ) );
    setAutoAckStates( resolveAutoAcknowledgeStates( temp ) );

    rdr >> temp;
    std::transform(temp.begin(), temp.end(), temp.begin(), tolower);
    setNotifyOnAcknowledge( temp[0] == 'a' || temp[0] == 'b' || temp[0] == 'y' );
    setNotifyOnClear( temp[0] == 'c' || temp[0] == 'b' );

    rdr >> _notificationGroupID;
}

CtiTablePointAlarming::CtiTablePointAlarming( LONG pid) :
_pointID( pid ),
_excludeNotifyStates( 0 ),
_autoAckStates( 0 ),
_notifyOnAcknowledge(FALSE),
_notifyOnClear(FALSE),
_notificationGroupID( 0 )
{
    for(int i = 0; i < ALARM_STATE_SIZE; i++)
    {
        _alarmCategory[i] = 1;            // This is event caliber!
    }
}

CtiTablePointAlarming::CtiTablePointAlarming(const CtiTablePointAlarming& aRef)
{
    *this = aRef;
}


