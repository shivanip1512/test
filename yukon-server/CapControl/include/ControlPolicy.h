#pragma once

#include "Policy.h"


namespace Cti           {
namespace CapControl    {

struct ControlPolicy : Policy
{
    virtual Action TapUp() = 0;
    virtual Action TapDown() = 0;

    virtual Action AdjustSetPoint( const double changeAmount ) = 0;

    virtual double getSetPointValue() = 0;
    virtual double getSetPointBandwidth() = 0;
    virtual long getTapPosition() = 0;
};

}
}

