#pragma once

#include "yukon.h"
#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

struct IncrementingKeepAlivePolicy : KeepAlivePolicy
{
    IncrementingKeepAlivePolicy();

    Actions SendKeepAlive( const long keepAliveValue ) override;

    Actions StopKeepAlive() override;

    Action EnableRemoteControl( const long keepAliveValue ) override;
    
    Action DisableRemoteControl() override;

protected:

    long readKeepAliveValue();
    long getNextKeepAliveValue();
    bool needsAutoBlockEnable();
};

}
}

