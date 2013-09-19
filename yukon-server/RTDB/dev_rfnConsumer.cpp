#include "precompiled.h"

#include "std_helper.h"
#include "config_data_rfn.h"
#include "config_device.h"
#include "dev_rfnConsumer.h"

#include <boost/optional.hpp>
#include <boost/make_shared.hpp>
#include <boost/assign/list_of.hpp>

#include <limits>
#include <string>



namespace Cti {
namespace Devices {

using Config::RfnStrings;

const RfnDevice::InstallMap RfnConsumerDevice::_putConfigInstallMap = initPutConfigInstallMap();
const RfnDevice::InstallMap RfnConsumerDevice::_getConfigInstallMap = initGetConfigInstallMap();


const RfnDevice::InstallMap RfnConsumerDevice::initPutConfigInstallMap()
{
    const InstallMap m = boost::assign::map_list_of
            ("display",          static_cast<InstallMethod>(&RfnConsumerDevice::executePutConfigInstallDisplay))
            ("freezeday",        static_cast<InstallMethod>(&RfnConsumerDevice::executePutConfigInstallFreezeDay))
            ("tou",              static_cast<InstallMethod>(&RfnConsumerDevice::executePutConfigInstallTou))
            ("voltageaveraging", static_cast<InstallMethod>(&RfnConsumerDevice::executePutConfigInstallVoltageAveragingInterval));

    return m;
}

const RfnDevice::InstallMap RfnConsumerDevice::initGetConfigInstallMap()
{
    const InstallMap m;

    // TODO

    return m;
}

RfnDevice::InstallMap RfnConsumerDevice::getPutConfigInstallMap() const
{
    return _putConfigInstallMap;
}

RfnDevice::InstallMap RfnConsumerDevice::getGetConfigInstallMap() const
{
    return _getConfigInstallMap;
}


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
 * Execute putconfig tou schedule
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
        logInfo("Missing day table.",
                __FUNCTION__, __FILE__, __LINE__ );

        return BADPARAM;
    }

    const string dayTableStr = parse.getsValue("tou_days");

    for( int day_nbr = 0; day_nbr < dayTableStr.length(); day_nbr++ )
    {
        schedule._dayTable.push_back( "schedule " + dayTableStr.substr(day_nbr,1) );
    }

    //
    // Default Rate
    //
    if( ! parse.isKeyValid("tou_default") )
    {
        logInfo("Missing TOU default rate.",
                __FUNCTION__, __FILE__, __LINE__ );

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
        const string schedule_str = "schedule " + CtiNumStr( parse.getiValue( schedule_name ));

        RfnTouScheduleConfigurationCommand::ScheduleNbr schedule_nbr = RfnTouScheduleConfigurationCommand::resolveScheduleName( schedule_str );

        int change_nbr = 0;
        string change_name = schedule_name + "_" + CtiNumStr(change_nbr).zpad(2);

        RfnTouScheduleConfigurationCommand::DailyTimes times;
        RfnTouScheduleConfigurationCommand::DailyRates rates;

        while( parse.isKeyValid( change_name ) )
        {
            if( change_nbr > rates.size() )
            {
                logInfo("Schedule " + CtiNumStr(schedule_nbr) + " has an invalid rate change.",
                        __FUNCTION__, __FILE__, __LINE__ );

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
 * Execute getconfig schedule
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
 * Execute putconfig holiday
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
            logInfo("Holiday " + holiday_str + " specified.",
                    __FUNCTION__, __FILE__, __LINE__ );

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



int RfnConsumerDevice::executePutConfigInstallFreezeDay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnConsumerDevice::executePutConfigInstallVoltageAveragingInterval(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

int RfnConsumerDevice::executePutConfigInstallDisplay(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests)
{
    return NoMethod;
}

/**
 * Execute putconfig install tou
 */
int RfnConsumerDevice::executePutConfigInstallTou( CtiRequestMsg     * pReq,
                                                   CtiCommandParser  & parse,
                                                   CtiMessageList    & retList,
                                                   RfnCommandList    & rfnRequests )
{
    using Commands::RfnTouScheduleConfigurationCommand;

    // check if the dynamic info has the current configuration
    if( ! parse.isKeyValid("force") && isTouConfigCurrent() )
    {
        return ConfigCurrent;
    }

    // if this is verify only
    if( parse.isKeyValid("verify") )
    {
        return ConfigNotCurrent;
    }

    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    RfnTouScheduleConfigurationCommand::Schedule schedule_to_send;

    //
    // Populate schedule to send
    //

    // day table
    schedule_to_send._dayTable.resize(8);

    schedule_to_send._dayTable[0] = deviceConfig->getValueFromKey( RfnStrings::MondaySchedule    );
    schedule_to_send._dayTable[1] = deviceConfig->getValueFromKey( RfnStrings::TuesdaySchedule   );
    schedule_to_send._dayTable[2] = deviceConfig->getValueFromKey( RfnStrings::WednesdaySchedule );
    schedule_to_send._dayTable[3] = deviceConfig->getValueFromKey( RfnStrings::ThursdaySchedule  );
    schedule_to_send._dayTable[4] = deviceConfig->getValueFromKey( RfnStrings::FridaySchedule    );
    schedule_to_send._dayTable[5] = deviceConfig->getValueFromKey( RfnStrings::SaturdaySchedule  );
    schedule_to_send._dayTable[6] = deviceConfig->getValueFromKey( RfnStrings::SundaySchedule    );
    schedule_to_send._dayTable[7] = deviceConfig->getValueFromKey( RfnStrings::HolidaySchedule   );

    // default rate
    schedule_to_send._defaultRate = deviceConfig->getValueFromKey( RfnStrings::DefaultTouRate );

    // switch rates
    RfnTouScheduleConfigurationCommand::DailyRates schedule1_rates(6),
                                                   schedule2_rates(6),
                                                   schedule3_rates(6),
                                                   schedule4_rates(6);

    schedule1_rates[0] = deviceConfig->getValueFromKey( RfnStrings::Schedule1MidnightRate );
    schedule1_rates[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Rate1        );
    schedule1_rates[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Rate2        );
    schedule1_rates[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Rate3        );
    schedule1_rates[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Rate4        );
    schedule1_rates[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Rate5        );

    schedule2_rates[0] = deviceConfig->getValueFromKey( RfnStrings::Schedule2MidnightRate );
    schedule2_rates[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Rate1        );
    schedule2_rates[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Rate2        );
    schedule2_rates[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Rate3        );
    schedule2_rates[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Rate4        );
    schedule2_rates[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Rate5        );

    schedule3_rates[0] = deviceConfig->getValueFromKey( RfnStrings::Schedule3MidnightRate );
    schedule3_rates[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Rate1        );
    schedule3_rates[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Rate2        );
    schedule3_rates[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Rate3        );
    schedule3_rates[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Rate4        );
    schedule3_rates[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Rate5        );

    schedule4_rates[0] = deviceConfig->getValueFromKey( RfnStrings::Schedule4MidnightRate );
    schedule4_rates[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Rate1        );
    schedule4_rates[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Rate2        );
    schedule4_rates[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Rate3        );
    schedule4_rates[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Rate4        );
    schedule4_rates[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Rate5        );

    schedule_to_send._rates[RfnTouScheduleConfigurationCommand::Schedule1] = schedule1_rates;
    schedule_to_send._rates[RfnTouScheduleConfigurationCommand::Schedule2] = schedule2_rates;
    schedule_to_send._rates[RfnTouScheduleConfigurationCommand::Schedule3] = schedule3_rates;
    schedule_to_send._rates[RfnTouScheduleConfigurationCommand::Schedule4] = schedule4_rates;

    // switch times
    RfnTouScheduleConfigurationCommand::DailyTimes schedule1_times(6),
                                                   schedule2_times(6),
                                                   schedule3_times(6),
                                                   schedule4_times(6);

    schedule1_times[0] = "00:00"; // midnight
    schedule1_times[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Time1 );
    schedule1_times[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Time2 );
    schedule1_times[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Time3 );
    schedule1_times[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Time4 );
    schedule1_times[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule1Time5 );

    schedule2_times[0] = "00:00"; // midnight
    schedule2_times[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Time1 );
    schedule2_times[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Time2 );
    schedule2_times[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Time3 );
    schedule2_times[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Time4 );
    schedule2_times[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule2Time5 );

    schedule3_times[0] = "00:00"; // midnight
    schedule3_times[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Time1 );
    schedule3_times[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Time2 );
    schedule3_times[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Time3 );
    schedule3_times[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Time4 );
    schedule3_times[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule3Time5 );

    schedule4_times[0] = "00:00"; // midnight
    schedule4_times[1] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Time1 );
    schedule4_times[2] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Time2 );
    schedule4_times[3] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Time3 );
    schedule4_times[4] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Time4 );
    schedule4_times[5] = deviceConfig->getValueFromKey( RfnStrings::Schedule4Time5 );

    schedule_to_send._times[RfnTouScheduleConfigurationCommand::Schedule1] = schedule1_times;
    schedule_to_send._times[RfnTouScheduleConfigurationCommand::Schedule2] = schedule2_times;
    schedule_to_send._times[RfnTouScheduleConfigurationCommand::Schedule3] = schedule3_times;
    schedule_to_send._times[RfnTouScheduleConfigurationCommand::Schedule4] = schedule4_times;

    //
    // create command
    //

    rfnRequests.push_back( boost::make_shared<RfnTouScheduleConfigurationCommand>( schedule_to_send ));

    return NoError;
}

namespace { // anonymous namespace

    typedef map<CtiDeviceBase::PaoInfoKeys, std::string> TouScheduleCompareKeysMap;

    const TouScheduleCompareKeysMap touScheduleCompareKeys = boost::assign::map_list_of
    // day table
    ( CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,        RfnStrings::MondaySchedule        )
    ( CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,       RfnStrings::TuesdaySchedule       )
    ( CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule,     RfnStrings::WednesdaySchedule     )
    ( CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,      RfnStrings::ThursdaySchedule      )
    ( CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,        RfnStrings::FridaySchedule        )
    ( CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,      RfnStrings::SaturdaySchedule      )
    ( CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,        RfnStrings::SundaySchedule        )
    ( CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,       RfnStrings::HolidaySchedule       )
    // default rate
    ( CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate,        RfnStrings::DefaultTouRate        )
    // schedule 1
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1MidnightRate, RfnStrings::Schedule1MidnightRate )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1,        RfnStrings::Schedule1Time1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1,        RfnStrings::Schedule1Rate1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2,        RfnStrings::Schedule1Time2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2,        RfnStrings::Schedule1Rate2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3,        RfnStrings::Schedule1Time3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3,        RfnStrings::Schedule1Rate3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4,        RfnStrings::Schedule1Time4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4,        RfnStrings::Schedule1Rate4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5,        RfnStrings::Schedule1Time5        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5,        RfnStrings::Schedule1Rate5        )
    // schedule 2
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2MidnightRate, RfnStrings::Schedule2MidnightRate )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1,        RfnStrings::Schedule2Time1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1,        RfnStrings::Schedule2Rate1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2,        RfnStrings::Schedule2Time2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2,        RfnStrings::Schedule2Rate2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3,        RfnStrings::Schedule2Time3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3,        RfnStrings::Schedule2Rate3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4,        RfnStrings::Schedule2Time4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4,        RfnStrings::Schedule2Rate4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5,        RfnStrings::Schedule2Time5        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5,        RfnStrings::Schedule2Rate5        )
    // schedule 3
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3MidnightRate, RfnStrings::Schedule3MidnightRate )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1,        RfnStrings::Schedule3Time1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1,        RfnStrings::Schedule3Rate1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2,        RfnStrings::Schedule3Time2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2,        RfnStrings::Schedule3Rate2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3,        RfnStrings::Schedule3Time3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3,        RfnStrings::Schedule3Rate3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4,        RfnStrings::Schedule3Time4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4,        RfnStrings::Schedule3Rate4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5,        RfnStrings::Schedule3Time5        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5,        RfnStrings::Schedule3Rate5        )
    // schedule 4
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4MidnightRate, RfnStrings::Schedule4MidnightRate )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1,        RfnStrings::Schedule4Time1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1,        RfnStrings::Schedule4Rate1        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2,        RfnStrings::Schedule4Time2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2,        RfnStrings::Schedule4Rate2        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3,        RfnStrings::Schedule4Time3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3,        RfnStrings::Schedule4Rate3        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4,        RfnStrings::Schedule4Time4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4,        RfnStrings::Schedule4Rate4        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5,        RfnStrings::Schedule4Time5        )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5,        RfnStrings::Schedule4Rate5        );

} // anonymous namespace

/**
 * check if the tou schedule from the config equals the dynamic info
 */
bool RfnConsumerDevice::isTouConfigCurrent()
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    for each( const TouScheduleCompareKeysMap::value_type & p in touScheduleCompareKeys )
    {
        std::string dynamicInfo;

        if( ! getDynamicInfo( p.first, dynamicInfo ) || deviceConfig->findValueForKey( p.second ) != dynamicInfo )
        {
            return false;
        }
    }

    return true;
}

/**
 * Execute getconfig holiday
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
    using Commands::RfnTouScheduleConfigurationCommand;

    boost::optional<RfnTouScheduleConfigurationCommand::Schedule> schedule_received = cmd.getTouScheduleReceived();

    if( ! schedule_received )
    {
        return;
    }

    //
    // Day table
    //
    if( schedule_received->_dayTable.size() == 8 )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,    schedule_received->_dayTable[0] );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,   schedule_received->_dayTable[1] );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule, schedule_received->_dayTable[2] );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,  schedule_received->_dayTable[3] );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,    schedule_received->_dayTable[4] );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,  schedule_received->_dayTable[5] );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,    schedule_received->_dayTable[6] );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,   schedule_received->_dayTable[7] );
    }

    //
    // Default rate
    //
    if( ! schedule_received->_defaultRate.empty() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate, schedule_received->_defaultRate );
    }

    //
    // Switch rates
    //
    {
        boost::optional<RfnTouScheduleConfigurationCommand::DailyRates> rates;

        // schedule 1
        rates = mapFind( schedule_received->_rates, RfnTouScheduleConfigurationCommand::Schedule1);
        if( rates && rates->size() == 6 )
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1MidnightRate, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1,        (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2,        (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3,        (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4,        (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5,        (*rates)[5] );
        }

        // schedule 2
        rates = mapFind( schedule_received->_rates, RfnTouScheduleConfigurationCommand::Schedule2);
        if( rates && rates->size() == 6 )
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2MidnightRate, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1,        (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2,        (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3,        (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4,        (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5,        (*rates)[5] );
        }

        // schedule 3
        rates = mapFind( schedule_received->_rates, RfnTouScheduleConfigurationCommand::Schedule3);
        if( rates && rates->size() == 6 )
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3MidnightRate, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1,        (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2,        (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3,        (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4,        (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5,        (*rates)[5] );
        }

        // schedule 3
        rates = mapFind( schedule_received->_rates, RfnTouScheduleConfigurationCommand::Schedule4);
        if( rates && rates->size() == 6 )
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4MidnightRate, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1,        (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2,        (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3,        (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4,        (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5,        (*rates)[5] );
        }
    }

    //
    // Switch times
    //
    {
        boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> times;

        // schedule 1
        times = mapFind( schedule_received->_times, RfnTouScheduleConfigurationCommand::Schedule1);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1, (*times)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2, (*times)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3, (*times)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4, (*times)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5, (*times)[5] );
        }

        // schedule 2
        times = mapFind( schedule_received->_times, RfnTouScheduleConfigurationCommand::Schedule2);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1, (*times)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2, (*times)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3, (*times)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4, (*times)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5, (*times)[5] );
        }

        // schedule 3
        times = mapFind( schedule_received->_times, RfnTouScheduleConfigurationCommand::Schedule3);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1, (*times)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2, (*times)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3, (*times)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4, (*times)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5, (*times)[5] );
        }

        // schedule 4
        times = mapFind( schedule_received->_times, RfnTouScheduleConfigurationCommand::Schedule4);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1, (*times)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2, (*times)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3, (*times)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4, (*times)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5, (*times)[5] );
        }
    }
}


void RfnConsumerDevice::handleResult( const Commands::RfnTouHolidayConfigurationCommand & cmd )
{
    using Commands::RfnTouHolidayConfigurationCommand;

    boost::optional<RfnTouHolidayConfigurationCommand::Holidays> holidays_received = cmd.getHolidaysReceived();

    if( ! holidays_received )
    {
        return;
    }

    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday1, (*holidays_received)[0].asStringUSFormat() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday2, (*holidays_received)[1].asStringUSFormat() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday3, (*holidays_received)[2].asStringUSFormat() );
}


}
}

