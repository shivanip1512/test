#include "precompiled.h"

#include "dev_rfn_edge.h"

#include "cmd_rfn_CommStatus.h"

namespace Cti::Devices {

YukonError_t RfnEdgeDevice::executeGetStatus(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests)
{
    return ClientErrors::NoMethod;
}

}
