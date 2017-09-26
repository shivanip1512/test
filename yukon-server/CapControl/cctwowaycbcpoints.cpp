#include "precompiled.h"

#include "cctwowaycbcpoints.h"
#include "ccid.h"
#include "database_util.h"
#include "ccutil.h"
#include "std_helper.h"
#include "row_reader.h"
#include "DeviceAttributeLookup.h"

#include "resolvers.h"

#include <regex>
#include <boost/range/adaptor/map.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/algorithm/copy.hpp>
#include <boost/range/algorithm/find_if.hpp>

extern unsigned long _CC_DEBUG;

using Cti::CapControl::deserializeFlag;

namespace Transport
{

TwoWayDynamicDataTransport::TwoWayDynamicDataTransport( Cti::RowReader & rdr )
{
    rdr[ "DeviceID" ]             >> DeviceID;
    rdr[ "LastControl" ]          >> LastControl;
    rdr[ "Condition" ]            >> Condition;
    rdr[ "AnalogInputOne" ]       >> AnalogInputOne;
    rdr[ "RSSI" ]                 >> RSSI;
    rdr[ "IgnoredReason" ]        >> IgnoredReason;
    rdr[ "TotalOpCount" ]         >> TotalOpCount;
    rdr[ "UvOpCount" ]            >> UvOpCount;
    rdr[ "OvOpCount" ]            >> OvOpCount;
    rdr[ "OvUvTrackTime" ]        >> OvUvTrackTime;
    rdr[ "NeutralCurrentSensor" ] >> NeutralCurrentSensor;
    rdr[ "IPAddress" ]            >> IPAddress;
    rdr[ "UDPPort" ]              >> UDPPort;

    rdr[ "Voltage" ]                     >> Voltage;
    rdr[ "HighVoltage" ]                 >> HighVoltage;
    rdr[ "LowVoltage" ]                  >> LowVoltage;
    rdr[ "DeltaVoltage" ]                >> DeltaVoltage;
    rdr[ "Temp" ]                        >> Temp;
    rdr[ "UvSetPoint" ]                  >> UvSetPoint;
    rdr[ "OvSetPoint" ]                  >> OvSetPoint;
    rdr[ "NeutralCurrentAlarmSetPoint" ] >> NeutralCurrentAlarmSetPoint;

    RecloseBlocked         = deserializeFlag( rdr[ "RecloseBlocked" ].as<std::string>()         );
    ControlMode            = deserializeFlag( rdr[ "ControlMode" ].as<std::string>()            );
    AutoVoltControl        = deserializeFlag( rdr[ "AutoVoltControl" ].as<std::string>()        );
    OpFailedNeutralCurrent = deserializeFlag( rdr[ "OpFailedNeutralCurrent" ].as<std::string>() );
    NeutralCurrentFault    = deserializeFlag( rdr[ "NeutralCurrentFault" ].as<std::string>()    );
    BadRelay               = deserializeFlag( rdr[ "BadRelay" ].as<std::string>()               );
    DailyMaxOps            = deserializeFlag( rdr[ "DailyMaxOps" ].as<std::string>()            );
    VoltageDeltaAbnormal   = deserializeFlag( rdr[ "VoltageDeltaAbnormal" ].as<std::string>()   );
    TempAlarm              = deserializeFlag( rdr[ "TempAlarm" ].as<std::string>()              );
    DSTActive              = deserializeFlag( rdr[ "DSTActive" ].as<std::string>()              );
    NeutralLockout         = deserializeFlag( rdr[ "NeutralLockout" ].as<std::string>()         );
    IgnoredIndicator       = deserializeFlag( rdr[ "IgnoredIndicator" ].as<std::string>()       );

    rdr[ "OvUvCountResetDate" ] >> OvUvCountResetDate;
    rdr[ "LastOvUvDateTime" ]   >> LastOvUvDateTime;
}

}


/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::CtiCCTwoWayPoints( const long paoid, const std::string & paotype,
                                      std::unique_ptr<LastControlReason>    lastControlReason,
                                      std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   _paoid( paoid ),
        _paotype( paotype ),
        _insertDynamicDataFlag( true ),
        _dirty( false ),
        _lastControlReason( std::move( lastControlReason ) ),
        _ignoredControlReason( std::move( ignoredControlReason ) )
{
    // empty...
}

void CtiCCTwoWayPoints::assignTwoWayPointsAndAttributes( const std::vector<LitePoint> & points,
                                                         const std::map<Attribute, std::string> & overloads )
{
    const DeviceTypes   deviceType = resolveDeviceType( _paotype );

    for ( const LitePoint & point : points )
    {
        _points.emplace( point.getPointId(), point );

        std::vector<Attribute> attributes
            = Cti::DeviceAttributeLookup::AttributeLookup( deviceType,
                                                           point.getPointType(),
                                                           point.getPointOffset() );
        for ( auto attribute : attributes )
        {
            _attributeIds[ attribute ] = point.getPointId();
        }
    }

    if ( ! overloads.empty() )
    {
        for ( auto entry : overloads )
        {
            auto pointLookup =
                boost::find_if( points,
                                [ pointName = entry.second ]( const LitePoint & p )
                                {
                                    return p.getPointName() == pointName;
                                } );

            if ( pointLookup != points.end() )
            {
                _attributeIds[ entry.first ] = pointLookup->getPointId();
            }
        }
    }
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
    using std::string;

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

    return Cti::mapFindOrDefault( _points, getPointIdByAttribute( attribute ), invalidPoint );
}

long CtiCCTwoWayPoints::getPointIdByAttribute( const Attribute & attribute ) const
{
    return  Cti::mapFindOrDefault( _attributeIds, attribute, 0 );
}

double CtiCCTwoWayPoints::getPointValueByAttribute( const Attribute & attribute, const double sentinel ) const
{
    double value = sentinel;
    _pointValues.getPointValue( getPointIdByAttribute( attribute ), value );
    return value;
}

bool CtiCCTwoWayPoints::isTimestampNew( const long pointID, const CtiTime & timestamp )
{
    CtiTime prevTime = gInvalidCtiTime;
    return ! (_pointValues.getPointTime(pointID, prevTime) && timestamp <= prevTime);
}

bool CtiCCTwoWayPoints::setTwoWayPointValue( const long pointID, const double value, const CtiPointType_t type, const CtiTime & timestamp )
{
    CtiPointType_t  lookupType = InvalidPointType;

    if ( auto pointLookup = Cti::mapFind( _points, pointID ) )
    {
        lookupType = pointLookup->getPointType();
    }

    if ( lookupType == type && isTimestampNew( pointID, timestamp ) )
    {
        // maybe check if the value is different before we set _dirty = true
        _pointValues.addPointValue(pointID, value, timestamp);
        _dirty = true;
        return true;
    }
    return false;
}

bool CtiCCTwoWayPoints::setTwoWayStatusPointValue( const long pointID, const long value, const CtiTime & timestamp )
{
    return setTwoWayPointValue( pointID, value, StatusPointType, timestamp );
}

bool CtiCCTwoWayPoints::setTwoWayAnalogPointValue( const long pointID,  const double value,  const CtiTime & timestamp )
{
    return setTwoWayPointValue( pointID, value, AnalogPointType, timestamp );
}

bool CtiCCTwoWayPoints::setTwoWayPulseAccumulatorPointValue( const long pointID, const double value, const CtiTime & timestamp )
{
    return setTwoWayPointValue( pointID, value, PulseAccumulatorPointType, timestamp );
}

void CtiCCTwoWayPoints::addAllCBCPointsToRegMsg( std::set<long> & pointList ) const
{
    boost::copy( _attributeIds
                    | boost::adaptors::map_values
                    | boost::adaptors::filtered( []( const long ID ){ return ID > 0; } ),
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

void CtiCCTwoWayPoints::setDynamicData( Transport::TwoWayDynamicDataTransport & transport,
                                        const long cbcState,
                                        const CtiTime & timestamp )
{
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::ControlPoint ), cbcState, timestamp );

    _pointValues.addPointValue( getPointIdByAttribute( Attribute::RecloseBlocked ), transport.RecloseBlocked, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::ControlMode ), transport.ControlMode, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::AutoVoltageControl ), transport.AutoVoltControl, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::OperationFailedNeutralCurrent ), transport.OpFailedNeutralCurrent, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::NeutralCurrentFault ), transport.NeutralCurrentFault, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::BadRelay ), transport.BadRelay, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::DailyMaxOperations ), transport.DailyMaxOps, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::VoltageDeltaAbnormal ), transport.VoltageDeltaAbnormal, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::TemperatureAlarm ), transport.TempAlarm, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::DSTActive ), transport.DSTActive, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::NeutralLockout ), transport.NeutralLockout, timestamp );

    _pointValues.addPointValue( getPointIdByAttribute( Attribute::AnalogInputOne ), transport.AnalogInputOne, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::RadioSignalStrengthIndicator ), transport.RSSI, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::TotalOperationCount ), transport.TotalOpCount, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::UnderVoltageCount ), transport.UvOpCount, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::OverVoltageCount ), transport.OvOpCount, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::OverUnderVoltageTrackTime ), transport.OvUvTrackTime, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::NeutralCurrentSensor ), transport.NeutralCurrentSensor, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::IpAddress ), transport.IPAddress, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::UdpPort ), transport.UDPPort, timestamp );

    _pointValues.addPointValue( getPointIdByAttribute( Attribute::Voltage ), transport.Voltage, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::HighVoltage ), transport.HighVoltage, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::LowVoltage ), transport.LowVoltage, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::DeltaVoltage ), transport.DeltaVoltage, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::TemperatureofDevice ), transport.Temp, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::UnderVoltageThreshold ), transport.UvSetPoint, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::OverVoltageThreshold ), transport.OvSetPoint, timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::NeutralCurrentAlarmThreshold ), transport.NeutralCurrentAlarmSetPoint, timestamp );

    _lastControlReason->deserialize( *this, transport.LastControl, timestamp );

    _ignoredControlReason->deserializeIndicator( *this, transport.IgnoredIndicator, timestamp );
    _ignoredControlReason->deserializeReason( *this, transport.IgnoredReason, timestamp );

    _pointValues.addPointValue( getPointIdByAttribute( Attribute::UnderVoltage ), !!( transport.Condition & 0x01 ), timestamp );
    _pointValues.addPointValue( getPointIdByAttribute( Attribute::OverVoltage ),  !!( transport.Condition & 0x02 ), timestamp );

    _ovuvCountResetDate = transport.OvUvCountResetDate;
    _lastOvUvDateTime   = transport.LastOvUvDateTime;

    _insertDynamicDataFlag = false;

    _dirty = false;     // _pointValues.addPointValue( .. ) may have made this dirty... reset to clean since we just restored from the db
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


CtiCCTwoWayPointsCbcDnpLogical::CtiCCTwoWayPointsCbcDnpLogical( const long paoid, const std::string & paotype,
                                                                std::unique_ptr<LastControlReason>    lastControlReason,
                                                                std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   CtiCCTwoWayPoints( paoid, paotype, std::move( lastControlReason ), std::move( ignoredControlReason ) )
{
    // empty...
}

void CtiCCTwoWayPointsCbcDnpLogical::assignTwoWayPointsAndAttributes( const std::vector<LitePoint> & points,
                                                                      const std::map<Attribute, std::string> & overloads )
{
    for ( const LitePoint & point : points )
    {
        _points.emplace( point.getPointId(), point );
    }

    if ( ! overloads.empty() )
    {
        // match size : 2
        //  match[ 0 ] == entire point name including the CBC name
        //  match[ 1 ] == just the point name
        const std::regex pointname_regex( "^\\*Logical<.*> (.*)$" );

        for ( auto entry : overloads )
        {
            for ( const LitePoint & point : points )
            {
                std::smatch pieces_match;
                std::string pointName = point.getPointName();

                if ( std::regex_match( pointName, pieces_match, pointname_regex ) )
                {
                    if ( pieces_match.size() == 2 
                            && pieces_match[ 1 ].str() == entry.second )
                    {
                        _attributeIds[ entry.first ] = point.getPointId();
                    }
                }
            }
        }
    }
}

// ------------------------------


CtiCCTwoWayPoints * CtiCCTwoWayPointsFactory::Create( const long paoID, const std::string & paoType )
{
    switch ( resolveDeviceType( paoType ) )
    {
        case TYPE_CBC7020:
        {
            return new CtiCCTwoWayPointsCbc702x( paoID, paoType,
                                                 std::make_unique<LastControlReasonCbc702x>(),
                                                 std::make_unique<IgnoredControlReasonCbc702x>() );
        }
        case TYPE_CBC8020:
        {
            return new CtiCCTwoWayPointsCbc802x( paoID, paoType,
                                                 std::make_unique<LastControlReasonCbc802x>(),
                                                 std::make_unique<IgnoredControlReasonCbc802x>() );
        }
        case TYPE_CBCDNP:
        {
            return new CtiCCTwoWayPointsCbcDnp( paoID, paoType,
                                                std::make_unique<LastControlReasonCbcDnp>(),
                                                std::make_unique<IgnoredControlReasonCbcDnp>() );
        }
        case TYPE_CBCLOGICAL:
        {
            return new CtiCCTwoWayPointsCbcDnpLogical( paoID, paoType,
                                                       std::make_unique<LastControlReasonCbcDnp>(),
                                                       std::make_unique<IgnoredControlReasonCbcDnp>() );
        }
        // Apparently 1-way devices need one of these guys even though they don't use it for anything,
        // returning a NULL here gives null pointer exceptions in 1-way code. Original behavior gave a set
        //  of CBC8000 points to 1-way devices, so maintain that behavior.
        // return 0;
        default:
        {
            return new CtiCCTwoWayPointsCbc802x( paoID, paoType,
                                                 std::make_unique<LastControlReasonCbc802x>(),
                                                 std::make_unique<IgnoredControlReasonCbc802x>() );
        }
    }
}

