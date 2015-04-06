#pragma once

#include "yukon.h"
#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

struct CountdownKeepAlivePolicy : KeepAlivePolicy
{
    CountdownKeepAlivePolicy();

    virtual AttributeList getSupportedAttributes() override;

    Actions SendKeepAlive( const long keepAliveValue ) override;

    Actions StopKeepAlive() override;

    Actions EnableRemoteControl( const long keepAliveValue ) override;

    Actions DisableRemoteControl() override;
};

}
}

