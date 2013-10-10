#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420CentronDevice :
    public RfnResidentialDevice
{
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    void handleResult(const Commands::RfnCentronLcdConfigurationCommand &cmd);
};


typedef Rfn420CentronDevice Rfn420clDevice;
typedef Rfn420CentronDevice Rfn420cdDevice;

}
}
