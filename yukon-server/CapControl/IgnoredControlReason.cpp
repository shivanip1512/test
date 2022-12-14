#include "precompiled.h"

#include "IgnoredControlReason.h"
#include "std_helper.h"
#include "dllyukon.h"
#include "cctwowaycbcpoints.h"
#include "ctitime.h"



std::string IgnoredControlReasonCbcDnp::getText( const CtiCCTwoWayPoints & points )
{
    return "---";
}

bool IgnoredControlReasonCbcDnp::controlRejectedByVoltageLimits( const CtiCCTwoWayPoints & points )
{
    return false;
}

bool IgnoredControlReasonCbcDnp::checkDeltaVoltageRejection( const CtiCCTwoWayPoints & points )
{
    return false;
}

bool IgnoredControlReasonCbcDnp::checkControlAccepted( const CtiCCTwoWayPoints & points )
{
    return false;
}

bool IgnoredControlReasonCbcDnp::serializeIndicator( const CtiCCTwoWayPoints & points )
{
    return false;
}

long IgnoredControlReasonCbcDnp::serializeReason( const CtiCCTwoWayPoints & points )
{
    return 0;
}

void IgnoredControlReasonCbcDnp::deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp, const PointQuality_t quality )
{
    // empty...
}

void IgnoredControlReasonCbcDnp::deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp, const PointQuality_t quality )
{
    // empty...
}

std::unique_ptr<IgnoredControlReason>  IgnoredControlReasonCbcDnp::clone() const
{
    return std::make_unique<IgnoredControlReasonCbcDnp>( *this );
}


// ------------------------------


std::string IgnoredControlReasonCbc702x::getText( const CtiCCTwoWayPoints & points )
{
    static const std::map<long, std::string>    _lookup 
    {
        { Local,        "Local"         },
        { FaultCurrent, "FaultCurrent"  },
        { EmVolt,       "EmVolt"        },
        { Time,         "Time"          },
        { Voltage,      "Voltage"       },
        { Digital1,     "Digital1"      },
        { Analog1,      "Analog1"       },
        { Digital2,     "Digital2"      },
        { Analog2,      "Analog2"       },
        { Digital3,     "Digital3"      },
        { Analog3,      "Analog3"       },
        { Digital4,     "Digital4"      },
        { Temp,         "Temp"          },
        { Remote,       "Remote"        },
        { NtrlLockOut,  "NtrlLockOut"   },
        { BrownOut,     "BrownOut"      },
        { BadActRelay,  "BadActRelay"   }
    };
 
    const long rawStateValue = serializeReason( points );
 
    if ( auto result = Cti::mapFind( _lookup, rawStateValue ) )
    {
        return *result;
    }
 
    return "unknown";
}

bool IgnoredControlReasonCbc702x::controlRejectedByVoltageLimits( const CtiCCTwoWayPoints & points )
{
    return serializeReason( points ) == Voltage;
}

bool IgnoredControlReasonCbc702x::checkDeltaVoltageRejection( const CtiCCTwoWayPoints & points )
{
    const double
        cbcVoltage  = points.getPointValueByAttribute( Attribute::Voltage ),
        ovThreshold = points.getPointValueByAttribute( Attribute::OverVoltageThreshold ),
        uvThreshold = points.getPointValueByAttribute( Attribute::UnderVoltageThreshold );

    /*
        If the actual voltage is within the OvUv limits it must have been rejected due to delta voltage
    */
    return ( ( uvThreshold < cbcVoltage ) && ( cbcVoltage < ovThreshold ) );
}

bool IgnoredControlReasonCbc702x::checkControlAccepted( const CtiCCTwoWayPoints & points )
{
    return false;
}

bool IgnoredControlReasonCbc702x::serializeIndicator( const CtiCCTwoWayPoints & points )
{
    if ( points.getPointIdByAttribute( Attribute::IgnoredIndicator ) > 0 )
    {
        return points.getPointValueByAttribute( Attribute::IgnoredIndicator, UninitializedIndicator );
    }

    return UninitializedIndicator;
}

long IgnoredControlReasonCbc702x::serializeReason( const CtiCCTwoWayPoints & points )
{
    if ( points.getPointIdByAttribute( Attribute::IgnoredControlReason ) > 0 )
    {
        return points.getPointValueByAttribute( Attribute::IgnoredControlReason, UninitializedReason );
    }

    return UninitializedReason;
}

void IgnoredControlReasonCbc702x::deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp, const PointQuality_t quality )
{
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::IgnoredIndicator ), dbValue, timestamp, quality );
}

void IgnoredControlReasonCbc702x::deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp, const PointQuality_t quality )
{
    points.setTwoWayAnalogPointValue( points.getPointIdByAttribute( Attribute::IgnoredControlReason ), dbValue, timestamp, quality );
}

std::unique_ptr<IgnoredControlReason>  IgnoredControlReasonCbc702x::clone() const
{
    return std::make_unique<IgnoredControlReasonCbc702x>( *this );
}


// ------------------------------


std::string IgnoredControlReasonCbc802x::getText( const CtiCCTwoWayPoints & points )
{
    // get the state group

    long stateGroupID = DefaultStateGroup;

    LitePoint point = points.getPointByAttribute( Attribute::IgnoredControlReason );

    if ( point.getPointId() > 0 )
    {
        stateGroupID = point.getStateGroupId();
    }

    const long rawStateValue = serializeReason( points );

    if ( rawStateValue == UninitializedReason )
    {
        return "Uninitialized";
    }

    return lookupStateName( rawStateValue, stateGroupID );
}

bool IgnoredControlReasonCbc802x::controlRejectedByVoltageLimits( const CtiCCTwoWayPoints & points )
{
    return serializeReason( points ) == OvUvControl;
}

bool IgnoredControlReasonCbc802x::checkDeltaVoltageRejection( const CtiCCTwoWayPoints & points )
{
    const double
        cbcVoltage  = points.getPointValueByAttribute( Attribute::Voltage ),
        ovThreshold = points.getPointValueByAttribute( Attribute::OverVoltageThreshold ),
        uvThreshold = points.getPointValueByAttribute( Attribute::UnderVoltageThreshold );

    /*
        If the actual voltage is within the OvUv limits it must have been rejected due to delta voltage
    */
    return ( ( uvThreshold < cbcVoltage ) && ( cbcVoltage < ovThreshold ) );
}

bool IgnoredControlReasonCbc802x::checkControlAccepted( const CtiCCTwoWayPoints & points )
{
    return serializeReason( points ) == ControlAccepted;
}

bool IgnoredControlReasonCbc802x::serializeIndicator( const CtiCCTwoWayPoints & points )
{
    return false;
}

long IgnoredControlReasonCbc802x::serializeReason( const CtiCCTwoWayPoints & points )
{
    if ( points.getPointIdByAttribute( Attribute::IgnoredControlReason ) > 0 )
    {
        return points.getPointValueByAttribute( Attribute::IgnoredControlReason, UninitializedReason );
    }

    return UninitializedReason;
}

void IgnoredControlReasonCbc802x::deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp, const PointQuality_t quality )
{
    // empty...
}

void IgnoredControlReasonCbc802x::deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp, const PointQuality_t quality )
{
    points.setTwoWayAnalogPointValue( points.getPointIdByAttribute( Attribute::IgnoredControlReason ), dbValue, timestamp, quality );
}

std::string IgnoredControlReasonCbc802x::lookupStateName( const long reason, const long stateGroup ) const
{
    return ResolveStateName( stateGroup, reason );
}

std::unique_ptr<IgnoredControlReason>  IgnoredControlReasonCbc802x::clone() const
{
    return std::make_unique<IgnoredControlReasonCbc802x>( *this );
}

