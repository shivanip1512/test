#include "precompiled.h"

#include "dev_rf_cellular_relay.h"

#include "cmd_rfn_CommStatus.h"

namespace Cti::Devices {

YukonError_t RfCellularRelayDevice::executeGetStatus(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests)
{
    if( containsString(parse.getCommandStr(), " cell") )
    {
        rfnRequests.emplace_back(std::make_unique<Commands::RfnGetCommunicationStatusUpdateCommand>());

        return ClientErrors::None;
    }

    return ClientErrors::NoMethod;
}

}