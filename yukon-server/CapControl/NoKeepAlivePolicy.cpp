#include "precompiled.h"

#include "NoKeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList NoKeepAlivePolicy::getSupportedAttributes() const
{
    return
    {
        Attribute::AutoRemoteControl
    };
}

Policy::Actions NoKeepAlivePolicy::SendKeepAlive( const long keepAliveValue )
{
    return
    {
    };
}

Policy::Actions NoKeepAlivePolicy::StopKeepAlive()
{
    return
    {
    };
}

Policy::Actions NoKeepAlivePolicy::EnableRemoteControl( const long keepAliveValue )
{
    return
    {
    };
}

Policy::Actions NoKeepAlivePolicy::DisableRemoteControl()
{
    return
    {
    };
}

KeepAlivePolicy::OperatingMode NoKeepAlivePolicy::getOperatingMode()
{
    return RemoteMode;
}

}
}

