#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420CentronDevice :
    public RfnResidentialDevice
{
    virtual ConfigMap getConfigMethods(bool readOnly);

    YukonError_t executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd);
    void handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd);

public:
    Rfn420CentronDevice() {};
};


typedef Rfn420CentronDevice Rfn420clDevice;
typedef Rfn420CentronDevice Rfn420cdDevice;

}
}
