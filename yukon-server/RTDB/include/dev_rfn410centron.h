#pragma once

#include "dev_rfnResidentialVoltage.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn410CentronDevice :
    public RfnResidentialVoltageDevice
{
    ConfigMap getConfigMethods(InstallType installType) override;

    YukonError_t executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    void storeLcdConfig(const Commands::RfnCentronLcdConfigurationCommand::Read &cmd);

protected:
    struct c1sx_display
    {
        Commands::RfnCentronLcdConfigurationCommand::metric_vector_t display_metrics;
        Commands::RfnCentronLcdConfigurationCommand::DisplayDigits display_digits;
        uint8_t cycle_delay;
    };

    c1sx_display getDisplayConfigItems(const Config::DeviceConfigSPtr &config);

    bool isDisplayConfigCurrent(const c1sx_display &config);

    void handleCommandResult(const Commands::RfnConfigNotificationCommand         &cmd) override;
    void handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd) override;
    void handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd) override;

public:
    Rfn410CentronDevice() = default;
};


typedef Rfn410CentronDevice Rfn410clDevice;

}
}
