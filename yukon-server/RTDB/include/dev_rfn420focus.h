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


typedef Rfn420FocusDevice Rfn420flDevice;
typedef Rfn420FocusDevice Rfn420fxDevice;
typedef Rfn420FocusDevice Rfn420fdDevice;

typedef Rfn420FocusDevice Rfn420frxDevice;
typedef Rfn420FocusDevice Rfn420frdDevice;


}
}
