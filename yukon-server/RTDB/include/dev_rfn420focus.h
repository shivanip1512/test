#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_FocusLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420FocusDevice
    :   public RfnResidentialDevice
{
    virtual ConfigMap getConfigMethods(bool readOnly);

    int executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult(const Commands::RfnFocusLcdConfigurationCommand &cmd);

    bool isDisplayConfigCurrent( const std::vector<std::string> &config_display_metrics,
                                 const std::vector<std::string> &config_display_alphamerics,
                                 const long config_display_duration );
};


typedef RfnResidentialDevice Rfn420flDevice;
typedef RfnResidentialDevice Rfn420fxDevice;
typedef RfnResidentialDevice Rfn420fdDevice;

typedef RfnResidentialDevice Rfn420frxDevice;
typedef RfnResidentialDevice Rfn420frdDevice;


}
}
