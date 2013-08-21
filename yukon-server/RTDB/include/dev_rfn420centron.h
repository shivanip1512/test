#pragma once

#include "dev_rfn.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420CentronDevice :
    public RfnDevice,
    public Commands::RfnCentronLcdConfigurationCommand::ResultHandler
{
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    void handleResult(const Commands::RfnCentronLcdConfigurationCommand &cmd);
};

}
}
