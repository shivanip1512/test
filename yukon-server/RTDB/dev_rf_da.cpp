#include "precompiled.h"

#include "dev_rf_da.h"

#include "cmd_rf_da_dnpAddress.h"

#include <boost/make_shared.hpp>

namespace Cti {
namespace Devices {

YukonError_t RfDaDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("dnp address") )
    {
        rfnRequests.push_back(boost::make_shared<Commands::RfDaReadDnpSlaveAddressCommand>());

        return ClientErrors::None;
    }

    return Inherited::executeGetConfig(pReq, parse, returnMsgs, rfnRequests);
}


void RfDaDevice::handleCommandResult(const Commands::RfDaReadDnpSlaveAddressCommand &cmd)
{
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress, cmd.getDnpSlaveAddress());
}


}
}
