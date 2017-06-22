#pragma once

#include "yukon.h"
#include "Policy.h"


namespace Cti           {
namespace CapControl    {


struct CbcHeartbeatPolicy : Policy
{
    enum OperatingMode
    {
        Normal,
        ScadaOverride
    };

    OperatingMode getOperatingMode();

    virtual Actions SendHeartbeat( const long keepAliveValue ) = 0;

    virtual Actions StopHeartbeat();

    Action WriteAnalogValue( const Attribute & attribute, const long keepAliveValue );
};


struct NoCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue ) override;

    Actions StopHeartbeat() override;

protected:

    AttributeList getSupportedAttributes() override;
};


struct AnalogCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue ) override;

protected:

    AttributeList getSupportedAttributes() override;
};


struct PulsedCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue ) override;

protected:

    AttributeList getSupportedAttributes() override;

    long readCurrentValue();
};


}
}

