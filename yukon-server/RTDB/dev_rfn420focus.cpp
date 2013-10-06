#include "precompiled.h"

#include <boost/make_shared.hpp>
#include <boost/assign/list_of.hpp>

#include "MissingConfigDataException.h"
#include "config_data_rfn.h"
#include "dev_rfn420focus.h"


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

const std::vector<CtiDeviceBase::PaoInfoKeys> displayAlphaPaoKeys = boost::assign::list_of
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric1 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric2 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric3 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric4 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric5 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric6 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric7 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric8 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric9 )
    ( CtiTableDynamicPaoInfo::Key_DisplayAlphameric10 );

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

const std::vector<std::string> displayAlphamericConfigKeys = boost::assign::list_of
    ( Config::RfnStrings::displayAlphameric1 )
    ( Config::RfnStrings::displayAlphameric2 )
    ( Config::RfnStrings::displayAlphameric3 )
    ( Config::RfnStrings::displayAlphameric4 )
    ( Config::RfnStrings::displayAlphameric5 )
    ( Config::RfnStrings::displayAlphameric6 )
    ( Config::RfnStrings::displayAlphameric7 )
    ( Config::RfnStrings::displayAlphameric8 )
    ( Config::RfnStrings::displayAlphameric9 )
    ( Config::RfnStrings::displayAlphameric10 );


std::string getConfigValue( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    boost::optional<std::string> val = deviceConfig->findValueForKey( configKey );

    if( ! val )
    {
        throw MissingConfigDataException( configKey );
    }

    return *val;
}

long getLongConfigValue( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    boost::optional<long> val = deviceConfig->findLongValueForKey( configKey );

    if( ! val )
    {
        throw MissingConfigDataException( configKey );
    }

    return *val;
}

} // anonymous namespace

int Rfn420FocusDevice::executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return NoConfigData;
        }

        std::vector<std::string> config_display_metrics,
                                 config_display_alphamerics;

        for each( const std::string & configKey in displayMetricConfigKeys )
        {
            config_display_metrics.push_back( getConfigValue( deviceConfig, configKey ));
        }

        for each( const std::string & configKey in displayAlphamericConfigKeys )
        {
            config_display_alphamerics.push_back( getConfigValue( deviceConfig, configKey ));
        }

        long config_display_duration = getLongConfigValue( deviceConfig, Config::RfnStrings::displayItemDuration );

        if( config_display_duration < 0 || config_display_duration > 255 )
        {
            logInfo( "display item duration is out of range (" + CtiNumStr(config_display_duration) + ", expected [0..255])",
                     __FUNCTION__, __FILE__, __LINE__ );

            return ErrorInvalidConfigData;
        }

        // check if the dynamic info has the current configuration
        if( ! parse.isKeyValid("force") && isDisplayConfigCurrent( config_display_metrics, config_display_alphamerics, config_display_duration ))
        {
            return ConfigCurrent;
        }

        // if this is verify only
        if( parse.isKeyValid("verify"))
        {
            return ConfigNotCurrent;
        }

        // Create display items config

        Commands::RfnFocusLcdConfigurationCommand::DisplayItemVector config_display_items;

        std::vector<std::string>::const_iterator config_metric_itr = config_display_metrics.begin(),
                                                 config_alpha_itr  = config_display_alphamerics.begin();

        while( config_metric_itr != config_display_metrics.end() &&
               config_alpha_itr  != config_display_alphamerics.end() )
        {
            Commands::RfnFocusLcdConfigurationCommand::DisplayItem display_item;

            display_item.metric       = *config_metric_itr++;
            display_item.alphamericId = *config_alpha_itr++;

            config_display_items.push_back( display_item );
        }

        rfnRequests.push_back( boost::make_shared<Commands::RfnFocusLcdConfigurationCommand>( config_display_items, config_display_duration ));

        return NoError;
    }
    catch( const MissingConfigDataException &e )
    {
        logInfo( e.what(),
                 __FUNCTION__, __FILE__, __LINE__ );

        return NoConfigData;
    }

}

bool Rfn420FocusDevice::isDisplayConfigCurrent( const std::vector<std::string> &config_display_metrics, const std::vector<std::string> &config_display_alphamerics, const long config_display_duration )
{
    std::vector<std::string> paoinfo_metrics,
                             paoinfo_alphamerics;

    for each( const PaoInfoKeys paoKey in displayMetricPaoKeys )
    {
        std::string pao_value;

        if( ! getDynamicInfo(paoKey, pao_value) )
        {
            break;
        }

        paoinfo_metrics.push_back(pao_value);
    }

    for each( const PaoInfoKeys paoKey in displayAlphaPaoKeys )
    {
        std::string pao_value;

        if( ! getDynamicInfo(paoKey, pao_value) )
        {
            break;
        }

        paoinfo_alphamerics.push_back(pao_value);
    }

    long l_pao_value;
    boost::optional<long> pao_duration;

    if( getDynamicInfo( CtiTableDynamicPaoInfo::Key_DisplayItemDuration, l_pao_value ))
    {
        pao_duration = l_pao_value;
    }

    return ( boost::equal( config_display_metrics,     paoinfo_metrics )     &&
             boost::equal( config_display_alphamerics, paoinfo_alphamerics ) &&
             pao_duration == config_display_duration );
}


void Rfn420FocusDevice::handleResult(const Commands::RfnFocusLcdConfigurationCommand &cmd)
{
    typedef Commands::RfnFocusLcdConfigurationCommand::DisplayItemVector DisplayItemVector;

    boost::optional<DisplayItemVector> displayItemsReceived = cmd.getDisplayItemsReceived();

    if( displayItemsReceived )
    {
        // set display metric pao info

        DisplayItemVector::const_iterator        received_itr = displayItemsReceived->begin();
        std::vector<PaoInfoKeys>::const_iterator pao_itr      = displayMetricPaoKeys.begin();

        while( received_itr != displayItemsReceived->end() &&
               pao_itr      != displayMetricPaoKeys.end() )
        {
            setDynamicInfo( *pao_itr++, (*received_itr++).metric );
        }

        // set display alpha numeric pao info

        received_itr = displayItemsReceived->begin();
        pao_itr      = displayAlphaPaoKeys.begin();

        while( received_itr != displayItemsReceived->end() &&
               pao_itr      != displayAlphaPaoKeys.end() )
        {
            setDynamicInfo( *pao_itr++, (*received_itr++).alphamericId );
        }
    }

    // set display item duration pao info

    boost::optional<unsigned char> displayItemDurationReceived = cmd.getDisplayItemDurationReceived();

    if( displayItemDurationReceived )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_DisplayItemDuration, (long)*displayItemDurationReceived );
    }
}


}
}
