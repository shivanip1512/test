#include "precompiled.h"

#include "dev_rfnMeter.h"
#include "tbl_rfnidentifier.h"
#include "config_data_rfn.h"
#include "config_helpers.h"
#include "std_helper.h"

#include <boost/make_shared.hpp>
#include <boost/assign/list_inserter.hpp>

#include <cmath>

namespace Cti {
namespace Devices {

const std::string RfnMeterDevice::ConfigPart::all              = "all";
const std::string RfnMeterDevice::ConfigPart::freezeday        = "freezeday";
const std::string RfnMeterDevice::ConfigPart::tou              = "tou";
const std::string RfnMeterDevice::ConfigPart::voltageaveraging = "voltageaveraging";
const std::string RfnMeterDevice::ConfigPart::ovuv             = "ovuv";
const std::string RfnMeterDevice::ConfigPart::display          = "display";
const std::string RfnMeterDevice::ConfigPart::disconnect       = "disconnect";
const std::string RfnMeterDevice::ConfigPart::temperaturealarm = "temperaturealarm";
const std::string RfnMeterDevice::ConfigPart::channelconfig    = "channelconfig";


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
    ConfigMap m;

    if ( readOnly )
    {
        boost::assign::insert( m )
            ( ConfigPart::temperaturealarm, bindConfigMethod( &RfnMeterDevice::executeGetConfigTemperatureAlarm,    this ) )
                ;
    }
    else
    {
        boost::assign::insert( m )
            ( ConfigPart::temperaturealarm, bindConfigMethod( &RfnMeterDevice::executePutConfigTemperatureAlarm,    this ) )
                ;
    }

    return m;
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

int RfnMeterDevice::executePutConfigTemperatureAlarm( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    using Commands::RfnTemperatureAlarmCommand;
    using Commands::RfnSetTemperatureAlarmConfigurationCommand;

    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if ( ! deviceConfig )
        {
            return NoConfigData;
        }

        if ( hasDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmUnsupported ) )
        {
            return ConfigCurrent;
        }

        RfnTemperatureAlarmCommand::AlarmConfiguration  configuration;

        configuration.alarmEnabled        = getConfigData<bool>( deviceConfig, Config::RfnStrings::TemperatureAlarmEnabled );
        configuration.alarmRepeatInterval = getConfigData<unsigned>( deviceConfig, Config::RfnStrings::TemperatureAlarmRepeatInterval );
        configuration.alarmRepeatCount    = getConfigData<unsigned>( deviceConfig, Config::RfnStrings::TemperatureAlarmRepeatCount );
        long threshold                    = getConfigData<long>( deviceConfig, Config::RfnStrings::TemperatureAlarmHighTempThreshold );

        // Convert device configuration threshold value from degrees F to degrees C rounding down

        configuration.alarmHighTempThreshold = static_cast<int>( std::floor( ( threshold - 32 ) * 5 / 9.0 ) );

        const boost::optional<bool>     paoAlarmEnabled        = findDynamicInfo<bool>( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmIsEnabled );
        const boost::optional<unsigned> paoAlarmRepeatInterval = findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatInterval );
        const boost::optional<unsigned> paoAlarmRepeatCount    = findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatCount );
        const boost::optional<int>      paoAlarmThreshold      = findDynamicInfo<int>( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmHighTempThreshold );


        if (    configuration.alarmEnabled           != paoAlarmEnabled
             || configuration.alarmRepeatInterval    != paoAlarmRepeatInterval
             || configuration.alarmRepeatCount       != paoAlarmRepeatCount
             || configuration.alarmHighTempThreshold != paoAlarmThreshold
             || parse.isKeyValid("force"))
        {
            if ( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }

            rfnRequests.push_back( boost::make_shared<RfnSetTemperatureAlarmConfigurationCommand>( configuration ) );
        }

        if ( ! parse.isKeyValid("force") && rfnRequests.empty() )
        {
            return ConfigCurrent;
        }

        return NoError;
    }
    catch ( const MissingConfigDataException &e )
    {
        logInfo( e.what(),
                __FUNCTION__, __FILE__, __LINE__ );

        return NoConfigData;
    }
    catch ( const InvalidConfigDataException &e )
    {
        logInfo( e.what(),
                __FUNCTION__, __FILE__, __LINE__ );

        return ErrorInvalidConfigData;
    }
}

int RfnMeterDevice::executeGetConfigTemperatureAlarm( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetTemperatureAlarmConfigurationCommand>() );

    return NoError;
}

void RfnMeterDevice::handleCommandResult( const Commands::RfnSetTemperatureAlarmConfigurationCommand & cmd )
{
    const bool unsupported = ! cmd.isSupported();

    if ( unsupported )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TempAlarmUnsupported, true );
    }
}

void RfnMeterDevice::handleCommandResult( const Commands::RfnGetTemperatureAlarmConfigurationCommand & cmd )
{
    const bool unsupported = ! cmd.isSupported();

    if ( unsupported )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TempAlarmUnsupported, true );
    }
    else
    {
        Commands::RfnTemperatureAlarmCommand::AlarmConfiguration    configuration = cmd.getAlarmConfiguration();

        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmIsEnabled,         configuration.alarmEnabled );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatInterval,    configuration.alarmRepeatInterval );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatCount,       configuration.alarmRepeatCount );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmHighTempThreshold, configuration.alarmHighTempThreshold );
    }
}

}
}
