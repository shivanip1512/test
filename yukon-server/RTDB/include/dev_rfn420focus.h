#pragma once

#include "dev_rfnConsumer.h"
#include "cmd_rfn_FocusLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420FocusDevice :
    public RfnConsumerDevice,
    public Commands::RfnFocusLcdConfigurationCommand::ResultHandler
{
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    void handleResult(const Commands::RfnFocusLcdConfigurationCommand &cmd);

private:
    bool isDisplayConfigCurrent( const std::vector<std::string> &config_display_metrics,
                                 const std::vector<std::string> &config_display_alphamerics,
                                 const long config_display_duration );
};

}
}
