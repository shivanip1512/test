#pragma once

#include "dev_rfnMeter.h"
#include "cmd_rfn_LoadProfile.h"
#include "cmd_rfn_DemandFreeze.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_OvUvConfiguration.h"

namespace Cti::Devices {


class IM_EX_DEVDB RfnCommercialDevice
    :   public RfnMeterDevice
{
protected:

    YukonError_t executePutConfig            (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executeGetConfig            (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

    YukonError_t executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    YukonError_t executeReadDemandFreezeInfo (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    void handleCommandResult( const Commands::RfnMeterProgrammingGetConfigurationCommand & cmd ) override;

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
typedef RfnCommercialDevice Rfn530s4xDevice;
typedef RfnCommercialDevice Rfn530s4eaxDevice;
typedef RfnCommercialDevice Rfn530s4eaxrDevice;
typedef RfnCommercialDevice Rfn530s4erxDevice;
typedef RfnCommercialDevice Rfn530s4erxrDevice;


}
