#pragma once

#include "dev_rfnMeter.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_DemandFreeze.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_OvUvConfiguration.h"


namespace Cti       {
namespace Devices   {


class IM_EX_DEVDB RfnCommercialDevice
    :   public RfnMeterDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    RfnCommercialDevice(const RfnCommercialDevice&);
    RfnCommercialDevice& operator=(const RfnCommercialDevice&);

protected:

    YukonError_t executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    YukonError_t executeReadDemandFreezeInfo (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

public:
    RfnCommercialDevice() {};
};

typedef RfnCommercialDevice Rfn430a3dDevice;
typedef RfnCommercialDevice Rfn430a3tDevice;
typedef RfnCommercialDevice Rfn430a3kDevice;
typedef RfnCommercialDevice Rfn430a3rDevice;
typedef RfnCommercialDevice Rfn430kvDevice;
typedef RfnCommercialDevice Rfn430sl0Device;
typedef RfnCommercialDevice Rfn430sl1Device;
typedef RfnCommercialDevice Rfn430sl2Device;
typedef RfnCommercialDevice Rfn430sl3Device;
typedef RfnCommercialDevice Rfn430sl4Device;


}
}

