#pragma once

#include "yukon.h"
#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

struct IncrementingKeepAlivePolicy : KeepAlivePolicy
{
    enum class AutoBlock 
    {
        Send,
        Suppress
    };

    IncrementingKeepAlivePolicy( AutoBlock autoBlock );

    AttributeList getSupportedAttributes() const override;

    Actions SendKeepAlive( const long keepAliveValue ) override;

    Actions StopKeepAlive() override;

    Actions EnableRemoteControl( const long keepAliveValue ) override;
    
    Actions DisableRemoteControl() override;

protected:

    long readKeepAliveValue();
    long getNextKeepAliveValue();
    virtual bool needsAutoBlockEnable();
    const AutoBlock _autoBlockBehavior;
};

}
}

