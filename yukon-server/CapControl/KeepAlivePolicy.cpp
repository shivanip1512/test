#include "precompiled.h"

#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

const std::string KeepAlivePolicy::KeepAliveText            = "Keep Alive";
const std::string KeepAlivePolicy::EnableRemoteControlText  = "Enable Remote Control";
const std::string KeepAlivePolicy::DisableRemoteControlText = "Disable Remote Control";

KeepAlivePolicy::OperatingMode KeepAlivePolicy::getOperatingMode()
{
    double value = getValueByAttribute( PointAttribute::AutoRemoteControl );

    return ( value == 1.0 )
                ? RemoteMode
                : LocalMode;
}

Policy::Action KeepAlivePolicy::WriteKeepAliveValue( const long keepAliveValue )
{
    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    const long pointOffset =
        point.getControlOffset() ?
            point.getControlOffset() :
            point.getPointOffset() % 10000;

    return
    {
        makeSignalTemplate( point.getPointOffset(), 0, KeepAliveText ),
        makeRequestTemplate( point.getPaoId(), putvalueAnalogCommand( pointOffset, keepAliveValue ) )
    };
}

}
}

