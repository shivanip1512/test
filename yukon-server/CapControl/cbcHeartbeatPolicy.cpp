#include "precompiled.h"

#include "cbcHeartbeatPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::Action CbcHeartbeatPolicy::WriteAnalogValue( const PointAttribute & attribute, const long keepAliveValue )
{
    LitePoint point = getPointByAttribute( PointAttribute::ScadaOverrideHeartbeat );

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
        PointAttribute::ScadaOverrideHeartbeat
    };
}

Policy::Actions AnalogCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue )
{
    Actions    actions;

    actions.emplace_back( WriteAnalogValue( PointAttribute::ScadaOverrideHeartbeat, keepAliveValue ) );

    return actions;
}

Policy::Actions AnalogCbcHeartbeatPolicy::StopHeartbeat( const long keepAliveValue )
{
    return SendHeartbeat( keepAliveValue );
}

/// 

PulsedCbcHeartbeatPolicy::PulsedCbcHeartbeatPolicy()
    :   firstRun( true )
{
    // empty
}

Policy::AttributeList PulsedCbcHeartbeatPolicy::getSupportedAttributes()
{
    return
    {
        PointAttribute::ScadaOverrideCountdownTimer,
        PointAttribute::ScadaOverrideType,
        PointAttribute::ScadaOverrideEnable
    };
}

Policy::Actions PulsedCbcHeartbeatPolicy::SendHeartbeat( const long keepAliveValue )
{
    Actions    actions;

    if ( firstRun )
    {
        firstRun = false;
        actions.emplace_back( WriteAnalogValue( PointAttribute::ScadaOverrideCountdownTimer, keepAliveValue ) );
    }

    actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( PointAttribute::ScadaOverrideEnable ),
                                                      "CBC Heartbeat Pulse" ) );

    return actions;
}

Policy::Actions PulsedCbcHeartbeatPolicy::StopHeartbeat( const long keepAliveValue )
{
    return
    {
    };
}


}
}

