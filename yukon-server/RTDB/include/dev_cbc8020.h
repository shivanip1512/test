#pragma once

#include "dev_cbc7020.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc8020Device : public Cbc7020Device
{
protected:

    enum PointOffsets
    {
        PointOffset_FirmwareRevisionMajor = 5,
        PointOffset_FirmwareRevisionMinor = 6
    };

    virtual void processPoints( Cti::Protocol::Interface::pointlist_t &points );

    static void combineFirmwarePoints( Cti::Protocol::Interface::pointlist_t &points );
};

}
}
