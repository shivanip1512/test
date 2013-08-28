#include "precompiled.h"

#include "config_data_rfn.h"
#include "dev_rfnConsumer.h"

#include <boost/optional.hpp>
#include <boost/make_shared.hpp>

#include <limits>
#include <string>



namespace Cti {
namespace Devices {

int RfnConsumerDevice::executePutConfigVoltageAveragingInterval( CtiRequestMsg     * pReq,
                                                                 CtiCommandParser  & parse,
                                                                 CtiMessageList    & retList,
                                                                 RfnCommandList    & rfnRequests )
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return NoConfigData;
    }

    struct IntervalSettings
    {
        boost::optional<unsigned>   demandInterval,
                                    loadProfileInterval;

        bool operator==( const IntervalSettings & rhs ) const
        {
            return demandInterval == rhs.demandInterval && loadProfileInterval == rhs.loadProfileInterval;
        }
    }
    configSettings,
    paoSettings;

    {
        const std::string           configKey( Config::RfnStrings::demandInterval );
        const boost::optional<long> configValue = deviceConfig->findLongValueForKey( configKey );

        if ( ! configValue  )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Missing value for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NoConfigData;
        }

        // Rudimentary check to see that we can coerce the 'long' into an 'unsigned'.
        //  - the command object will perform further validation of its input.
        if ( *configValue < 0 || *configValue > std::numeric_limits<unsigned>::max() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Invalid value (" << *configValue << ") for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NoConfigData;
        }

        configSettings.demandInterval = static_cast<unsigned>( *configValue );
    }

    {
        const std::string           configKey( Config::RfnStrings::loadProfileInterval );
        const boost::optional<long> configValue = deviceConfig->findLongValueForKey( configKey );

        if ( ! configValue  )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Missing value for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NoConfigData;
        }

        if ( *configValue < 0 || *configValue > std::numeric_limits<unsigned>::max() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Invalid value (" << *configValue << ") for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NoConfigData;
        }

        configSettings.loadProfileInterval = static_cast<unsigned>( *configValue );
    }

    {
        long pao_value;

        if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandInterval, pao_value ) )
        {
            paoSettings.demandInterval = static_cast<unsigned>( pao_value );
        }

        if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval, pao_value ) )
        {
            paoSettings.loadProfileInterval = static_cast<unsigned>( pao_value );
        }
    }

    if( configSettings == paoSettings )
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

    rfnRequests.push_back(
        boost::make_shared<Commands::RfnVoltageProfileConfigurationCommand>( boost::ref(*this),
                                                                             *configSettings.demandInterval,
                                                                             *configSettings.loadProfileInterval ) );

    return NoError;
}


int RfnConsumerDevice::executeLoadProfileRecording( CtiRequestMsg     * pReq,
                                                    CtiCommandParser  & parse,
                                                    CtiMessageList    & retList,
                                                    RfnCommandList    & rfnRequests )
{
    // TBD

    if ( 0 /* parse says enable or disable recording -- TBD */ )
    {
        Commands::RfnLoadProfileRecordingCommand::RecordingOption option =
            Commands::RfnLoadProfileRecordingCommand::DisableRecording;  // get from parse...

        rfnRequests.push_back( boost::make_shared<Commands::RfnLoadProfileRecordingCommand>( boost::ref(*this), option ) );
    }
    else    // reading the state of recording from the device
    {
        rfnRequests.push_back( boost::make_shared<Commands::RfnLoadProfileRecordingCommand>( boost::ref(*this) ) );
    }

    return NoError;
}


int RfnConsumerDevice::executePutConfigDemandFreezeDay( CtiRequestMsg     * pReq,
                                                        CtiCommandParser  & parse,
                                                        CtiMessageList    & retList,
                                                        RfnCommandList    & rfnRequests )
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return NoConfigData;
    }

    boost::optional<unsigned char>  configFreezeDay,
                                    paoFreezeDay;

    {
        const std::string           configKey( Config::RfnStrings::demandFreezeDay );
        const boost::optional<long> configValue = deviceConfig->findLongValueForKey( configKey );

        if ( ! configValue  )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Missing value for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NoConfigData;
        }

        // Rudimentary check to see that we can coerce the 'long' into an 'unsigned char'.
        //  - the command object will perform further validation of its input.
        if ( *configValue < 0 || *configValue > std::numeric_limits<unsigned char>::max() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Invalid value (" << *configValue << ") for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NoConfigData;
        }

        configFreezeDay = static_cast<unsigned>( *configValue );
    }

    {
        long pao_value;

        if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay, pao_value ) )
        {
            paoFreezeDay = static_cast<unsigned>( pao_value );
        }
    }

    if( *configFreezeDay == *paoFreezeDay )  // both exist and are equal
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

    rfnRequests.push_back( boost::make_shared<Commands::RfnDemandFreezeConfigurationCommand>( *configFreezeDay ) );

    return NoError;
}


int RfnConsumerDevice::executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                                     CtiCommandParser  & parse,
                                                     CtiMessageList    & retList,
                                                     RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnImmediateDemandFreezeCommand>() );

    return NoError;
}


int RfnConsumerDevice::executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                                    CtiCommandParser  & parse,
                                                    CtiMessageList    & retList,
                                                    RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetDemandFreezeInfoCommand>( boost::ref(*this) ) );

    return NoError;
}


int RfnConsumerDevice::executeTouCriticalPeak( CtiRequestMsg     * pReq,
                                               CtiCommandParser  & parse,
                                               CtiMessageList    & retList,
                                               RfnCommandList    & rfnRequests )
{

    if( parse.isKeyValid("tou_critical_peak_cancel") )
    {
        rfnRequests.push_back( boost::make_shared<Commands::RfnTouCancelCriticalPeakCommand>() );
    }
    else
    {
        const std::string rateStr = parse.getsValue( "tou_critical_peak_rate" );

        Commands::RfnTouCriticalPeakCommand::Rate   rate = Commands::RfnTouCriticalPeakCommand::RateD;
        if ( rateStr == "a" ) { rate = Commands::RfnTouCriticalPeakCommand::RateA; }
        if ( rateStr == "b" ) { rate = Commands::RfnTouCriticalPeakCommand::RateB; }
        if ( rateStr == "c" ) { rate = Commands::RfnTouCriticalPeakCommand::RateC; }

        const unsigned hour   = parse.getiValue( "tou_critical_peak_stop_time_hour" );
        const unsigned minute = parse.getiValue( "tou_critical_peak_stop_time_minute" );

        rfnRequests.push_back( boost::make_shared<Commands::RfnTouCriticalPeakCommand>( rate, hour, minute ) );
    }

    return NoError;
}


void RfnConsumerDevice::handleResult( const Commands::RfnVoltageProfileConfigurationCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandInterval,      cmd.getDemandIntervalSeconds() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval, cmd.getLoadProfileIntervalMinutes() );
}


void RfnConsumerDevice::handleResult( const Commands::RfnLoadProfileRecordingCommand & cmd )
{
    // TBD

    Commands::RfnLoadProfileRecordingCommand::RecordingOption option = cmd.getRecordingOption();
}


void RfnConsumerDevice::handleResult( const Commands::RfnGetDemandFreezeInfoCommand & cmd )
{
    // TBD

    Commands::RfnGetDemandFreezeInfoCommand::DemandFreezeData freezeData = cmd.getDemandFreezeData();
}


}
}

