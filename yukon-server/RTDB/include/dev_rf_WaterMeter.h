#pragma once

#include "dev_rfn.h"



namespace Cti       {
namespace Devices   {

class IM_EX_DEVDB RfWaterMeterDevice :
    public RfnDevice
{
protected:

    YukonError_t executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;
    YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests) override;

private:

    YukonError_t executePutConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    YukonError_t executeGetConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
};

typedef RfWaterMeterDevice Rfw201Device;
typedef RfWaterMeterDevice Rfw205Device;

}
}

