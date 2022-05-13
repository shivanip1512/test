#include "precompiled.h"

#include "dev_rfn530s4x_der.h"

#include "cmd_rfn_CommStatus.h"

namespace Cti::Devices {

YukonError_t RfnDerEdgeCoordinator::executeGetStatus(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests)
{
    return ClientErrors::NoMethod;
}

}
