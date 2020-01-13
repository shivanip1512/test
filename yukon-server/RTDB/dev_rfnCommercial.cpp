#include "precompiled.h"

#include "dev_rfnCommercial.h"
#include "mgr_meter_programming.h"
#include "cmd_rfn_MeterProgramming.h"
#include "MeterProgramStatusArchiveRequestMsg.h"

using namespace Cti::Messaging::Pil;

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
        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress) )
        {
            std::string guid;

            getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, guid);

            sendMeterProgramStatusUpdate({
                    getRfnIdentifier(),
                    guid,
                    ProgrammingStatus::Canceled,
                    ClientErrors::None,
                    std::chrono::system_clock::now() });

            // when we have a ProgrammingProgress entry we also have a ProgrammingConfigID entry
            purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress);
            purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID);

            returnMsgs.emplace_back(
                makeReturnMsg(*pReq, "Meter programming canceled", ClientErrors::None));
        }
        else
        {
            returnMsgs.emplace_back(
                makeReturnMsg(*pReq, "Meter programming not in progress", ClientErrors::None));
        }

        return ClientErrors::None;
    }
    else if( containsString(parse.getCommandStr(), meterProgrammingCmd) )
    {
        if( auto programDescriptor = MeterProgramming::gMeterProgrammingManager->describeAssignedProgram(getRfnIdentifier()) )
        {
            // when we have a ProgrammingProgress entry we also have a ProgrammingConfigID entry
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress, 0.0);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, programDescriptor->guid);

            rfnRequests.push_back(std::make_unique<Commands::RfnMeterProgrammingSetConfigurationCommand>(programDescriptor->guid, programDescriptor->length));

            return ClientErrors::None;
        }

        return ClientErrors::NoMeterProgramAssigned;
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

}
