#include "precompiled.h"

#include "ccid.h"
#include "cctwowaycbcpoints.h"
#include "cbcHeartbeatPolicy.h"
#include "Requests.h"
#include "desolvers.h"
#include "logger.h"

extern unsigned long _CC_DEBUG;

namespace Cti::CapControl {

CbcHeartbeatPolicy::OperatingMode CbcHeartbeatPolicy::getOperatingMode( CtiCCTwoWayPoints & twoWayPoints )
try
{
    double value = getValueByAttribute( Attribute::ScadaOverrideMode, twoWayPoints );

    return ( value == 1.0 )
                ? ScadaOverride
                : Normal;
}
catch ( UninitializedPointValue & failedRead )
{
    // Haven't initialized this value yet - assume Normal mode operation.

    return Normal;
}
catch (FailedAttributeLookup & missingAttribute)
{
    // This shouldn't happen, but might still be valid.  Give information, but continue assuming Normal mode operation.
    if( _CC_DEBUG & CC_DEBUG_ATTRIBUTE_LOOKUP ) 
    {
        CTILOG_INFO(dout, "Scada Override Mode attribute not found, assuming normal control.");
    }

    return Normal;
}

Policy::Actions CbcHeartbeatPolicy::StopHeartbeat( CtiCCTwoWayPoints & twoWayPoints )
{
    // Check the current reported mode of the CBC, if we are not in ScadaOverride mode, then don't do anything,
    //  if we are, pulse the ScadaOverrideClear point.

    const auto AttributeClear = Attribute::ScadaOverrideClear;

    Actions actions;
    LitePoint point = getPointByAttribute( AttributeClear, twoWayPoints );

    if ( getOperatingMode( twoWayPoints ) == ScadaOverride )
    {
        switch ( point.getPointType() )
        {
            case CtiPointType_t::StatusPointType:
                actions.emplace_back( makeStandardDigitalControl( point, "CBC Heartbeat Clear", RequestType::Heartbeat ) );
                break;
            case CtiPointType_t::AnalogPointType:
                actions.emplace_back( WriteAnalogValue( AttributeClear, 0, twoWayPoints ) );
                break;
            default:
                CTILOG_DEBUG( dout, "Could not execute Stop Heartbeat command because the attribute is not mapped to a status or analog point type (" << desolvePointType( point.getPointType() ) << "). " << 
                                    "Point used: " << point.getPointName() << ", " << point.getPointId() << "." );
                break;
        }
    }

    return actions;
}

Policy::Action CbcHeartbeatPolicy::WriteAnalogValue( const Attribute & attribute, const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints )
{
    LitePoint point = getPointByAttribute( attribute, twoWayPoints );

    return
    {
        makeSignalTemplate( point.getPointId(), 0, "CBC Heartbeat" ),
        makeRequestTemplate( point.getPaoId(), putvalueAnalogCommand( point, keepAliveValue ), RequestType::Heartbeat )
    };
}

///

Policy::AttributeList NoCbcHeartbeatPolicy::getSupportedAttributes() const
{
    return
    {
    };
}

Policy::Actions NoCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints )
{
    return
    {
    };
}

Policy::Actions NoCbcHeartbeatPolicy::StopHeartbeat( CtiCCTwoWayPoints & twoWayPoints )
{
    return
    {
    };
}

///

Policy::AttributeList AnalogCbcHeartbeatPolicy::getSupportedAttributes() const
{
    return
    {
        Attribute::ScadaOverrideClear,
        Attribute::ScadaOverrideHeartbeat,
        Attribute::ScadaOverrideMode
    };
}

Policy::Actions AnalogCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints )
{
    // Write the current keep alive value to the CBC

    Actions    actions;

    actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideHeartbeat, keepAliveValue, twoWayPoints ) );

    return actions;
}

/// 

Policy::AttributeList PulsedCbcHeartbeatPolicy::getSupportedAttributes() const
{
    return
    {
        Attribute::ScadaOverrideClear,
        Attribute::ScadaOverrideCountdownTimer,
        Attribute::ScadaOverrideEnable,
        Attribute::ScadaOverrideMode
    };
}

Policy::Actions PulsedCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints )
{
    // If the value in the CBC doesn't match the value in the config, update it.  Then pulse the enable point
    //  to put the CBC into ScadaOverride mode.

    Actions actions;

    if ( readCurrentValue( twoWayPoints ) != keepAliveValue )
    {
        actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideCountdownTimer, keepAliveValue, twoWayPoints ) );
    }
    
    actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( Attribute::ScadaOverrideEnable, twoWayPoints ),
                                                      "CBC Heartbeat Pulse",
                                                      RequestType::Heartbeat ) );

    return actions;
}

long PulsedCbcHeartbeatPolicy::readCurrentValue( CtiCCTwoWayPoints & twoWayPoints )
try
{
    return getValueByAttribute( Attribute::ScadaOverrideCountdownTimer, twoWayPoints );
}
catch ( UninitializedPointValue & )
{
    return 0L;  // can't read value -- send default of 0
}

}