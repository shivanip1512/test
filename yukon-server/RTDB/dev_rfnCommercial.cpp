#include "precompiled.h"

#include "dev_rfnCommercial.h"
#include "mgr_meter_programming.h"
#include "cmd_rfn_MeterProgramming.h"
#include "meter_programming_prefixes.h"
#include "MeterProgramStatusArchiveRequestMsg.h"

#include "error.h"

using Cti::Messaging::Rfn::MeterProgramStatusArchiveRequestMsg;
using Cti::Messaging::Rfn::ProgrammingStatus;

namespace Cti::Devices {

namespace
{
    static const std::string meterProgrammingCmd { " meter programming" };
}

YukonError_t RfnCommercialDevice::executePutConfig(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests)
{
    constexpr auto YukonPrefix = static_cast<char>(Cti::MeterProgramming::GuidPrefixes::YukonProgrammed);

    if( containsString(parse.getCommandStr(), " freezeday reset") )
    {
        rfnRequests.push_back(std::make_unique<Commands::RfnDemandFreezeConfigurationCommand>(0));

        return ClientErrors::None;
    }

    if( containsString(parse.getCommandStr(), meterProgrammingCmd + " cancel") )
    {
        if( isE2eServerDisabled() )
        {
            CTILOG_INFO(dout, "E2E server is disabled, disallowing meter programming request for device " << getName());

            return ClientErrors::NoMethod;
        }

        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress) )
        {
            std::string guid;

            getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, guid);

            sendMeterProgramStatusUpdate({
                    getRfnIdentifier(),
                    YukonPrefix + guid,
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
    
    if( containsString(parse.getCommandStr(), meterProgrammingCmd) )
    {
        if( isE2eServerDisabled() )
        {
            CTILOG_INFO(dout, "E2E server is disabled, disallowing meter programming request for device " << getName());

            return ClientErrors::NoMethod;
        }

        const auto guid = MeterProgramming::gMeterProgrammingManager->getAssignedGuid(getRfnIdentifier());
        if( guid.empty() )
        {
            const auto error = ClientErrors::NoMeterProgramAssigned;

            CTILOG_ERROR(dout, CtiError::GetErrorString(error) << FormattedList::of(
                "RfnIdentifier", getRfnIdentifier()));

            //  No assignment, can't send a MeterProgramStatusUpdate
            
            return error;
        }
        
        const auto programSize = MeterProgramming::gMeterProgrammingManager->getProgramSize(guid);
        if( ! programSize )
        {
            CTILOG_ERROR(dout, CtiError::GetErrorString(programSize.error()) << FormattedList::of(
                "RfnIdentifier", getRfnIdentifier()));

            sendMeterProgramStatusUpdate({
                    getRfnIdentifier(),
                    YukonPrefix + guid,
                    ProgrammingStatus::Failed,
                    programSize.error(),
                    std::chrono::system_clock::now() });

            return programSize.error();
        }

        // when we have a ProgrammingProgress entry we also have a ProgrammingConfigID entry
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress, 0.0);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigID, guid);

        rfnRequests.push_back(std::make_unique<Commands::RfnMeterProgrammingSetConfigurationCommand>(guid, *programSize));

        return ClientErrors::None;
    }

    return RfnMeterDevice::executePutConfig(pReq, parse, returnMsgs, requestMsgs, rfnRequests);
}

void RfnCommercialDevice::sendMeterProgramStatusUpdate(MeterProgramStatusArchiveRequestMsg msg)
{
    Messaging::Rfn::sendMeterProgramStatusUpdate(std::move(msg));
}


YukonError_t RfnCommercialDevice::executeGetConfig(CtiRequestMsg* pReq, CtiCommandParser& parse, ReturnMsgList& returnMsgs, RequestMsgList& requestMsgs, RfnIndividualCommandList& rfnRequests)
{
    if( containsString(parse.getCommandStr(), meterProgrammingCmd) )
    {
        rfnRequests.push_back( std::make_unique<Commands::RfnMeterProgrammingGetConfigurationCommand>() );

        return ClientErrors::None;
    }

    return RfnMeterDevice::executeGetConfig(pReq, parse, returnMsgs, requestMsgs, rfnRequests);
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
