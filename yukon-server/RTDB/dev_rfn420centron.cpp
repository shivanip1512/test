#include "precompiled.h"

#include "dev_rfn420centron.h"

#include "config_data_rfn.h"

#include <boost/assign/list_of.hpp>

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
}

int Rfn420CentronDevice::executePutConfigDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return NoConfigData;
    }

    Commands::RfnCentronLcdConfigurationCommand::metric_vector_t config_display_metrics;

    std::vector<unsigned char> paoinfo_metrics;

    for each( const std::string configKey in displayMetricConfigKeys )
    {
        const boost::optional<long> configValue = deviceConfig->findLongValueForKey(configKey);

        if( ! configValue  )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device \"" << getName() << "\" - Missing value for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            return NoConfigData;
        }

        if( *configValue < 0x00 ||
            *configValue > 0xff )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device \"" << getName() << "\" - Invalid value (" << *configValue << ") for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            return ErrorInvalidConfigData;
        }

        config_display_metrics.push_back(*configValue);
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

    if( config_display_metrics.size() == paoinfo_metrics.size() && std::equal(config_display_metrics.begin(), config_display_metrics.end(), paoinfo_metrics.begin()) )
    {
        if( ! parse.isKeyValid("force") )
        {
            return ConfigCurrent;
        }
    }
    else
    {
        if( parse.isKeyValid("verify") )
        {
            return ConfigNotCurrent;
        }
    }

    std::auto_ptr<Commands::RfnCommand> displayCommand(
       new Commands::RfnCentronLcdConfigurationCommand(*this, config_display_metrics));

    rfnRequests.push_back(
       Commands::RfnCommandSPtr(displayCommand.release()));

    return NoError;
}


void Rfn420CentronDevice::handleResult(const Commands::RfnCentronLcdConfigurationCommand &cmd)
{
    typedef Commands::RfnCentronLcdConfigurationCommand::metric_vector_t metric_vector_t;

    const metric_vector_t received_metrics = cmd.getReceivedMetrics();

    std::vector<PaoInfoKeys>::const_iterator pao_itr = displayMetricPaoKeys.begin();
    metric_vector_t::const_iterator received_itr = received_metrics.begin();

    while( received_itr != received_metrics.end()
           && pao_itr != displayMetricPaoKeys.end() )
    {
        setDynamicInfo(*pao_itr++, *received_itr++);
    }
}


}
}
