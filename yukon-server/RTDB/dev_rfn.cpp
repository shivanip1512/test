#include "precompiled.h"

#include "dev_rfn.h"
#include "tbl_rfnidentifier.h"
#include "cmd_rfn_Aggregate.h"

#include "std_helper.h"

namespace Cti {
namespace Devices {

std::string RfnDevice::getSQLCoreStatement() const
{
    static const std::string sqlCore =
        "SELECT "
            "YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
            "DV.deviceid, DV.alarminhibit, DV.controlinhibit, "
            "RFN.SerialNumber, RFN.Manufacturer, RFN.Model "
        "FROM "
            "YukonPAObject YP "
            "JOIN Device DV ON YP.PaObjectId = DV.DeviceID "
            "JOIN RFNAddress RFN on DV.DeviceId = RFN.DeviceID "
         "WHERE "
            "1=1 ";  //  Unfortunately, it seems that our selectors require a WHERE clause.

    return sqlCore;
}


YukonError_t RfnDevice::invokeDeviceHandler(DeviceHandler &handler)
{
    return handler.execute(*this);
}

void RfnDevice::extractCommandResult(const Commands::RfnCommand &command)
{
    return command.invokeResultHandler(*this);
}

RfnIdentifier RfnDevice::getRfnIdentifier() const
{
    return _rfnId;
}


void RfnDevice::DecodeDatabaseReader(RowReader &rdr)
{
    using Database::Tables::RfnIdentifierTable;

    _rfnId = RfnIdentifierTable::DecodeDatabaseReader(rdr);

    CtiDeviceSingle::DecodeDatabaseReader(rdr);
}


bool RfnDevice::areAggregateCommandsSupported() const
{
    return gConfigParms.getValueAsDouble("RFN_FIRMWARE") >= 9.0;
}


std::unique_ptr<CtiReturnMsg> RfnDevice::makeReturnMsg(const CtiRequestMsg& req, const std::string result, YukonError_t nRet)
{
    return std::make_unique<CtiReturnMsg>(
        req.DeviceId(),
        req.CommandString(),
        result,
        nRet,
        0,
        MacroOffset::none,
        0,
        req.GroupMessageId(),
        req.UserMessageId());
}


YukonError_t RfnDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    using RfnExecuteMethod = decltype(&RfnDevice::executeGetConfig);

    const std::map<int, RfnExecuteMethod> executeMethods {
        { GetConfigRequest, &RfnDevice::executeGetConfig },
        { PutConfigRequest, &RfnDevice::executePutConfig },
        { GetValueRequest,  &RfnDevice::executeGetValue  },
        { PutValueRequest,  &RfnDevice::executePutValue  },
        { GetStatusRequest, &RfnDevice::executeGetStatus },
        { PutStatusRequest, &RfnDevice::executePutStatus }};

    YukonError_t errorCode = ClientErrors::NoMethod;
    std::string errorDescription = "Invalid command.";

    RfnIndividualCommandList commands;

    if( const auto executeMethod = mapFind(executeMethods, parse.getCommand()) )
    {
        try
        {
            errorCode = (this->**executeMethod)(pReq, parse, returnMsgs, commands);

            if( errorCode )
            {
                errorDescription = CtiError::GetErrorString(errorCode);
            }
        }
        catch( const YukonErrorException &ce )
        {
            errorCode        = ce.error_code;
            errorDescription = ce.error_description;
        }
        catch( std::exception &e )
        {
            errorCode        = ClientErrors::E2eErrorUnmapped;
            errorDescription = e.what();
        }
    }

    if( errorCode )
    {
        CTILOG_ERROR(dout, "Execute error for device " << getName() <<". Command: "<< pReq->CommandString());

        returnMsgs.emplace_back(
                makeReturnMsg(
                    *pReq, 
                    errorDescription,
                    errorCode));
    }

    if( ! commands.empty() )
    {
        const size_t numRequests = commands.size();

        auto commandsSent = 
                std::make_unique<CtiReturnMsg>(
                        getID( ),
                        pReq->CommandString(),
                        CtiNumStr(numRequests) + (numRequests == 1?" command":" commands") + " queued for device",
                        ClientErrors::None,
                        0,
                        MacroOffset::none,
                        0,
                        pReq->GroupMessageId(),
                        pReq->UserMessageId());

        commandsSent->setExpectMore(true);

        returnMsgs.push_back(std::move(commandsSent));

        incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle(), numRequests);

        if( commands.size() > 1 && areAggregateCommandsSupported() )
        {
            auto aggregateCommand = std::make_unique<Commands::RfnAggregateCommand>(std::move(commands));

            rfnRequests.emplace_back(std::move(aggregateCommand));
        }
        else
        {
            rfnRequests.insert(
                rfnRequests.end(), 
                std::make_move_iterator(commands.begin()),
                std::make_move_iterator(commands.end()));
        }
    }

    for( auto itr = returnMsgs.begin(); itr != returnMsgs.end(); )
    {
        auto & retMsg = **itr;

        // Set expectMore on all CtiReturnMsgs but the last, unless there was a command sent, in which case set expectMore on all of them.
        if( ++itr != returnMsgs.end() || ! rfnRequests.empty() )
        {
            retMsg.setExpectMore(true);
        }
    }

    return ExecutionComplete;
}

/**
 * Called by executeConfigInstall() to execute a putconfig/getconfig install for one config part
 */
YukonError_t RfnDevice::executeConfigInstallSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests, const std::string &configPart, const ConfigMethod &configMethod )
{
    YukonError_t nRet = ClientErrors::NoMethod;
    std::string error_description;

    try
    {
        nRet = configMethod(pReq, parse, returnMsgs, rfnRequests);
    }
    catch( const Commands::RfnCommand::CommandException &ce )
    {
        error_description = ce.error_description;
        nRet              = ce.error_code;
    }

    if( nRet )
    {
        std::string result;

        switch(nRet)
        {
            case ClientErrors::NoConfigData:
            {
                result = "ERROR: Device had no configuration for config:" + configPart;

                CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - had no configuration for config: "<< configPart);

                break;
            }
            case ClientErrors::ConfigCurrent:
            {
                result = "Config " + configPart + " is current.";

                nRet = ClientErrors::None; //This is an OK return! Note that nRet is no longer propagated!

                break;
            }
            case ClientErrors::ConfigNotCurrent:
            {
                result = "Config " + configPart + " is NOT current.";

                break;
            }
            default:
            {
                if( error_description.empty() )
                {
                    error_description = "NoMethod or invalid config";
                }

                result = "ERROR: " + error_description + ". Config name:" + configPart;

                CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - had a configuration error using config: "<< configPart);
            }
        }

        returnMsgs.emplace_back(
            makeReturnMsg(
                *pReq, 
                result, 
                nRet));
    }

    return nRet;
}

YukonError_t RfnDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnDevice::executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnDevice::executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnDevice::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

}
}
