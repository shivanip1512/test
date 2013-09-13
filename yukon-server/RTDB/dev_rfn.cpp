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
        return executeConfigInstall(pReq, parse, retList, rfnRequests, getPutConfigInstallMethods());
    }
    if( parse.isKeyValid("tou") )
    {
        return executePutConfigTou(pReq, parse, retList, rfnRequests);
    }
    if( parse.isKeyValid("holiday") )
    {
        return executePutConfigHoliday(pReq, parse, retList, rfnRequests);
    }

    return NoMethod;
}


int RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("install") )
    {
        return executeConfigInstall(pReq, parse, retList, rfnRequests, getGetConfigInstallMethods());
    }
    if( parse.isKeyValid("tou") )
    {
        return executeGetConfigTou(pReq, parse, retList, rfnRequests);
    }
    if( parse.isKeyValid("holiday") )
    {
        return executeGetConfigHoliday(pReq, parse, retList, rfnRequests);
    }

    return NoMethod;
}

/**
 *
 */
RfnDevice::InstallLookup RfnDevice::getPutConfigInstallMethods() const
{
    const InstallLookup methods = boost::assign::map_list_of
        ("display",          &RfnDevice::executePutConfigInstallDisplay)
        ("freezeday",        &RfnDevice::executePutConfigInstallFreezeDay)
        ("tou",              &RfnDevice::executePutConfigInstallTou)
        ("voltageaveraging", &RfnDevice::executePutConfigInstallVoltageAveragingInterval);

    return methods;
}

/**
 *
 */
RfnDevice::InstallLookup RfnDevice::getGetConfigInstallMethods() const
{
    const InstallLookup methods;

    // TODO

    return methods;
}

/**
 * Execute putconfig/getconfig Install
 */
int RfnDevice::executeConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests, const InstallLookup & lookup)
{
    const boost::optional<std::string> installValue = parse.findStringForKey("installvalue");

    if( ! installValue )
    {
        return NoMethod;
    }

    if( *installValue == "all" )
    {
        int nRet = NoMethod;

        for each( const std::pair<std::string, InstallMethod> & p in lookup )
        {
            nRet = (this->*p.second)(pReq, parse, retList, rfnRequests);

            if( nRet != NoError && nRet != ConfigCurrent )
            {
                // if we get an error or if the configuration is not current (in the case of putconfig install verify)
                return nRet;
            }
        }

        return nRet;
    }

    if( boost::optional<InstallMethod> func = mapFind( lookup, *installValue ))
    {
        return (this->**func)(pReq, parse, retList, rfnRequests);
    }

    return NoMethod;
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

int RfnDevice::executePutConfigInstallFreezeDay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigInstallVoltageAveragingInterval(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigInstallTou(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executePutConfigInstallDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}


}
}
