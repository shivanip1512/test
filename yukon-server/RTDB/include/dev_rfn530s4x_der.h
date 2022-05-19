#pragma once

#include "dev_rfn.h"

namespace Cti::Devices {

class IM_EX_DEVDB Rfn530s4xDerDevice : public RfnDevice
{
    typedef RfnDevice Inherited;

    virtual YukonError_t executeGetStatus(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests) override;
};

}
