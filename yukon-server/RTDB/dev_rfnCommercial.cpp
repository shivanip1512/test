#include "precompiled.h"

#include "dev_rfnCommercial.h"
#include "cmd_rfn_MeterProgramming.h"

namespace Cti::Devices {

namespace
{
    static const std::string meterProgrammingCmd { " meter programming" };
}

YukonError_t RfnCommercialDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    if( containsString(parse.getCommandStr(), " freezeday reset") )
    {
        rfnRequests.push_back(std::make_unique<Commands::RfnDemandFreezeConfigurationCommand>(0));

        return ClientErrors::None;
    }

    if( containsString(parse.getCommandStr(), meterProgrammingCmd + " cancel") )
    {
        //  cancel the thing
    }
    else if( containsString(parse.getCommandStr(), meterProgrammingCmd) )
    {
        rfnRequests.push_back(std::make_unique<Commands::RfnMeterProgrammingSetConfigurationCommand>("7d444840-9dc0-11d1-b245-5ffdce74fad2", 11235));

        return ClientErrors::None;
    }

    return RfnMeterDevice::executePutConfig(pReq, parse, returnMsgs, rfnRequests);
}

YukonError_t RfnCommercialDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    if( containsString(parse.getCommandStr(), meterProgrammingCmd) )
    {
        rfnRequests.push_back( std::make_unique<Commands::RfnMeterProgrammingGetConfigurationCommand>() );

        return ClientErrors::None;
    }

    return RfnMeterDevice::executeGetConfig(pReq, parse, returnMsgs, rfnRequests);
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

void RfnCommercialDevice::handleCommandResult( const Commands::RfnMeterProgrammingGetConfigurationCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigurationId, cmd.getMeterConfigurationID() );
}

}
