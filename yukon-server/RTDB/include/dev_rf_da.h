#pragma once

#include "dev_rfn.h"

namespace Cti::Devices {

class IM_EX_DEVDB RfDaDevice : public RfnDevice
{
    typedef RfnDevice Inherited;

    YukonError_t executeGetConfig(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests) override;

    void handleCommandResult(const Commands::RfDaReadDnpSlaveAddressCommand &) override;
};

}