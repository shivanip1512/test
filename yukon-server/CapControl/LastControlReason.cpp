#include "precompiled.h"

#include "LastControlReason.h"
#include "std_helper.h"
#include "dllyukon.h"
#include "cctwowaycbcpoints.h"
#include "ctitime.h"



std::string LastControlReasonCbcDnp::getText( const CtiCCTwoWayPoints & points )
{
    return "---";
}

long LastControlReasonCbcDnp::serialize( const CtiCCTwoWayPoints & points )
{
    return 0;
}

void LastControlReasonCbcDnp::deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    // empty!
}

std::unique_ptr<LastControlReason>  LastControlReasonCbcDnp::clone() const
{
    return std::make_unique<LastControlReasonCbcDnp>( *this );
}


// ------------------------------


std::string LastControlReasonCbc702x::getText( const CtiCCTwoWayPoints & points )
{
    static const std::map<long, std::string>    _lookup 
    {
        { Uninitialized,    "Uninitialized" },
        { Local,            "Local"         },
        { Remote,           "Remote"        },
        { OvUv,             "OvUv"          },
        { NeutralFault,     "NeutralFault"  },
        { Scheduled,        "Schedule"      },
        { Digital,          "Digital"       },
        { Analog,           "Analog"        },
        { Temperature,      "Temp"          }
    };

    const long rawStateValue = serialize( points );

    if ( auto result = Cti::mapFind( _lookup, rawStateValue ) )
    {
        return *result;
    }

    return "Unknown State. Value = " + std::to_string( rawStateValue );
}

long LastControlReasonCbc702x::serialize( const CtiCCTwoWayPoints & points )
{
    int lastControlReason = 0;

    lastControlReason |= Local        * !!points.getPointValueByAttribute( Attribute::LastControlReasonLocal );
    lastControlReason |= Remote       * !!points.getPointValueByAttribute( Attribute::LastControlReasonRemote );
    lastControlReason |= OvUv         * !!points.getPointValueByAttribute( Attribute::LastControlReasonOvUv );
    lastControlReason |= NeutralFault * !!points.getPointValueByAttribute( Attribute::LastControlReasonNeutralFault );
    lastControlReason |= Scheduled    * !!points.getPointValueByAttribute( Attribute::LastControlReasonScheduled );
    lastControlReason |= Digital      * !!points.getPointValueByAttribute( Attribute::LastControlReasonDigital );
    lastControlReason |= Analog       * !!points.getPointValueByAttribute( Attribute::LastControlReasonAnalog );
    lastControlReason |= Temperature  * !!points.getPointValueByAttribute( Attribute::LastControlReasonTemperature );

    return lastControlReason;
}

void LastControlReasonCbc702x::deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonLocal ),        !!( dbValue & Local ),        timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonRemote ),       !!( dbValue & Remote ),       timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonOvUv ),         !!( dbValue & OvUv ),         timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonNeutralFault ), !!( dbValue & NeutralFault ), timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonScheduled ),    !!( dbValue & Scheduled ),    timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonDigital ),      !!( dbValue & Digital ),      timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonAnalog ),       !!( dbValue & Analog ),       timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( Attribute::LastControlReasonTemperature ),  !!( dbValue & Temperature ),  timestamp );
}

std::unique_ptr<LastControlReason>  LastControlReasonCbc702x::clone() const
{
    return std::make_unique<LastControlReasonCbc702x>( *this );
}


// ------------------------------


std::string LastControlReasonCbc802x::getText( const CtiCCTwoWayPoints & points )
{
    // get the state group

    long stateGroupID = DefaultStateGroup;

    LitePoint point = points.getPointByAttribute( Attribute::LastControlReason );

    if ( point.getPointId() > 0 )
    {
        stateGroupID = point.getStateGroupId();
    }

    const long rawStateValue = serialize( points );

    if ( rawStateValue == UninitializedRawSate )
    {
        return "Uninitialized";
    }

    return lookupStateName( rawStateValue, stateGroupID );
}

long LastControlReasonCbc802x::serialize( const CtiCCTwoWayPoints & points )
{
    if ( points.getPointIdByAttribute( Attribute::LastControlReason ) > 0 )
    {
        return points.getPointValueByAttribute( Attribute::LastControlReason, UninitializedRawSate );
    }

    return UninitializedRawSate;
}

void LastControlReasonCbc802x::deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    const long pointID = points.getPointIdByAttribute( Attribute::LastControlReason );

    if ( pointID > 0 && dbValue != UninitializedRawSate )
    {
        points.setTwoWayAnalogPointValue( pointID, dbValue, timestamp );
    }
}

std::string LastControlReasonCbc802x::lookupStateName( const long reason, const long stateGroup ) const
{
    return ResolveStateName( stateGroup, reason );
}

std::unique_ptr<LastControlReason>  LastControlReasonCbc802x::clone() const
{
    return std::make_unique<LastControlReasonCbc802x>( *this );
}

