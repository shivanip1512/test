#include "precompiled.h"

#include "CountdownKeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList CountdownKeepAlivePolicy::getSupportedAttributes() const
{
    return
    {
        Attribute::AutoRemoteControl,
        Attribute::KeepAlive
    };
}

Policy::Actions CountdownKeepAlivePolicy::SendKeepAlive( const long keepAliveValue )
{
    Actions    actions;

    actions.emplace_back( WriteKeepAliveValue( keepAliveValue ) );

    return actions;
}

Policy::Actions CountdownKeepAlivePolicy::StopKeepAlive()
{
    Actions    actions;

    actions.emplace_back( WriteKeepAliveValue( 0 ) );

    return actions;
}

Policy::Actions CountdownKeepAlivePolicy::EnableRemoteControl( const long keepAliveValue )
{
    Actions    actions;

    LitePoint point = getPointByAttribute( Attribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), keepAliveValue, EnableRemoteControlText ),
                          PorterRequest::none() );

    return actions;
}

Policy::Actions CountdownKeepAlivePolicy::DisableRemoteControl()
{
    Actions    actions;

    LitePoint point = getPointByAttribute( Attribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), 0, DisableRemoteControlText ),
                          PorterRequest::none() );

    return actions;
}

}
}

