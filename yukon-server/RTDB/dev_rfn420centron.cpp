#include "precompiled.h"

#include "dev_rfn420centron.h"
#include "config_data_rfn.h"
#include "config_helpers.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/make_shared.hpp>

namespace Cti {
namespace Devices {

namespace {

const std::vector<CtiDeviceBase::PaoInfoKeys> displayMetricPaoKeys = boost::assign::list_of
    ( CtiTableDynamicPaoInfo::Key_DisplayItem01 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem02 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem03 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem04 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem05 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem06 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem07 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem08 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem09 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem10 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem11 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem12 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem13 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem14 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem15 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem16 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem17 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem18 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem19 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem20 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem21 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem22 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem23 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem24 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem25 )
    ( CtiTableDynamicPaoInfo::Key_DisplayItem26 );

const std::vector<std::string> displayMetricConfigKeys = boost::assign::list_of
    ( Config::RfnStrings::displayItem01 )
    ( Config::RfnStrings::displayItem02 )
    ( Config::RfnStrings::displayItem03 )
    ( Config::RfnStrings::displayItem04 )
    ( Config::RfnStrings::displayItem05 )
    ( Config::RfnStrings::displayItem06 )
    ( Config::RfnStrings::displayItem07 )
    ( Config::RfnStrings::displayItem08 )
    ( Config::RfnStrings::displayItem09 )
    ( Config::RfnStrings::displayItem10 )
    ( Config::RfnStrings::displayItem11 )
    ( Config::RfnStrings::displayItem12 )
    ( Config::RfnStrings::displayItem13 )
    ( Config::RfnStrings::displayItem14 )
    ( Config::RfnStrings::displayItem15 )
    ( Config::RfnStrings::displayItem16 )
    ( Config::RfnStrings::displayItem17 )
    ( Config::RfnStrings::displayItem18 )
    ( Config::RfnStrings::displayItem19 )
    ( Config::RfnStrings::displayItem20 )
    ( Config::RfnStrings::displayItem21 )
    ( Config::RfnStrings::displayItem22 )
    ( Config::RfnStrings::displayItem23 )
    ( Config::RfnStrings::displayItem24 )
    ( Config::RfnStrings::displayItem25 )
    ( Config::RfnStrings::displayItem26 );

const std::map<unsigned, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits> displayDigitLookup = boost::assign::map_list_of
    ( 4, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits4x1 )
    ( 5, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits5x1 )
    ( 6, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits6x1 );

}

RfnMeterDevice::ConfigMap Rfn420CentronDevice::getConfigMethods(bool readOnly)
{
    ConfigMap m = RfnResidentialDevice::getConfigMethods( readOnly );

    if( readOnly )
    {
        m.insert( ConfigMap::value_type( ConfigPart::display, bindConfigMethod( &Rfn420CentronDevice::executeGetConfigDisplay, this )));
    }
    else
    {
        m.insert( ConfigMap::value_type( ConfigPart::display, bindConfigMethod( &Rfn420CentronDevice::executePutConfigDisplay, this )));
    }

    return m;
}

YukonError_t Rfn420CentronDevice::executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    rfnRequests.push_back(
            boost::make_shared<Commands::RfnCentronGetLcdConfigurationCommand>());

    return ClientErrors::None;
}

YukonError_t Rfn420CentronDevice::executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
        }

        Commands::RfnCentronLcdConfigurationCommand::metric_vector_t config_display_metrics;

        std::vector<unsigned char> paoinfo_metrics;

        for each( const std::string configKey in displayMetricConfigKeys )
        {
            long configValue = getConfigData<long>(deviceConfig, configKey);

            if( configValue < 0x00 ||
                configValue > 0xff )
            {
                CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Invalid value ("<< configValue <<") for config key \""<< configKey <<"\"");

                return ClientErrors::InvalidConfigData;
            }

            config_display_metrics.push_back(configValue);
        }

        const long config_cycle_delay                 = getConfigData<long>(deviceConfig, Config::RfnStrings::LcdCycleTime);
        const long config_display_digits              = getConfigData<long>(deviceConfig, Config::RfnStrings::DisplayDigits);
        const bool config_disconnect_display_disabled = getConfigData<bool>(deviceConfig, Config::RfnStrings::DisconnectDisplayDisabled);

        if( config_cycle_delay < 0x00 ||
            config_cycle_delay > 0x0f )
        {
            CTILOG_ERROR(dout, "Device \"" << getName() << "\" - Invalid value (" << config_cycle_delay << ") for config key \"" << Config::RfnStrings::LcdCycleTime << "\"");

            return ClientErrors::InvalidConfigData;
        }

        if( config_display_digits < 4 ||
            config_display_digits > 6 )
        {
            CTILOG_ERROR(dout, "Device \"" << getName() << "\" - Invalid value (" << config_display_digits << ") for config key \"" << Config::RfnStrings::DisplayDigits << "\"");

            return ClientErrors::InvalidConfigData;
        }

        for each( const PaoInfoKeys paoKey in displayMetricPaoKeys )
        {
            long pao_value;

            if( ! getDynamicInfo(paoKey, pao_value) )
            {
                break;
            }

            paoinfo_metrics.push_back(pao_value);
        }

        bool configMatches = true;

        configMatches &= config_display_metrics.size() == paoinfo_metrics.size() && std::equal(config_display_metrics.begin(), config_display_metrics.end(), paoinfo_metrics.begin());
        configMatches &= config_display_digits              == getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits);
        configMatches &= config_disconnect_display_disabled == (getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled) == 1);
        configMatches &= config_cycle_delay                 == getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime);

        if( configMatches )
        {
            if( ! parse.isKeyValid("force") )
            {
                return ClientErrors::ConfigCurrent;
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }

        const boost::optional<Commands::RfnCentronLcdConfigurationCommand::DisplayDigits> displayDigits = mapFind(displayDigitLookup, config_display_digits);

        if( ! displayDigits )
        {
            CTILOG_ERROR(dout, "Device \"" << getName() << "\" - Invalid value (" << config_display_digits << ") for display digit lookup");

            return ClientErrors::InvalidConfigData;
        }

        const Commands::RfnCentronLcdConfigurationCommand::DisconnectDisplayState disconnectDisplay =
            config_disconnect_display_disabled
                ? Commands::RfnCentronLcdConfigurationCommand::DisconnectDisplayDisabled  //  note that the key is disconnect display DISABLED if the config value is TRUE
                : Commands::RfnCentronLcdConfigurationCommand::DisconnectDisplayEnabled;

        rfnRequests.push_back(
            boost::make_shared<Commands::RfnCentronSetLcdConfigurationCommand>(
                    config_display_metrics,
                    disconnectDisplay,
                    *displayDigits,
                    config_cycle_delay));

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
}


void Rfn420CentronDevice::handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd)
{
    typedef Commands::RfnCentronGetLcdConfigurationCommand::metric_vector_t metric_vector_t;

    std::vector<PaoInfoKeys>::const_iterator pao_itr = displayMetricPaoKeys.begin();
    metric_vector_t::const_iterator sent_itr = cmd.display_metrics.begin();

    while( sent_itr != cmd.display_metrics.end()
           && pao_itr != displayMetricPaoKeys.end() )
    {
        setDynamicInfo(*pao_itr++, *sent_itr++);
    }

    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime,                 cmd.cycle_time);
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled, cmd.disconnect_display == Commands::RfnCentronGetLcdConfigurationCommand::DisconnectDisplayDisabled);
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits,             cmd.display_digits);
}


void Rfn420CentronDevice::handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd)
{
    typedef Commands::RfnCentronGetLcdConfigurationCommand::metric_map_t metric_map_t;

    const metric_map_t received_metrics = cmd.getDisplayMetrics();

    for each( const metric_map_t::value_type &metric in received_metrics )
    {
        if( metric.first < displayMetricPaoKeys.size() )
        {
            setDynamicInfo(displayMetricPaoKeys[metric.first], metric.second);
        }
    }

    if( const boost::optional<bool> disconnectDisabled = cmd.getDisconnectDisplayDisabled() )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisconnectDisplayDisabled, *disconnectDisabled);
    }

    if( const boost::optional<unsigned char> displayDigits = cmd.getDigitConfiguration() )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits, *displayDigits);
    }

    if( const boost::optional<unsigned char> lcdCycleDelay = cmd.getLcdCycleTime() )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime, *lcdCycleDelay);
    }
}


}
}
