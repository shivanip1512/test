#pragma once

#include "dev_mct440_213xb.h"

namespace Cti {
namespace Devices {


class IM_EX_DEVDB Mct440_2131BDevice : public Mct440_213xBDevice
{
    typedef Mct440_213xBDevice Inherited;

protected:

    virtual int getPhaseCount();
};


}
}
