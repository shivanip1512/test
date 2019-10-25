#include "precompiled.h"

#include "dev_rfnCommercial.h"
#include "cmd_rfn_MeterProgramming.h"

namespace Cti::Devices {

YukonError_t RfnCommercialDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    if( containsString(parse.getCommandStr(), " freezeday reset") )
    {
        rfnRequests.push_back(std::make_unique<Commands::RfnDemandFreezeConfigurationCommand>(0));

        return ClientErrors::None;
    }

    if( containsString(parse.getCommandStr(), " meter programming cancel") )
    {
        //  cancel the thing
    }
    else if( containsString(parse.getCommandStr(), " meter programming") )
    {
        rfnRequests.push_back(std::make_unique<Commands::RfnMeterProgrammingSetConfigurationCommand>("unused", 42));

        return ClientErrors::None;
    }

    return RfnMeterDevice::executePutConfig(pReq, parse, returnMsgs, rfnRequests);
}

YukonError_t RfnCommercialDevice::executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                                                CtiCommandParser  & parse,
                                                                ReturnMsgList     & returnMsgs,
                                                                RfnIndividualCommandList & rfnRequests )
{
    rfnRequests.emplace_back( std::make_unique<Commands::RfnImmediateDemandFreezeCommand>() );

    return ClientErrors::None;
}


YukonError_t RfnCommercialDevice::executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                                               CtiCommandParser  & parse,
                                                               ReturnMsgList     & returnMsgs,
                                                               RfnIndividualCommandList & rfnRequests )
{
    rfnRequests.emplace_back( std::make_unique<Commands::RfnGetDemandFreezeInfoCommand>() );

    return ClientErrors::None;
}

}