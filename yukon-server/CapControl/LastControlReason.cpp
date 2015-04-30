#include "precompiled.h"

#include "LastControlReason.h"
#include "std_helper.h"
#include "dllyukon.h"
#include "cctwowaycbcpoints.h"
#include "ctitime.h"



std::string LastControlReasonCbcDnp::getText( const CtiCCTwoWayPoints & points )
{
    return "Uninitialized";
}

long LastControlReasonCbcDnp::serialize( const CtiCCTwoWayPoints & points )
{
    return 0;
}

void LastControlReasonCbcDnp::deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    // empty!
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

    lastControlReason |= Local        * !!points.getPointValueByAttribute( PointAttribute::LastControlLocal );
    lastControlReason |= Remote       * !!points.getPointValueByAttribute( PointAttribute::LastControlRemote );
    lastControlReason |= OvUv         * !!points.getPointValueByAttribute( PointAttribute::LastControlOvUv );
    lastControlReason |= NeutralFault * !!points.getPointValueByAttribute( PointAttribute::LastControlNeutralFault );
    lastControlReason |= Scheduled    * !!points.getPointValueByAttribute( PointAttribute::LastControlScheduled );
    lastControlReason |= Digital      * !!points.getPointValueByAttribute( PointAttribute::LastControlDigital );
    lastControlReason |= Analog       * !!points.getPointValueByAttribute( PointAttribute::LastControlAnalog );
    lastControlReason |= Temperature  * !!points.getPointValueByAttribute( PointAttribute::LastControlTemperature );

    return lastControlReason;
}

void LastControlReasonCbc702x::deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlLocal),        !!( dbValue & Local ),        timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlRemote),       !!( dbValue & Remote ),       timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlOvUv),         !!( dbValue & OvUv ),         timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlNeutralFault), !!( dbValue & NeutralFault ), timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlScheduled),    !!( dbValue & Scheduled ),    timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlDigital),      !!( dbValue & Digital ),      timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlAnalog),       !!( dbValue & Analog ),       timestamp );
    points.setTwoWayStatusPointValue( points.getPointIdByAttribute( PointAttribute::LastControlTemperature),  !!( dbValue & Temperature ),  timestamp );
}


// ------------------------------


std::string LastControlReasonCbc802x::getText( const CtiCCTwoWayPoints & points )
{
    // get the state group

    long stateGroupID = DefaultStateGroup;

    LitePoint point = points.getPointByAttribute( PointAttribute::LastControlReason );

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
    if ( points.getPointIdByAttribute( PointAttribute::LastControlReason ) > 0 )
    {
        return points.getPointValueByAttribute( PointAttribute::LastControlReason, UninitializedRawSate );
    }

    return UninitializedRawSate;
}

void LastControlReasonCbc802x::deserialize( CtiCCTwoWayPoints & points, const int dbValue, const CtiTime & timestamp )
{
    const long pointID = points.getPointIdByAttribute( PointAttribute::LastControlReason );

    if ( pointID > 0 && dbValue != UninitializedRawSate )
    {
        points.setTwoWayAnalogPointValue( pointID, dbValue, timestamp );
    }
}

std::string LastControlReasonCbc802x::lookupStateName( const long reason, const long stateGroup ) const
{
    return ResolveStateName( stateGroup, reason );
}

