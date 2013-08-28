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
        return executePutConfigInstall(pReq, parse, retList, rfnRequests);
    }

    return NoMethod;
}


int RfnDevice::executePutConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    const boost::optional<std::string> installValue = parse.findStringForKey("installvalue");

    if( ! installValue )
    {
        return NoMethod;
    }

    if( *installValue == "all" )
    {
        return NoMethod;  // executePutConfigInstallAll(pReq, parse, retList, rfnRequests);
    }

    typedef Commands::RfnCommandSPtr (RfnDevice::*PutConfigInstallMethod)(CtiRequestMsg *pReq, CtiCommandParser &parse);

    typedef std::map<std::string, PutConfigInstallMethod> PutConfigLookup;

    const PutConfigLookup putConfigInstallMethods = boost::assign::map_list_of
        ("display",          &RfnDevice::executePutConfigInstallDisplay)
        ("freezeday",        &RfnDevice::executePutConfigInstallFreezeDay)
        ("tou",              &RfnDevice::executePutConfigInstallTou)
        ("voltageaveraging", &RfnDevice::executePutConfigInstallVoltageAveragingInterval);

    if( boost::optional<PutConfigInstallMethod> putConfigInstallMethod = mapFind(putConfigInstallMethods, *installValue) )
    {
        rfnRequests.push_back(
           (this->**putConfigInstallMethod)(pReq, parse));
    }

    return rfnRequests.empty()
        ? NoMethod
        : NoError;
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


Commands::RfnCommandSPtr RfnDevice::executePutConfigInstallFreezeDay(CtiRequestMsg *pReq, CtiCommandParser &parse)
{
    return Commands::RfnCommandSPtr();
}

Commands::RfnCommandSPtr RfnDevice::executePutConfigInstallVoltageAveragingInterval(CtiRequestMsg *pReq, CtiCommandParser &parse)
{
    return Commands::RfnCommandSPtr();
}

Commands::RfnCommandSPtr RfnDevice::executePutConfigInstallTou(CtiRequestMsg *pReq, CtiCommandParser &parse)
{
    return Commands::RfnCommandSPtr();
}

Commands::RfnCommandSPtr RfnDevice::executePutConfigInstallDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse)
{
    return Commands::RfnCommandSPtr();
}

int RfnDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnDevice::executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}


}
}
