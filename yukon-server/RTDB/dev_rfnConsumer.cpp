#include "precompiled.h"

#include "config_data_rfn.h"
#include "dev_rfnConsumer.h"

#include <boost/optional.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

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

    struct // IntervalSettings
    {
        boost::optional<unsigned>   demandInterval,
                                    loadProfileInterval;
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

    const bool equalDemandIntervals = configSettings.demandInterval && paoSettings.demandInterval &&        // both exist -and-
                                        *configSettings.demandInterval == *paoSettings.demandInterval;      // are the same

    const bool equalLpIntervals = configSettings.loadProfileInterval && paoSettings.loadProfileInterval &&
                                    *configSettings.loadProfileInterval == *paoSettings.loadProfileInterval;

    if( equalDemandIntervals && equalLpIntervals )
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

    std::auto_ptr<Commands::RfnCommand> voltageProfileConfigCommand(
       new Commands::RfnVoltageProfileConfigurationCommand( *this,
                                                            *configSettings.demandInterval,
                                                            *configSettings.loadProfileInterval ) );

    rfnRequests.push_back( Commands::RfnCommandSPtr( voltageProfileConfigCommand.release() ) );

    return NoError;
}


int RfnConsumerDevice::executeLoadProfileRecording( CtiRequestMsg     * pReq,
                                                    CtiCommandParser  & parse,
                                                    CtiMessageList    & retList,
                                                    RfnCommandList    & rfnRequests )
{
    // TBD

    std::auto_ptr<Commands::RfnCommand> command;

    if ( 0 /* parse says enable or disable recording -- TBD */ )
    {
        Commands::RfnLoadProfileRecordingCommand::RecordingOption option =
            Commands::RfnLoadProfileRecordingCommand::DisableRecording;  // get from parse...

        command = std::auto_ptr<Commands::RfnCommand>( new Commands::RfnLoadProfileRecordingCommand( *this, option ) );
    }
    else    // reading the state of recording from the device
    {
        command = std::auto_ptr<Commands::RfnCommand>( new Commands::RfnLoadProfileRecordingCommand( *this ) );
    }

    rfnRequests.push_back( Commands::RfnCommandSPtr( command.release() ) );

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

    if( configFreezeDay && paoFreezeDay && *configFreezeDay == *paoFreezeDay )  // both exist and are equal
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

    std::auto_ptr<Commands::RfnCommand> freezeDayConfigCommand(
       new Commands::RfnDemandFreezeConfigurationCommand( *configFreezeDay ) );

    rfnRequests.push_back( Commands::RfnCommandSPtr( freezeDayConfigCommand.release() ) );

    return NoError;
}


int RfnConsumerDevice::executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                                     CtiCommandParser  & parse,
                                                     CtiMessageList    & retList,
                                                     RfnCommandList    & rfnRequests )
{
    std::auto_ptr<Commands::RfnCommand> immediateFreezeCommand(
       new Commands::RfnImmediateDemandFreezeCommand );

    rfnRequests.push_back( Commands::RfnCommandSPtr( immediateFreezeCommand.release() ) );

    return NoError;
}


int RfnConsumerDevice::executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                                    CtiCommandParser  & parse,
                                                    CtiMessageList    & retList,
                                                    RfnCommandList    & rfnRequests )
{
    std::auto_ptr<Commands::RfnCommand> readFreezeInfoCommand(
       new Commands::RfnGetDemandFreezeInfoCommand( *this ) );

    rfnRequests.push_back( Commands::RfnCommandSPtr( readFreezeInfoCommand.release() ) );

    return NoError;
}


int RfnConsumerDevice::executeTouCriticalPeak( CtiRequestMsg     * pReq,
                                               CtiCommandParser  & parse,
                                               CtiMessageList    & retList,
                                               RfnCommandList    & rfnRequests )
{

    if( parse.isKeyValid("tou_critical_peak_cancel") )
    {
        std::auto_ptr<Commands::RfnCommand> cancelCriticalPeakCommand(
            new Commands::RfnTouCancelCriticalPeakCommand );

        rfnRequests.push_back( Commands::RfnCommandSPtr( cancelCriticalPeakCommand.release() ) );
    }
    else
    {
        const std::string rateStr = parse.getsValue( "tou_critical_peak_rate" );

        Commands::RfnTouCriticalPeakCommand::Rate   rate = Commands::RfnTouCriticalPeakCommand::RateD;
        if ( rateStr == "a" ) { rate = Commands::RfnTouCriticalPeakCommand::RateA; }
        if ( rateStr == "b" ) { rate = Commands::RfnTouCriticalPeakCommand::RateB; }
        if ( rateStr == "c" ) { rate = Commands::RfnTouCriticalPeakCommand::RateC; }

        const std::string stopTime = parse.getsValue( "tou_critical_peak_stop_time" );

        std::vector<std::string>    timeComponents;

        boost::split( timeComponents, stopTime, boost::is_any_of(":"), boost::token_compress_on );

        const unsigned hour   = strtoul( timeComponents[0].c_str(), NULL, 10 );
        const unsigned minute = strtoul( timeComponents[1].c_str(), NULL, 10 );

        std::auto_ptr<Commands::RfnCommand> criticalPeakCommand(
            new Commands::RfnTouCriticalPeakCommand( rate, hour, minute ) );

        rfnRequests.push_back( Commands::RfnCommandSPtr( criticalPeakCommand.release() ) );
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

