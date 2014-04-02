#include "precompiled.h"

#include "dev_rfn.h"
#include "tbl_rfnidentifier.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

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


int RfnDevice::invokeDeviceHandler(DeviceHandler &handler)
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


int RfnDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    typedef int (RfnDevice::*RfnExecuteMethod)(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    typedef std::map<int, RfnExecuteMethod> ExecuteLookup;

    const ExecuteLookup executeMethods = boost::assign::map_list_of
        (GetConfigRequest, &RfnDevice::executeGetConfig)
        (PutConfigRequest, &RfnDevice::executePutConfig)
        (GetValueRequest,  &RfnDevice::executeGetValue)
        (PutValueRequest,  &RfnDevice::executePutValue)
        (GetStatusRequest, &RfnDevice::executeGetStatus)
        (PutStatusRequest, &RfnDevice::executePutStatus);

    int errorCode = NoMethod;
    std::string errorDescription = "Invalid command.";

    if( const boost::optional<RfnExecuteMethod> executeMethod = mapFind(executeMethods, parse.getCommand()) )
    {
        try
        {
            errorCode = (this->**executeMethod)(pReq, parse, returnMsgs, rfnRequests);
        }
        catch( const Commands::RfnCommand::CommandException &ce )
        {
            errorCode        = ce.error_code;
            errorDescription = ce.error_description;
        }
    }

    if( errorCode != NoError )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Couldn't come up with an operation for device " << getName( ) << std::endl;
            dout << CtiTime( ) << "   Command: " << pReq->CommandString( ) << std::endl;
        }

        std::auto_ptr<CtiReturnMsg> executeError(
                new CtiReturnMsg(
                        getID( ),
                        pReq->CommandString(),
                        errorDescription,
                        errorCode,
                        0,
                        MacroOffset::none,
                        0,
                        pReq->GroupMessageId(),
                        pReq->UserMessageId()));

        returnMsgs.push_back(executeError);
    }

    if( ! rfnRequests.empty() )
    {
        const size_t numRequests = rfnRequests.size();

        std::auto_ptr<CtiReturnMsg> commandsSent(
                new CtiReturnMsg(
                        getID( ),
                        pReq->CommandString(),
                        CtiNumStr(numRequests) + (numRequests == 1?" command":" commands") + " queued for device",
                        NoError,
                        0,
                        MacroOffset::none,
                        0,
                        pReq->GroupMessageId(),
                        pReq->UserMessageId()));

        commandsSent->setExpectMore(true);

        returnMsgs.push_back(commandsSent);

        incrementGroupMessageCount(pReq->GroupMessageId(), reinterpret_cast<long>(pReq->getConnectionHandle()), rfnRequests.size());
    }

    for( ReturnMsgList::iterator itr = returnMsgs.begin(); itr != returnMsgs.end(); )
    {
        CtiReturnMsg &retMsg = *itr;

        // Set expectMore on all CtiReturnMsgs but the last, unless there was a command sent, in which case set expectMore on all of them.
        if( ++itr != returnMsgs.end() || ! rfnRequests.empty() )
        {
            retMsg.setExpectMore(true);
        }
    }

    return ExecutionComplete;
}

int RfnDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}


void RfnDevice::logInfo( const std::string &note,
                         const char* function,
                         const char* file,
                         int line ) const
{
    std::string functionName = (function) ? " " + std::string(function)  : "",
                fileName     = (file)     ? " " + std::string(file)      : "",
                lineNumber   = (line > 0) ? " (" + CtiNumStr(line) + ")" : "";

    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << CtiTime() << " Device \"" << getName() << "\" - " << note << functionName << fileName << lineNumber << std::endl;
}

void RfnDevice::logInfo( int debugLevel,
                         const std::string &note,
                         const char* function,
                         const char* file,
                         int line ) const
{
    if( getDebugLevel() & debugLevel )
    {
        logInfo( note, function, file, line );
    }
}

}
}
