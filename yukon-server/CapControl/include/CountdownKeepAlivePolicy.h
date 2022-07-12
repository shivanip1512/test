#pragma once

#include "yukon.h"
#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

struct CountdownKeepAlivePolicy : KeepAlivePolicy
{
    AttributeList getSupportedAttributes() const override;

    Actions SendKeepAlive( const long keepAliveValue, std::chrono::seconds regulatorTimeout) override;

    Actions StopKeepAlive() override;

    Actions EnableRemoteControl( const long keepAliveValue ) override;

    Actions DisableRemoteControl() override;
};

}
}

