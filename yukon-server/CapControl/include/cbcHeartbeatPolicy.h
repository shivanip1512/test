#pragma once

#include "yukon.h"
#include "Policy.h"


namespace Cti           {
namespace CapControl    {


struct CbcHeartbeatPolicy : Policy
{
    virtual Actions SendHeartbeat( const long keepAliveValue ) = 0;

    virtual Actions StopHeartbeat( const long keepAliveValue ) = 0;

    Action WriteAnalogValue( const Attribute & attribute, const long keepAliveValue );
};


struct NoCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue ) override;

    Actions StopHeartbeat( const long keepAliveValue ) override;

protected:

    AttributeList getSupportedAttributes() override;
};


struct AnalogCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue ) override;

    Actions StopHeartbeat( const long keepAliveValue ) override;

protected:

    AttributeList getSupportedAttributes() override;

    long readCurrentValue();
};


struct PulsedCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue ) override;

    Actions StopHeartbeat( const long keepAliveValue ) override;

protected:

    AttributeList getSupportedAttributes() override;

    long readCurrentValue();
};


}
}

