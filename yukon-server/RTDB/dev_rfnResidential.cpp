#include "precompiled.h"

#include "std_helper.h"
#include "config_data_rfn.h"
#include "config_device.h"
#include "MissingConfigDataException.h"
#include "dev_rfnResidential.h"
#include "devicetypes.h"

#include <boost/optional.hpp>
#include <boost/make_shared.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/type_traits.hpp>

#include <limits>
#include <string>


namespace Cti {
namespace Devices {

using Config::RfnStrings;

const RfnDevice::InstallMap RfnResidentialDevice::_putConfigInstallMap = initPutConfigInstallMap();
const RfnDevice::InstallMap RfnResidentialDevice::_getConfigInstallMap = initGetConfigInstallMap();

/**
 * initialize  static variable _putConfigInstallMap
 */
const RfnDevice::InstallMap RfnResidentialDevice::initPutConfigInstallMap()
{
    const InstallMap m = boost::assign::map_list_of
            ("freezeday",        static_cast<InstallMethod>(&RfnResidentialDevice::executePutConfigDemandFreezeDay))
            ("tou",              static_cast<InstallMethod>(&RfnResidentialDevice::executePutConfigInstallTou))
            ("voltageaveraging", static_cast<InstallMethod>(&RfnResidentialDevice::executePutConfigVoltageAveragingInterval))
            ("display",          static_cast<InstallMethod>(&RfnResidentialDevice::executePutConfigDisplay))
            ("ovuv",             static_cast<InstallMethod>(&RfnResidentialDevice::executePutConfigOvUv));

    return m;
}

/**
 * initialize  static variable _getConfigInstallMap
 */
const RfnDevice::InstallMap RfnResidentialDevice::initGetConfigInstallMap()
{
    const InstallMap m = boost::assign::map_list_of
            ("freezeday",        static_cast<InstallMethod>(&RfnResidentialDevice::executeReadDemandFreezeInfo))
            ("tou",              static_cast<InstallMethod>(&RfnResidentialDevice::executeGetConfigInstallTou))
            ("voltageaveraging", static_cast<InstallMethod>(&RfnResidentialDevice::executeGetConfigVoltageAveragingInterval))
            ("ovuv",             static_cast<InstallMethod>(&RfnResidentialDevice::executeGetConfigOvUv));

    return m;
}

const RfnDevice::InstallMap & RfnResidentialDevice::getPutConfigInstallMap() const
{
    return _putConfigInstallMap;
}

const RfnDevice::InstallMap & RfnResidentialDevice::getGetConfigInstallMap() const
{
    return _getConfigInstallMap;
}

namespace { // anonymous namespace

/**
 * throws MissingConfigDataException() if no config value exist
 */
std::string getConfigData( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    boost::optional<std::string> val = deviceConfig->findValueForKey( configKey );

    if( ! val )
    {
        throw MissingConfigDataException( configKey );
    }

    return *val;
}

/**
 * retrieve data from a local config map, throws MissingConfigDataException() if no config value can be found
 */
template<class Map>
typename Map::mapped_type getConfigData( const Map & configMap, const std::string & configKey )
{
    boost::optional<Map::mapped_type> val = mapFind(configMap, configKey);

    if( ! val )
    {
        throw MissingConfigDataException( configKey );
    }

    return *val;
}

/**
 * create CtiDate object from string "mm/dd/yyyy"
 */
CtiDate getDateFromString( std::string date )
{
    int month, day, year;
    char sep1, sep2;

    std::istringstream ss(date);
    ss >> month >> sep1 >> day >> sep2 >> year;

    return CtiDate(day, month, year);
}

} // anonymous namespace


int RfnResidentialDevice::executePutConfigVoltageProfile( CtiRequestMsg     * pReq,
                                                          CtiCommandParser  & parse,
                                                          CtiMessageList    & retList,
                                                          RfnCommandList    & rfnRequests )
{
    // putconfig voltage profile demandinterval 1234 lpinterval 123
    // putconfig voltage profile enable|disable

    using Commands::RfnLoadProfileSetRecordingCommand;
    using Commands::RfnVoltageProfileSetConfigurationCommand;

    //
    // enable|disable recording
    //

    if( parse.isKeyValid("voltage_profile_enable") )
    {
        RfnLoadProfileSetRecordingCommand::RecordingOption option = (parse.getiValue("voltage_profile_enable")) ? RfnLoadProfileSetRecordingCommand::EnableRecording
                                                                                                                : RfnLoadProfileSetRecordingCommand::DisableRecording;

        rfnRequests.push_back( boost::make_shared<RfnLoadProfileSetRecordingCommand>( option ));

        return NoError;
    }

    //
    // demand interval
    //

    const int demand_interval_seconds = parse.getiValue("demand_interval_seconds");

    if( demand_interval_seconds < 0 )
    {
        logInfo("Missing \"demandinterval\" parameter.",
                __FUNCTION__, __FILE__, __LINE__ );

        return MISPARAM;
    }

    //
    // load profile interval
    //

    const int load_profile_interval_minutes = parse.getiValue("load_profile_interval_minutes");

    if( load_profile_interval_minutes < 0 )
    {
        logInfo("Missing \"lpinterval\" parameter.",
                __FUNCTION__, __FILE__, __LINE__ );

        return MISPARAM;
    }

    rfnRequests.push_back( boost::make_shared<RfnVoltageProfileSetConfigurationCommand>( demand_interval_seconds,
                                                                                         load_profile_interval_minutes ));

    return NoError;
}

int RfnResidentialDevice::executeGetConfigVoltageProfile( CtiRequestMsg     * pReq,
                                                          CtiCommandParser  & parse,
                                                          CtiMessageList    & retList,
                                                          RfnCommandList    & rfnRequests )
{
    // getconfig voltage profile
    // getconfig voltage profile state

    using Commands::RfnLoadProfileGetRecordingCommand;
    using Commands::RfnVoltageProfileGetConfigurationCommand;

    if( parse.isKeyValid("voltage_profile_state") )
    {
        rfnRequests.push_back( boost::make_shared<RfnLoadProfileGetRecordingCommand>());

        return NoError;
    }

    rfnRequests.push_back( boost::make_shared<RfnVoltageProfileGetConfigurationCommand>());

    return NoError;
}


int RfnResidentialDevice::executeGetValueVoltageProfile( CtiRequestMsg     * pReq,
                                                         CtiCommandParser  & parse,
                                                         CtiMessageList    & retList,
                                                         RfnCommandList    & rfnRequests )
{
    // getvalue voltage profile 12/13/2005 12/15/2005

    using Commands::RfnLoadProfileReadPointsCommand;

    //
    // date begin
    //

    boost::optional<std::string> date_begin = parse.findStringForKey("read_points_date_begin");

    if( ! date_begin )
    {
        logInfo("Missing voltage profile start date.",
                __FUNCTION__, __FILE__, __LINE__ );

        return MISPARAM;
    }

    CtiDate begin = getDateFromString( *date_begin );

    //
    // date end
    //

    boost::optional<std::string> date_end = parse.findStringForKey("read_points_date_end");

    if( ! date_end )
    {
        logInfo("Missing voltage profile end date.",
                __FUNCTION__, __FILE__, __LINE__ );

        return MISPARAM;
    }

    CtiDate end = getDateFromString( *date_end );

    rfnRequests.push_back( boost::make_shared<RfnLoadProfileReadPointsCommand>( CtiTime::now(),
                                                                                begin,
                                                                                end ));

    return NoError;
}


int RfnResidentialDevice::executePutConfigVoltageAveragingInterval( CtiRequestMsg     * pReq,
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

            return ErrorInvalidConfigData;
        }

        configSettings.demandInterval = static_cast<unsigned>( *configValue );
    }

    {
        const std::string           configKey( Config::RfnStrings::profileInterval );
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

            return ErrorInvalidConfigData;
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

    rfnRequests.push_back( boost::make_shared<Commands::RfnVoltageProfileSetConfigurationCommand>( *configSettings.demandInterval,
                                                                                                   *configSettings.loadProfileInterval ) );

    return NoError;
}


int RfnResidentialDevice::executeGetConfigVoltageAveragingInterval( CtiRequestMsg     * pReq,
                                                                 CtiCommandParser  & parse,
                                                                 CtiMessageList    & retList,
                                                                 RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnVoltageProfileGetConfigurationCommand>() );

    return NoError;
}


//int RfnResidentialDevice::executeLoadProfileRecording( CtiRequestMsg     * pReq,
//                                                       CtiCommandParser  & parse,
//                                                       CtiMessageList    & retList,
//                                                       RfnCommandList    & rfnRequests )
//{
//    // TBD
//
//    if ( 0 /* parse says enable or disable recording -- TBD */ )
//    {
//        Commands::RfnLoadProfileRecordingCommand::RecordingOption option =
//            Commands::RfnLoadProfileRecordingCommand::DisableRecording;  // get from parse...
//
//        rfnRequests.push_back( boost::make_shared<Commands::RfnLoadProfileSetRecordingCommand>( option ) );
//    }
//    else    // reading the state of recording from the device
//    {
//        rfnRequests.push_back( boost::make_shared<Commands::RfnLoadProfileGetRecordingCommand>() );
//    }
//
//    return NoError;
//}


int RfnResidentialDevice::executePutConfigDemandFreezeDay( CtiRequestMsg     * pReq,
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

            return ErrorInvalidConfigData;
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


int RfnResidentialDevice::executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                                     CtiCommandParser  & parse,
                                                     CtiMessageList    & retList,
                                                     RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnImmediateDemandFreezeCommand>() );

    return NoError;
}


int RfnResidentialDevice::executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                                    CtiCommandParser  & parse,
                                                    CtiMessageList    & retList,
                                                    RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetDemandFreezeInfoCommand>() );

    return NoError;
}


int RfnResidentialDevice::executeTouCriticalPeak( CtiRequestMsg     * pReq,
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
int RfnResidentialDevice::executePutConfigTou( CtiRequestMsg     * pReq,
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
int RfnResidentialDevice::executeGetConfigTou( CtiRequestMsg     * pReq,
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
int RfnResidentialDevice::executePutConfigHoliday( CtiRequestMsg     * pReq,
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

/**
 * Execute putconfig install tou
 */
int RfnResidentialDevice::executePutConfigInstallTou( CtiRequestMsg     * pReq,
                                                   CtiCommandParser  & parse,
                                                   CtiMessageList    & retList,
                                                   RfnCommandList    & rfnRequests )
{
    using Commands::RfnTouScheduleConfigurationCommand;

    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return NoConfigData;
        }

        std::map<std::string, std::string> configMap;

        // check if the dynamic info has the current configuration
        if( isTouConfigCurrent( deviceConfig, configMap ) && ! parse.isKeyValid("force") )
        {
            return ConfigCurrent;
        }

        // if this is verify only
        if( parse.isKeyValid("verify") )
        {
            return ConfigNotCurrent;
        }

        RfnTouScheduleConfigurationCommand::Schedule schedule_to_send;

        //
        // Populate schedule to send
        //

        // day table
        schedule_to_send._dayTable.resize(8);

        schedule_to_send._dayTable[0] = getConfigData( configMap, RfnStrings::MondaySchedule    );
        schedule_to_send._dayTable[1] = getConfigData( configMap, RfnStrings::TuesdaySchedule   );
        schedule_to_send._dayTable[2] = getConfigData( configMap, RfnStrings::WednesdaySchedule );
        schedule_to_send._dayTable[3] = getConfigData( configMap, RfnStrings::ThursdaySchedule  );
        schedule_to_send._dayTable[4] = getConfigData( configMap, RfnStrings::FridaySchedule    );
        schedule_to_send._dayTable[5] = getConfigData( configMap, RfnStrings::SaturdaySchedule  );
        schedule_to_send._dayTable[6] = getConfigData( configMap, RfnStrings::SundaySchedule    );
        schedule_to_send._dayTable[7] = getConfigData( configMap, RfnStrings::HolidaySchedule   );

        // default rate
        schedule_to_send._defaultRate = getConfigData( configMap, RfnStrings::DefaultTouRate );

        // switch rates
        RfnTouScheduleConfigurationCommand::DailyRates schedule1_rates(6),
                                                       schedule2_rates(6),
                                                       schedule3_rates(6),
                                                       schedule4_rates(6);

        schedule1_rates[0] = getConfigData( configMap, RfnStrings::Schedule1Rate0 );
        schedule1_rates[1] = getConfigData( configMap, RfnStrings::Schedule1Rate1 );
        schedule1_rates[2] = getConfigData( configMap, RfnStrings::Schedule1Rate2 );
        schedule1_rates[3] = getConfigData( configMap, RfnStrings::Schedule1Rate3 );
        schedule1_rates[4] = getConfigData( configMap, RfnStrings::Schedule1Rate4 );
        schedule1_rates[5] = getConfigData( configMap, RfnStrings::Schedule1Rate5 );

        schedule2_rates[0] = getConfigData( configMap, RfnStrings::Schedule2Rate0 );
        schedule2_rates[1] = getConfigData( configMap, RfnStrings::Schedule2Rate1 );
        schedule2_rates[2] = getConfigData( configMap, RfnStrings::Schedule2Rate2 );
        schedule2_rates[3] = getConfigData( configMap, RfnStrings::Schedule2Rate3 );
        schedule2_rates[4] = getConfigData( configMap, RfnStrings::Schedule2Rate4 );
        schedule2_rates[5] = getConfigData( configMap, RfnStrings::Schedule2Rate5 );

        schedule3_rates[0] = getConfigData( configMap, RfnStrings::Schedule3Rate0 );
        schedule3_rates[1] = getConfigData( configMap, RfnStrings::Schedule3Rate1 );
        schedule3_rates[2] = getConfigData( configMap, RfnStrings::Schedule3Rate2 );
        schedule3_rates[3] = getConfigData( configMap, RfnStrings::Schedule3Rate3 );
        schedule3_rates[4] = getConfigData( configMap, RfnStrings::Schedule3Rate4 );
        schedule3_rates[5] = getConfigData( configMap, RfnStrings::Schedule3Rate5 );

        schedule4_rates[0] = getConfigData( configMap, RfnStrings::Schedule4Rate0 );
        schedule4_rates[1] = getConfigData( configMap, RfnStrings::Schedule4Rate1 );
        schedule4_rates[2] = getConfigData( configMap, RfnStrings::Schedule4Rate2 );
        schedule4_rates[3] = getConfigData( configMap, RfnStrings::Schedule4Rate3 );
        schedule4_rates[4] = getConfigData( configMap, RfnStrings::Schedule4Rate4 );
        schedule4_rates[5] = getConfigData( configMap, RfnStrings::Schedule4Rate5 );

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
        schedule1_times[1] = getConfigData( configMap, RfnStrings::Schedule1Time1 );
        schedule1_times[2] = getConfigData( configMap, RfnStrings::Schedule1Time2 );
        schedule1_times[3] = getConfigData( configMap, RfnStrings::Schedule1Time3 );
        schedule1_times[4] = getConfigData( configMap, RfnStrings::Schedule1Time4 );
        schedule1_times[5] = getConfigData( configMap, RfnStrings::Schedule1Time5 );

        schedule2_times[0] = "00:00"; // midnight
        schedule2_times[1] = getConfigData( configMap, RfnStrings::Schedule2Time1 );
        schedule2_times[2] = getConfigData( configMap, RfnStrings::Schedule2Time2 );
        schedule2_times[3] = getConfigData( configMap, RfnStrings::Schedule2Time3 );
        schedule2_times[4] = getConfigData( configMap, RfnStrings::Schedule2Time4 );
        schedule2_times[5] = getConfigData( configMap, RfnStrings::Schedule2Time5 );

        schedule3_times[0] = "00:00"; // midnight
        schedule3_times[1] = getConfigData( configMap, RfnStrings::Schedule3Time1 );
        schedule3_times[2] = getConfigData( configMap, RfnStrings::Schedule3Time2 );
        schedule3_times[3] = getConfigData( configMap, RfnStrings::Schedule3Time3 );
        schedule3_times[4] = getConfigData( configMap, RfnStrings::Schedule3Time4 );
        schedule3_times[5] = getConfigData( configMap, RfnStrings::Schedule3Time5 );

        schedule4_times[0] = "00:00"; // midnight
        schedule4_times[1] = getConfigData( configMap, RfnStrings::Schedule4Time1 );
        schedule4_times[2] = getConfigData( configMap, RfnStrings::Schedule4Time2 );
        schedule4_times[3] = getConfigData( configMap, RfnStrings::Schedule4Time3 );
        schedule4_times[4] = getConfigData( configMap, RfnStrings::Schedule4Time4 );
        schedule4_times[5] = getConfigData( configMap, RfnStrings::Schedule4Time5 );

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
    catch( const MissingConfigDataException &e )
    {
        logInfo( e.what(),
                __FUNCTION__, __FILE__, __LINE__ );

        return NoConfigData;
    }
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
    ( CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate, RfnStrings::DefaultTouRate )
    // schedule 1
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0, RfnStrings::Schedule1Rate0 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1, RfnStrings::Schedule1Time1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1, RfnStrings::Schedule1Rate1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2, RfnStrings::Schedule1Time2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2, RfnStrings::Schedule1Rate2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3, RfnStrings::Schedule1Time3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3, RfnStrings::Schedule1Rate3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4, RfnStrings::Schedule1Time4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4, RfnStrings::Schedule1Rate4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5, RfnStrings::Schedule1Time5 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5, RfnStrings::Schedule1Rate5 )
    // schedule 2
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0, RfnStrings::Schedule2Rate0 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1, RfnStrings::Schedule2Time1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1, RfnStrings::Schedule2Rate1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2, RfnStrings::Schedule2Time2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2, RfnStrings::Schedule2Rate2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3, RfnStrings::Schedule2Time3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3, RfnStrings::Schedule2Rate3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4, RfnStrings::Schedule2Time4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4, RfnStrings::Schedule2Rate4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5, RfnStrings::Schedule2Time5 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5, RfnStrings::Schedule2Rate5 )
    // schedule 3
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0, RfnStrings::Schedule3Rate0 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1, RfnStrings::Schedule3Time1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1, RfnStrings::Schedule3Rate1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2, RfnStrings::Schedule3Time2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2, RfnStrings::Schedule3Rate2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3, RfnStrings::Schedule3Time3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3, RfnStrings::Schedule3Rate3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4, RfnStrings::Schedule3Time4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4, RfnStrings::Schedule3Rate4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5, RfnStrings::Schedule3Time5 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5, RfnStrings::Schedule3Rate5 )
    // schedule 4
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0, RfnStrings::Schedule4Rate0 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1, RfnStrings::Schedule4Time1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1, RfnStrings::Schedule4Rate1 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2, RfnStrings::Schedule4Time2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2, RfnStrings::Schedule4Rate2 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3, RfnStrings::Schedule4Time3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3, RfnStrings::Schedule4Rate3 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4, RfnStrings::Schedule4Time4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4, RfnStrings::Schedule4Rate4 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5, RfnStrings::Schedule4Time5 )
    ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5, RfnStrings::Schedule4Rate5 );

} // anonymous namespace

/**
 * Check if the tou schedule from the config equals the dynamic info.
 * Throws MissingConfigDataException() if no config data exist
 */
bool RfnResidentialDevice::isTouConfigCurrent( const Config::DeviceConfigSPtr &deviceConfig, std::map<std::string, std::string> &configMap )
{
    bool bConfigCurrent = true;

    std::string dynamicInfo,
                configValue;

    for each( const TouScheduleCompareKeysMap::value_type & p in touScheduleCompareKeys )
    {
        configValue = getConfigData( deviceConfig, p.second ); // throws MissingConfigDataException()

        configMap[p.second] = configValue;

        if( ! getDynamicInfo( p.first, dynamicInfo ) || configValue != dynamicInfo )
        {
            bConfigCurrent = false;
        }
    }

    return bConfigCurrent;
}

int RfnResidentialDevice::executeGetConfigInstallTou( CtiRequestMsg     * pReq,
                                                   CtiCommandParser  & parse,
                                                   CtiMessageList    & retList,
                                                   RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouScheduleConfigurationCommand>());

    return NoError;
}


/**
 * Execute getconfig holiday
 */
int RfnResidentialDevice::executeGetConfigHoliday( CtiRequestMsg     * pReq,
                                                CtiCommandParser  & parse,
                                                CtiMessageList    & retList,
                                                RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouHolidayConfigurationCommand>());

    return NoError;
}

int RfnResidentialDevice::executePutConfigDisplay( CtiRequestMsg     * pReq,
                                                   CtiCommandParser  & parse,
                                                   CtiMessageList    & retList,
                                                   RfnCommandList    & rfnRequests )
{
    return NoMethod;
}

int RfnResidentialDevice::executePutConfigOvUv( CtiRequestMsg    * pReq,
                                                CtiCommandParser & parse,
                                                CtiMessageList   & retList,
                                                RfnCommandList   & rfnRequests )
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return NoConfigData;
    }

    {
        boost::optional<bool>   configOvUvEnabled,
                                paoOvUvEnabled;
        {
            const std::string           configKey( Config::RfnStrings::OvUvEnabled );
            const boost::optional<bool> configValue = deviceConfig->findBoolValueForKey( configKey );

            if ( ! configValue  )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device \"" << getName() << "\" - Missing value for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                return NoConfigData;
            }

            configOvUvEnabled = *configValue;
        }
        {
            long pao_value;

            if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled, pao_value ) )
            {
                paoOvUvEnabled = static_cast<bool>( pao_value );
            }
        }

        if( *configOvUvEnabled == *paoOvUvEnabled )  // both exist and are equal
        {
            if ( parse.isKeyValid("force") )
            {
                Commands::RfnSetOvUvAlarmProcessingStateCommand::AlarmStates state = *configOvUvEnabled
                    ? Commands::RfnSetOvUvAlarmProcessingStateCommand::EnableOvUv
                    : Commands::RfnSetOvUvAlarmProcessingStateCommand::DisableOvUv;

                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvAlarmProcessingStateCommand>( state ) );
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
            else
            {
                Commands::RfnSetOvUvAlarmProcessingStateCommand::AlarmStates state = *configOvUvEnabled
                    ? Commands::RfnSetOvUvAlarmProcessingStateCommand::EnableOvUv
                    : Commands::RfnSetOvUvAlarmProcessingStateCommand::DisableOvUv;

                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvAlarmProcessingStateCommand>( state ) );
            }
        }
    }

    {
        boost::optional<unsigned>   configOvUvReportingInterval,
                                    paoOvUvReportingInterval;
        {
            const std::string           configKey( Config::RfnStrings::OvUvAlarmReportingInterval );
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

                return ErrorInvalidConfigData;
            }

            configOvUvReportingInterval = static_cast<unsigned>( *configValue );
        }
        {
            long pao_value;

            if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval, pao_value ) )
            {
                paoOvUvReportingInterval = static_cast<unsigned>( pao_value );
            }
        }

        if ( *configOvUvReportingInterval == *paoOvUvReportingInterval )
        {
            if ( parse.isKeyValid("force") )
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvNewAlarmReportIntervalCommand>( *configOvUvReportingInterval ) );
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
            else
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvNewAlarmReportIntervalCommand>( *configOvUvReportingInterval ) );
            }
        }
    }

    {
        boost::optional<unsigned>   configOvUvRepeatInterval,
                                    paoOvUvRepeatInterval;
        {
            const std::string           configKey( Config::RfnStrings::OvUvAlarmRepeatInterval );
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

                return ErrorInvalidConfigData;
            }

            configOvUvRepeatInterval = static_cast<unsigned>( *configValue );
        }
        {
            long pao_value;

            if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval, pao_value ) )
            {
                paoOvUvRepeatInterval = static_cast<unsigned>( pao_value );
            }
        }

        if ( *configOvUvRepeatInterval == *paoOvUvRepeatInterval )
        {
            if ( parse.isKeyValid("force") )
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvAlarmRepeatIntervalCommand>( *configOvUvRepeatInterval ) );
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
            else
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvAlarmRepeatIntervalCommand>( *configOvUvRepeatInterval ) );
            }
        }
    }

    {
        boost::optional<unsigned>   configOvUvRepeatCount,
                                    paoOvUvRepeatCount;
        {
            const std::string           configKey( Config::RfnStrings::OvUvRepeatCount );
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

                return ErrorInvalidConfigData;
            }

            configOvUvRepeatCount = static_cast<unsigned>( *configValue );
        }
        {
            long pao_value;

            if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount, pao_value ) )
            {
                paoOvUvRepeatCount = static_cast<unsigned>( pao_value );
            }
        }

        if ( *configOvUvRepeatCount == *paoOvUvRepeatCount )
        {
            if ( parse.isKeyValid("force") )
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvAlarmRepeatCountCommand>( *configOvUvRepeatCount ) );
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
            else
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvAlarmRepeatCountCommand>( *configOvUvRepeatCount ) );
            }
        }
    }

    // get the meter ID

    Commands::RfnOvUvConfigurationCommand::MeterID  meterID = Commands::RfnOvUvConfigurationCommand::Unspecified;

    switch ( getType() )
    {
        case TYPE_RFN410CL:
        case TYPE_RFN420CL:
        case TYPE_RFN420CD:
        {
                meterID = Commands::RfnOvUvConfigurationCommand::CentronC2SX;
                break;
        }
        case TYPE_RFN410FX:
        case TYPE_RFN410FD:
        case TYPE_RFN420FX:
        case TYPE_RFN420FD:
        case TYPE_RFN420FRX:
        case TYPE_RFN420FRD:
        {
            meterID = Commands::RfnOvUvConfigurationCommand::LGFocusAX;
            break;
        }
        case TYPE_RFN420FL:
        {
            meterID = Commands::RfnOvUvConfigurationCommand::LGFocusAL;
            break;
        }
        default:
        {
            meterID = Commands::RfnOvUvConfigurationCommand::Unspecified;
        }
    }

    {
        boost::optional<double> configOvThreshold,
                                paoOvThreshold;
        {
            const std::string   configKey( Config::RfnStrings::OvThreshold );
            const double        configValue = deviceConfig->getFloatValueFromKey( configKey );

            if ( configValue != std::numeric_limits<double>::min() )
            {
                configOvThreshold = configValue;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device \"" << getName() << "\" - Missing value for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                return NoConfigData;
            }
        }
        {
            double pao_value;

            if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvThreshold, pao_value ) )
            {
                paoOvThreshold = pao_value;
            }
        }

        Commands::RfnOvUvConfigurationCommand::EventID  eventID = Commands::RfnOvUvConfigurationCommand::OverVoltage;

        if ( *configOvThreshold == *paoOvThreshold )
        {
            if ( parse.isKeyValid("force") )
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvSetThresholdCommand>( meterID, eventID, *configOvThreshold ) );
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
            else
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvSetThresholdCommand>( meterID, eventID, *configOvThreshold ) );
            }
        }
    }

    {
        boost::optional<double> configUvThreshold,
                                paoUvThreshold;
        {
            const std::string   configKey( Config::RfnStrings::UvThreshold );
            const double        configValue = deviceConfig->getFloatValueFromKey( configKey );

            if ( configValue != std::numeric_limits<double>::min() )
            {
                configUvThreshold = configValue;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device \"" << getName() << "\" - Missing value for config key \"" << configKey << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                return NoConfigData;
            }
        }
        {
            double pao_value;

            if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_UvThreshold, pao_value ) )
            {
                paoUvThreshold = pao_value;
            }
        }

        Commands::RfnOvUvConfigurationCommand::EventID  eventID = Commands::RfnOvUvConfigurationCommand::UnderVoltage;

        if ( *configUvThreshold == *paoUvThreshold )
        {
            if ( parse.isKeyValid("force") )
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvSetThresholdCommand>( meterID, eventID, *configUvThreshold ) );
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
            else
            {
                rfnRequests.push_back( boost::make_shared<Commands::RfnSetOvUvSetThresholdCommand>( meterID, eventID, *configUvThreshold ) );
            }
        }
    }

    if ( ! parse.isKeyValid("force") && rfnRequests.size() == 0 )
    {
        return ConfigCurrent;
    }

    return NoError;
}

int RfnResidentialDevice::executeGetConfigOvUv( CtiRequestMsg    * pReq,
                                                CtiCommandParser & parse,
                                                CtiMessageList   & retList,
                                                RfnCommandList   & rfnRequests )
{
    // get the meter ID

    Commands::RfnOvUvConfigurationCommand::MeterID  meterID = Commands::RfnOvUvConfigurationCommand::Unspecified;

    switch ( getType() )
    {
        case TYPE_RFN410CL:
        case TYPE_RFN420CL:
        case TYPE_RFN420CD:
        {
            meterID = Commands::RfnOvUvConfigurationCommand::CentronC2SX;
            break;
        }
        case TYPE_RFN410FX:
        case TYPE_RFN410FD:
        case TYPE_RFN420FX:
        case TYPE_RFN420FD:
        case TYPE_RFN420FRX:
        case TYPE_RFN420FRD:
        {
            meterID = Commands::RfnOvUvConfigurationCommand::LGFocusAX;
            break;
        }
        case TYPE_RFN420FL:
        {
            meterID = Commands::RfnOvUvConfigurationCommand::LGFocusAL;
            break;
        }
        default:
        {
            meterID = Commands::RfnOvUvConfigurationCommand::Unspecified;
        }
    }

    rfnRequests.push_back( boost::make_shared<Commands::RfnGetOvUvAlarmConfigurationCommand>( meterID, Commands::RfnOvUvConfigurationCommand::OverVoltage ) );
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetOvUvAlarmConfigurationCommand>( meterID, Commands::RfnOvUvConfigurationCommand::UnderVoltage ) );

    return NoError;
}


void RfnResidentialDevice::handleResult( const Commands::RfnGetOvUvAlarmConfigurationCommand & cmd )
{
    Commands::RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration   config = cmd.getAlarmConfiguration();

    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvEnabled,   config.ovuvEnabled );

    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmReportingInterval, config.ovuvAlarmReportingInterval );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvAlarmRepeatInterval,    config.ovuvAlarmRepeatInterval );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvUvRepeatCount,            config.ovuvAlarmRepeatCount );

    if ( config.ovThreshold )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_OvThreshold, *config.ovThreshold );
    }
    else
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_UvThreshold, *config.uvThreshold );
    }
}


void RfnResidentialDevice::handleResult( const Commands::RfnVoltageProfileGetConfigurationCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandInterval,      cmd.getDemandIntervalSeconds() );
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_LoadProfileInterval, cmd.getLoadProfileIntervalMinutes() );
}


void RfnResidentialDevice::handleResult( const Commands::RfnLoadProfileGetRecordingCommand & cmd )
{
    // TBD

    Commands::RfnLoadProfileRecordingCommand::RecordingOption option = cmd.getRecordingOption();
}


void RfnResidentialDevice::handleResult( const Commands::RfnGetDemandFreezeInfoCommand & cmd )
{
    // TBD

    Commands::RfnGetDemandFreezeInfoCommand::DemandFreezeData freezeData = cmd.getDemandFreezeData();
}


void RfnResidentialDevice::handleResult( const Commands::RfnTouScheduleConfigurationCommand & cmd )
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
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1, (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2, (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3, (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4, (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5, (*rates)[5] );
        }

        // schedule 2
        rates = mapFind( schedule_received->_rates, RfnTouScheduleConfigurationCommand::Schedule2);
        if( rates && rates->size() == 6 )
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1, (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2, (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3, (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4, (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5, (*rates)[5] );
        }

        // schedule 3
        rates = mapFind( schedule_received->_rates, RfnTouScheduleConfigurationCommand::Schedule3);
        if( rates && rates->size() == 6 )
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1, (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2, (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3, (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4, (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5, (*rates)[5] );
        }

        // schedule 3
        rates = mapFind( schedule_received->_rates, RfnTouScheduleConfigurationCommand::Schedule4);
        if( rates && rates->size() == 6 )
        {
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0, (*rates)[0] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1, (*rates)[1] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2, (*rates)[2] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3, (*rates)[3] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4, (*rates)[4] );
            setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5, (*rates)[5] );
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


void RfnResidentialDevice::handleResult( const Commands::RfnTouHolidayConfigurationCommand & cmd )
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

