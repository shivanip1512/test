#pragma once

#include "dev_rfnConsumer.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420CentronDevice :
    public RfnConsumerDevice,
    public Commands::RfnCentronLcdConfigurationCommand::ResultHandler
{
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    void handleResult(const Commands::RfnCentronLcdConfigurationCommand &cmd);
};

}
}
