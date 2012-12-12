#include "precompiled.h"

#include "dev_mct440_2133b.h"
#include "config_device.h"


using namespace Cti::Devices::Commands;
using Cti::Protocols::EmetconProtocol;

namespace Cti {
namespace Devices {

int Mct440_2133BDevice::getPhaseCount()
{
    return 3;
}

}
}

