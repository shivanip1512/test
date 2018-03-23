#pragma once

#include "dev_rfnResidentialVoltage.h"
#include "cmd_rfn_FocusAlLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfnLgyrFocusAlDevice
    :   public RfnResidentialVoltageDevice
{
    virtual ConfigMap getConfigMethods(InstallType installType);

    YukonError_t executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    void handleCommandResult(const Commands::RfnFocusAlLcdConfigurationReadCommand &cmd);
    void handleCommandResult(const Commands::RfnFocusAlLcdConfigurationWriteCommand &cmd);

    void storeDisplayMetricInfo(const Commands::RfnFocusAlLcdConfigurationCommand::MetricVector &metrics);

    bool isDisplayConfigCurrent( const std::vector<std::string> &config_display_metrics,
                                 const unsigned config_display_digits,
                                 const unsigned char config_display_duration );

public:
    RfnLgyrFocusAlDevice() {};
};


typedef RfnLgyrFocusAlDevice Rfn420flDevice;
typedef RfnLgyrFocusAlDevice Rfn510flDevice;

}
}
