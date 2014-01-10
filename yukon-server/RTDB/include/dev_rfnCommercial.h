#pragma once

#include "dev_rfn.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_DemandFreeze.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_OvUvConfiguration.h"


namespace Cti       {
namespace Devices   {


class IM_EX_DEVDB RfnCommercialDevice
    :   public RfnDevice
{
protected:

    int executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    int executeReadDemandFreezeInfo (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
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

