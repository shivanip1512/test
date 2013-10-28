#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420CentronDevice :
    public RfnResidentialDevice
{
    virtual ConfigMap getConfigMethods(bool readOnly);

    int executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd);
    void handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd);
};


typedef Rfn420CentronDevice Rfn420clDevice;
typedef Rfn420CentronDevice Rfn420cdDevice;

}
}
