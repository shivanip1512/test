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

    OperatingMode getOperatingMode( CtiCCTwoWayPoints & twoWayPoints );

    virtual Actions SendHeartbeat( const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints) = 0;

    virtual Actions StopHeartbeat( CtiCCTwoWayPoints & twoWayPoints );

    Action WriteAnalogValue( const Attribute & attribute, const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints );
};


struct NoCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints) override;

    Actions StopHeartbeat( CtiCCTwoWayPoints & twoWayPoints ) override;

protected:

    AttributeList getSupportedAttributes() const override;
};


struct AnalogCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints) override;

protected:

    AttributeList getSupportedAttributes() const override;
};


struct PulsedCbcHeartbeatPolicy : CbcHeartbeatPolicy
{
    Actions SendHeartbeat( const long keepAliveValue, CtiCCTwoWayPoints & twoWayPoints) override;

protected:

    AttributeList getSupportedAttributes() const override;

    long readCurrentValue( CtiCCTwoWayPoints & twoWayPoints);
};


}
}

