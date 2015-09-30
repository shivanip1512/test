#include "precompiled.h"

#include "std_helper.h"
#include "config_data_rfn.h"
#include "config_device.h"
#include "config_helpers.h"
#include "dev_rfnResidential.h"
#include "devicetypes.h"

#include <boost/optional.hpp>
#include <boost/make_shared.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/assign/list_inserter.hpp>
#include <boost/type_traits.hpp>
#include <boost/bimap.hpp>

#include <limits>
#include <string>
#include <sstream>

namespace Cti {
namespace Devices {

using Config::RfnStrings;

namespace { // anonymous namespace

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

unsigned getSecondsFromTimeString( std::string time )
{
    int hour, minute;
    char sep;

    std::istringstream ss(time);
    ss >> hour >> sep >> minute;

    return hour * 3600 + minute * 60;
}

const std::set<int> disconnectConfigTypes
{
    TYPE_RFN410FD,
    TYPE_RFN420CD,
    TYPE_RFN420FD,
    TYPE_RFN420FRD
};

typedef Commands::RfnRemoteDisconnectConfigurationCommand DisconnectCmd;

typedef boost::bimap<DisconnectCmd::Reconnect, std::string> ReconnectDisplayMap;
typedef boost::bimap<DisconnectCmd::DisconnectMode, std::string> DisconnectModeDisplayMap;

const ReconnectDisplayMap reconnectResolver = boost::assign::list_of< ReconnectDisplayMap::relation >
    ( DisconnectCmd::Reconnect_Arm, "ARM" )
    ( DisconnectCmd::Reconnect_Immediate, "IMMEDIATE" )
    ;

const DisconnectModeDisplayMap disconnectModeResolver = boost::assign::list_of< DisconnectModeDisplayMap::relation >
    ( DisconnectCmd::DisconnectMode_OnDemand, "ON_DEMAND" )
    ( DisconnectCmd::DisconnectMode_DemandThreshold, "DEMAND_THRESHOLD" )
    ( DisconnectCmd::DisconnectMode_Cycling, "CYCLING" )
    ;

const std::map<unsigned, DisconnectCmd::DemandInterval> intervalResolver
{
    {  5, DisconnectCmd::DemandInterval_Five    },
    { 10, DisconnectCmd::DemandInterval_Ten     },
    { 15, DisconnectCmd::DemandInterval_Fifteen }
};

} // anonymous namespace

RfnMeterDevice::ConfigMap RfnResidentialDevice::getConfigMethods(bool readOnly)
{
    ConfigMap m = RfnMeterDevice::getConfigMethods( readOnly );

    if( readOnly )
    {
        boost::assign::insert( m )
            ( ConfigPart::freezeday,        bindConfigMethod( &RfnResidentialDevice::executeReadDemandFreezeInfo,              this ) )
            ( ConfigPart::tou,              bindConfigMethod( &RfnResidentialDevice::executeGetConfigInstallTou,               this ) )
                ;

        if( disconnectConfigSupported() )
        {
            m.insert( ConfigMap::value_type( ConfigPart::disconnect, bindConfigMethod( &RfnResidentialDevice::executeGetConfigDisconnect, this ) ) );
        }
    }
    else
    {
        boost::assign::insert( m )
            ( ConfigPart::freezeday,        bindConfigMethod( &RfnResidentialDevice::executePutConfigDemandFreezeDay,          this ) )
            ( ConfigPart::tou,              bindConfigMethod( &RfnResidentialDevice::executePutConfigInstallTou,               this ) )
                ;

        if( disconnectConfigSupported() )
        {
            m.insert( ConfigMap::value_type( ConfigPart::disconnect, bindConfigMethod( &RfnResidentialDevice::executePutConfigDisconnect, this ) ) );
        }
    }

    return m;
}

YukonError_t RfnResidentialDevice::executePutConfigVoltageProfile( CtiRequestMsg     * pReq,
                                                                   CtiCommandParser  & parse,
                                                                   ReturnMsgList     & returnMsgs,
                                                                   RfnCommandList    & rfnRequests )
{
    // putconfig voltage profile enable|disable

    using Commands::RfnLoadProfileSetRecordingCommand;

    //
    // enable|disable recording
    //

    if( parse.isKeyValid("voltage_profile_enable") )
    {
        RfnLoadProfileSetRecordingCommand::RecordingOption option = (parse.getiValue("voltage_profile_enable")) ? RfnLoadProfileSetRecordingCommand::EnableRecording
                                                                                                                : RfnLoadProfileSetRecordingCommand::DisableRecording;

        rfnRequests.push_back( boost::make_shared<RfnLoadProfileSetRecordingCommand>( option ));

        return ClientErrors::None;
    }

    return ClientErrors::NoMethod;
}

YukonError_t RfnResidentialDevice::executeGetConfigVoltageProfile( CtiRequestMsg     * pReq,
                                                                   CtiCommandParser  & parse,
                                                                   ReturnMsgList     & returnMsgs,
                                                                   RfnCommandList    & rfnRequests )
{
    // getconfig voltage profile state

    using Commands::RfnLoadProfileGetRecordingCommand;

    if( parse.isKeyValid("voltage_profile_state") )
    {
        rfnRequests.push_back( boost::make_shared<RfnLoadProfileGetRecordingCommand>());

        return ClientErrors::None;
    }

    return ClientErrors::NoMethod;
}


YukonError_t RfnResidentialDevice::executeGetValueVoltageProfile( CtiRequestMsg     * pReq,
                                                                  CtiCommandParser  & parse,
                                                                  ReturnMsgList     & returnMsgs,
                                                                  RfnCommandList    & rfnRequests )
{
    // getvalue voltage profile 12/13/2005 12/15/2005

    using Commands::RfnLoadProfileReadPointsCommand;

    //
    // date begin
    //

    CtiDate date_begin;

    if( const boost::optional<std::string> dateStr = parse.findStringForKey("read_points_date_begin") )
    {
        date_begin = getDateFromString( *dateStr );
    }
    else
    {
        CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing voltage profile start date");

        return ClientErrors::MissingParameter;
    }

    CtiTime begin = date_begin;

    if( const boost::optional<std::string> timeStr = parse.findStringForKey("read_points_time_begin") )
    {
        begin += getSecondsFromTimeString(*timeStr);
    }

    //
    // date end
    //

    CtiTime end = date_begin + 1;  //  If no end date provided, default to end of the requested day

    if( const boost::optional<std::string> dateStr = parse.findStringForKey("read_points_date_end") )
    {
        CtiDate date_end = getDateFromString(*dateStr);

        if( const boost::optional<std::string> timeStr = parse.findStringForKey("read_points_time_end") )
        {
            end = date_end;
            end += getSecondsFromTimeString(*timeStr);
        }
        else
        {
            end = date_end + 1;
        }
    }

    rfnRequests.push_back( boost::make_shared<RfnLoadProfileReadPointsCommand>( CtiTime::now(),
                                                                                begin,
                                                                                end ));

    return ClientErrors::None;
}


YukonError_t RfnResidentialDevice::executePutValueTouReset(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    rfnRequests.push_back(
            boost::make_shared<Commands::RfnTouResetCommand>());

    return ClientErrors::None;
}

YukonError_t RfnResidentialDevice::executePutValueTouResetZero(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)
{
    rfnRequests.push_back(
            boost::make_shared<Commands::RfnTouResetZeroCommand>());

    return ClientErrors::None;
}


YukonError_t RfnResidentialDevice::executePutConfigDemandFreezeDay( CtiRequestMsg     * pReq,
                                                                    CtiCommandParser  & parse,
                                                                    ReturnMsgList     & returnMsgs,
                                                                    RfnCommandList    & rfnRequests )
{
    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
        }

        const unsigned char configFreezeDay               = getConfigData   <unsigned char> ( deviceConfig, Config::RfnStrings::demandFreezeDay );
        const boost::optional<unsigned char> paoFreezeDay = findDynamicInfo <unsigned char> ( CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay );

        // check if the dynamic info has the current configuration
        if( configFreezeDay == paoFreezeDay && ! parse.isKeyValid("force") )
        {
            return ClientErrors::ConfigCurrent;
        }

        // if this is verify only
        if( parse.isKeyValid("verify") )
        {
            return ClientErrors::ConfigNotCurrent;
        }

        rfnRequests.push_back( boost::make_shared<Commands::RfnDemandFreezeConfigurationCommand>( configFreezeDay ) );

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::InvalidConfigData;
    }
}


YukonError_t RfnResidentialDevice::executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                                                 CtiCommandParser  & parse,
                                                                 ReturnMsgList     & returnMsgs,
                                                                 RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnImmediateDemandFreezeCommand>() );

    return ClientErrors::None;
}


YukonError_t RfnResidentialDevice::executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                                                CtiCommandParser  & parse,
                                                                ReturnMsgList     & returnMsgs,
                                                                RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetDemandFreezeInfoCommand>() );

    return ClientErrors::None;
}


YukonError_t RfnResidentialDevice::executeGetStatusTou( CtiRequestMsg    * pReq,
                                                        CtiCommandParser & parse,
                                                        ReturnMsgList    & returnMsgs,
                                                        RfnCommandList   & rfnRequests)
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouStateConfigurationCommand>());

    return ClientErrors::None;
}


YukonError_t RfnResidentialDevice::executeTouCriticalPeak( CtiRequestMsg     * pReq,
                                                           CtiCommandParser  & parse,
                                                           ReturnMsgList     & returnMsgs,
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
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing hour value");

            return ClientErrors::BadParameter;
        }
        if ( parsedMinute == -1 )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing minute value");

            return ClientErrors::BadParameter;
        }

        const unsigned hour     = parsedHour;
        const unsigned minute   = parsedMinute;
        const std::string rate  = parse.getsValue( "tou_critical_peak_rate" );

        rfnRequests.push_back( boost::make_shared<Commands::RfnTouCriticalPeakCommand>( rate, hour, minute ) );
    }

    return ClientErrors::None;
}


/**
 * Execute putconfig tou schedule
 */
YukonError_t RfnResidentialDevice::executePutConfigTou( CtiRequestMsg     * pReq,
                                                        CtiCommandParser  & parse,
                                                        ReturnMsgList     & returnMsgs,
                                                        RfnCommandList    & rfnRequests )
{
    using Commands::RfnTouConfigurationCommand;
    using Commands::RfnTouStateConfigurationCommand;
    using Commands::RfnTouScheduleConfigurationCommand;
    using Commands::RfnTouScheduleSetConfigurationCommand;

    RfnTouScheduleConfigurationCommand::Schedule schedule;

    if( parse.isKeyValid("tou_enable") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouStateConfigurationCommand>( RfnTouConfigurationCommand::TouEnable ));

        return ClientErrors::None;
    }

    if( parse.isKeyValid("tou_disable") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouStateConfigurationCommand>( RfnTouConfigurationCommand::TouDisable ));

        return ClientErrors::None;
    }

    //
    // Day Table
    //
    if( ! parse.isKeyValid("tou_days") )
    {
        CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing day table");

        return ClientErrors::BadParameter;
    }

    const std::string dayTableStr = parse.getsValue("tou_days");

    for( int day_nbr = 0; day_nbr < dayTableStr.length(); day_nbr++ )
    {
        schedule._dayTable.push_back( RfnTouConfigurationCommand::SchedulePrefix + dayTableStr.substr(day_nbr,1) );
    }

    //
    // Default Rate
    //
    if( ! parse.isKeyValid("tou_default") )
    {
        CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Missing TOU default rate");

        return ClientErrors::BadParameter;
    }

    schedule._defaultRate = parse.getsValue("tou_default");

    //
    // Switch Rates and Times
    //
    int iter = 0;
    std::string schedule_name = "tou_schedule_" + CtiNumStr(iter).zpad(2);

    while( parse.isKeyValid( schedule_name ))
    {
        RfnTouScheduleConfigurationCommand::ScheduleNbr schedule_nbr =
                RfnTouScheduleConfigurationCommand::resolveScheduleName(
                        RfnTouConfigurationCommand::SchedulePrefix + CtiNumStr( parse.getiValue( schedule_name )));

        int change_nbr = 0;
        std::string change_name = schedule_name + "_" + CtiNumStr(change_nbr).zpad(2);

        RfnTouScheduleConfigurationCommand::DailyTimes times;
        RfnTouScheduleConfigurationCommand::DailyRates rates;

        while( parse.isKeyValid( change_name ) )
        {
            if( change_nbr > rates.size() )
            {
                CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Schedule "<< schedule_nbr <<" has an invalid rate change");

                return ClientErrors::BadParameter;
            }

            const std::string change_str = parse.getsValue( change_name );

            const std::string rate_str   = change_str.substr(0, 1),
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

    rfnRequests.push_back( boost::make_shared<RfnTouScheduleSetConfigurationCommand>( schedule ));

    return ClientErrors::None;
}

/**
 * Execute getconfig schedule
 */
YukonError_t RfnResidentialDevice::executeGetConfigTou( CtiRequestMsg     * pReq,
                                                        CtiCommandParser  & parse,
                                                        ReturnMsgList     & returnMsgs,
                                                        RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouScheduleGetConfigurationCommand>());

    return ClientErrors::None;
}

/**
 * Execute putconfig holiday
 */
YukonError_t RfnResidentialDevice::executePutConfigHoliday( CtiRequestMsg     * pReq,
                                                            CtiCommandParser  & parse,
                                                            ReturnMsgList     & returnMsgs,
                                                            RfnCommandList    & rfnRequests )
{
    using Commands::RfnTouHolidayConfigurationCommand;
    using Commands::RfnTouSetHolidayActiveCommand;
    using Commands::RfnTouCancelHolidayActiveCommand;

    if( parse.isKeyValid("holiday_set_active") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouSetHolidayActiveCommand>());

        return ClientErrors::None;
    }

    if( parse.isKeyValid("holiday_cancel_active") )
    {
        rfnRequests.push_back( boost::make_shared<RfnTouCancelHolidayActiveCommand>());

        return ClientErrors::None;
    }

    Commands::RfnTouHolidayConfigurationCommand::Holidays holidays;

    int holiday_nbr = 0;

    while( parse.isKeyValid("holiday_date" + CtiNumStr(holiday_nbr)))
    {
        const std::string holiday_str = parse.getsValue("holiday_date" + CtiNumStr(holiday_nbr));

        if( holiday_nbr >= holidays.size() )
        {
            CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Holiday "<< holiday_str <<" specified");

            return ClientErrors::BadParameter;
        }

        int day, month, year;
        char sep1, sep2;

        std::istringstream iss( holiday_str );
        iss >> month >> sep1 >> day >> sep2 >> year;

        holidays[holiday_nbr] = CtiDate(day, month, year);

        holiday_nbr++;
    }

    rfnRequests.push_back( boost::make_shared<RfnTouHolidayConfigurationCommand>( holidays ));

    return ClientErrors::None;
}

/**
 * Execute putconfig install tou
 */
YukonError_t RfnResidentialDevice::executePutConfigInstallTou( CtiRequestMsg     * pReq,
                                                               CtiCommandParser  & parse,
                                                               ReturnMsgList     & returnMsgs,
                                                               RfnCommandList    & rfnRequests )
{
    using Commands::RfnTouScheduleConfigurationCommand;
    using Commands::RfnTouScheduleSetConfigurationCommand;
    using Commands::RfnTouStateConfigurationCommand;

    try
    {
        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
        }

        const bool sendForced = parse.isKeyValid("force");

        std::map<std::string, std::string> configMap;
        const bool touScheduleMatches = isTouConfigCurrent( deviceConfig, configMap );

        const bool configTouEnabled = getConfigData<bool>( deviceConfig, RfnStrings::touEnabled );

        const bool touEnableMatches = (configTouEnabled == (getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_TouEnabled) == (long)true));

        // check if the dynamic info has the current configuration
        if( touScheduleMatches && touEnableMatches && ! sendForced )
        {
            return ClientErrors::ConfigCurrent;
        }

        // if this is verify only
        if( parse.isKeyValid("verify") )
        {
            return ClientErrors::ConfigNotCurrent;
        }

        if( ! touEnableMatches || sendForced )
        {
            rfnRequests.push_back(
                    boost::make_shared<RfnTouStateConfigurationCommand>(
                            configTouEnabled ?
                                RfnTouStateConfigurationCommand::TouEnable :
                                RfnTouStateConfigurationCommand::TouDisable));
        }

        if( ! touScheduleMatches || sendForced )
        {
            RfnTouScheduleConfigurationCommand::Schedule schedule_to_send;

            //
            // Populate schedule to send
            //

            // day table
            schedule_to_send._dayTable.resize(8);

            schedule_to_send._dayTable[0] = getConfigData( configMap, RfnStrings::SundaySchedule    );
            schedule_to_send._dayTable[1] = getConfigData( configMap, RfnStrings::MondaySchedule    );
            schedule_to_send._dayTable[2] = getConfigData( configMap, RfnStrings::TuesdaySchedule   );
            schedule_to_send._dayTable[3] = getConfigData( configMap, RfnStrings::WednesdaySchedule );
            schedule_to_send._dayTable[4] = getConfigData( configMap, RfnStrings::ThursdaySchedule  );
            schedule_to_send._dayTable[5] = getConfigData( configMap, RfnStrings::FridaySchedule    );
            schedule_to_send._dayTable[6] = getConfigData( configMap, RfnStrings::SaturdaySchedule  );
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

            rfnRequests.push_back(
                    boost::make_shared<RfnTouScheduleSetConfigurationCommand>(
                            schedule_to_send ));
        }

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
}

namespace { // anonymous namespace

typedef std::map<CtiDeviceBase::PaoInfoKeys, std::string> TouScheduleCompareKeysMap;

const TouScheduleCompareKeysMap touScheduleCompareKeys
{
    // day table
    { CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,        RfnStrings::MondaySchedule    },
    { CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,       RfnStrings::TuesdaySchedule   },
    { CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule,     RfnStrings::WednesdaySchedule },
    { CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,      RfnStrings::ThursdaySchedule  },
    { CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,        RfnStrings::FridaySchedule    },
    { CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,      RfnStrings::SaturdaySchedule  },
    { CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,        RfnStrings::SundaySchedule    },
    { CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,       RfnStrings::HolidaySchedule   },
    // default rate
    { CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate, RfnStrings::DefaultTouRate },
    // schedule 1
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0, RfnStrings::Schedule1Rate0 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1, RfnStrings::Schedule1Time1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1, RfnStrings::Schedule1Rate1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2, RfnStrings::Schedule1Time2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2, RfnStrings::Schedule1Rate2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3, RfnStrings::Schedule1Time3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3, RfnStrings::Schedule1Rate3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4, RfnStrings::Schedule1Time4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4, RfnStrings::Schedule1Rate4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5, RfnStrings::Schedule1Time5 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5, RfnStrings::Schedule1Rate5 },
    // schedule 2
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0, RfnStrings::Schedule2Rate0 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1, RfnStrings::Schedule2Time1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1, RfnStrings::Schedule2Rate1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2, RfnStrings::Schedule2Time2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2, RfnStrings::Schedule2Rate2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3, RfnStrings::Schedule2Time3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3, RfnStrings::Schedule2Rate3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4, RfnStrings::Schedule2Time4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4, RfnStrings::Schedule2Rate4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5, RfnStrings::Schedule2Time5 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5, RfnStrings::Schedule2Rate5 },
    // schedule 3
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0, RfnStrings::Schedule3Rate0 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1, RfnStrings::Schedule3Time1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1, RfnStrings::Schedule3Rate1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2, RfnStrings::Schedule3Time2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2, RfnStrings::Schedule3Rate2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3, RfnStrings::Schedule3Time3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3, RfnStrings::Schedule3Rate3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4, RfnStrings::Schedule3Time4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4, RfnStrings::Schedule3Rate4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5, RfnStrings::Schedule3Time5 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5, RfnStrings::Schedule3Rate5 },
    // schedule 4
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0, RfnStrings::Schedule4Rate0 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1, RfnStrings::Schedule4Time1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1, RfnStrings::Schedule4Rate1 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2, RfnStrings::Schedule4Time2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2, RfnStrings::Schedule4Rate2 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3, RfnStrings::Schedule4Time3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3, RfnStrings::Schedule4Rate3 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4, RfnStrings::Schedule4Time4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4, RfnStrings::Schedule4Rate4 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5, RfnStrings::Schedule4Time5 },
    { CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5, RfnStrings::Schedule4Rate5 }
};

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
        configValue = getConfigData<std::string>( deviceConfig, p.second ); // throws MissingConfigDataException()

        configMap[p.second] = configValue;

        if( bConfigCurrent && ! getDynamicInfo( p.first, dynamicInfo ) || configValue != dynamicInfo )
        {
            bConfigCurrent = false;
        }
    }

    return bConfigCurrent;
}

YukonError_t RfnResidentialDevice::executeGetConfigInstallTou( CtiRequestMsg     * pReq,
                                                               CtiCommandParser  & parse,
                                                               ReturnMsgList     & returnMsgs,
                                                               RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouScheduleGetConfigurationCommand>());

    return ClientErrors::None;
}


/**
 * Execute getconfig holiday
 */
YukonError_t RfnResidentialDevice::executeGetConfigHoliday( CtiRequestMsg     * pReq,
                                                            CtiCommandParser  & parse,
                                                            ReturnMsgList     & returnMsgs,
                                                            RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnTouHolidayConfigurationCommand>());

    return ClientErrors::None;
}


YukonError_t RfnResidentialDevice::executeGetConfigDisconnect( CtiRequestMsg    * pReq,
                                                               CtiCommandParser & parse,
                                                               ReturnMsgList    & returnMsgs,
                                                               RfnCommandList   & rfnRequests )
{
    if( ! disconnectConfigSupported() )
    {
        return ClientErrors::NoMethod;
    }

    rfnRequests.push_back( boost::make_shared<Commands::RfnRemoteDisconnectGetConfigurationCommand>() );

    return ClientErrors::None;
}

YukonError_t RfnResidentialDevice::executePutConfigDisconnect( CtiRequestMsg    * pReq,
                                                               CtiCommandParser & parse,
                                                               ReturnMsgList    & returnMsgs,
                                                               RfnCommandList   & rfnRequests )
{
    try
    {
        if( ! disconnectConfigSupported() )
        {
            return ClientErrors::NoMethod;
        }

        Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

        if( ! deviceConfig )
        {
            return ClientErrors::NoConfigData;
        }

        const std::string configDisconnectMode               = getConfigData   <std::string> ( deviceConfig, Config::RfnStrings::DisconnectMode );
        const boost::optional<std::string> paoDisconnectMode = findDynamicInfo <std::string> ( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode );

        const DisconnectCmd::DisconnectMode disconnectMode = resolveConfigData( disconnectModeResolver.right, configDisconnectMode, Config::RfnStrings::DisconnectMode );

        const bool disconnectModesMatch = (configDisconnectMode == paoDisconnectMode);

        switch( disconnectMode )
        {
            case DisconnectCmd::DisconnectMode_OnDemand:
            {
                const std::string configReconnectParam               = getConfigData   <std::string> ( deviceConfig, Config::RfnStrings::ReconnectParam );
                const boost::optional<std::string> paoReconnectParam = findDynamicInfo <std::string> ( CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam );

                const DisconnectCmd::Reconnect reconnectParam = resolveConfigData( reconnectResolver.right, configReconnectParam, Config::RfnStrings::ReconnectParam );

                if( ! disconnectModesMatch ||
                    configReconnectParam != paoReconnectParam  ||
                    parse.isKeyValid("force") )
                {
                    if( parse.isKeyValid("verify") )
                    {
                        return ClientErrors::ConfigNotCurrent;
                    }

                    rfnRequests.push_back( boost::make_shared<Commands::RfnRemoteDisconnectSetOnDemandConfigurationCommand>( reconnectParam ));
                }

                break;
            }
            case DisconnectCmd::DisconnectMode_DemandThreshold:
            {
                const std::string configReconnectParam               = getConfigData   <std::string> ( deviceConfig, Config::RfnStrings::ReconnectParam );
                const unsigned configDemandInterval                  = getConfigData   <unsigned>    ( deviceConfig, Config::RfnStrings::DisconnectDemandInterval );
                const double configDemandThreshold                   = getConfigData   <double>      ( deviceConfig, Config::RfnStrings::DisconnectDemandThreshold );
                const unsigned configConnectDelay                    = getConfigData   <unsigned>    ( deviceConfig, Config::RfnStrings::LoadLimitConnectDelay );
                const unsigned configMaxDisconnect                   = getConfigData   <unsigned>    ( deviceConfig, Config::RfnStrings::MaxDisconnects );

                const boost::optional<std::string> paoReconnectParam = findDynamicInfo <std::string> ( CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam );
                const boost::optional<unsigned> paoDemandInterval    = findDynamicInfo <unsigned>    ( CtiTableDynamicPaoInfo::Key_RFN_DisconnectDemandInterval );
                const boost::optional<double> paoDemandThreshold     = findDynamicInfo <double>      ( CtiTableDynamicPaoInfo::Key_RFN_DemandThreshold );
                const boost::optional<unsigned> paoConnectDelay      = findDynamicInfo <unsigned>    ( CtiTableDynamicPaoInfo::Key_RFN_ConnectDelay );
                const boost::optional<unsigned> paoMaxDisconnect     = findDynamicInfo <unsigned>    ( CtiTableDynamicPaoInfo::Key_RFN_MaxDisconnects );

                const DisconnectCmd::Reconnect      reconnectParam = resolveConfigData( reconnectResolver.right, configReconnectParam, Config::RfnStrings::ReconnectParam );
                const DisconnectCmd::DemandInterval demandInterval = resolveConfigData( intervalResolver,        configDemandInterval, Config::RfnStrings::DisconnectDemandInterval );

                if ( ! disconnectModesMatch ||
                     configReconnectParam  != paoReconnectParam  ||
                     configDemandInterval  != paoDemandInterval  ||
                     configDemandThreshold != paoDemandThreshold ||
                     configConnectDelay    != paoConnectDelay    ||
                     configMaxDisconnect   != paoMaxDisconnect   ||
                     parse.isKeyValid("force"))
                {
                    if( parse.isKeyValid("verify") )
                    {
                        return ClientErrors::ConfigNotCurrent;
                    }

                    rfnRequests.push_back( boost::make_shared<Commands::RfnRemoteDisconnectSetThresholdConfigurationCommand>(
                            reconnectParam,
                            demandInterval,
                            configDemandThreshold,
                            configConnectDelay,
                            configMaxDisconnect ));
                }

                break;
            }
            case DisconnectCmd::DisconnectMode_Cycling:
            {
                const unsigned configDisconnectMinutes               = getConfigData   <unsigned> ( deviceConfig, Config::RfnStrings::DisconnectMinutes );
                const unsigned configConnectMinutes                  = getConfigData   <unsigned> ( deviceConfig, Config::RfnStrings::ConnectMinutes );

                const boost::optional<unsigned> paoDisconnectMinutes = findDynamicInfo <unsigned> ( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMinutes );
                const boost::optional<unsigned> paoConnectMinutes    = findDynamicInfo <unsigned> ( CtiTableDynamicPaoInfo::Key_RFN_ConnectMinutes );

                if ( ! disconnectModesMatch ||
                     configDisconnectMinutes != paoDisconnectMinutes ||
                     configConnectMinutes    != paoConnectMinutes    ||
                     parse.isKeyValid("force") )
                {
                    if( parse.isKeyValid("verify") )
                    {
                        return ClientErrors::ConfigNotCurrent;
                    }

                    rfnRequests.push_back( boost::make_shared<Commands::RfnRemoteDisconnectSetCyclingConfigurationCommand>(
                            configDisconnectMinutes,
                            configConnectMinutes ));
                }

                break;
            }
        }

        if( ! parse.isKeyValid("force") && rfnRequests.size() == 0 )
        {
            return ClientErrors::ConfigCurrent;
        }

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
    catch( const InvalidConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::InvalidConfigData;
    }
}

bool RfnResidentialDevice::disconnectConfigSupported() const
{
    return disconnectConfigTypes.count(getType());
}



void RfnResidentialDevice::handleCommandResult( const Commands::RfnLoadProfileGetRecordingCommand & cmd )
{
    //  If it's enabled, we don't know when it will auto-disable...  but if it's disabled, clear it out
    if( cmd.getRecordingOption() == Commands::RfnLoadProfileRecordingCommand::DisableRecording )
    {
        setDynamicInfo(
                CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabledUntil,
                CtiTime::not_a_time);
    }
}


void RfnResidentialDevice::handleCommandResult(const Commands::RfnLoadProfileSetRecordingCommand & cmd)
{
    setDynamicInfo(
            CtiTableDynamicPaoInfo::Key_RFN_VoltageProfileEnabledUntil,
            cmd.recordingOption == Commands::RfnLoadProfileSetRecordingCommand::EnableRecording
                ? (CtiTime::now() + 14 * 86400)
                : CtiTime::not_a_time);
}


void RfnResidentialDevice::handleCommandResult( const Commands::RfnGetDemandFreezeInfoCommand & cmd )
{
    Commands::RfnGetDemandFreezeInfoCommand::DemandFreezeData freezeData = cmd.getDemandFreezeData();

    if( freezeData.dayOfFreeze )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay, *freezeData.dayOfFreeze );
    }
}


void RfnResidentialDevice::handleCommandResult( const Commands::RfnDemandFreezeConfigurationCommand & cmd )
{
    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandFreezeDay, cmd.freezeDay );
}

void RfnResidentialDevice::handleCommandResult( const Commands::RfnRemoteDisconnectConfigurationCommand & cmd )
{
    if( cmd.getDisconnectMode() )    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMode, getDisconnectModeString( *cmd.getDisconnectMode() ) );
    if( cmd.getReconnectParam() )    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ReconnectParam, getReconnectParamString( *cmd.getReconnectParam() ) );

    if( cmd.getDemandInterval() )    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectDemandInterval, *cmd.getDemandInterval() );
    if( cmd.getDemandThreshold() )   setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DemandThreshold,          *cmd.getDemandThreshold() );
    if( cmd.getConnectDelay() )      setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ConnectDelay,             *cmd.getConnectDelay() );
    if( cmd.getMaxDisconnects() )    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_MaxDisconnects,           *cmd.getMaxDisconnects() );
    if( cmd.getDisconnectMinutes() ) setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DisconnectMinutes,        *cmd.getDisconnectMinutes() );
    if( cmd.getConnectMinutes() )    setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ConnectMinutes,           *cmd.getConnectMinutes() );
}

std::string RfnResidentialDevice::getDisconnectModeString( Commands::RfnRemoteDisconnectConfigurationCommand::DisconnectMode disconnectMode )
{
    boost::optional<std::string> disconnectStr = bimapFind<std::string>( disconnectModeResolver.left, disconnectMode );

    if( ! disconnectStr )
    {
        CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Unable to determine db value for disconnect mode ("<< disconnectMode <<")");

        return "(unknown)";
    }

    return *disconnectStr;
}

std::string RfnResidentialDevice::getReconnectParamString( Commands::RfnRemoteDisconnectConfigurationCommand::Reconnect reconnectMode )
{
    boost::optional<std::string> reconnectStr = bimapFind<std::string>( reconnectResolver.left, reconnectMode );

    if( ! reconnectStr )
    {
        CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - Unable to determine db value for reconnect param ("<< reconnectMode <<")");

        return "(unknown)";
    }

    return *reconnectStr;
}



namespace {

void storeSchedule(RfnResidentialDevice &dev, const Commands::RfnTouScheduleConfigurationCommand::Schedule &schedule)
{
    using Commands::RfnTouScheduleConfigurationCommand;

    //
    // Day table
    //
    if( schedule._dayTable.size() == 8 )
    {
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule,    schedule._dayTable[0] );
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule,    schedule._dayTable[1] );
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule,   schedule._dayTable[2] );
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule, schedule._dayTable[3] );
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule,  schedule._dayTable[4] );
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule,    schedule._dayTable[5] );
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule,  schedule._dayTable[6] );
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule,   schedule._dayTable[7] );
    }

    //
    // Default rate
    //
    if( ! schedule._defaultRate.empty() )
    {
        dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate, schedule._defaultRate );
    }

    //
    // Switch rates
    //
    {
        boost::optional<RfnTouScheduleConfigurationCommand::DailyRates> rates;

        // schedule 1
        rates = mapFind( schedule._rates, RfnTouScheduleConfigurationCommand::Schedule1);
        if( rates && rates->size() == 6 )
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate0, (*rates)[0] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1, (*rates)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2, (*rates)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3, (*rates)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4, (*rates)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5, (*rates)[5] );
        }

        // schedule 2
        rates = mapFind( schedule._rates, RfnTouScheduleConfigurationCommand::Schedule2);
        if( rates && rates->size() == 6 )
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate0, (*rates)[0] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1, (*rates)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2, (*rates)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3, (*rates)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4, (*rates)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5, (*rates)[5] );
        }

        // schedule 3
        rates = mapFind( schedule._rates, RfnTouScheduleConfigurationCommand::Schedule3);
        if( rates && rates->size() == 6 )
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate0, (*rates)[0] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1, (*rates)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2, (*rates)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3, (*rates)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4, (*rates)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5, (*rates)[5] );
        }

        // schedule 3
        rates = mapFind( schedule._rates, RfnTouScheduleConfigurationCommand::Schedule4);
        if( rates && rates->size() == 6 )
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate0, (*rates)[0] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1, (*rates)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2, (*rates)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3, (*rates)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4, (*rates)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5, (*rates)[5] );
        }
    }

    //
    // Switch times
    //
    {
        boost::optional<RfnTouScheduleConfigurationCommand::DailyTimes> times;

        // schedule 1
        times = mapFind( schedule._times, RfnTouScheduleConfigurationCommand::Schedule1);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1, (*times)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2, (*times)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3, (*times)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4, (*times)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5, (*times)[5] );
        }

        // schedule 2
        times = mapFind( schedule._times, RfnTouScheduleConfigurationCommand::Schedule2);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1, (*times)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2, (*times)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3, (*times)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4, (*times)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5, (*times)[5] );
        }

        // schedule 3
        times = mapFind( schedule._times, RfnTouScheduleConfigurationCommand::Schedule3);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1, (*times)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2, (*times)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3, (*times)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4, (*times)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5, (*times)[5] );
        }

        // schedule 4
        times = mapFind( schedule._times, RfnTouScheduleConfigurationCommand::Schedule4);
        if( times && times->size() == 6 ) // times[0] is midnight, there is no paoinfo for it
        {
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1, (*times)[1] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2, (*times)[2] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3, (*times)[3] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4, (*times)[4] );
            dev.setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5, (*times)[5] );
        }
    }
}

}


void RfnResidentialDevice::handleCommandResult( const Commands::RfnTouScheduleGetConfigurationCommand & cmd )
{
    using Commands::RfnTouScheduleConfigurationCommand;

    if( const boost::optional<RfnTouScheduleConfigurationCommand::TouState> touState = cmd.getTouStateReceived() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TouEnabled, *touState == RfnTouScheduleConfigurationCommand::TouEnable);
    }

    if( const boost::optional<RfnTouScheduleConfigurationCommand::Schedule> schedule_received = cmd.getTouScheduleReceived() )
    {
        storeSchedule(*this, *schedule_received);
    }
}


void RfnResidentialDevice::handleCommandResult( const Commands::RfnTouScheduleSetConfigurationCommand & cmd )
{
    using Commands::RfnTouScheduleSetConfigurationCommand;

    if( const boost::optional<RfnTouScheduleSetConfigurationCommand::TouState> touState = cmd.getTouStateReceived() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TouEnabled, *touState == RfnTouScheduleSetConfigurationCommand::TouEnable);
    }

    storeSchedule(*this, cmd.schedule_to_send);
}


void RfnResidentialDevice::handleCommandResult( const Commands::RfnTouStateConfigurationCommand & cmd )
{
    using Commands::RfnTouStateConfigurationCommand;

    if( const boost::optional<RfnTouStateConfigurationCommand::TouState> touState = cmd.getTouState() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TouEnabled, *touState == RfnTouStateConfigurationCommand::TouEnable);
    }
}


void RfnResidentialDevice::handleCommandResult( const Commands::RfnTouHolidayConfigurationCommand & cmd )
{
    using Commands::RfnTouHolidayConfigurationCommand;

    if( const boost::optional<RfnTouHolidayConfigurationCommand::TouState> touState = cmd.getTouStateReceived() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_TouEnabled, *touState == RfnTouHolidayConfigurationCommand::TouEnable);
    }

    if( const boost::optional<RfnTouHolidayConfigurationCommand::Holidays> holidays_received = cmd.getHolidaysReceived() )
    {
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday1, (*holidays_received)[0].asStringUSFormat() );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday2, (*holidays_received)[1].asStringUSFormat() );
        setDynamicInfo( CtiTableDynamicPaoInfo::Key_RFN_Holiday3, (*holidays_received)[2].asStringUSFormat() );
    }
}

}
}

