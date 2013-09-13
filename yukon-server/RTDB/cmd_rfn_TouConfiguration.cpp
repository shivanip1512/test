#include "precompiled.h"

#include "boost/assign/list_of.hpp"
#include "std_helper.h"
#include "cmd_rfn_TouConfiguration.h"

using namespace std;
using boost::assign::map_list_of;
using boost::assign::list_of;

namespace Cti {
namespace Devices {
namespace Commands {

//-----------------------------------------------------------------------------
//  RFN TOU configuration command base class
//-----------------------------------------------------------------------------

namespace { // anonymous namespace

void validateCondition( const bool condition, const int error_code, const string & error_message )
{
    if ( ! condition )
    {
        throw RfnCommand::CommandException( error_code, error_message );
    }
}

enum
{
    CommandCode_Request                             = 0x60,
    CommandCode_Response                            = 0x61,
};

enum
{
    Operation_Reserved                              = 0x00,
    Operation_EnableTou                             = 0x01,
    Operation_DisableTou                            = 0x02,
    Operation_GetTouState                           = 0x03,
    Operation_SetTouSchedule                        = 0x04,
    Operation_GetTouSchedule                        = 0x05,
    Operation_SetHoliday                            = 0x06,
    Operation_GetHoliday                            = 0x07,
    Operation_CriticalPeak                          = 0x08,
    Operation_CancelCriticalPeak                    = 0x09,
    Operation_ClearAccumulators                     = 0x0A,
    Operation_ClearAccumulatorsAndSetCurrentReading = 0x0B,
    Operation_SetHolidayActive                      = 0x0C,
    Operation_CancelHolidayActive                   = 0x0D,
};

enum
{
    Type_DayTable                                   = 0x01,
    Type_Schedule1_SwitchTimes                      = 0x02,
    Type_Schedule2_SwitchTimes                      = 0x03,
    Type_Schedule3_SwitchTimes                      = 0x04,
    Type_Schedule4_SwitchTimes                      = 0x05,
    Type_Schedule1_Rates                            = 0x06,
    Type_Schedule2_Rates                            = 0x07,
    Type_Schedule3_Rates                            = 0x08,
    Type_Schedule4_Rates                            = 0x09,
    Type_DefaultTouRate                             = 0x0A,
    Type_CriticalPeak                               = 0x0B,
    Type_Holiday                                    = 0x0C,
};

const map<unsigned char, string> statusItems = map_list_of
        ( 0x0, "Success" )
        ( 0x1, "Not Ready" )
        ( 0x2, "Busy" )
        ( 0x3, "Protocol Error" )
        ( 0x4, "Meter Error" )
        ( 0x5, "Illegal Request" )
        ( 0x6, "Aborted Command" )
        ( 0x7, "Timeout" );

const map<unsigned char, string> rateItems = map_list_of
        ( 0x0, "A" )
        ( 0x1, "B" )
        ( 0x2, "C" )
        ( 0x3, "D" );

const map<unsigned char, map<unsigned char, string>> additionalStatusItems = map_list_of
        ( 0x0, map_list_of
                ( 0x0, "NO ADDITIONAL STATUS" )
                ( 0x1, "REJECTED, SERVICE NOT SUPPORTED" )
                ( 0x2, "REJECTED, INVALID FIELD IN COMMAND" )
                ( 0x3, "REJECTED, INAPPROPRIATE ACTION REQUESTED" )
                ( 0x4, "REJECTED, LOAD VOLTAGE HIGHER THAN THRESHOLD" )
                ( 0x5, "REJECTED, SWITCH IS OPEN" )
                ( 0x6, "REJECTED, TEST MODE ENABLED" )
                ( 0x7, "REJECTED, SERVICE DISCONNECT BUTTON PRESSED BUT METER NOT ARMED" )
                ( 0x8, "REJECTED, SERVICE DISCONNECT NOT ENABLED" )
                ( 0x9, "REJECTED, SERVICE DISCONNECT IS CURRENTLY CHARGING" )
                ( 0xA, "REJECTED, SERVICE DISCONNECT IN OPERATION" ))
        ( 0x1, map_list_of
                ( 0x0, "ACCESS DENIED, INSUFFICIENT SECURITY CLEARANCE" )
                ( 0x1, "ACCESS DENIED, DATA LOCKED" )
                ( 0x2, "ACCESS DENIED, INVALID SERVICE SEQUENCE STATE" )
                ( 0x3, "ACCESS DENIED, RENEGOTIATE REQUEST" ))
        ( 0x2, map_list_of
                ( 0x0, "DATA NOT READY" ))
        ( 0x3, map_list_of
                ( 0x0, "DEVICE NOT PRESENT" ));

const map<unsigned char, RfnTouConfigurationCommand::TouState> touStateItems = map_list_of
        ( 0x0, RfnTouConfigurationCommand::TouDisable )
        ( 0x1, RfnTouConfigurationCommand::TouEnable );

const map<unsigned char, RfnTouScheduleConfigurationCommand::ScheduleNbr> timesScheduleNbrItems = map_list_of
        ( Type_Schedule1_SwitchTimes, RfnTouScheduleConfigurationCommand::Schedule1 )
        ( Type_Schedule2_SwitchTimes, RfnTouScheduleConfigurationCommand::Schedule2 )
        ( Type_Schedule3_SwitchTimes, RfnTouScheduleConfigurationCommand::Schedule3 )
        ( Type_Schedule4_SwitchTimes, RfnTouScheduleConfigurationCommand::Schedule4 );

const map<unsigned char, RfnTouScheduleConfigurationCommand::ScheduleNbr> ratesScheduleNbrItems = map_list_of
        ( Type_Schedule1_Rates, RfnTouScheduleConfigurationCommand::Schedule1 )
        ( Type_Schedule2_Rates, RfnTouScheduleConfigurationCommand::Schedule2 )
        ( Type_Schedule3_Rates, RfnTouScheduleConfigurationCommand::Schedule3 )
        ( Type_Schedule4_Rates, RfnTouScheduleConfigurationCommand::Schedule4 );

const map<string, RfnTouConfigurationCommand::Rate> rateResolver = map_list_of
        ( "A", RfnTouConfigurationCommand::RateA )
        ( "a", RfnTouConfigurationCommand::RateA )
        ( "B", RfnTouConfigurationCommand::RateB )
        ( "b", RfnTouConfigurationCommand::RateB )
        ( "C", RfnTouConfigurationCommand::RateC )
        ( "c", RfnTouConfigurationCommand::RateC )
        ( "D", RfnTouConfigurationCommand::RateD )
        ( "d", RfnTouConfigurationCommand::RateD );

const map<string, RfnTouScheduleConfigurationCommand::ScheduleNbr> scheduleResolver = map_list_of
        ( "schedule 1", RfnTouScheduleConfigurationCommand::Schedule1 )
        ( "schedule 2", RfnTouScheduleConfigurationCommand::Schedule2 )
        ( "schedule 3", RfnTouScheduleConfigurationCommand::Schedule3 )
        ( "schedule 4", RfnTouScheduleConfigurationCommand::Schedule4 );

} // anonymous namespace

/**
 * Constructor
 */
RfnTouConfigurationCommand::RfnTouConfigurationCommand()
{
}

/**
 * Get Command code common to all tou config commands
 * @return TOU config command code
 */
unsigned char RfnTouConfigurationCommand::getCommandCode() const
{
    return CommandCode_Request;
}

/**
 * Decode rfn response and generate result struct
 * @param now time of decode
 * @param response byte vector containing the response
 * @return rfn result struct
 */
RfnCommand::RfnResult RfnTouConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponse &response)
{
    RfnResult result;

    // check size

    validateCondition( response.size() >= 6,
                       ErrorInvalidData, "Response too small - (" + CtiNumStr(response.size()) + ", expecting >= 6-byte)");

    const unsigned char commandCode  = response[0],
                        statusCode   = response[1],
                        ascCode      = response[2],
                        ascqCode     = response[3],
                        touStateCode = response[4];

    // check command

    validateCondition( commandCode == CommandCode_Response,
                       ErrorInvalidData, "Invalid command - (" + CtiNumStr(commandCode) + ", expecting " + CtiNumStr((int)CommandCode_Response) + ")");

    // decode status

    boost::optional<string> statusDesc = mapFind( statusItems, statusCode );

    validateCondition( statusDesc,
                       ErrorInvalidData, "Invalid status - (" + CtiNumStr(statusCode) + ")");

    // decode acs

    boost::optional<map<unsigned char, string>> acsMap = mapFind( additionalStatusItems, ascCode );

    validateCondition( acsMap,
                       ErrorInvalidData, "Invalid additional status - (" + CtiNumStr(ascCode) + ")");

    // decode acsq

    boost::optional<string> acsqDesc = mapFind( *acsMap, ascqCode );

    validateCondition( acsqDesc,
                       ErrorInvalidData, "Invalid additional status qualifier - (" + CtiNumStr(ascCode) + ")");

    // decode tou state

    _touState_received = mapFind( touStateItems, touStateCode );

    validateCondition( _touState_received,
                       ErrorInvalidData, "Invalid TOU state - (" + CtiNumStr(touStateCode) + ")");

    string touStateDesc = ( *_touState_received == TouEnable ) ? "Enabled" : "Disabled";

    result.description += "Status : " + *statusDesc + "\n"
                       +  "Additional Status : " + *acsqDesc + "\n"
                       +  "TOU State : " + touStateDesc + "\n";

    const vector<TypeLengthValue> tlvs = getTlvsFromBytes( Bytes( response.begin() + 5 , response.end() ));
    for each(const TypeLengthValue& tlv in tlvs)
    {
        decodeTlv( result, tlv );
    }

    return result;
}

/**
 * throws CommandException
 * @param now time of error
 * @param error_code code of the error to throw with exception
 * @return may return a result if function is overloaded in child class
 */
RfnCommand::RfnResult RfnTouConfigurationCommand::error(const CtiTime now, const YukonError_t error_code)
{
    // This should probably be the default for all commands unless specified otherwise.
    throw CommandException(error_code, GetErrorString(error_code));
}

/**
 * get the tou state decoded
 * @return the tou state
 */
boost::optional<RfnTouConfigurationCommand::TouState> RfnTouConfigurationCommand::getTouStateReceived() const
{
    return _touState_received;
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration command schedule
//-----------------------------------------------------------------------------

/**
 * Constructor
 * @param schedule contain the day table, the switch times, the rates and the default rate
 * @param readOnly set to true to read back
 */
RfnTouScheduleConfigurationCommand::RfnTouScheduleConfigurationCommand()
{
}


/**
 * Constructor
 * @param schedule contain the day table, the switch times, the rates and the default rate
 * @param readOnly set to true to read back
 */
RfnTouScheduleConfigurationCommand::RfnTouScheduleConfigurationCommand( const Schedule &schedule ) :
    _commandData_to_send( createCommandData(schedule) )
{
}

/**
 * Get TOU config schedule operation code
 * @return TOU config schedule operation code
 */
unsigned char RfnTouScheduleConfigurationCommand::getOperation() const
{
    return _commandData_to_send ? Operation_SetTouSchedule : Operation_GetTouSchedule;
}

/**
 * get the command data
 * @return
 */
RfnCommand::Bytes RfnTouScheduleConfigurationCommand::getCommandData()
{
    if( ! _commandData_to_send )
    {
        return list_of(0); // zero tlvs
    }

    return *_commandData_to_send;
}


/**
 * get the byte vector from the schedule
 * @return byte vector that contains the data
 */
RfnCommand::Bytes RfnTouScheduleConfigurationCommand::createCommandData( const Schedule & schedule_to_send )
{
    vector<TypeLengthValue> tlvs;

    // day table
    if( ! schedule_to_send._dayTable.empty() )
    {
        validateCondition( schedule_to_send._dayTable.size() == 8,
                           BADPARAM, "Invalid day table size (expected 8)");

        TypeLengthValue tlv( Type_DayTable );
        tlv.value.resize(3, 0);

        for( int day_nbr = 0; day_nbr < 8; day_nbr++ )
        {
            const string & schedule_str = schedule_to_send._dayTable[day_nbr];

            ScheduleNbr schedule_nbr = resolveScheduleName( schedule_str );

            setBits_lEndian(tlv.value, day_nbr*3, 3, schedule_nbr);
        }

        tlvs.push_back(tlv);
    }

    // schedule 1 - 4 times
    for( int schedule_nbr = 0; schedule_nbr < 4; schedule_nbr++ )
    {
        boost::optional<DailyTimes> times = mapFind( schedule_to_send._times, (ScheduleNbr)schedule_nbr );

        if( times )
        {
            validateCondition( times->size() == 6,
                               BADPARAM, "Invalid number of switch time - (" + CtiNumStr(times->size()) + ", expected 6)");

            TypeLengthValue tlv( Type_Schedule1_SwitchTimes + schedule_nbr );
            tlv.value.resize(10, 0);

            unsigned prevSwitchTime = 0;

            for( int time_nbr = 0; time_nbr < 6; time_nbr++)
            {
                const string & time_str = (*times)[time_nbr];

                char sep;
                int hour, minute;

                istringstream iss(time_str);
                iss >> hour >> sep >> minute;

                validateCondition( hour >= 0 && hour < 24 && minute >= 0 && minute < 60,
                                   BADPARAM, "Invalid switch time - (" + time_str + ")");

                if( time_nbr == 0 )
                {
                    validateCondition( hour == 0  && minute == 0,
                                       BADPARAM, "Invalid switch time for midnight - (" + time_str + ")");

                    continue;
                }

                const unsigned switchTime = hour * 60 + minute;

                validateCondition( switchTime >= prevSwitchTime,
                                   BADPARAM, "Invalid switch time - (" + time_str + ")");

                const unsigned duration = switchTime - prevSwitchTime;

                setBits_bEndian(tlv.value, (time_nbr-1)*16, 16, duration);

                prevSwitchTime = switchTime;
            }

            tlvs.push_back(tlv);
        }
    }

    // schedule 1 - 4 rates
    for( int schedule_nbr = 0; schedule_nbr < 4; schedule_nbr++ )
    {
        boost::optional<DailyRates> rates = mapFind( schedule_to_send._rates, (ScheduleNbr)schedule_nbr );

        if( rates )
        {
            validateCondition( rates->size() == 6,
                               BADPARAM, "Invalid number of rates - (" + CtiNumStr(rates->size()) + ", expected 6)");

            TypeLengthValue tlv( Type_Schedule1_Rates + schedule_nbr );
            tlv.value.resize(3, 0);

            for( int rate_nbr = 0; rate_nbr < 6; rate_nbr++ )
            {
                const string & rate_str = (*rates)[rate_nbr];

                boost::optional<Rate> rate = Cti::mapFind( rateResolver, rate_str );

                validateCondition( rate,
                                   BADPARAM, "Invalid switch time - (" + rate_str + ")");

                setBits_lEndian(tlv.value, rate_nbr*3, 3, *rate);
            }

            tlvs.push_back(tlv);
        }
    }

    // default rate
    if( ! schedule_to_send._defaultRate.empty() )
    {
        TypeLengthValue tlv( Type_DefaultTouRate );

        boost::optional<Rate> rate = Cti::mapFind( rateResolver, schedule_to_send._defaultRate );

        validateCondition( rate,
                           BADPARAM, "Invalid default rate - (" + schedule_to_send._defaultRate + ")");

        tlv.value.push_back( (unsigned char)*rate );

        tlvs.push_back(tlv);
    }

    return getBytesFromTlvs( tlvs );
}

/**
 * decode a tlv (type length value) receive in the context of a schedule
 * @param result
 * @param tlv
 */
void RfnTouScheduleConfigurationCommand::decodeTlv( RfnResult& result,  const TypeLengthValue& tlv )
{
    switch( tlv.type )
    {
        case Type_DayTable :
        {
            decodeDayTable( result, tlv.value );
            break;
        }
        case Type_Schedule1_SwitchTimes :
        case Type_Schedule2_SwitchTimes :
        case Type_Schedule3_SwitchTimes :
        case Type_Schedule4_SwitchTimes :
        {
            ScheduleNbr schedule_nbr = *mapFind( timesScheduleNbrItems, tlv.type );
            decodeScheduleSwitchTimes( result, tlv.value, schedule_nbr );
            break;
        }
        case Type_Schedule1_Rates :
        case Type_Schedule2_Rates :
        case Type_Schedule3_Rates :
        case Type_Schedule4_Rates :
        {
            ScheduleNbr schedule_nbr = *mapFind( ratesScheduleNbrItems, tlv.type );
            decodeScheduleRates( result, tlv.value, schedule_nbr );
            break;
        }
        case Type_DefaultTouRate :
        {
            decodeDefaultTouRate( result, tlv.value );
            break;
        }
        default :
        {
            throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv type - (" + CtiNumStr(tlv.type) + ")");
        }
    }
}

/**
 * Decode day table TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 */
void RfnTouScheduleConfigurationCommand::decodeDayTable( RfnResult& result, const Bytes& value )
{
    validateCondition( value.size() == 3,
                       ErrorInvalidData, "Invalid day table data size - (" + CtiNumStr(value.size()) + ", expecting 3-byte)");

    const char *dayNames[] =
    {
        "Sunday   ",    // 0
        "Monday   ",    // 1
        "Tuesday  ",    // 2
        "Wednesday",    // 3
        "Thursday ",    // 4
        "Friday   ",    // 5
        "Saturday ",    // 6
        "Holiday  ",    // 7
    };

    DayTable dayTable;

    result.description += "Day Table :\n";

    for( int day_nbr = 0; day_nbr < 8; day_nbr++ )
    {
        const unsigned schedule_nbr = getValueFromBits_lEndian( value, day_nbr*3, 3 );

        validateCondition( schedule_nbr <= 3,
                           ErrorInvalidData, "Invalid day table schedule number - (" + CtiNumStr(schedule_nbr) + ")");

        const string schedule_name = "schedule " + CtiNumStr(schedule_nbr + 1);

        result.description += string(" ") + dayNames[day_nbr] + " - " + schedule_name + "\n";

        dayTable.push_back( schedule_name );
    }

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validateCondition( _schedule_received->_dayTable.empty(),
                       ErrorInvalidData, "Unexpected day table tlv has been already received" );

    _schedule_received->_dayTable = dayTable;
}

/**
 * Decode switch times TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 * @param schedule_nbr schedule number <=> [0,3]
 */
void RfnTouScheduleConfigurationCommand::decodeScheduleSwitchTimes( RfnResult& result, const Bytes& value, const ScheduleNbr schedule_nbr )
{
    validateCondition( value.size() == 10,
                       ErrorInvalidData, "Invalid schedule switch times data size - (" + CtiNumStr(value.size()) + ", expecting 10-byte)" );

    DailyTimes times;

    result.description += "Schedule " + CtiNumStr((int)schedule_nbr+1) + " switch times :\n";

    times.push_back("00:00");

    unsigned switchTime = 0; // time in minutes from midnight

    for( int time_nbr = 1; time_nbr < 6; time_nbr++ )
    {
        const unsigned duration = getValueFromBits_bEndian( value, (time_nbr-1)*16, 16 );

        switchTime += duration;

        const unsigned hour   = switchTime / 60,
                       minute = switchTime % 60;

        validateCondition( hour < 24,
                           ErrorInvalidData, "Invalid switch time - (" + CtiNumStr(duration) + ")");

        const string time_str = CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2);

        result.description += " Switch time " + CtiNumStr(time_nbr) + " - " + time_str + "\n";

        times.push_back( time_str );
    }

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validateCondition( ! mapFind( _schedule_received->_times, schedule_nbr ),
                       ErrorInvalidData, "Unexpected switch Times tlv has been already received" );

    _schedule_received->_times[schedule_nbr] = times;
}

/**
 * Decode rates TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 * @param schedule_nbr schedule number <=> [0,3]
 */
void RfnTouScheduleConfigurationCommand::decodeScheduleRates( RfnResult& result, const Bytes& value, const ScheduleNbr schedule_nbr )
{
    validateCondition( value.size() == 3,
                       ErrorInvalidData, "Invalid schedule rate data size - (" + CtiNumStr(value.size()) + ", expecting 3-byte)");

    const char *switchRates[] =
    {
        "Midnight",  // 0
        "Switch 1",  // 1
        "Switch 2",  // 2
        "Switch 3",  // 3
        "Switch 4",  // 4
        "Switch 5",  // 5
    };

    DailyRates rates;

    result.description += "Schedule " + CtiNumStr((int)schedule_nbr+1) + " rates :\n";

    for( int rate_nbr = 0; rate_nbr < 6; rate_nbr++ )
    {
        const unsigned char rate = getValueFromBits_lEndian( value, rate_nbr*3, 3 );
        boost::optional<string> rate_str = mapFind( rateItems, rate );

        validateCondition( rate_str,
                           ErrorInvalidData, "Invalid schedule rate - (" + CtiNumStr(rate) + ")");

        result.description += string(" ") + switchRates[rate_nbr] + " rate - " + *rate_str + "\n";

        rates.push_back( *rate_str );
    }

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validateCondition( ! mapFind( _schedule_received->_rates, schedule_nbr ),
                       ErrorInvalidData, "Unexpected switch rates tlv has been already received" );

    _schedule_received->_rates[schedule_nbr] = rates;
}

/**
 * Decode Default TOU rate TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 */
void RfnTouScheduleConfigurationCommand::decodeDefaultTouRate( RfnResult& result, const Bytes& value )
{
    validateCondition( value.size() == 1,
                       ErrorInvalidData, "Invalid default rate data size - (" + CtiNumStr(value.size()) + ", expecting 1-byte)");

    const unsigned char rate = value[0];
    boost::optional<string> rate_str = mapFind( rateItems, rate );

    validateCondition( rate_str,
                       ErrorInvalidData, "Invalid default rate - (" + CtiNumStr(rate) + ")");

    result.description += "Default TOU rate : " + *rate_str + "\n";

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validateCondition( _schedule_received->_defaultRate.empty(),
                       ErrorInvalidData, "Unexpected tlv - default rates has been already received" );

    _schedule_received->_defaultRate = *rate_str;
}

/**
 * get the tou schedule that has been decoded
 * @return the tou schedule
 */
boost::optional<RfnTouScheduleConfigurationCommand::Schedule> RfnTouScheduleConfigurationCommand::getTouScheduleReceived() const
{
    return _schedule_received;
}

/**
 * static helper function to convert schedule to schedule string
 * @param schedule_str
 * @return
 */
RfnTouScheduleConfigurationCommand::ScheduleNbr RfnTouScheduleConfigurationCommand::resolveScheduleName( const string & schedule_name )
{
    boost::optional<ScheduleNbr> schedule_nbr = Cti::mapFind( scheduleResolver, boost::to_lower_copy( schedule_name ));

    validateCondition( schedule_nbr,
                       BADPARAM, "Invalid schedule number - (" + schedule_name + ")");

    return *schedule_nbr;
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration command holiday
//-----------------------------------------------------------------------------

/**
 * Constructor
 * @param holiday contains 3 dates to configuration
 * @param readOnly set to true to read back
 */
RfnTouHolidayConfigurationCommand::RfnTouHolidayConfigurationCommand()
{
}

/**
 * Constructor
 * @param holiday contains 3 dates to configuration
 * @param readOnly set to true to read back
 */
RfnTouHolidayConfigurationCommand::RfnTouHolidayConfigurationCommand( const Holidays &holidays ) :
    _holidays_to_send(holidays)
{
    for( int holiday_nbr = 0 ; holiday_nbr < holidays.size() ; holiday_nbr++ )
    {
        validateCondition( holidays[holiday_nbr].isValid() && holidays[holiday_nbr] > CtiDate::now(),
                           BADPARAM, "Invalid holiday date " + CtiNumStr(holiday_nbr + 1));
    }
}

/**
 * Get Operation code for TOU Holiday configuration
 * @return operation code
 */
unsigned char RfnTouHolidayConfigurationCommand::getOperation() const
{
    return _holidays_to_send ? Operation_SetHoliday :
                               Operation_GetHoliday;
}

/**
 * Get Data for a TOU TOU Holiday configuration
 * @return byte list with formatted request
 */
RfnCommand::Bytes RfnTouHolidayConfigurationCommand::getCommandData()
{
    if( ! _holidays_to_send )
    {
        return list_of(0); // zero tlvs
    }

    TypeLengthValue tlv( Type_Holiday );
    tlv.value.resize(12);

    for( int holiday_nbr = 0; holiday_nbr < 3; holiday_nbr++ )
    {
        setBits_bEndian( tlv.value, holiday_nbr*32, 32, CtiTime((*_holidays_to_send)[holiday_nbr]).seconds() );
    }

    vector<TypeLengthValue> tlvs;
    tlvs.push_back(tlv);

    return getBytesFromTlvs( tlvs );
}

/**
 * decode tlv in the context of a holiday configuration command
 * @param result append description to the result
 * @param tlv item to decode
 */
void RfnTouHolidayConfigurationCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    validateCondition( tlv.type == Type_Holiday,
                       ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");

    decodeHoliday( result, tlv.value );
}

/**
 * Decode Holiday dates from a TOU configuration
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 */
void RfnTouHolidayConfigurationCommand::decodeHoliday( RfnResult& result, const Bytes& value )
{
    validateCondition( value.size() == 12,
                       ErrorInvalidData, "Invalid holiday data size - (" + CtiNumStr(value.size()) + ", expecting 12-byte)");

    Holidays holidays;

    result.description += "Holidays :\n";

    for( int holiday_nbr = 0; holiday_nbr < 3; holiday_nbr++ )
    {
        const unsigned long date = getValueFromBits_bEndian( value, holiday_nbr*32, 32 );

        CtiTime holidayTime( date );

        result.description += " Date " + CtiNumStr( holiday_nbr + 1 ) + " - " + holidayTime.asString() + "\n";

        holidays[holiday_nbr] = CtiDate( holidayTime );
    }

    validateCondition( ! _holidays_received,
                       ErrorInvalidData, "Unexpected tlv - holiday has been already received" );

    _holidays_received = holidays;
}

/**
 * get the holiday that has been received
 * @return the holidays
 */
boost::optional<RfnTouHolidayConfigurationCommand::Holidays> RfnTouHolidayConfigurationCommand::getHolidaysReceived() const
{
    return _holidays_received;
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration command enable/disable
//-----------------------------------------------------------------------------

/**
 * Constructor
 * @param bEnable TOU enable
 * @param readOnly set to true to read back
 */
RfnTouStateConfigurationCommand::RfnTouStateConfigurationCommand()
{
}

/**
 * Constructor
 * @param bEnable TOU enable
 * @param readOnly set to true to read back
 */
RfnTouStateConfigurationCommand::RfnTouStateConfigurationCommand( TouState touState_to_send  ) :
    _touState_to_send( touState_to_send )
{
}

/**
 * Get Operation code for TOU Enable configuration.
 * @return Operation_GetTouState if read_only, otherwize, Operation_EnableTou or Operation_DisableTou
 */
unsigned char RfnTouStateConfigurationCommand::getOperation() const
{
    if( ! _touState_to_send )
    {
        return Operation_GetTouState;
    }

    return (*_touState_to_send == TouEnable) ? Operation_EnableTou :
                                               Operation_DisableTou;
}

/**
 * No data except 1 byte to 0 resquired
 * @return byte vector containing 1 byte set to zero
 */
RfnCommand::Bytes RfnTouStateConfigurationCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

/**
 * Called if a tlv is to be decoded
 * since no tlv is expected, throws a command exception
 * @param result
 * @param tlv
 */
void RfnTouStateConfigurationCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration command set holiday active
//-----------------------------------------------------------------------------

/**
 * Constructor
 * @param holidayActive_to_send
 */
RfnTouSetHolidayActiveCommand::RfnTouSetHolidayActiveCommand()
{
}

/**
 * Get the operation code to execute (set or cancel)
 * @return the operation code
 */
unsigned char RfnTouSetHolidayActiveCommand::getOperation() const
{
    return Operation_SetHolidayActive;
}

/**
 * Return 0 tlv as payload data
 * @return list of byte containing zero tlvs
 */
RfnCommand::Bytes RfnTouSetHolidayActiveCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

/**
 * Called if a tlv is decoded - throw since no tlv are expected
 * @param result
 * @param tlv
 */
void RfnTouSetHolidayActiveCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration command cancel holiday active
//-----------------------------------------------------------------------------

/**
 * Constructor
 * @param holidayActive_to_send
 */
RfnTouCancelHolidayActiveCommand::RfnTouCancelHolidayActiveCommand()
{
}

/**
 * Get the operation code to execute (set or cancel)
 * @return the operation code
 */
unsigned char RfnTouCancelHolidayActiveCommand::getOperation() const
{
    return Operation_CancelHolidayActive;
}

/**
 * Return 0 tlv as payload data
 * @return list of byte containing zero tlvs
 */
RfnCommand::Bytes RfnTouCancelHolidayActiveCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

/**
 * Called if a tlv is decoded - throw since no tlv are expected
 * @param result
 * @param tlv
 */
void RfnTouCancelHolidayActiveCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration critical peak set
//-----------------------------------------------------------------------------

RfnTouCriticalPeakCommand::RfnTouCriticalPeakCommand( const std::string & rate, const unsigned hour, const unsigned minute )
    :   _hour( hour ),
        _minute( minute )
{
    validateCondition( _hour < 24,
                       BADPARAM, "Invalid hour (" + CtiNumStr(_hour) + "), expecting hour < 24" );

    validateCondition( _minute < 60,
                       BADPARAM, "Invalid minute (" + CtiNumStr(_hour) + "), expecting minute < 60" );

    boost::optional<Rate>   rateLookup = Cti::mapFind( rateResolver, rate );

    validateCondition( rateLookup,
                       BADPARAM, "Invalid rate - (" + rate + ")");

    _rate = *rateLookup;
}

unsigned char RfnTouCriticalPeakCommand::getOperation() const
{
    return Operation_CriticalPeak;
}

void RfnTouCriticalPeakCommand::prepareCommandData( const CtiTime & now )
{
    CtiDate todaysDate( now );

    CtiTime today( todaysDate, _hour, _minute, 0 );
    CtiTime tomorrow( todaysDate + 1, _hour, _minute, 0 );

    _utcExpireTime = ( today > now ) ? today : tomorrow;
}

RfnCommand::Bytes RfnTouCriticalPeakCommand::getCommandData()
{
    TypeLengthValue tlv( Type_CriticalPeak );
    tlv.value.resize(5);

    tlv.value[0] = _rate;
    setBits_bEndian( tlv.value, 8, 32, _utcExpireTime.seconds() );

    vector<TypeLengthValue> tlvs;
    tlvs.push_back(tlv);

    return getBytesFromTlvs( tlvs );
}

/**
 * Called if a tlv is decoded - throw since no tlv are expected
 * @param result
 * @param tlv
 */
void RfnTouCriticalPeakCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration critical peak cancel
//-----------------------------------------------------------------------------

RfnTouCancelCriticalPeakCommand::RfnTouCancelCriticalPeakCommand()
{

}

unsigned char RfnTouCancelCriticalPeakCommand::getOperation() const
{
    return Operation_CancelCriticalPeak;
}


RfnCommand::Bytes RfnTouCancelCriticalPeakCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

/**
 * Called if a tlv is decoded - throw since no tlv are expected
 * @param result
 * @param tlv
 */
void RfnTouCancelCriticalPeakCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

}
}
}

