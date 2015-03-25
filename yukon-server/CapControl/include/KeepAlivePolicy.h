#pragma once

#include "Policy.h"


namespace Cti           {
namespace CapControl    {

struct KeepAlivePolicy : Policy
{
    enum OperatingMode
    {
        UnknownMode = -1,
        RemoteMode,
        LocalMode
    };

    virtual Actions SendKeepAlive( const long keepAliveValue ) = 0;

    virtual Actions StopKeepAlive() = 0;

    virtual Action EnableRemoteControl( const long keepAliveValue ) = 0;

    virtual Action DisableRemoteControl() = 0;

    OperatingMode   getOperatingMode();

protected:

    Action WriteKeepAliveValue( const long keepAliveValue );
};

}
}

