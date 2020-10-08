#pragma once

#include "dev_rfn.h"
#include "RfnWaterNodeMessaging.h"

namespace Cti::Devices {

class IM_EX_DEVDB RfBatteryNodeDevice :
    public RfnDevice
{
protected:

    YukonError_t executePutConfig(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests) override;
    YukonError_t executeGetConfig(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests) override;

    virtual boost::optional<Messaging::Rfn::RfnGetChannelConfigReplyMessage> RfBatteryNodeDevice::readConfigurationFromNM(const RfnIdentifier& rfnId) const;

private:

    YukonError_t executePutConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    YukonError_t executeGetConfigIntervals(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
};

typedef RfBatteryNodeDevice Rfw201Device;
typedef RfBatteryNodeDevice Rfg201Device;
typedef RfBatteryNodeDevice Rfg301Device;

}