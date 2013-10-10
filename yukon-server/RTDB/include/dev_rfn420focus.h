#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_FocusLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420FocusDevice
    :   public RfnResidentialDevice
{
    int executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    void handleResult(const Commands::RfnFocusLcdConfigurationCommand &cmd);

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
