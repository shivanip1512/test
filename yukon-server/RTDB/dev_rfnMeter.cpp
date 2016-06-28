#include "precompiled.h"

#include "dev_rfnMeter.h"
#include "tbl_rfnidentifier.h"
#include "config_data_rfn.h"
#include "config_helpers.h"

#include "cmd_rfn_TemperatureAlarm.h"
#include "cmd_rfn_ChannelConfiguration.h"

#include "PointAttribute.h"
#include "MetricIdLookup.h"

#include "std_helper.h"

#include <boost/make_shared.hpp>

#include <cmath>

namespace Cti {
namespace Devices {

namespace { // anonymous namespace

enum ReadingMode
{
    Interval,
    Midnight,
    Disabled
};

std::map<std::string, ReadingMode> ReadingModeResolver {
    { "INTERVAL", Interval },
    { "MIDNIGHT", Midnight },
    { "DISABLED", Disabled }};

/**
 * Convert a set of metrics ids to a vector of ids for dynamic info
 * Note(1): will result in one metric per row in the database,
*  Note(2): the order is guaranteed by the metrics set in argument
 *
 * @param metrics set of ids
 * @return vector of ids (ordered) for dynamic info
 */
std::vector<unsigned long> makeMetricIdsDynamicInfo( const Commands::RfnChannelConfigurationCommand::MetricIds& metrics )
{
    return std::vector<unsigned long>( metrics.begin(), metrics.end() );
}

} // anonymous namespace

const std::string RfnMeterDevice::ConfigPart::all              = "all";
const std::string RfnMeterDevice::ConfigPart::freezeday        = "freezeday";
const std::string RfnMeterDevice::ConfigPart::tou              = "tou";
const std::string RfnMeterDevice::ConfigPart::voltageaveraging = "voltageaveraging";
const std::string RfnMeterDevice::ConfigPart::ovuv             = "ovuv";
const std::string RfnMeterDevice::ConfigPart::display          = "display";
const std::string RfnMeterDevice::ConfigPart::disconnect       = "disconnect";
const std::string RfnMeterDevice::ConfigPart::temperaturealarm = "temperaturealarm";
const std::string RfnMeterDevice::ConfigPart::channelconfig    = "channelconfig";
const std::string RfnMeterDevice::ConfigPart::voltageprofile   = "voltageprofile";


YukonError_t RfnMeterDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("install") )
    {
        return executeConfigInstall(pReq, parse, returnMsgs, rfnRequests, InstallType::PutConfig);
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

    return ClientErrors::NoMethod;
}


YukonError_t RfnMeterDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("install") )
    {
        return executeConfigInstall(pReq, parse, returnMsgs, rfnRequests, InstallType::GetConfig);
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

    return ClientErrors::NoMethod;
}

/**
 * define in inherited device classes
 */
RfnMeterDevice::ConfigMap RfnMeterDevice::getConfigMethods(InstallType installType)
{
    ConfigMap m;

    if( installType == InstallType::GetConfig )
    {
        m.emplace(ConfigPart::channelconfig,    bindConfigMethod( &RfnMeterDevice::executeGetConfigInstallChannels,     this ) );
        m.emplace(ConfigPart::temperaturealarm, bindConfigMethod( &RfnMeterDevice::executeGetConfigTemperatureAlarm,    this ) );
    }
    else
    {
        m.emplace(ConfigPart::channelconfig,    bindConfigMethod( &RfnMeterDevice::executePutConfigInstallChannels,     this ) );
        m.emplace(ConfigPart::temperaturealarm, bindConfigMethod( &RfnMeterDevice::executePutConfigTemperatureAlarm,    this ) );
    }

    return m;
}

/**
 * Execute putconfig/getconfig Install
 */
YukonError_t RfnMeterDevice::executeConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, InstallType installType )
{
    boost::optional<std::string> configPart = parse.findStringForKey("installvalue");
    if( ! configPart )
    {
        return ClientErrors::NoMethod;
    }

    const ConfigMap configMethods = getConfigMethods( installType );
    if( configMethods.empty() )
    {
        return ClientErrors::NoMethod;
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
            return ClientErrors::NoMethod;
        }

        executeConfigInstallSingle( pReq, parse, returnMsgs, rfnRequests, *configPart, *configMethod );
    }

    return ClientErrors::None;
}

/**
 * Called by executeConfigInstall() to execute a putconfig/getconfig install for one config part
 */
void RfnMeterDevice::executeConfigInstallSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, const std::string &configPart, const ConfigMethod &configMethod )
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


YukonError_t RfnMeterDevice::executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.getFlags() & CMD_FLAG_GS_TOU )
    {
        return executeGetStatusTou(pReq, parse, returnMsgs, rfnRequests);
    }

    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeGetStatusTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("freeze") )
    {
        return executeImmediateDemandFreeze(pReq, parse, returnMsgs, rfnRequests);
    }
    if( parse.isKeyValid("tou_critical_peak") )
    {
        return executeTouCriticalPeak(pReq, parse, returnMsgs, rfnRequests);
    }

    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeGetConfigTou(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeGetConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutConfigVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeGetValueVoltageProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("voltage_profile") )
    {
        return executeGetValueVoltageProfile(pReq, parse, returnMsgs, rfnRequests);
    }

    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    if( parse.isKeyValid("reset") && parse.isKeyValid("tou") )
    {
        if( parse.isKeyValid("tou_zero") )
        {
            return executePutValueTouResetZero(pReq, parse, returnMsgs, rfnRequests);
        }

        return executePutValueTouReset(pReq, parse, returnMsgs, rfnRequests);
    }

    return ClientErrors::NoMethod;
}


YukonError_t RfnMeterDevice::executePutValueTouReset(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutValueTouResetZero(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executeGetConfigDisconnect( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutConfigDisconnect( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    return ClientErrors::NoMethod;
}

YukonError_t RfnMeterDevice::executePutConfigInstallChannels( CtiRequestMsg    * pReq,
                                                              CtiCommandParser & parse,
                                                              ReturnMsgList    & returnMsgs,
                                                              RfnCommandList   & rfnRequests )
{
    using Commands::RfnChannelConfigurationCommand;
    using Commands::RfnSetChannelSelectionCommand;

    YukonError_t ret = ClientErrors::ConfigCurrent;
    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();
        if( ! deviceConfig )
        {
            return reportConfigErrorDetails( ClientErrors::NoConfigData, "Device \"" + getName() + "\"", pReq, returnMsgs );
        }

        std::set<unsigned> midnightMetrics;
        std::set<unsigned> intervalMetrics;

        {
            struct AttributeAndMode
            {
                AttributeAndMode( const Config::DeviceConfig::ItemsByName &src ) :
                    attributeName(getConfigData(src, Config::RfnStrings::ChannelConfiguration::EnabledChannels::Attribute)),
                    readingMode  (getConfigData(src, Config::RfnStrings::ChannelConfiguration::EnabledChannels::Read))
                {
                }

                std::string attributeName;
                std::string readingMode;
            };

            // channel selection configuration data
            const std::vector<AttributeAndMode> cfgChannelAttributes =
                    getConfigDataVector<AttributeAndMode>( deviceConfig, Config::RfnStrings::ChannelConfiguration::EnabledChannels_Prefix );

            for each( const AttributeAndMode &channel in cfgChannelAttributes )
            {
                ReadingMode mode =
                    resolveConfigData(
                            ReadingModeResolver,
                            channel.readingMode,
                            Config::RfnStrings::ChannelConfiguration::EnabledChannels::Read);

                const Attribute &attr = Attribute::Lookup(channel.attributeName);

                if( attr == Attribute::Unknown )
                {
                    throw InvalidConfigDataException(
                            Config::RfnStrings::ChannelConfiguration::EnabledChannels::Attribute,
                            "Invalid attribute name \"" + channel.attributeName + "\"");
                }

                MetricIdLookup::MetricId metric =
                        MetricIdLookup::GetForAttribute(attr);

                switch( mode )
                {
                    //  TODO: throw on duplicate insert
                    case Interval:   intervalMetrics.insert(metric);
                    case Midnight:   midnightMetrics.insert(metric);
                    //  case Disabled:
                }
            }
        }

        {
            const PaoMetricIds cfgMidnightMetrics = makeMetricIdsDynamicInfo( midnightMetrics );

            boost::optional<PaoMetricIds> paoMidnightMetrics = findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics );

            if( cfgMidnightMetrics != paoMidnightMetrics || parse.isKeyValid("force") )
            {
                if( parse.isKeyValid( "verify" ) )
                {
                    ret = compareChannels(pReq, parse, returnMsgs, "Midnight", cfgMidnightMetrics, paoMidnightMetrics);
                }
                else
                {
                    rfnRequests.push_back(
                        boost::make_shared<RfnSetChannelSelectionCommand>( midnightMetrics ) );
                }
            }
        }

        {
            // channel recording interval configuration data
            const PaoMetricIds cfgIntervalMetrics = makeMetricIdsDynamicInfo( intervalMetrics );

            const unsigned cfgRecordingIntervalMinutes = getConfigData<unsigned>( deviceConfig, Config::RfnStrings::ChannelConfiguration::RecordingIntervalMinutes );
            const unsigned cfgRecordingIntervalSeconds = cfgRecordingIntervalMinutes * 60;
            const unsigned cfgReportingIntervalMinutes = getConfigData<unsigned>( deviceConfig, Config::RfnStrings::ChannelConfiguration::ReportingIntervalMinutes );
            const unsigned cfgReportingIntervalSeconds = cfgReportingIntervalMinutes * 60;

            // channel recording interval pao dynamic info
            const boost::optional<PaoMetricIds> paoIntervalMetrics = findDynamicInfo<unsigned long>( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics );
            const boost::optional<unsigned>     paoRecordingIntervalSeconds = findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds );
            const boost::optional<unsigned>     paoReportingIntervalSeconds = findDynamicInfo<unsigned>( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds );

            if( cfgIntervalMetrics != paoIntervalMetrics ||
                cfgRecordingIntervalSeconds != paoRecordingIntervalSeconds ||
                cfgReportingIntervalSeconds != paoReportingIntervalSeconds ||
                parse.isKeyValid("force") )
            {
                if( parse.isKeyValid( "verify" ) )
                {
                    ret = compareChannels(pReq, parse, returnMsgs, "Interval", cfgIntervalMetrics, paoIntervalMetrics);

                    if (cfgReportingIntervalSeconds != paoReportingIntervalSeconds)
                    {
                        reportConfigMismatchDetails<unsigned>("Channel Recording Interval (sec)",
                            cfgRecordingIntervalSeconds, paoRecordingIntervalSeconds,
                            pReq, returnMsgs);
                    }

                    if (cfgReportingIntervalSeconds != paoReportingIntervalSeconds)
                    {
                        reportConfigMismatchDetails<unsigned>("Channel Reporting Interval (sec)",
                            cfgReportingIntervalSeconds, paoReportingIntervalSeconds,
                            pReq, returnMsgs);
                    }
                    ret = ClientErrors::ConfigNotCurrent;
                }
                else
                {
                    rfnRequests.push_back( boost::make_shared<Commands::RfnChannelIntervalRecording::SetConfigurationCommand>(
                        intervalMetrics,
                        cfgRecordingIntervalSeconds,
                        cfgReportingIntervalSeconds ) );
                    ret = ClientErrors::None;
                }
            }
        }

        return ret;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
}

/**
  * Compare the channel configuration between the PAO config and what is reported by the meter.
  *
  * At this point we know that the set's are not the same.
  *
  * If there are no PAO metrics, we simple state that it's empty.
  * If the set of configuration channels contains more than meter channels, we suggest that 
  *   they have the wrong config.
  * If the set of meter channels contain more than the configuration, thel them there is a 
  *   mismatch and say which channels.
  * If the sets are totally or partially disjoint, then we just tell them what we have and 
  *   let them figure it out.
  * 
  */
YukonError_t RfnMeterDevice::compareChannels(
    CtiRequestMsg    * pReq,
    CtiCommandParser & parse,
    ReturnMsgList    & returnMsgs,
    std::string prefix,
    const PaoMetricIds &cfgMetrics,
    const boost::optional<PaoMetricIds> &paoMetrics)
{
    if (paoMetrics)
    {
        /* If we have device/config channels mismatched */

        auto metric_to_string = [](const MetricIdLookup::MetricId i) { return MetricIdLookup::getName(i); };

        PaoMetricIds cfgOnly, meterOnly;

        std::set_difference(cfgMetrics.begin(), cfgMetrics.end(),
            paoMetrics.get().begin(), paoMetrics.get().end(),
            std::inserter(cfgOnly, cfgOnly.begin()));

        std::set_difference(paoMetrics.get().begin(), paoMetrics.get().end(),
            cfgMetrics.begin(), cfgMetrics.end(),
            std::inserter(meterOnly, meterOnly.begin()));

        if (cfgOnly.size() != 0 && meterOnly.size() == 0)
        {
            /* Configuration has channels that the meter does not.
                * It may be that the meter is not configured for them.
                */
            if (std::includes(cfgMetrics.begin(), cfgMetrics.end(),
                paoMetrics.get().begin(), paoMetrics.get().end()))
            {
                std::string msg;

                msg = prefix + " channel config possibly not supported by meter.  "
                    "Meter is missing " + join(cfgOnly | transformed(metric_to_string), ", ");
                reportConfigDetails(msg, pReq, returnMsgs);

                msg = "Config: " + join(cfgMetrics | transformed(metric_to_string), ", ");
                reportConfigDetails(msg, pReq, returnMsgs);

                msg = "Meter: " + join(paoMetrics.get() | transformed(metric_to_string), ", ");
                reportConfigDetails(msg, pReq, returnMsgs);
            }
        }
        else if ((cfgOnly.size() == 0 && meterOnly.size() != 0))
        {
            /* The meter has channels the configuration does not. */
            std::string msg;

            msg = prefix + " channel program mismatch.  "
                "Meter also contains " + join(meterOnly | transformed(metric_to_string), ", ");
            reportConfigDetails(msg, pReq, returnMsgs);

            msg = "Config: " + join(cfgMetrics | transformed(metric_to_string), ", ");
            reportConfigDetails(msg, pReq, returnMsgs);

            msg = "Meter: " + join(paoMetrics.get() | transformed(metric_to_string), ", ");
            reportConfigDetails(msg, pReq, returnMsgs);
        }
        else if ((cfgOnly.size() != 0 && meterOnly.size() != 0))
        {
            /* The meter is a total or partial (disjoint) mismatch */
            std::string msg;
            msg = prefix + " channel program mismatch.";
            reportConfigDetails(msg, pReq, returnMsgs);

            msg = "Config: " + join(cfgMetrics | transformed(metric_to_string), ", ");
            reportConfigDetails(msg, pReq, returnMsgs);

            msg = "Meter: " + join(paoMetrics.get() | transformed(metric_to_string), ", ");
            reportConfigDetails(msg, pReq, returnMsgs);
        }
        return ClientErrors::ConfigNotCurrent;
    }
    std::string msg(prefix + " channel program mismatch.  Meter data is Empty.");

    reportConfigDetails(msg, pReq, returnMsgs);
    return ClientErrors::ConfigNotCurrent;
}

YukonError_t RfnMeterDevice::executeGetConfigInstallChannels( CtiRequestMsg    * pReq,
                                                              CtiCommandParser & parse,
                                                              ReturnMsgList    & returnMsgs,
                                                              RfnCommandList   & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetChannelSelectionFullDescriptionCommand>() );
    rfnRequests.push_back( boost::make_shared<Commands::RfnChannelIntervalRecording::GetActiveConfigurationCommand>() );

    return ClientErrors::None;
}

YukonError_t RfnMeterDevice::executePutConfigTemperatureAlarm( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    using Commands::RfnTemperatureAlarmCommand;
    using Commands::RfnSetTemperatureAlarmConfigurationCommand;

    YukonError_t ret = ClientErrors::ConfigCurrent;
    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if ( ! deviceConfig )
        {
            return reportConfigErrorDetails( ClientErrors::NoConfigData, "Device \"" + getName() + "\"", pReq, returnMsgs );
        }

        if ( hasDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmUnsupported ) )
        {
            return ClientErrors::ConfigCurrent;
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
                reportConfigMismatchDetails<>( "Temperature Alarm Enabled",
                    configuration.alarmEnabled, paoAlarmEnabled,
                    pReq, returnMsgs );

                reportConfigMismatchDetails<>( "Temperature Alarm Repeat Interval",
                    configuration.alarmRepeatInterval, paoAlarmRepeatInterval,
                    pReq, returnMsgs );

                reportConfigMismatchDetails<>( "Temperature Alarm Repeat Count",
                    configuration.alarmRepeatCount, paoAlarmRepeatCount,
                    pReq, returnMsgs );

                reportConfigMismatchDetails<>( "High Temperature Threshold",
                    configuration.alarmHighTempThreshold, paoAlarmThreshold,
                    pReq, returnMsgs );

                ret = ClientErrors::ConfigNotCurrent;
            }
            else
            {
                rfnRequests.push_back( boost::make_shared<RfnSetTemperatureAlarmConfigurationCommand>( configuration ) );
                ret = ClientErrors::None;
            }
        }

        return ret;
    }
    catch ( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
    catch ( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
}

YukonError_t RfnMeterDevice::executeGetConfigTemperatureAlarm( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetTemperatureAlarmConfigurationCommand>() );

    return ClientErrors::None;
}

void RfnMeterDevice::handleCommandResult( const Commands::RfnTemperatureAlarmCommand & cmd )
{
    const bool unsupported = ! cmd.isSupported();

    if ( unsupported )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TempAlarmUnsupported, true );
    }
    else if ( const auto configuration = cmd.getAlarmConfiguration() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmIsEnabled,         configuration->alarmEnabled );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatInterval,    configuration->alarmRepeatInterval );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmRepeatCount,       configuration->alarmRepeatCount );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TempAlarmHighTempThreshold, configuration->alarmHighTempThreshold );
    }
}

void RfnMeterDevice::handleCommandResult( const Commands::RfnChannelSelectionCommand & cmd )
{
    std::vector<unsigned long> paoMetrics = makeMetricIdsDynamicInfo( cmd.getMetricsReceived() );

    setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_MidnightMetrics, paoMetrics );
}

void RfnMeterDevice::handleCommandResult( const Commands::RfnChannelIntervalRecording::GetConfigurationCommand & cmd )
{
    //  Don't do anything with this yet - this command returns the filter, not the enabled channels!
    /*
    std::vector<unsigned long> paoMetrics = makeMetricIdsDynamicInfo( cmd.getMetricsReceived() );

    setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoMetrics );
    */
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, cmd.getIntervalRecordingSecondsReceived() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, cmd.getIntervalReportingSecondsReceived() );
}

void RfnMeterDevice::handleCommandResult( const Commands::RfnChannelIntervalRecording::GetActiveConfigurationCommand & cmd )
{
    std::vector<unsigned long> paoMetrics = makeMetricIdsDynamicInfo( cmd.getMetricsReceived() );

    setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoMetrics );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, cmd.getIntervalRecordingSecondsReceived() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, cmd.getIntervalReportingSecondsReceived() );
}

void RfnMeterDevice::handleCommandResult( const Commands::RfnChannelIntervalRecording::SetConfigurationCommand & cmd )
{
    std::vector<unsigned long> paoMetrics = makeMetricIdsDynamicInfo( cmd.getMetricsReceived() );

    setDynamicInfo( CtiTableDynamicPaoInfoIndexed::Key_RFN_IntervalMetrics, paoMetrics );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_RecordingIntervalSeconds, cmd.getIntervalRecordingSeconds() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReportingIntervalSeconds, cmd.getIntervalReportingSeconds() );
}
}
}
