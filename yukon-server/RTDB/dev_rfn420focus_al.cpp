#include "precompiled.h"

#include "config_helpers.h"
#include "config_data_rfn.h"
#include "dev_rfn420focus_al.h"

#include "std_helper.h"

#include <boost/make_shared.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/bimap.hpp>

namespace Cti {
namespace Devices {

namespace {  // anonymous namespace

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
    ( CtiTableDynamicPaoInfo::Key_DisplayItem10 );

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
    ( Config::RfnStrings::displayItem10 );

typedef Commands::RfnFocusAlLcdConfigurationCommand LcdCommand;

typedef boost::bimap<std::string, LcdCommand::Metrics> StringsToMetrics;

const StringsToMetrics ConfigToCommand = boost::assign::list_of<StringsToMetrics::relation>
    ( "DELIVERED_KWH_4X1",  LcdCommand::deliveredKwh4x1 )
    ( "DELIVERED_KWH_5X1",  LcdCommand::deliveredKwh5x1 )
    ( "DELIVERED_KWH_6X1",  LcdCommand::deliveredKwh6x1 )

    ( "REVERSE_KWH_4X1",    LcdCommand::reverseKwh4x1 )
    ( "REVERSE_KWH_5X1",    LcdCommand::reverseKwh5x1 )
    ( "REVERSE_KWH_6X1",    LcdCommand::reverseKwh6x1 )

    ( "TOTAL_KWH_4X1",      LcdCommand::totalKwh4x1 )
    ( "TOTAL_KWH_5X1",      LcdCommand::totalKwh5x1 )
    ( "TOTAL_KWH_6X1",      LcdCommand::totalKwh6x1 )

    ( "NET_KWH_4X1",        LcdCommand::netKwh4x1 )
    ( "NET_KWH_5X1",        LcdCommand::netKwh5x1 )
    ( "NET_KWH_6X1",        LcdCommand::netKwh6x1 )

    ( "DIAGNOSTIC_FLAGS",   LcdCommand::diagnosticFlags )
    ( "ALL_SEGMENTS",       LcdCommand::allSegments     )
    ( "FIRMWARE_VERSION",   LcdCommand::firmwareVersion );

} // anonymous namespace

RfnMeterDevice::ConfigMap Rfn420FocusAlDevice::getConfigMethods(bool readOnly)
{
    ConfigMap m = RfnResidentialVoltageDevice::getConfigMethods( readOnly );

    if( readOnly )
    {
        m.insert( ConfigMap::value_type( ConfigPart::display, bindConfigMethod( &Rfn420FocusAlDevice::executeGetConfigDisplay, this )));
    }
    else
    {
        m.insert( ConfigMap::value_type( ConfigPart::display, bindConfigMethod( &Rfn420FocusAlDevice::executePutConfigDisplay, this )));
    }

    return m;
}

YukonError_t Rfn420FocusAlDevice::executeGetConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    rfnRequests.push_back(
            boost::make_shared<Commands::RfnFocusAlLcdConfigurationReadCommand>());

    return ClientErrors::None;
}

YukonError_t Rfn420FocusAlDevice::executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return reportConfigErrorDetails( ClientErrors::NoConfigData, "Device \"" + getName() + "\"", pReq, returnMsgs );
        }

        const unsigned config_display_digits        = getConfigData<unsigned>     ( deviceConfig, Config::RfnStrings::DisplayDigits );

        if( config_display_digits > 6 ||
            config_display_digits < 4 )
        {
            CTILOG_ERROR(dout, "Device \"" << getName() << "\" - Invalid value (" << config_display_digits << ") for config key \"" << Config::RfnStrings::DisplayDigits << "\"");

            return reportConfigErrorDetails( ClientErrors::InvalidConfigData,
                "Device \"" + getName() + "\" - Invalid value (" + std::to_string(config_display_digits) + 
                    ") for config key \"" + Config::RfnStrings::DisplayDigits + "\"", 
                pReq, returnMsgs );
        }

        const unsigned char config_display_duration = getConfigData<unsigned char>( deviceConfig, Config::RfnStrings::LcdCycleTime );

        LcdCommand::MetricVector config_display_items;
        std::vector<std::string> config_display_strings;

        bool seen_slot_disabled = false;

        for each( const std::string & configKey in displayMetricConfigKeys )
        {
            std::string configDisplayItem = getConfigData<std::string>( deviceConfig, configKey );

            if( configDisplayItem == "SLOT_DISABLED" )
            {
                seen_slot_disabled = true;
            }
            else if( seen_slot_disabled )
            {
                CTILOG_ERROR(dout, "Device \"" << getName() << "\" - Invalid value (" << configDisplayItem << ") seen after SLOT_DISABLED for config key \"" << configKey << "\"");

                return reportConfigErrorDetails( ClientErrors::InvalidConfigData,
                    "Device \"" + getName() + "\" - Invalid value (" + configDisplayItem + 
                        ") seen after SLOT_DISABLED for config key \"" + configKey + "\"", 
                    pReq, returnMsgs );
            }
            else
            {
                if( boost::algorithm::ends_with(configDisplayItem, "_KWH") )
                {
                    //  if it's a KWH entry, tack on _4X1, _5X1, _6X1
                    configDisplayItem += "_";
                    configDisplayItem += config_display_digits + '0';
                    configDisplayItem += "X1";
                }

                const boost::optional<LcdCommand::Metrics> metricItem = bimapFind<LcdCommand::Metrics>(ConfigToCommand.left, configDisplayItem);

                if( ! metricItem )
                {
                    CTILOG_ERROR(dout, "Device \"" << getName() << "\" - Invalid value (" << configDisplayItem << ") for config key \"" << configKey << "\"");

                    return reportConfigErrorDetails( ClientErrors::InvalidConfigData,
                        "Device \"" + getName() + "\" - Invalid value (" + configDisplayItem + ") for config key \"" + configKey + "\"",
                        pReq, returnMsgs );
                }

                config_display_items.push_back(*metricItem);
            }

            config_display_strings.push_back(configDisplayItem);
        }

        // check if the dynamic info has the current configuration
        if( ! parse.isKeyValid("force") && isDisplayConfigCurrent( config_display_strings, config_display_digits, config_display_duration ))
        {
            return ClientErrors::ConfigCurrent;
        }

        // if this is verify only
        if( parse.isKeyValid("verify"))
        {
            return ClientErrors::ConfigNotCurrent;
        }

        // Create display items config

        rfnRequests.push_back( boost::make_shared<Commands::RfnFocusAlLcdConfigurationWriteCommand>( config_display_items, config_display_duration ));

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return reportConfigErrorDetails( e, pReq, returnMsgs );
    }
}

bool Rfn420FocusAlDevice::isDisplayConfigCurrent( const std::vector<std::string> &config_display_metrics, const unsigned config_display_digits, const unsigned char config_display_duration )
{
    std::vector<std::string> paoinfo_metrics;

    for each( const PaoInfoKeys paoKey in displayMetricPaoKeys )
    {
        std::string pao_value;

        if( ! getDynamicInfo(paoKey, pao_value) )
        {
            break;
        }

        paoinfo_metrics.push_back(pao_value);
    }

    const boost::optional<unsigned char> pao_duration      = findDynamicInfo<unsigned char>(CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime);

    return
        boost::equal( config_display_metrics, paoinfo_metrics )
            && pao_duration      == config_display_duration;
}


void Rfn420FocusAlDevice::handleCommandResult(const Commands::RfnFocusAlLcdConfigurationReadCommand &cmd)
{
    typedef Commands::RfnFocusAlLcdConfigurationCommand::MetricVector MetricVector;

    if( const boost::optional<MetricVector> displayItemsReceived = cmd.getDisplayItemsReceived() )
    {
        storeDisplayMetricInfo(*displayItemsReceived);
    }

    // set display item duration pao info
    if( const boost::optional<unsigned char> displayItemDurationReceived = cmd.getDisplayItemDurationReceived() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime, (long)*displayItemDurationReceived );
    }
}


void Rfn420FocusAlDevice::handleCommandResult(const Commands::RfnFocusAlLcdConfigurationWriteCommand &cmd)
{
    storeDisplayMetricInfo(cmd.metrics);

    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LcdCycleTime, cmd.displayItemDuration );
}


void Rfn420FocusAlDevice::storeDisplayMetricInfo(const Commands::RfnFocusAlLcdConfigurationCommand::MetricVector &metrics)
{
    Commands::RfnFocusAlLcdConfigurationCommand::MetricVector::const_iterator received_itr = metrics.begin();
    std::vector<PaoInfoKeys>::const_iterator pao_itr = displayMetricPaoKeys.begin();

    while( pao_itr != displayMetricPaoKeys.end() )
    {
        std::string paoInfo;

        if( received_itr == metrics.end() )
        {
            paoInfo = "SLOT_DISABLED";
        }
        else
        {
            const Commands::RfnFocusAlLcdConfigurationCommand::Metrics metric = *received_itr++;

            if( const boost::optional<std::string> metricName = bimapFind<std::string>(ConfigToCommand.right, metric ) )
            {
                paoInfo = *metricName;
            }
            else
            {
                paoInfo = "UNKNOWN (" + CtiNumStr(metric) + ")";
            }
        }

        setDynamicInfo( *pao_itr++, paoInfo );
    }
}


}
}
