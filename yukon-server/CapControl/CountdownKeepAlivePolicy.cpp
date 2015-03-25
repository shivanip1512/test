#include "precompiled.h"

#include "CountdownKeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

CountdownKeepAlivePolicy::CountdownKeepAlivePolicy()
{
    _supportedAttributes = AttributeList
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

Policy::Action CountdownKeepAlivePolicy::EnableRemoteControl( const long keepAliveValue )
{
    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    return 
    {
        makeSignalTemplate( point.getPointId(), keepAliveValue ),
        nullptr
    };
}

Policy::Action CountdownKeepAlivePolicy::DisableRemoteControl()
{
    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    return 
    {
        makeSignalTemplate( point.getPointId(), 0 ),
        nullptr
    };
}

}
}

