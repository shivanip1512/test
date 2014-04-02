#include "precompiled.h"

#include "dev_rfnMeter.h"
#include "tbl_rfnidentifier.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Devices {


const std::string RfnMeterDevice::ConfigPart::all              = "all";
const std::string RfnMeterDevice::ConfigPart::freezeday        = "freezeday";
const std::string RfnMeterDevice::ConfigPart::tou              = "tou";
const std::string RfnMeterDevice::ConfigPart::voltageaveraging = "voltageaveraging";
const std::string RfnMeterDevice::ConfigPart::ovuv             = "ovuv";
const std::string RfnMeterDevice::ConfigPart::display          = "display";
const std::string RfnMeterDevice::ConfigPart::disconnect       = "disconnect";


int RfnMeterDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
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


int RfnMeterDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
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
RfnMeterDevice::ConfigMap RfnMeterDevice::getConfigMethods(bool readOnly)
{
    return ConfigMap();
}

/**
 * Execute putconfig/getconfig Install
 */
int RfnMeterDevice::executeConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, bool readOnly )
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
void RfnMeterDevice::executeConfigInstallSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, const std::string &configPart, const ConfigMethod &configMethod )
{
    int nRet = NoMethod;
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

    if( nRet != NoError )
    {
        std::string result;

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


int RfnMeterDevice::executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.getFlags() & CMD_FLAG_GS_TOU )
    {
        return executeGetStatusTou(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}

int RfnMeterDevice::executeGetStatusTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
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

int RfnMeterDevice::executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executePutConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executeGetConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executeGetConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executePutConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executeGetValueVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("voltage_profile") )
    {
        return executeGetValueVoltageProfile(pReq, parse, returnMsgs, rfnRequests);
    }

    return NoMethod;
}

int RfnMeterDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
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


int RfnMeterDevice::executePutValueTouReset(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executePutValueTouResetZero(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnMeterDevice::executeGetConfigDisconnect( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    return NoMethod;
}

int RfnMeterDevice::executePutConfigDisconnect( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    return NoMethod;
}


}
}
