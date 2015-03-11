#pragma once

#include "yukon.h"
#include "ControlPolicy.h"

namespace Cti           {
namespace CapControl    {


struct StandardControlPolicy : ControlPolicy
{
    StandardControlPolicy();

    ControlRequest TapUp() override;
    ControlRequest TapDown() override;

    ControlRequest AdjustSetPoint( const double changeAmount ) override;

    double getSetPointValue() override;
    double getSetPointBandwidth() override;


protected:

    ControlRequest manualTapControl( const LitePoint & point );

    std::unique_ptr<CtiSignalMsg>   makeSignalTemplate( const long ID );

    std::unique_ptr<CtiRequestMsg>  makeRequestTemplate( const long ID, const std::string & command );
};

}
}

