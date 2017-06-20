#include "precompiled.h"

#include "cbcHeartbeatPolicy.h"


namespace Cti           {
namespace CapControl    {

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

Policy::Actions NoCbcHeartbeatPolicy::StopHeartbeat( const long keepAliveValue )
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
        Attribute::ScadaOverrideHeartbeat
    };
}

Policy::Actions AnalogCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue )
{
    // Write the current keep alive value to the CBC

    Actions    actions;

    actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideHeartbeat, keepAliveValue ) );

    return actions;
}

Policy::Actions AnalogCbcHeartbeatPolicy::StopHeartbeat( const long keepAliveValue /* ignored */ )
{
    // Read the current heartbeat config value, if it's not zero then write a zero to put the CBC into
    //  local mode.  If it is zero, the CBC is already released, do nothing.

    Actions    actions;

    if ( readCurrentValue() )   // != 0
    {
        actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideHeartbeat, 0L ) );
    }

    return actions;
}

long AnalogCbcHeartbeatPolicy::readCurrentValue()
try
{
    return getValueByAttribute( Attribute::ScadaOverrideHeartbeat );
}
catch ( UninitializedPointValue & )
{
    return 0L;  // can't read value -- send default of 0
}

/// 

Policy::AttributeList PulsedCbcHeartbeatPolicy::getSupportedAttributes()
{
    return
    {
        Attribute::ScadaOverrideCountdownTimer,
        Attribute::ScadaOverrideType,
        Attribute::ScadaOverrideEnable
    };
}

Policy::Actions PulsedCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue )
{
    // If the value in the CBC doesn't match the value in the config, then update it.  Then pulse the enable
    //  to put the CBC in remote mode if the config value is non zero.

    Actions    actions;

    if ( readCurrentValue() != keepAliveValue )
    {
        actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideCountdownTimer, keepAliveValue ) );
    }
    
    if ( keepAliveValue > 0 )
    {
        actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( Attribute::ScadaOverrideEnable ),
                                                          "CBC Heartbeat Pulse" ) );
    }

    return actions;
}

Policy::Actions PulsedCbcHeartbeatPolicy::StopHeartbeat( const long keepAliveValue /* ignored */ )
{
    // If the value in the CBC is non zero, then update it to zero.  Then pulse the enable
    //  to put the CBC into local mode.

    Actions    actions;

    if ( readCurrentValue() )   // != 0
    {
        actions.emplace_back( WriteAnalogValue( Attribute::ScadaOverrideCountdownTimer, 0L ) );
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

