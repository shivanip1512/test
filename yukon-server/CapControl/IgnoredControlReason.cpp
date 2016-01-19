#include "precompiled.h"

#include "IgnoredControlReason.h"
#include "std_helper.h"
#include "dllyukon.h"
#include "cctwowaycbcpoints.h"
#include "ctitime.h"



std::string IgnoredControlReasonCbcDnp::getText( const CtiCCTwoWayPoints & points )
{
    return "Uninitialized";
}

bool IgnoredControlReasonCbcDnp::serializeIndicator( const CtiCCTwoWayPoints & points )
{
    return false;
}

long IgnoredControlReasonCbcDnp::serializeReason( const CtiCCTwoWayPoints & points )
{
    return 0;
}

void IgnoredControlReasonCbcDnp::deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    // empty...
}

void IgnoredControlReasonCbcDnp::deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    // empty...
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

bool IgnoredControlReasonCbc702x::serializeIndicator( const CtiCCTwoWayPoints & points )
{
    if ( points.getPointIdByAttribute( PointAttribute::IgnoredIndicator ) > 0 )
    {
        return points.getPointValueByAttribute( PointAttribute::IgnoredIndicator, UninitializedIndicator );
    }

    return UninitializedIndicator;
}

long IgnoredControlReasonCbc702x::serializeReason( const CtiCCTwoWayPoints & points )
{
    if ( points.getPointIdByAttribute( PointAttribute::IgnoredReason ) > 0 )
    {
        return points.getPointValueByAttribute( PointAttribute::IgnoredReason, UninitializedReason );
    }

    return UninitializedReason;
}

void IgnoredControlReasonCbc702x::deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::IgnoredIndicator), dbValue, timestamp );
}

void IgnoredControlReasonCbc702x::deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    points.setTwoWayAnalogPointValue( points.getPointIdByAttribute( PointAttribute::IgnoredReason), dbValue, timestamp );
}


// ------------------------------


std::string IgnoredControlReasonCbc802x::getText( const CtiCCTwoWayPoints & points )
{
    // get the state group

    long stateGroupID = DefaultStateGroup;

    LitePoint point = points.getPointByAttribute( PointAttribute::IgnoredReason );

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

bool IgnoredControlReasonCbc802x::serializeIndicator( const CtiCCTwoWayPoints & points )
{
    return false;
}

long IgnoredControlReasonCbc802x::serializeReason( const CtiCCTwoWayPoints & points )
{
    if ( points.getPointIdByAttribute( PointAttribute::IgnoredReason ) > 0 )
    {
        return points.getPointValueByAttribute( PointAttribute::IgnoredReason, UninitializedReason );
    }

    return UninitializedReason;
}

void IgnoredControlReasonCbc802x::deserializeIndicator( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    // empty...
}

void IgnoredControlReasonCbc802x::deserializeReason( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    points.setTwoWayAnalogPointValue( points.getPointIdByAttribute( PointAttribute::IgnoredReason), dbValue, timestamp );
}

std::string IgnoredControlReasonCbc802x::lookupStateName( const long reason, const long stateGroup ) const
{
    return ResolveStateName( stateGroup, reason );
}

