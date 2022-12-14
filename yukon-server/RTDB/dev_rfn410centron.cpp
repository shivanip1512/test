#include "precompiled.h"

#include "dev_rfn410centron.h"
#include "cmd_rfn_ConfigNotification.h"
#include "config_data_rfn.h"
#include "config_helpers.h"
#include "std_helper.h"

#include <boost/range/algorithm/transform.hpp>

namespace Cti {
namespace Devices {

namespace {

const std::vector<CtiDeviceBase::PaoInfoKeys> displayMetricPaoKeys {
        CtiTableDynamicPaoInfo::Key_DisplayItem01,
        CtiTableDynamicPaoInfo::Key_DisplayItem02,
        CtiTableDynamicPaoInfo::Key_DisplayItem03,
        CtiTableDynamicPaoInfo::Key_DisplayItem04,
        CtiTableDynamicPaoInfo::Key_DisplayItem05,
        CtiTableDynamicPaoInfo::Key_DisplayItem06,
        CtiTableDynamicPaoInfo::Key_DisplayItem07,
        CtiTableDynamicPaoInfo::Key_DisplayItem08,
        CtiTableDynamicPaoInfo::Key_DisplayItem09,
        CtiTableDynamicPaoInfo::Key_DisplayItem10,
        CtiTableDynamicPaoInfo::Key_DisplayItem11,
        CtiTableDynamicPaoInfo::Key_DisplayItem12,
        CtiTableDynamicPaoInfo::Key_DisplayItem13,
        CtiTableDynamicPaoInfo::Key_DisplayItem14,
        CtiTableDynamicPaoInfo::Key_DisplayItem15,
        CtiTableDynamicPaoInfo::Key_DisplayItem16,
        CtiTableDynamicPaoInfo::Key_DisplayItem17,
        CtiTableDynamicPaoInfo::Key_DisplayItem18,
        CtiTableDynamicPaoInfo::Key_DisplayItem19,
        CtiTableDynamicPaoInfo::Key_DisplayItem20,
        CtiTableDynamicPaoInfo::Key_DisplayItem21,
        CtiTableDynamicPaoInfo::Key_DisplayItem22,
        CtiTableDynamicPaoInfo::Key_DisplayItem23,
        CtiTableDynamicPaoInfo::Key_DisplayItem24,
        CtiTableDynamicPaoInfo::Key_DisplayItem25,
        CtiTableDynamicPaoInfo::Key_DisplayItem26 };

const std::vector<std::string> displayMetricConfigKeys {
        Config::RfnStrings::displayItem01,
        Config::RfnStrings::displayItem02,
        Config::RfnStrings::displayItem03,
        Config::RfnStrings::displayItem04,
        Config::RfnStrings::displayItem05,
        Config::RfnStrings::displayItem06,
        Config::RfnStrings::displayItem07,
        Config::RfnStrings::displayItem08,
        Config::RfnStrings::displayItem09,
        Config::RfnStrings::displayItem10,
        Config::RfnStrings::displayItem11,
        Config::RfnStrings::displayItem12,
        Config::RfnStrings::displayItem13,
        Config::RfnStrings::displayItem14,
        Config::RfnStrings::displayItem15,
        Config::RfnStrings::displayItem16,
        Config::RfnStrings::displayItem17,
        Config::RfnStrings::displayItem18,
        Config::RfnStrings::displayItem19,
        Config::RfnStrings::displayItem20,
        Config::RfnStrings::displayItem21,
        Config::RfnStrings::displayItem22,
        Config::RfnStrings::displayItem23,
        Config::RfnStrings::displayItem24,
        Config::RfnStrings::displayItem25,
        Config::RfnStrings::displayItem26 };

const std::map<long, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits> displayDigitLookup {
    { 4, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits4x1 },
    { 5, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits5x1 },
    { 6, Commands::RfnCentronLcdConfigurationCommand::DisplayDigits6x1 } };

}


RfnMeterDevice::ConfigMap Rfn410CentronDevice::getConfigMethods(InstallType installType)
{
    ConfigMap m = RfnResidentialVoltageDevice::getConfigMethods( installType );

    if( installType == InstallType::GetConfig )
    {
        m.emplace(ConfigPart::display, bindConfigMethod( &Rfn410CentronDevice::executeGetConfigDisplay, this ));
    }
    else
    {
        m.emplace(ConfigPart::display, bindConfigMethod( &Rfn410CentronDevice::executePutConfigDisplay, this ));
    }

    return m;
}

YukonError_t Rfn410CentronDevice::executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    rfnRequests.push_back(
            std::make_unique<Commands::RfnCentronGetLcdConfigurationCommand>());

    return ClientErrors::None;
}


auto Rfn410CentronDevice::getDisplayConfigItems(const Config::DeviceConfigSPtr &deviceConfig) -> c1sx_display
{
    c1sx_display config;

    boost::range::transform(
            displayMetricConfigKeys,
            std::back_inserter(config.display_metrics),
            [&](const std::string &configKey) {
                return getConfigDataEnum(deviceConfig, configKey, Cti::Config::displayItemMap);
            });

    config.cycle_delay    = getConfigData<uint8_t, 0x00, 0x0f>(deviceConfig, Config::RfnStrings::LcdCycleTime);
    config.display_digits = getConfigDataEnum(deviceConfig, Config::RfnStrings::DisplayDigits, displayDigitLookup);

    return config;
}


bool Rfn410CentronDevice::isDisplayConfigCurrent(const c1sx_display &config)
{
    std::vector<long> paoinfo_metrics;

    for( auto paoKey : displayMetricPaoKeys )
    {
        if( const auto pao_value = findDynamicInfo<long>(paoKey) )
        {
            paoinfo_metrics.push_back(*pao_value);
        }
        else
        {
            break;
        }
    }

    if( boost::equal(config.display_metrics, paoinfo_metrics) )
    {
        if( config.cycle_delay == getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime) )
        {
            if( const auto paoDigits = mapFind(displayDigitLookup, getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits)) )
            {
                return *paoDigits == config.display_digits;
            }
        }
    }

    return false;
}


YukonError_t Rfn410CentronDevice::executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)
{
    try
    {
        auto deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return reportConfigErrorDetails( ClientErrors::NoConfigData, "Device \"" + getName() + "\"", pReq, returnMsgs );
        }

        const auto configItems = getDisplayConfigItems(deviceConfig);

        if( isDisplayConfigCurrent(configItems) )
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

        rfnRequests.push_back(
                std::make_unique<Commands::RfnCentronSetLcdConfigurationCommand>(
                        configItems.display_metrics,
                        configItems.display_digits,
                        configItems.cycle_delay));

        return ClientErrors::None;
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \"" << getName() << "\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
}


void Rfn410CentronDevice::handleCommandResult(const Commands::RfnConfigNotificationCommand & cmd)
{
    RfnResidentialVoltageDevice::handleCommandResult(cmd);

    if( cmd.c2sxDisplay )
    {
        storeLcdConfig(*cmd.c2sxDisplay);
    }
}


void Rfn410CentronDevice::handleCommandResult(const Commands::RfnCentronSetLcdConfigurationCommand &cmd)
{
    typedef Commands::RfnCentronGetLcdConfigurationCommand::metric_vector_t metric_vector_t;

    std::vector<PaoInfoKeys>::const_iterator pao_itr = displayMetricPaoKeys.begin();
    metric_vector_t::const_iterator sent_itr = cmd.display_metrics.begin();

    while( sent_itr != cmd.display_metrics.end()
           && pao_itr != displayMetricPaoKeys.end() )
    {
        setDynamicInfo(*pao_itr++, *sent_itr++);
    }

    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime,     cmd.cycle_time);
    setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_LcdDisplayDigits, cmd.display_digits);
}


void Rfn410CentronDevice::handleCommandResult(const Commands::RfnCentronGetLcdConfigurationCommand &cmd)
{
    storeLcdConfig(cmd);
}


void Rfn410CentronDevice::storeLcdConfig(const Commands::RfnCentronLcdConfigurationCommand::Read &cmd)
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
