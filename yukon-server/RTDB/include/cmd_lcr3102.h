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
        Read_ExpresscomMsgSend  =  0x00,
        Read_ActOnStoredMessage =  0x100,
    };

};

}
}
}
