#include "precompiled.h"

#include "cbcHeartbeatPolicy.h"


namespace Cti           {
namespace CapControl    {

CbcHeartbeatPolicy::OperatingMode CbcHeartbeatPolicy::getOperatingMode()
try
{
    double value = getValueByAttribute( Attribute::ScadaOverrideMode );

    return ( value == 1.0 )
                ? ScadaOverride
                : Normal;
}
catch ( UninitializedPointValue & failedRead )
{
    // Haven't initialized this value yet - assume Normal mode operation.

    return Normal;
}

Policy::Actions CbcHeartbeatPolicy::StopHeartbeat()
{
    // Read the current heartbeat config value, if it's not zero then write a zero to put the CBC into
    //  local mode.  If it is zero, the CBC is already released, do nothing.

    Actions    actions;

    if ( getOperatingMode() == ScadaOverride )
    {
        actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( Attribute::ScadaOverrideClear ),
                                                          "CBC Heartbeat Clear" ) );
    }

    return actions;
}

Policy::Action CbcHeartbeatPolicy::WriteAnalogValue( const Attribute & attribute, const long keepAliveValue )
{
    LitePoint point = getPointByAttribute( attribute );

    const long pointOffset =
        point.getControlOffset() ?
            point.getControlOffset() :
            point.getPointOffset() % 10000;

    return
    {
        makeSignalTemplate( point.getPointOffset(), 0, "CBC Heartbeat" ),
        makeRequestTemplate( point.getPaoId(), putvalueAnalogCommand( pointOffset, keepAliveValue ) )
    };
}

///

Policy::AttributeList NoCbcHeartbeatPolicy::getSupportedAttributes()
{
    return
    {
    };
}

Policy::Actions NoCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue )
{
    return
    {
    };
}

Policy::Actions NoCbcHeartbeatPolicy::StopHeartbeat()
{
    return
    {
    };
}

///

Policy::AttributeList AnalogCbcHeartbeatPolicy::getSupportedAttributes()
{
    return
    {
        Attribute::ScadaOverrideClear,
        Attribute::ScadaOverrideHeartbeat,
        Attribute::ScadaOverrideMode
    };
}

Policy::Actions AnalogCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue )
{
    // Write the current keep alive value to the CBC

    Actions    actions;

    actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideHeartbeat, keepAliveValue ) );

    return actions;
}

/// 

Policy::AttributeList PulsedCbcHeartbeatPolicy::getSupportedAttributes()
{
    return
    {
        Attribute::ScadaOverrideClear,
        Attribute::ScadaOverrideCountdownTimer,
        Attribute::ScadaOverrideEnable,
        Attribute::ScadaOverrideMode
    };
}

Policy::Actions PulsedCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue )
{
    // If the value in the CBC doesn't match the value in the config, update it.  Then pulse the enable point
    //  to put the CBC into ScadaOverride mode.

    Actions    actions;

    if ( readCurrentValue() != keepAliveValue )
    {
        actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideCountdownTimer, keepAliveValue ) );
    }
    
    actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( Attribute::ScadaOverrideEnable ),
                                                      "CBC Heartbeat Pulse" ) );

    return actions;
}

long PulsedCbcHeartbeatPolicy::readCurrentValue()
try
{
    return getValueByAttribute( Attribute::ScadaOverrideCountdownTimer );
}
catch ( UninitializedPointValue & )
{
    return 0L;  // can't read value -- send default of 0
}

}
}

