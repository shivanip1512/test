#include "precompiled.h"

#include "cmd_mct420_hourlyRead.h"
#include "dev_mct420.h"

namespace Cti {
namespace Devices {
namespace Commands {

CtiDeviceSingle::point_info Mct420HourlyReadCommand::getAccumulatorData(const unsigned char *buf, const unsigned len) const
{
    return Mct420Device::getMct420AccumulatorData(buf, len);
}

}
}
}
