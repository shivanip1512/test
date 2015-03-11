#pragma once

#include "Policy.h"

#include <memory>

class CtiSignalMsg;
class CtiRequestMsg;

namespace Cti           {
namespace CapControl    {


struct ControlPolicy : Policy
{
    using ControlRequest = std::pair<std::unique_ptr<CtiSignalMsg>,
                                     std::unique_ptr<CtiRequestMsg> >;

    virtual ControlRequest TapUp() = 0;
    virtual ControlRequest TapDown() = 0;

    virtual ControlRequest AdjustSetPoint( const double changeAmount ) = 0;

    virtual double getSetPointValue() = 0;
    virtual double getSetPointBandwidth() = 0;
};

}
}

