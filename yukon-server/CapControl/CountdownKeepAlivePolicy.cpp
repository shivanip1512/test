#include "precompiled.h"

#include "CountdownKeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList CountdownKeepAlivePolicy::getSupportedAttributes()
{
    return
    {
        PointAttribute::AutoRemoteControl,
        PointAttribute::KeepAlive
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

    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), keepAliveValue, EnableRemoteControlText ),
                          nullptr );

    return actions;
}

Policy::Actions CountdownKeepAlivePolicy::DisableRemoteControl()
{
    Actions    actions;

    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), 0, DisableRemoteControlText ),
                          nullptr );

    return actions;
}

}
}

