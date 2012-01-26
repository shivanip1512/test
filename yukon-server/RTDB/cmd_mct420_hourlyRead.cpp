#include "precompiled.h"

#include "cmd_mct420_hourlyRead.h"
#include "dev_mct420.h"

namespace Cti {
namespace Devices {
namespace Commands {

Mct420Device::point_info Mct420HourlyReadCommand::getAccumulatorData(const unsigned char *buf, const unsigned len) const
{
    return Mct420Device::decodePulseAccumulator(buf, len, 0);
}

}
}
}
