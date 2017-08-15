#include "precompiled.h"

#include "cctwowaycbcpoints.h"
#include "ccid.h"
#include "database_util.h"
#include "ccutil.h"
#include "std_helper.h"
#include "row_reader.h"
#include "DeviceAttributeLookup.h"

#include "resolvers.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/algorithm/copy.hpp>
#include <boost/range/algorithm/find_if.hpp>

using std::string;

extern unsigned long _CC_DEBUG;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::CtiCCTwoWayPoints( const long paoid, const std::string & paotype,
                                      std::unique_ptr<LastControlReason>    lastControlReason,
                                      std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   _paoid( paoid ),
        _paotype( paotype ),
        _insertDynamicDataFlag( true ),
        _dirty( true ),
        _lastControlReason( std::move( lastControlReason ) ),
        _ignoredControlReason( std::move( ignoredControlReason ) )
{
    // empty...
}

void CtiCCTwoWayPoints::assignTwoWayPointBulk( const std::vector<LitePoint> & points,
                                               const std::map<Attribute, std::string> & overloads )
{
    for ( const LitePoint & point : points )
    {
        _pointidPointtypeMap[ point.getPointId() ] = point.getPointType();      // boo! figure a way to get rid of this guy!!

        std::vector<Attribute> attributes
            = Cti::DeviceAttributeLookup::AttributeLookup( resolveDeviceType( _paotype ),
                                                           point.getPointType(),
                                                           point.getPointOffset() );

        for ( auto attribute : attributes )
        {
            _attributes[ attribute ] = point;
        }
    }

    if ( ! overloads.empty()   )
    {

        for ( auto entry : overloads )
        {
            auto pointLookup =
                boost::find_if( points,
                                [ entry ]( const LitePoint & p )
                                {
                                    return p.getPointName() == entry.second;
                                } );

            if ( pointLookup != points.end() )
            {
                _attributes[ entry.first ] = *pointLookup;
            }
        }
    }
}

void CtiCCTwoWayPoints::assignTwoWayPoint( const LitePoint & point )
{
    std::vector<Attribute> attributes
        = Cti::DeviceAttributeLookup::AttributeLookup( resolveDeviceType( _paotype ),
                                                       point.getPointType(),
                                                       point.getPointOffset() );

    for ( auto attribute : attributes )
    {
        _attributes[ attribute ] = point;
    }

    _pointidPointtypeMap[ point.getPointId() ] = point.getPointType();      // boo! figure a way to get rid of this guy!!
}

long CtiCCTwoWayPoints::getPAOId() const
{
    return _paoid;
}

void CtiCCTwoWayPoints::setPAOId(long paoId)
{
    _paoid = paoId;
}

void CtiCCTwoWayPoints::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( ! _dirty )
    {
        return;
    }

    int lastControlReason = _lastControlReason->serialize( *this );
    int ignoredControlReason = _ignoredControlReason->serializeReason( *this );
    bool ignoredControlIndicator = _ignoredControlReason->serializeIndicator( *this );

    int condition = 0;
    condition |= 0x01 * !!getPointValueByAttribute( Attribute::UnderVoltage );
    condition |= 0x02 * !!getPointValueByAttribute( Attribute::OverVoltage );

    if( !_insertDynamicDataFlag )
    {
        static const string updateSql = "update dynamiccctwowaycbc set "
                                        "recloseblocked = ?, "
                                        "controlmode = ?, "
                                        "autovoltcontrol = ?, "
                                        "lastcontrol = ?, "
                                        "condition = ?, "
                                        "opfailedneutralcurrent = ?, "
                                        "neutralcurrentfault = ?, "
                                        "badrelay = ?, "
                                        "dailymaxops = ?, "
                                        "voltagedeltaabnormal = ?, "
                                        "tempalarm = ?, "
                                        "dstactive = ?, "
                                        "neutrallockout = ?, "
                                        "ignoredindicator = ?, "
                                        "voltage = ?, "
                                        "highvoltage = ?, "
                                        "lowvoltage = ?, "
                                        "deltavoltage = ?, "
                                        "analoginputone = ?, "
                                        "temp = ?, "
                                        "rssi = ?, "
                                        "ignoredreason = ?, "
                                        "totalopcount = ?, "
                                        "uvopcount = ?, "
                                        "ovopcount = ?, "
                                        "ovuvcountresetdate = ?, "
                                        "uvsetpoint = ?, "
                                        "ovsetpoint = ?, "
                                        "ovuvtracktime = ?, "
                                        "lastovuvdatetime = ?, "
                                        "neutralcurrentsensor = ?, "
                                        "neutralcurrentalarmsetpoint = ?, "
                                        "ipaddress = ?, "
                                        "udpport = ?"
                                        " where deviceid = ?";

        Cti::Database::DatabaseWriter updater(conn, updateSql);

        updater
            << (string)(getPointValueByAttribute( Attribute::RecloseBlocked )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::ControlMode )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::AutoVoltageControl )?"Y":"N")
            << lastControlReason
            << condition
            << (string)(getPointValueByAttribute( Attribute::OperationFailedNeutralCurrent )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::NeutralCurrentFault )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::BadRelay )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::DailyMaxOperations )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::VoltageDeltaAbnormal )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::TemperatureAlarm )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::DSTActive )?"Y":"N")
            << (string)(getPointValueByAttribute( Attribute::NeutralLockout )?"Y":"N")
            << (string)(ignoredControlIndicator?"Y":"N")
            << getPointValueByAttribute( Attribute::Voltage )
            << getPointValueByAttribute( Attribute::HighVoltage )
            << getPointValueByAttribute( Attribute::LowVoltage )
            << getPointValueByAttribute( Attribute::DeltaVoltage )
            << getPointValueByAttribute( Attribute::AnalogInputOne )
            << getPointValueByAttribute( Attribute::TemperatureofDevice )
            << getPointValueByAttribute( Attribute::RadioSignalStrengthIndicator )
            << ignoredControlReason
            << getPointValueByAttribute( Attribute::TotalOperationCount )
            << getPointValueByAttribute( Attribute::UnderVoltageCount )
            << getPointValueByAttribute( Attribute::OverVoltageCount )
            << _ovuvCountResetDate
            << getPointValueByAttribute( Attribute::UnderVoltageThreshold )
            << getPointValueByAttribute( Attribute::OverVoltageThreshold )
            << getPointValueByAttribute( Attribute::OverUnderVoltageTrackTime )
            << _lastOvUvDateTime
            << getPointValueByAttribute( Attribute::NeutralCurrentSensor )
            << getPointValueByAttribute( Attribute::NeutralCurrentAlarmThreshold )
            << getPointValueByAttribute( Attribute::IpAddress )
            << getPointValueByAttribute( Attribute::UdpPort )
            << _paoid;

        if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
        {
            _dirty = false; // No error occured!
        }
    }
    else
    {
        CTILOG_INFO(dout, "Inserted TwoWay CBC data into DynamicCCTwoWayCBC: " << getPAOId());
        static const string insertSql = "insert into dynamiccctwowaycbc values ( "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?)";

        Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

        dbInserter
                 << _paoid
                 << (string)(getPointValueByAttribute( Attribute::RecloseBlocked )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::ControlMode )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::AutoVoltageControl )?"Y":"N")
                 << lastControlReason
                 << condition
                 << (string)(getPointValueByAttribute( Attribute::OperationFailedNeutralCurrent )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::NeutralCurrentFault )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::BadRelay )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::DailyMaxOperations )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::VoltageDeltaAbnormal )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::TemperatureAlarm )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::DSTActive )?"Y":"N")
                 << (string)(getPointValueByAttribute( Attribute::NeutralLockout )?"Y":"N")
                 << (string)(ignoredControlIndicator?"Y":"N")
                 << getPointValueByAttribute( Attribute::Voltage )
                 << getPointValueByAttribute( Attribute::HighVoltage )
                 << getPointValueByAttribute( Attribute::LowVoltage )
                 << getPointValueByAttribute( Attribute::DeltaVoltage )
                 << getPointValueByAttribute( Attribute::AnalogInputOne )
                 << getPointValueByAttribute( Attribute::TemperatureofDevice )
                 << getPointValueByAttribute( Attribute::RadioSignalStrengthIndicator )
                 << ignoredControlReason
                 << getPointValueByAttribute( Attribute::TotalOperationCount )
                 << getPointValueByAttribute( Attribute::UnderVoltageCount )
                 << getPointValueByAttribute( Attribute::OverVoltageCount )
                 << _ovuvCountResetDate
                 << getPointValueByAttribute( Attribute::UnderVoltageThreshold )
                 << getPointValueByAttribute( Attribute::OverVoltageThreshold )
                 << getPointValueByAttribute( Attribute::OverUnderVoltageTrackTime )
                 << _lastOvUvDateTime
                 << getPointValueByAttribute( Attribute::NeutralCurrentSensor )
                 << getPointValueByAttribute( Attribute::NeutralCurrentAlarmThreshold )
                 << getPointValueByAttribute( Attribute::IpAddress )
                 << getPointValueByAttribute( Attribute::UdpPort );

        if( Cti::Database::executeCommand( dbInserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
        {
            _insertDynamicDataFlag = false;
            _dirty = false; // No error occured!
        }
    }
}

LitePoint CtiCCTwoWayPoints::getPointByAttribute( const Attribute & attribute ) const
{
    static const LitePoint  invalidPoint;   // <-- return this guy if lookup fails - pointID == 0

    return Cti::mapFindOrDefault( _attributes, attribute, invalidPoint );
}

long CtiCCTwoWayPoints::getPointIdByAttribute( const Attribute & attribute ) const
{
    return getPointByAttribute( attribute ).getPointId();
}

double CtiCCTwoWayPoints::getPointValueByAttribute( const Attribute & attribute, const double sentinel ) const
{
    double value = sentinel;
    _pointValues.getPointValue( getPointIdByAttribute( attribute ), value );
    return value;
}

bool CtiCCTwoWayPoints::isTimestampNew(long pointID, CtiTime timestamp)
{
    CtiTime prevTime = gInvalidCtiTime;
    return ! (_pointValues.getPointTime(pointID, prevTime) && timestamp <= prevTime);
}

bool CtiCCTwoWayPoints::setTwoWayPointValue(long pointID, double value, CtiPointType_t type, CtiTime timestamp)
{
    boost::optional<CtiPointType_t> lookupType = Cti::mapFind( _pointidPointtypeMap, pointID );
    if ( lookupType && *lookupType == type && isTimestampNew( pointID, timestamp ) )
    {
        _pointValues.addPointValue(pointID, value, timestamp);
        _dirty = true;
        return true;
    }
    return false;
}

bool CtiCCTwoWayPoints::setTwoWayStatusPointValue(long pointID, long value, CtiTime timestamp)
{
    return setTwoWayPointValue( pointID, value, StatusPointType, timestamp );
}

bool CtiCCTwoWayPoints::setTwoWayAnalogPointValue(long pointID, double value, CtiTime timestamp)
{
    return setTwoWayPointValue( pointID, value, AnalogPointType, timestamp );
}

bool CtiCCTwoWayPoints::setTwoWayPulseAccumulatorPointValue(long pointID, double value, CtiTime timestamp)
{
    return setTwoWayPointValue( pointID, value, PulseAccumulatorPointType, timestamp );
}

void CtiCCTwoWayPoints::addAllCBCPointsToRegMsg(std::set<long>& pointList)
{
    boost::copy( _attributes
                    | boost::adaptors::map_values
                    | boost::adaptors::transformed( std::mem_fun_ref( &LitePoint::getPointId ) )
                    | boost::adaptors::filtered( std::bind2nd( std::greater<long>(), 0 ) ),
                 std::inserter( pointList, pointList.begin() ) );
}

std::string CtiCCTwoWayPoints::getLastControlText()
{
    return _lastControlReason->getText( *this );
}

std::string CtiCCTwoWayPoints::getIgnoredControlText()
{
    return _ignoredControlReason->getText( *this );
}

bool CtiCCTwoWayPoints::controlRejectedByVoltageLimits()
{
    return _ignoredControlReason->controlRejectedByVoltageLimits( *this );
}

bool CtiCCTwoWayPoints::checkDeltaVoltageRejection()
{
    return _ignoredControlReason->checkDeltaVoltageRejection( *this );
}

bool CtiCCTwoWayPoints::isControlAccepted()
{
    return _ignoredControlReason->checkControlAccepted( *this );
}

struct ColumnMapping
{
    enum
    {
        Boolean,
        Long,
        Double

    } type;
    const char *name;
    const Attribute &attribute;
}
static const TwoWayColumns[] =
{
    { ColumnMapping::Boolean, "recloseblocked",                 Attribute::RecloseBlocked                   },
    { ColumnMapping::Boolean, "controlmode",                    Attribute::ControlMode                      },
    { ColumnMapping::Boolean, "autovoltcontrol",                Attribute::AutoVoltageControl               },
    { ColumnMapping::Boolean, "opfailedneutralcurrent",         Attribute::OperationFailedNeutralCurrent    },
    { ColumnMapping::Boolean, "neutralcurrentfault",            Attribute::NeutralCurrentFault              },
    { ColumnMapping::Boolean, "badrelay",                       Attribute::BadRelay                         },
    { ColumnMapping::Boolean, "dailymaxops",                    Attribute::DailyMaxOperations               },
    { ColumnMapping::Boolean, "voltagedeltaabnormal",           Attribute::VoltageDeltaAbnormal             },
    { ColumnMapping::Boolean, "tempalarm",                      Attribute::TemperatureAlarm                 },
    { ColumnMapping::Boolean, "dstactive",                      Attribute::DSTActive                        },
    { ColumnMapping::Boolean, "neutrallockout",                 Attribute::NeutralLockout                   },
    { ColumnMapping::Double,  "voltage",                        Attribute::Voltage                          },
    { ColumnMapping::Double,  "highvoltage",                    Attribute::HighVoltage                      },
    { ColumnMapping::Double,  "lowvoltage",                     Attribute::LowVoltage                       },
    { ColumnMapping::Double,  "deltavoltage",                   Attribute::DeltaVoltage                     },
    { ColumnMapping::Long,    "analoginputone",                 Attribute::AnalogInputOne                   },
    { ColumnMapping::Double,  "temp",                           Attribute::TemperatureofDevice              },
    { ColumnMapping::Long,    "rssi",                           Attribute::RadioSignalStrengthIndicator     },
    { ColumnMapping::Long,    "totalopcount",                   Attribute::TotalOperationCount              },
    { ColumnMapping::Long,    "uvopcount",                      Attribute::UnderVoltageCount                },
    { ColumnMapping::Long,    "ovopcount",                      Attribute::OverVoltageCount                 },
    { ColumnMapping::Double,  "uvsetpoint",                     Attribute::UnderVoltageThreshold            },
    { ColumnMapping::Double,  "ovsetpoint",                     Attribute::OverVoltageThreshold             },
    { ColumnMapping::Long,    "ovuvtracktime",                  Attribute::OverUnderVoltageTrackTime        },
    { ColumnMapping::Long,    "neutralcurrentsensor",           Attribute::NeutralCurrentSensor             },
    { ColumnMapping::Double,  "neutralcurrentalarmsetpoint",    Attribute::NeutralCurrentAlarmThreshold     },
    { ColumnMapping::Long,    "ipaddress",                      Attribute::IpAddress                        },
    { ColumnMapping::Long,    "udpport",                        Attribute::UdpPort                          }
};

void CtiCCTwoWayPoints::setDynamicData(Cti::RowReader& rdr, LONG cbcState, CtiTime timestamp)
{
    _pointValues.addPointValue(getPointIdByAttribute( Attribute::ControlPoint ), cbcState, timestamp);

    for each( const ColumnMapping &cm in TwoWayColumns )
    {
        double value;

        if( cm.type == ColumnMapping::Boolean )
        {
            string tempBoolString;
            rdr[cm.name] >> tempBoolString;
            value = ciStringEqual(tempBoolString, "y");
        }
        else if( cm.type == ColumnMapping::Long )
        {
            long tempLong;
            rdr[cm.name] >> tempLong;
            value = tempLong;
        }
        else  //  cm.type == ColumnMapping::Double
        {
            rdr[cm.name] >> value;
        }

        _pointValues.addPointValue(getPointIdByAttribute(cm.attribute), value, timestamp);
    }

    rdr["ovuvcountresetdate"] >> _ovuvCountResetDate; //toADD
    rdr["lastovuvdatetime"] >> _lastOvUvDateTime; //toAdd

    int lastControlReason;
    rdr["lastcontrol"] >> lastControlReason;
    _lastControlReason->deserialize( *this, lastControlReason, timestamp );

    std::string ignoredControlIndicator;
    rdr["ignoredindicator"] >> ignoredControlIndicator;
    _ignoredControlReason->deserializeIndicator( *this, ciStringEqual(ignoredControlIndicator, "Y"), timestamp );

    int ignoredControlReason;
    rdr["ignoredreason"] >> ignoredControlReason;
    _ignoredControlReason->deserializeReason( *this, ignoredControlReason, timestamp );

    int condition;
    rdr["condition"] >> condition;
    _pointValues.addPointValue(getPointIdByAttribute( Attribute::UnderVoltage ), !!(condition & 0x01), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute( Attribute::OverVoltage ),  !!(condition & 0x02), timestamp);

    _insertDynamicDataFlag = false;
    _dirty = false;
}


// ------------------------------


CtiCCTwoWayPointsCbcDnp::CtiCCTwoWayPointsCbcDnp( const long paoid, const std::string & paotype,
                                                  std::unique_ptr<LastControlReason>    lastControlReason,
                                                  std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   CtiCCTwoWayPoints( paoid, paotype, std::move( lastControlReason ), std::move( ignoredControlReason ) )
{
    // empty...
}


// ------------------------------


CtiCCTwoWayPointsCbc702x::CtiCCTwoWayPointsCbc702x( const long paoid, const std::string & paotype,
                                                    std::unique_ptr<LastControlReason>    lastControlReason,
                                                    std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   CtiCCTwoWayPoints( paoid, paotype, std::move( lastControlReason ), std::move( ignoredControlReason ) )
{
    // empty...
}


// ------------------------------


CtiCCTwoWayPointsCbc802x::CtiCCTwoWayPointsCbc802x( const long paoid, const std::string & paotype,
                                                    std::unique_ptr<LastControlReason>    lastControlReason,
                                                    std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   CtiCCTwoWayPoints( paoid, paotype, std::move( lastControlReason ), std::move( ignoredControlReason ) )
{
    // empty...
}


// ------------------------------


CtiCCTwoWayPoints * CtiCCTwoWayPointsFactory::Create( const long paoID, const std::string & paoType )
{
    if ( stringContainsIgnoreCase( paoType, "CBC 702" ) )
    {
        return new CtiCCTwoWayPointsCbc702x( paoID, paoType,
                                             std::make_unique<LastControlReasonCbc702x>(),
                                             std::make_unique<IgnoredControlReasonCbc702x>() );
    }
    if ( stringContainsIgnoreCase( paoType, "CBC 802" ) )
    {
        return new CtiCCTwoWayPointsCbc802x( paoID, paoType,
                                             std::make_unique<LastControlReasonCbc802x>(),
                                             std::make_unique<IgnoredControlReasonCbc802x>() );
    }
    if ( stringContainsIgnoreCase( paoType, "CBC DNP" ) )
    {
        return new CtiCCTwoWayPointsCbcDnp( paoID, paoType,
                                            std::make_unique<LastControlReasonCbcDnp>(),
                                            std::make_unique<IgnoredControlReasonCbcDnp>() );
    }

    // Apparently 1-way devices need one of these guys even though they don't use it for anything,
    // returning a NULL here gives null pointer exceptions in 1-way code. Original behavior gave a set
    //  of CBC8000 points to 1-way devices, so maintain that behavior.
    // return 0;
    return new CtiCCTwoWayPointsCbc802x( paoID, paoType,
                                         std::make_unique<LastControlReasonCbc802x>(),
                                         std::make_unique<IgnoredControlReasonCbc802x>() );
}

