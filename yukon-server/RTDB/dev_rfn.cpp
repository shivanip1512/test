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
            "JOIN Device DV ON YP.YukonPaObjectId = DV.DeviceID "
            "JOIN RFNAddress RFN on DV.DeviceId = RFN.DeviceID "
         "WHERE "
            "1=1 ";  //  Unfortunately, it seems that our selectors require a WHERE clause.

    return sqlCore;
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


int RfnDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    typedef int (RfnDevice::*RfnExecuteMethod)(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    typedef std::map<int, RfnExecuteMethod> ExecuteLookup;

    const ExecuteLookup executeMethods = boost::assign::map_list_of
        (PutConfigRequest, &RfnDevice::executePutConfig)
        (GetConfigRequest, &RfnDevice::executeGetConfig)
        (GetValueRequest,  &RfnDevice::executeGetValue)
        (PutStatusRequest, &RfnDevice::executePutStatus);

    boost::optional<RfnExecuteMethod> executeMethod = mapFind(executeMethods, parse.getCommand());

    if( ! executeMethod )
    {
        return NoMethod;
    }

    try
    {
        return (this->**executeMethod)(pReq, parse, retList, rfnRequests);
    }
    catch( const Commands::RfnCommand::CommandException &ce )
    {
        std::auto_ptr<CtiReturnMsg> commandExceptionError(
            new CtiReturnMsg(
                    pReq->DeviceId(),
                    pReq->CommandString(),
                    ce.error_description,
                    ce.error_code,
                    0,
                    0,
                    0,
                    pReq->GroupMessageId(),
                    pReq->UserMessageId()));

        retList.push_back(commandExceptionError.release());

        return ExecutionComplete;
    }
}

int RfnDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("install") )
    {
        return executeConfigInstall(pReq, parse, retList, rfnRequests, getPutConfigInstallMap());
    }
    if( parse.isKeyValid("tou") )
    {
        return executePutConfigTou(pReq, parse, retList, rfnRequests);
    }
    if( parse.isKeyValid("holiday_date0") || parse.isKeyValid("holiday_set_active") || parse.isKeyValid("holiday_cancel_active"))
    {
        return executePutConfigHoliday(pReq, parse, retList, rfnRequests);
    }
    if( parse.isKeyValid("voltage_profile") )
    {
        return executePutConfigVoltageProfile(pReq, parse, retList, rfnRequests);
    }

    return NoMethod;
}


int RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("install") )
    {
        return executeConfigInstall(pReq, parse, retList, rfnRequests, getGetConfigInstallMap());
    }
    if( parse.isKeyValid("tou") )
    {
        return executeGetConfigTou(pReq, parse, retList, rfnRequests);
    }
    if( parse.isKeyValid("holiday") )
    {
        return executeGetConfigHoliday(pReq, parse, retList, rfnRequests);
    }
    if( parse.isKeyValid("voltage_profile") )
    {
        return executeGetConfigVoltageProfile(pReq, parse, retList, rfnRequests);
    }

    return NoMethod;
}


const RfnDevice::InstallMap RfnDevice::emptyMap;

/**
 * define in inherited device classes
 */
const RfnDevice::InstallMap & RfnDevice::getPutConfigInstallMap() const
{
    return emptyMap;
}

/**
 * define in inherited device classes
 */
const RfnDevice::InstallMap & RfnDevice::getGetConfigInstallMap() const
{
    return emptyMap;
}

/**
 * Execute putconfig/getconfig Install
 */
int RfnDevice::executeConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests, const InstallMap &installMap )
{
    boost::optional<std::string> installValue;

    if( installMap.empty() || ! (installValue = parse.findStringForKey("installvalue")) )
    {
        return NoMethod;
    }

    if( *installValue == "all" )
    {
        for each( const InstallMap::value_type & p in installMap )
        {
            executeConfigInstallSingle( pReq, parse, retList, rfnRequests, p.first, p.second );
        }
    }
    else
    {
        boost::optional<InstallMethod> installMethod = mapFind( installMap, *installValue );

        if( ! installMethod )
        {
            return NoMethod;
        }

        executeConfigInstallSingle( pReq, parse, retList, rfnRequests, *installValue, *installMethod );
    }

    // Set ExpectMore on all messages.
    // In the case of verify we need to let the Last expect more flag remain to false
    for( CtiMessageList::iterator itr = retList.begin(); itr != retList.end(); )
    {
        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>(*itr);

        if( ++itr != retList.end() || ! parse.isKeyValid("verify") )
        {
            retMsg->setExpectMore(true);
        }
    }

    return NoError;
}

/**
 * Called by executeConfigInstall() to execute a putconfig/getconfig install for one config part
 */
void RfnDevice::executeConfigInstallSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests, const string & installValue, InstallMethod installMethod )
{
    int nRet = NoMethod;
    string error_description;

    try
    {
        nRet = (this->*installMethod)(pReq, parse, retList, rfnRequests);
    }
    catch( const Commands::RfnCommand::CommandException &ce )
    {
        error_description = ce.error_description;
        nRet              = ce.error_code;
    }

    if( nRet != NoError )
    {
        string resultString;

        if( nRet == NoConfigData )
        {
            resultString = "ERROR: Invalid config data. Config name:" + installValue;

            logInfo("had no configuration for config: " + installValue,
                    __FUNCTION__, __FILE__, __LINE__ );
        }
        else if( nRet == ConfigCurrent )
        {
            resultString = "Config " + installValue + " is current.";

            nRet = NoError; //This is an OK return! Note that nRet is no longer propogated!
        }
        else if( nRet == ConfigNotCurrent )
        {
            resultString = "Config " + installValue + " is NOT current.";
        }
        else
        {
            if( error_description.empty() )
            {
                error_description = "NoMethod or invalid config";
            }

            resultString = "ERROR: " + error_description + ". Config name:" + installValue;

            logInfo("had a configuration error using config: " + installValue,
                    __FUNCTION__, __FILE__, __LINE__ );
        }

        std::auto_ptr<CtiReturnMsg> retMsg(
                new CtiReturnMsg(
                        pReq->DeviceId(),
                        pReq->CommandString(),
                        resultString,
                        nRet,
                        0,
                        0,
                        0,
                        pReq->GroupMessageId(),
                        pReq->UserMessageId()));

        retList.push_back( retMsg.release() );
    }
}


int RfnDevice::executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("freeze") )
    {
        return executeImmediateDemandFreeze(pReq, parse, retList, rfnRequests);
    }
    if( parse.isKeyValid("tou_critical_peak") )
    {
        return executeTouCriticalPeak(pReq, parse, retList, rfnRequests);
    }

    return NoMethod;
}

int RfnDevice::executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValueVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("voltage_profile") )
    {
        return executeGetValueVoltageProfile(pReq, parse, retList, rfnRequests);
    }

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
