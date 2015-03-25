#pragma once

#include "yukon.h"
#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

struct CountdownKeepAlivePolicy : KeepAlivePolicy
{
    CountdownKeepAlivePolicy();

    Actions SendKeepAlive( const long keepAliveValue ) override;

    Actions StopKeepAlive() override;

    Action EnableRemoteControl( const long keepAliveValue ) override;

    Action DisableRemoteControl() override;
};

}
}

