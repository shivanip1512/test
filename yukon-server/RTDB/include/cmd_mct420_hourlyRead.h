#pragma once

#include "cmd_mct410_hourlyRead.h"

namespace Cti {
namespace Devices {
namespace Commands {

struct IM_EX_DEVDB Mct420HourlyReadCommand : Mct410HourlyReadCommand
{
    Mct420HourlyReadCommand(CtiDate date_begin, CtiDate date_end, const unsigned channel) :
        Mct410HourlyReadCommand(date_begin, date_end, channel)
    {
    }

    virtual CtiDeviceSingle::point_info getAccumulatorData(const unsigned char *buf, const unsigned len) const;
};

}
}
}

