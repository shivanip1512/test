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
        const int parsedHour     = parse.getiValue( "tou_critical_peak_stop_time_hour" );
        const int parsedMinute   = parse.getiValue( "tou_critical_peak_stop_time_minute" );

        if ( parsedHour == -1 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Missing hour value. " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return BADPARAM;
        }
        if ( parsedMinute == -1 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Missing minute value. " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return BADPARAM;
        }

        const unsigned hour     = parsedHour;
        const unsigned minute   = parsedMinute;
        const std::string rate  = parse.getsValue( "tou_critical_peak_rate" );

        rfnRequests.push_back( boost::make_shared<Commands::RfnTouCriticalPeakCommand>( rate, hour, minute ) );
    }

    return NoError;
}


/**
 * Execute put config tou schedule
 */
int RfnConsumerDevice::executePutConfigTou( CtiRequestMsg     * pReq,
                                            CtiCommandParser  & parse,
                                            CtiMessageList    & retList,
                                            RfnCommandList    & rfnRequests )
{
    using Commands::RfnTouConfigurationCommand;
    using Commands::RfnTouStateConfigurationCommand;
    using Commands::RfnTouScheduleConfigurationCommand;

    RfnTouScheduleConfigurationCommand::Schedule schedule;

    if( parse.isKeyValid("tou_enable") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouStateConfigurationCommand>( RfnTouConfigurationCommand::TouEnable ));

        return NoError;
    }

    if( parse.isKeyValid("tou_disable") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouStateConfigurationCommand>( RfnTouConfigurationCommand::TouDisable ));

        return NoError;
    }

    //
    // Day Table
    //

    if( ! parse.isKeyValid("tou_days") )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Device \"" << getName() << "\" - Missing day table. " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

        return BADPARAM;
    }

    const string dayTableStr = parse.getsValue("tou_days");

    for( int day_nbr = 0; day_nbr < dayTableStr.length(); day_nbr++ )
    {
        schedule._dayTable.push_back( dayTableStr.substr(day_nbr,1) );
    }

    //
    // Default Rate
    //

    if( ! parse.isKeyValid("tou_default") )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Device \"" << getName() << "\" - Missing TOU default rate. " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

        return BADPARAM;
    }

    schedule._defaultRate = parse.getsValue("tou_default");

    //
    // Switch Rates and Times
    //

    int iter = 0;
    string schedule_name = "tou_schedule_" + CtiNumStr(iter).zpad(2);

    while( parse.isKeyValid( schedule_name ))
    {
        const string schedule_str = CtiNumStr( parse.getiValue( schedule_name ));

        RfnTouScheduleConfigurationCommand::ScheduleNbr schedule_nbr = RfnTouScheduleConfigurationCommand::resolveScheduleNbr( schedule_str );

        int change_nbr = 0;
        string change_name = schedule_name + "_" + CtiNumStr(change_nbr).zpad(2);

        RfnTouScheduleConfigurationCommand::DailyTimes times;
        RfnTouScheduleConfigurationCommand::DailyRates rates;

        while( parse.isKeyValid( change_name ) )
        {
            if( change_nbr > rates.size() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device \"" << getName() << "\" - Schedule " << schedule_str << " has an invalid rate change. " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                return BADPARAM;
            }

            const string change_str = parse.getsValue( change_name );

            const string rate_str   = change_str.substr(0, 1),
                         time_str   = change_str.substr(2, change_str.length()-2);

            times.push_back( time_str );
            rates.push_back( rate_str );

            change_nbr++;
            change_name = schedule_name + "_" + CtiNumStr(change_nbr).zpad(2);
        }

        schedule._times[schedule_nbr] = times;
        schedule._rates[schedule_nbr] = rates;

        iter++;
        schedule_name = "tou_schedule_" + CtiNumStr(iter).zpad(2);
    }

    rfnRequests.push_back( boost::make_shared<RfnTouScheduleConfigurationCommand>( schedule ));

    return NoError;
}

/**
 * Execute get config schedule
 */
int RfnConsumerDevice::executeGetConfigTou( CtiRequestMsg     * pReq,
                                            CtiCommandParser  & parse,
                                            CtiMessageList    & retList,
                                            RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouScheduleConfigurationCommand>());

    return NoError;
}

/**
 * Execute put config holiday
 */
int RfnConsumerDevice::executePutConfigHoliday( CtiRequestMsg     * pReq,
                                                CtiCommandParser  & parse,
                                                CtiMessageList    & retList,
                                                RfnCommandList    & rfnRequests )
{
    using Commands::RfnTouHolidayConfigurationCommand;
    using Commands::RfnTouSetHolidayActiveCommand;
    using Commands::RfnTouCancelHolidayActiveCommand;

    if( parse.isKeyValid("holiday_set_active") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouSetHolidayActiveCommand>());

        return NoError;
    }

    if( parse.isKeyValid("holiday_cancel_active") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouCancelHolidayActiveCommand>());

        return NoError;
    }

    Commands::RfnTouHolidayConfigurationCommand::Holidays holidays;

    int holiday_nbr = 0;

    while( parse.isKeyValid("holiday_date" + CtiNumStr(holiday_nbr)))
    {
        const std::string holiday_str = parse.getsValue("holiday_date" + CtiNumStr(holiday_nbr));

        if( holiday_nbr >= holidays.size() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device \"" << getName() << "\" - Holiday " << holiday_str << " specified. " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return BADPARAM;
        }

        int day, month, year;
        char sep1, sep2;

        std::istringstream iss( holiday_str );
        iss >> month >> sep1 >> day >> sep2 >> year;

        holidays[holiday_nbr] = CtiDate(day, month, year);

        holiday_nbr++;
    }

    rfnRequests.push_back( boost::make_shared<RfnTouHolidayConfigurationCommand>( holidays ));

    return NoError;
}

/**
 * Execute get config holiday
 */
int RfnConsumerDevice::executeGetConfigHoliday( CtiRequestMsg     * pReq,
                                                CtiCommandParser  & parse,
                                                CtiMessageList    & retList,
                                                RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouHolidayConfigurationCommand>());

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


void RfnConsumerDevice::handleResult( const Commands::RfnTouScheduleConfigurationCommand & cmd )
{
    // TODO

}


void RfnConsumerDevice::handleResult( const Commands::RfnTouHolidayConfigurationCommand & cmd )
{
    // TODO
}


}
}

