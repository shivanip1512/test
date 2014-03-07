#include "precompiled.h"

#include "dev_rfn.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Devices {

std::string RfnDevice::getSQLCoreStatement() const
{
    static const string sqlCore =
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


const std::string RfnDevice::ConfigPart::all              = "all";
const std::string RfnDevice::ConfigPart::freezeday        = "freezeday";
const std::string RfnDevice::ConfigPart::tou              = "tou";
const std::string RfnDevice::ConfigPart::voltageaveraging = "voltageaveraging";
const std::string RfnDevice::ConfigPart::ovuv             = "ovuv";
const std::string RfnDevice::ConfigPart::display          = "display";


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
    RfnIdentifier rfnId;

    rdr["SerialNumber"] >> rfnId.serialNumber;
    rdr["Manufacturer"] >> rfnId.manufacturer;
    rdr["Model"]        >> rfnId.model;

    _rfnId = rfnId;

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
    if( parse.isKeyValid("install") )
    {
        return executeConfigInstall(pReq, parse, returnMsgs, rfnRequests, false);
    }
    if( parse.isKeyValid("tou") )
    {
        return executePutConfigTou(pReq, parse, returnMsgs, rfnRequests);
    }
    if( parse.isKeyValid("holiday_date0") || parse.isKeyValid("holiday_set_active") || parse.isKeyValid("holiday_cancel_active"))
    {
        return executePutConfigHoliday(pReq, parse, returnMsgs, rfnRequests);
    }
    if( parse.isKeyValid("voltage_profile") )
    {
        return executePutConfigVoltageProfile(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}


int RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("install") )
    {
        return executeConfigInstall(pReq, parse, returnMsgs, rfnRequests, true);
    }
    if( parse.isKeyValid("tou") )
    {
        return executeGetConfigTou(pReq, parse, returnMsgs, rfnRequests);
    }
    if( parse.isKeyValid("holiday") )
    {
        return executeGetConfigHoliday(pReq, parse, returnMsgs, rfnRequests);
    }
    if( parse.isKeyValid("voltage_profile") )
    {
        return executeGetConfigVoltageProfile(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}

/**
 * define in inherited device classes
 */
RfnDevice::ConfigMap RfnDevice::getConfigMethods(bool readOnly)
{
    return ConfigMap();
}

/**
 * Execute putconfig/getconfig Install
 */
int RfnDevice::executeConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, bool readOnly )
{
    boost::optional<std::string> configPart = parse.findStringForKey("installvalue");
    if( ! configPart )
    {
        return NoMethod;
    }

    const ConfigMap configMethods = getConfigMethods( readOnly );
    if( configMethods.empty() )
    {
        return NoMethod;
    }

    if( *configPart == ConfigPart::all )
    {
        for each( const ConfigMap::value_type & p in configMethods )
        {
            executeConfigInstallSingle( pReq, parse, returnMsgs, rfnRequests, p.first, p.second );
        }
    }
    else
    {
        boost::optional<ConfigMethod> configMethod = mapFind( configMethods, *configPart );
        if( ! configMethod )
        {
            return NoMethod;
        }

        executeConfigInstallSingle( pReq, parse, returnMsgs, rfnRequests, *configPart, *configMethod );
    }

    return NoError;
}

/**
 * Called by executeConfigInstall() to execute a putconfig/getconfig install for one config part
 */
void RfnDevice::executeConfigInstallSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, const std::string &configPart, const ConfigMethod &configMethod )
{
    int nRet = NoMethod;
    string error_description;

    try
    {
        nRet = configMethod(pReq, parse, returnMsgs, rfnRequests);
    }
    catch( const Commands::RfnCommand::CommandException &ce )
    {
        error_description = ce.error_description;
        nRet              = ce.error_code;
    }

    if( nRet != NoError )
    {
        string result;

        switch(nRet)
        {
            case NoConfigData:
            {
                result = "ERROR: Invalid config data. Config name:" + configPart;

                logInfo("had no configuration for config: " + configPart,
                        __FUNCTION__, __FILE__, __LINE__ );

                break;
            }
            case ConfigCurrent:
            {
                result = "Config " + configPart + " is current.";

                nRet = NoError; //This is an OK return! Note that nRet is no longer propogated!

                break;
            }
            case ConfigNotCurrent:
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

                logInfo("had a configuration error using config: " + configPart,
                        __FUNCTION__, __FILE__, __LINE__ );
            }
        }

        std::auto_ptr<CtiReturnMsg> retMsg(
                new CtiReturnMsg(
                        pReq->DeviceId(),
                        pReq->CommandString(),
                        result,
                        nRet,
                        0,
                        MacroOffset::none,
                        0,
                        pReq->GroupMessageId(),
                        pReq->UserMessageId()));

        returnMsgs.push_back( retMsg );
    }
}


int RfnDevice::executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.getFlags() & CMD_FLAG_GS_TOU )
    {
        return executeGetStatusTou(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}

int RfnDevice::executeGetStatusTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("freeze") )
    {
        return executeImmediateDemandFreeze(pReq, parse, returnMsgs, rfnRequests);
    }
    if( parse.isKeyValid("tou_critical_peak") )
    {
        return executeTouCriticalPeak(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}

int RfnDevice::executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValueVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("voltage_profile") )
    {
        return executeGetValueVoltageProfile(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}

int RfnDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("reset") && parse.isKeyValid("tou") )
    {
        if( parse.isKeyValid("tou_zero") )
        {
            return executePutValueTouResetZero(pReq, parse, returnMsgs, rfnRequests);
        }

        return executePutValueTouReset(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}


int RfnDevice::executePutValueTouReset(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutValueTouResetZero(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfigDisconnect( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    return NoError;
}

int RfnDevice::executePutConfigDisconnect( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    return NoError;
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
