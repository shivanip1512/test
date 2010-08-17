#pragma once

#include "cmd_dlc.h"

namespace Cti {
namespace Devices {
namespace Commands {

class Mct410Command : public DlcCommand
{
protected:

    enum FunctionReads
    {
        Read_HourlyReadChannel1BasePos = 0x1c0,
        Read_HourlyReadChannel2BasePos = 0x1d0,
        Read_HourlyReadLen =   13,
    };
};

}
}
}

