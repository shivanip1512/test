#include "precompiled.h"

#include "dev_rf_da.h"

#include "cmd_rf_da_dnpAddress.h"

namespace Cti::Devices {

YukonError_t RfDaDevice::executeGetConfig(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests)
{
    if( parse.isKeyValid("dnp address") )
    {
        rfnRequests.emplace_back(std::make_unique<Commands::RfDaReadDnpSlaveAddressCommand>());

        return ClientErrors::None;
    }

    return Inherited::executeGetConfig(pReq, parse, returnMsgs, requestMsgs, rfnRequests);
}


void RfDaDevice::handleCommandResult(const Commands::RfDaReadDnpSlaveAddressCommand &cmd)
{
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress, cmd.getDnpSlaveAddress());
}


}