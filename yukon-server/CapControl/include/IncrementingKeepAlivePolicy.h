#pragma once

#include "yukon.h"
#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

// If the regulator's heartbeat mode is INCREMENT and the regulators control mode is NOT SetPoint,
// then the AutoBlockEnable command should be sent in the SendKeepAlive() method. In this case the 
// struct below will be used.
struct IncrementingKeepAlivePolicy_SendAutoBlock : KeepAlivePolicy
{
    AttributeList getSupportedAttributes() const override;

    Actions SendKeepAlive( const long keepAliveValue ) override;

    Actions StopKeepAlive() override;

    Actions EnableRemoteControl( const long keepAliveValue ) override;
    
    Actions DisableRemoteControl() override;

protected:

    long readKeepAliveValue();
    long getNextKeepAliveValue();
    virtual bool needsAutoBlockEnable();
};

// If the regulator's heartbeat mode is INCREMENT and the regulators control mode is SetPoint,
// then the AutoBlockEnable command should NOT be sent in the SendKeepAlive() method. In this 
// case the struct below will be used.
struct IncrementingKeepAlivePolicy_SuppressAutoBlock : IncrementingKeepAlivePolicy_SendAutoBlock
{

protected:

    bool needsAutoBlockEnable() override;
};

}
}

