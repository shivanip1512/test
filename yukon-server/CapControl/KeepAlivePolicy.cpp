#include "precompiled.h"

#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

const std::string KeepAlivePolicy::KeepAliveText            = "Keep Alive";
const std::string KeepAlivePolicy::EnableRemoteControlText  = "Enable Remote Control";
const std::string KeepAlivePolicy::DisableRemoteControlText = "Disable Remote Control";

KeepAlivePolicy::OperatingMode KeepAlivePolicy::getOperatingMode()
{
    double value = getValueByAttribute( Attribute::AutoRemoteControl );

    return ( value == 1.0 )
                ? RemoteMode
                : LocalMode;
}

Policy::Action KeepAlivePolicy::WriteKeepAliveValue( const long keepAliveValue )
{
    LitePoint point = getPointByAttribute( Attribute::KeepAlive );

    return
    {
        makeSignalTemplate( point.getPointId(), 0, KeepAliveText ),
        makeRequestTemplate( point.getPaoId(), putvalueAnalogCommand( point, keepAliveValue ) )
    };
}

}
}

