#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_FocusAlLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420FocusAlDevice
    :   public RfnResidentialDevice
{
    virtual ConfigMap getConfigMethods(bool readOnly);

    YukonError_t executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult(const Commands::RfnFocusAlLcdConfigurationReadCommand &cmd);
    void handleCommandResult(const Commands::RfnFocusAlLcdConfigurationWriteCommand &cmd);

    void storeDisplayMetricInfo(const Commands::RfnFocusAlLcdConfigurationCommand::MetricVector &metrics);

    bool isDisplayConfigCurrent( const std::vector<std::string> &config_display_metrics,
                                 const unsigned config_display_digits,
                                 const unsigned char config_display_duration );

public:
    Rfn420FocusAlDevice() {};
};


typedef Rfn420FocusAlDevice  Rfn420flDevice;
typedef RfnResidentialDevice Rfn420fxDevice;
typedef RfnResidentialDevice Rfn420fdDevice;

typedef RfnResidentialDevice Rfn420frxDevice;
typedef RfnResidentialDevice Rfn420frdDevice;


}
}
