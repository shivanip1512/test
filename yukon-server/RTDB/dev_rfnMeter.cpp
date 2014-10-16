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
#include <boost/assign/list_inserter.hpp>
#include <boost/assign/list_of.hpp>

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

std::map<std::string, ReadingMode> ReadingModeResolver = boost::assign::map_list_of
    ("INTERVAL", Interval)
    ("MIDNIGHT", Midnight)
    ("DISABLED", Disabled);

/**
 * Convert a set of metrics ids to a vector of ids for dynamic info
 * Note(1): will result in one metric per row in the database,
*  Note(2): the order is garanty by the metrics set in argument
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


YukonError_t RfnMeterDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
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

    return ClientErrors::NoMethod;
}


YukonError_t RfnMeterDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
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

    return ClientErrors::NoMethod;
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
            ( ConfigPart::channelconfig,    bindConfigMethod( &RfnMeterDevice::executeGetConfigInstallChannels,     this ) )
            ( ConfigPart::temperaturealarm, bindConfigMethod( &RfnMeterDevice::executeGetConfigTemperatureAlarm,    this ) )
                ;
    }
    else
    {
        boost::assign::insert( m )
            ( ConfigPart::channelconfig,    bindConfigMethod( &RfnMeterDevice::executePutConfigInstallChannels,     this ) )
            ( ConfigPart::temperaturealarm, bindConfigMethod( &RfnMeterDevice::executePutConfigTemperatureAlarm,    this ) )
                ;
    }

    return m;
}

/**
 * Execute putconfig/getconfig Install
 */
YukonError_t RfnMeterDevice::executeConfigInstall(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, bool readOnly )
{
    boost::optional<std::string> configPart = parse.findStringForKey("installvalue");
    if( ! configPart )
    {
        return ClientErrors::NoMethod;
    }

    const ConfigMap configMethods = getConfigMethods( readOnly );
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
                result = "ERROR: Invalid config data. Config name:" + configPart;

                CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - had no configuration for config: "<< configPart);

                break;
            }
            case ClientErrors::ConfigCurrent:
            {
                result = "Config " + configPart + " is current.";

                nRet = ClientErrors::None; //This is an OK return! Note that nRet is no longer propogated!

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

    typedef Commands::RfnChannelConfigurationCommand::MetricIds MetricIds;
    typedef std::vector<unsigned long> PaoMetricIds;

    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();
        if( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
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
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                rfnRequests.push_back(
                        boost::make_shared<RfnSetChannelSelectionCommand>(
                                midnightMetrics ));
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
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }

                rfnRequests.push_back( boost::make_shared<Commands::RfnChannelIntervalRecording::SetConfigurationCommand>(
                        intervalMetrics,
                        cfgRecordingIntervalSeconds,
                        cfgReportingIntervalSeconds));
            }
        }

        if( ! parse.isKeyValid("force") && rfnRequests.size() == 0 )
        {
            return ClientErrors::ConfigCurrent;
        }

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::InvalidConfigData;
    }
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

    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if ( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
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
                return ClientErrors::ConfigNotCurrent;
            }

            rfnRequests.push_back( boost::make_shared<RfnSetTemperatureAlarmConfigurationCommand>( configuration ) );
        }

        if ( ! parse.isKeyValid("force") && rfnRequests.empty() )
        {
            return ClientErrors::ConfigCurrent;
        }

        return ClientErrors::None;
    }
    catch ( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
    catch ( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::InvalidConfigData;
    }
}

YukonError_t RfnMeterDevice::executeGetConfigTemperatureAlarm( CtiRequestMsg * pReq, CtiCommandParser & parse, ReturnMsgList & returnMsgs, RfnCommandList & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetTemperatureAlarmConfigurationCommand>() );

    return ClientErrors::None;
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
