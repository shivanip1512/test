#pragma once

#include "dev_rfn410Centron.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420CentronDevice :
    public Rfn410CentronDevice
{
    struct c2sx_display : c1sx_display
    {
        c2sx_display(const c1sx_display &rhs) :
            c1sx_display(rhs)
        {
        }

        Commands::RfnCentronLcdConfigurationCommand::DisconnectDisplayState disconnect_display;
    };

    c2sx_display getDisplayConfigItems(const Config::DeviceConfigSPtr &config);

    bool isDisplayConfigCurrent(const c2sx_display &config);

    void storeLcdConfig(const Commands::RfnCentronLcdConfigurationCommand::Read &cmd);

protected:
    YukonError_t executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executeGetStatusWifi(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override final;

    void handleCommandResult(const Commands::RfnConfigNotificationCommand         &cmd) override;
    void handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd) override;
    void handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd) override;
};


using Rfn420clDevice  = Rfn420CentronDevice;
using Rfn420clwDevice = Rfn420CentronDevice;
using Rfn420cdDevice  = Rfn420CentronDevice;
using Rfn420cdwDevice = Rfn420CentronDevice;

}
}
