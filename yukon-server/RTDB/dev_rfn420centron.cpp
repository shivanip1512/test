#include "precompiled.h"

#include "dev_rfn420centron.h"

#include "config_data_rfn.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Devices {

namespace {

const std::vector<CtiDeviceBase::PaoInfoKeys> displayMetricPaoKeys = boost::assign::list_of
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric01 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric02 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric03 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric04 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric05 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric06 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric07 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric08 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric09 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric10 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric11 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric12 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric13 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric14 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric15 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric16 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric17 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric18 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric19 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric20 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric21 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric22 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric23 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric24 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric25 )
    ( CtiTableDynamicPaoInfo::Key_DisplayMetric26 );

const std::vector<std::string> displayMetricConfigKeys = boost::assign::list_of
    ( Config::RfnStrings::displayMetric01 )
    ( Config::RfnStrings::displayMetric02 )
    ( Config::RfnStrings::displayMetric03 )
    ( Config::RfnStrings::displayMetric04 )
    ( Config::RfnStrings::displayMetric05 )
    ( Config::RfnStrings::displayMetric06 )
    ( Config::RfnStrings::displayMetric07 )
    ( Config::RfnStrings::displayMetric08 )
    ( Config::RfnStrings::displayMetric09 )
    ( Config::RfnStrings::displayMetric10 )
    ( Config::RfnStrings::displayMetric11 )
    ( Config::RfnStrings::displayMetric12 )
    ( Config::RfnStrings::displayMetric13 )
    ( Config::RfnStrings::displayMetric14 )
    ( Config::RfnStrings::displayMetric15 )
    ( Config::RfnStrings::displayMetric16 )
    ( Config::RfnStrings::displayMetric17 )
    ( Config::RfnStrings::displayMetric18 )
    ( Config::RfnStrings::displayMetric19 )
    ( Config::RfnStrings::displayMetric20 )
    ( Config::RfnStrings::displayMetric21 )
    ( Config::RfnStrings::displayMetric22 )
    ( Config::RfnStrings::displayMetric23 )
    ( Config::RfnStrings::displayMetric24 )
    ( Config::RfnStrings::displayMetric25 )
    ( Config::RfnStrings::displayMetric26 );
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
