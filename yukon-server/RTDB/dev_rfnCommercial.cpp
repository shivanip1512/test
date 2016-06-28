#include "precompiled.h"

#include "dev_rfnCommercial.h"

#include <boost/make_shared.hpp>


namespace Cti {
namespace Devices {

YukonError_t RfnCommercialDevice::executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                                                CtiCommandParser  & parse,
                                                                ReturnMsgList     & returnMsgs,
                                                                RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnImmediateDemandFreezeCommand>() );

    return ClientErrors::None;
}


YukonError_t RfnCommercialDevice::executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                                               CtiCommandParser  & parse,
                                                               ReturnMsgList     & returnMsgs,
                                                               RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetDemandFreezeInfoCommand>() );

    return ClientErrors::None;
}


}
}

