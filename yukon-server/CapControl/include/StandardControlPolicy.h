#pragma once

#include "yukon.h"
#include "ControlPolicy.h"


namespace Cti           {
namespace CapControl    {

struct StandardControlPolicy : ControlPolicy
{
    StandardControlPolicy();

    Action TapUp() override;
    Action TapDown() override;

    Action AdjustSetPoint( const double changeAmount ) override;

    double getSetPointValue() override;
    double getSetPointBandwidth() override;
};

}
}

