#pragma once

#include "dev_mct440_213xb.h"

namespace Cti {
namespace Devices {


class IM_EX_DEVDB Mct440_2133BDevice : public Mct440_213xBDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Mct440_2133BDevice(const Mct440_2133BDevice&);
    Mct440_2133BDevice& operator=(const Mct440_2133BDevice&);

    typedef Mct440_213xBDevice Inherited;

protected:
    virtual int getPhaseCount();

public:
    Mct440_2133BDevice() {};
    virtual ~Mct440_2133BDevice() {};
};


}
}
