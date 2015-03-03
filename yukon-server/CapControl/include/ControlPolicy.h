#pragma once

#include "Policy.h"

class CtiSignalMsg;
class CtiRequestMsg;

namespace Cti           {
namespace CapControl    {


struct ControlPolicy : Policy
{
    struct ControlRequest
    {
        CtiSignalMsg    * signal;
        CtiRequestMsg   * request;
    };

    virtual ControlRequest TapUp() = 0;
    virtual ControlRequest TapDown() = 0;

    virtual ControlRequest AdjustSetPoint( const double changeAmount ) = 0;

    virtual double getSetPointValue() = 0;
    virtual double getSetPointBandwidth() = 0;


};

}
}

