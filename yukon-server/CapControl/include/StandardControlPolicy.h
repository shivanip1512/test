#pragma once

#include "yukon.h"
#include "ControlPolicy.h"


namespace Cti           {
namespace CapControl    {

struct StandardControlPolicy : ControlPolicy
{
    AttributeList getSupportedAttributes() const override;

    Action TapUp() override;
    Action TapDown() override;

    Action setSetPointValue( const double newSetPoint ) override;
    Action AdjustSetPoint( const double changeAmount ) override;

    double getSetPointValue() override;
    double getSetPointBandwidth() override;
    long getTapPosition() override;
};

}
}

