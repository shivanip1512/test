#pragma once

#include "dev_rfnResidential.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Rfn420CentronDevice :
    public RfnResidentialDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Rfn420CentronDevice(const Rfn420CentronDevice&);
    Rfn420CentronDevice& operator=(const Rfn420CentronDevice&);

    virtual ConfigMap getConfigMethods(bool readOnly);

    YukonError_t executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    void handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd);
    void handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd);

public:
    Rfn420CentronDevice() {};
    virtual ~Rfn420CentronDevice() {};
};


typedef Rfn420CentronDevice Rfn420clDevice;
typedef Rfn420CentronDevice Rfn420cdDevice;

}
}
