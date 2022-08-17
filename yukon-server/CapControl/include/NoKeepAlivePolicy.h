#pragma once

#include "yukon.h"
#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

struct NoKeepAlivePolicy : KeepAlivePolicy
{
    AttributeList getSupportedAttributes() const override;

    Actions SendKeepAlive( const long keepAliveValue ) override;

    Actions StopKeepAlive() override;

    Actions EnableRemoteControl( const long keepAliveValue ) override;
    
    Actions DisableRemoteControl() override;

    OperatingMode getOperatingMode() override;
};

}
}

