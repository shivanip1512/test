#pragma once

#include "dev_rfn.h"

namespace Cti       {
namespace Devices   {

class IM_EX_DEVDB RfBatteryNodeDevice :
    public RfnDevice
{
protected:

    YukonError_t executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;
    YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) override;

private:

    YukonError_t executePutConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executeGetConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
};

typedef RfBatteryNodeDevice Rfw201Device;
typedef RfBatteryNodeDevice Rfg201Device;
typedef RfBatteryNodeDevice Rfg301Device;

}
}

