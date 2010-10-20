#pragma once

#include "cmd_dlc.h"

namespace Cti {
namespace Devices {
namespace Commands {

class Lcr3102Command : public DlcCommand
{
protected:

    enum FunctionReads
    {
        Read_DummyRead          =  0x00,
        Read_ActOnStoredMessage =   0x0,

        // Lengths for the responses
        Read_TamperReadLength = 1,
        Read_DRSummaryLength  = 1
    };

};

}
}
}
