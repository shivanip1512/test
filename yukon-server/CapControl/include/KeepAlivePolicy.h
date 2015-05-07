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

    virtual Actions EnableRemoteControl( const long keepAliveValue ) = 0;

    virtual Actions DisableRemoteControl() = 0;

    virtual OperatingMode getOperatingMode();

protected:

    Action WriteKeepAliveValue( const long keepAliveValue );

    static const std::string KeepAliveText;
    static const std::string EnableRemoteControlText;
    static const std::string DisableRemoteControlText;
};

}
}

