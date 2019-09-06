#include "precompiled.h"

#include <boost/assign/list_of.hpp>
#include "std_helper.h"
#include "cmd_rfn_TouConfiguration.h"
#include "cmd_rfn_helper.h"

using namespace std;
using boost::assign::map_list_of;
using boost::assign::list_of;

namespace Cti {
namespace Devices {
namespace Commands {

//-----------------------------------------------------------------------------
//  RFN TOU configuration command base class
//-----------------------------------------------------------------------------

const std::string RfnTouConfigurationCommand::SchedulePrefix = "SCHEDULE_";

namespace { // anonymous namespace

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
        ( RfnTouScheduleConfigurationCommand::SchedulePrefix + "1", RfnTouScheduleConfigurationCommand::Schedule1 )
        ( RfnTouScheduleConfigurationCommand::SchedulePrefix + "2", RfnTouScheduleConfigurationCommand::Schedule2 )
        ( RfnTouScheduleConfigurationCommand::SchedulePrefix + "3", RfnTouScheduleConfigurationCommand::Schedule3 )
        ( RfnTouScheduleConfigurationCommand::SchedulePrefix + "4", RfnTouScheduleConfigurationCommand::Schedule4 );

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
RfnCommandResult RfnTouConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    // check size

    validate( Condition( response.size() >= 6, ClientErrors::InvalidData )
            << "Response too small - (" << response.size() << ", expecting >= 6-byte)" );

    const unsigned char commandCode  = response[0],
                        statusCode   = response[1],
                        ascCode      = response[2],
                        ascqCode     = response[3],
                        touStateCode = response[4];

    // check command

    validate( Condition( commandCode == CommandCode_Response, ClientErrors::InvalidData )
            << "Invalid command - (" << commandCode << ", expecting " << CommandCode_Response << ")");

    // decode status

    boost::optional<string> statusDesc = mapFind( statusItems, statusCode );

    validate( Condition( !! statusDesc, ClientErrors::InvalidData )
            << "Invalid status - (" << statusCode << ")" );

    // decode asc/asq

    boost::optional<std::string> additionalStatusDesc = findDescriptionForAscAsq( response[2], response[3] );

    validate( Condition( !! additionalStatusDesc, ClientErrors::InvalidData )
            << "Invalid Additional Status (ASC: " << CtiNumStr(response[2]).xhex(2) << ", ASCQ: " << CtiNumStr(response[3]).xhex(2) << ")" );

    // decode tou state

    _touState_received = mapFind( touStateItems, touStateCode );

    validate( Condition( !! _touState_received, ClientErrors::InvalidData )
            << "Invalid TOU state - (" << touStateCode << ")");

    string touStateDesc = ( *_touState_received == TouEnable ) ? "Enabled" : "Disabled";

    std::string description = 
        "Status : " + *statusDesc 
        + "\nAdditional Status : " + *additionalStatusDesc
        + "\nTOU State : " + touStateDesc;

    for( const auto & tlv : getTlvsFromBytes(Bytes(response.begin() + 5, response.end())) )
    {
        description += "\n" + decodeTlv( tlv );
    }

    return description;
}

/**
 * get the tou state decoded
 * @return the tou state
 */
boost::optional<RfnTouConfigurationCommand::TouState> RfnTouConfigurationCommand::getTouStateReceived() const
{
    return _touState_received;
}

std::string RfnTouConfigurationCommand::getCommandName()
{
    return "TOU Configuration Request";
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration command schedule
//-----------------------------------------------------------------------------

RfnTouScheduleGetConfigurationCommand::RfnTouScheduleGetConfigurationCommand()
{
}

unsigned char RfnTouScheduleGetConfigurationCommand::getOperation() const
{
    return Operation_GetTouSchedule;
}

RfnCommand::Bytes RfnTouScheduleGetConfigurationCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

RfnTouScheduleSetConfigurationCommand::RfnTouScheduleSetConfigurationCommand( const Schedule &schedule ) :
    schedule_to_send(schedule),
    _commandData_to_send( createCommandData(schedule) )
{
}


unsigned char RfnTouScheduleSetConfigurationCommand::getOperation() const
{
    return Operation_SetTouSchedule;
}


RfnCommand::Bytes RfnTouScheduleSetConfigurationCommand::getCommandData()
{
    return _commandData_to_send;
}


/**
 * get the byte vector from the schedule
 * @return byte vector that contains the data
 */
RfnCommand::Bytes RfnTouScheduleSetConfigurationCommand::createCommandData( const Schedule & schedule_to_send )
{
    vector<TypeLengthValue> tlvs;

    // day table
    if( ! schedule_to_send._dayTable.empty() )
    {
        validate( Condition( schedule_to_send._dayTable.size() == 8, ClientErrors::BadParameter )
                << "Invalid day table size (expected 8)" );

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
        const boost::optional<DailyTimes> times = mapFind( schedule_to_send._times, (ScheduleNbr)schedule_nbr );

        const string scheduleName = SchedulePrefix + CtiNumStr(schedule_nbr+1);

        if( times )
        {
            validate( Condition( times->size() == 6, ClientErrors::BadParameter )
                    << "Invalid number of switch time for " << scheduleName << " - (" << times->size() << ", expected 6)" );

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

                validate( Condition( hour >= 0 && hour < 24 && minute >= 0 && minute < 60, ClientErrors::BadParameter )
                        << "Invalid switch time for " << scheduleName << " - (" << time_str << ")" );

                if( time_nbr == 0 )
                {
                    validate( Condition( hour == 0 && minute == 0, ClientErrors::BadParameter )
                            << "Invalid midnight time for " << scheduleName << " - (" << time_str << ", expected 00:00)" );

                    continue;
                }

                if( ! hour && ! minute )
                {
                    hour = 24;  //  means it runs until end of day
                }

                const unsigned switchTime = hour * 60 + minute;

                const string & prev_time_str = (*times)[time_nbr-1];

                validate( Condition( switchTime >= prevSwitchTime, ClientErrors::BadParameter )
                        << "Invalid switch time for " << scheduleName << " - (" << time_str << ", expected > " << prev_time_str << ")" );

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

        const string scheduleName = SchedulePrefix + CtiNumStr(schedule_nbr+1);

        if( rates )
        {
            validate( Condition( rates->size() == 6, ClientErrors::BadParameter )
                    << "Invalid number of rates for " << scheduleName + " - (" << rates->size() << ", expected 6)");

            TypeLengthValue tlv( Type_Schedule1_Rates + schedule_nbr );
            tlv.value.resize(3, 0);

            for( int rate_nbr = 0; rate_nbr < 6; rate_nbr++ )
            {
                const string & rate_str = (*rates)[rate_nbr];

                boost::optional<Rate> rate = Cti::mapFind( rateResolver, rate_str );

                validate( Condition( !! rate, ClientErrors::BadParameter )
                        << "Invalid rate for " << scheduleName << " - (" << rate_str << ")" );

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

        validate( Condition( !! rate, ClientErrors::BadParameter )
                << "Invalid default rate - (" << schedule_to_send._defaultRate << ")" );

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
std::string RfnTouScheduleConfigurationCommand::decodeTlv( const TypeLengthValue& tlv )
{
    switch( tlv.type )
    {
        case Type_DayTable :
        {
            return decodeDayTable( tlv.value );
        }
        case Type_Schedule1_SwitchTimes :
        case Type_Schedule2_SwitchTimes :
        case Type_Schedule3_SwitchTimes :
        case Type_Schedule4_SwitchTimes :
        {
            return decodeScheduleSwitchTimes( tlv.value, *mapFind(timesScheduleNbrItems, tlv.type) );
        }
        case Type_Schedule1_Rates :
        case Type_Schedule2_Rates :
        case Type_Schedule3_Rates :
        case Type_Schedule4_Rates :
        {
            return decodeScheduleRates( tlv.value, *mapFind(ratesScheduleNbrItems, tlv.type) );
        }
        case Type_DefaultTouRate :
        {
            return decodeDefaultTouRate( tlv.value );
        }
        default :
        {
            throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv type - (" + CtiNumStr(tlv.type) + ")");
        }
    }
}

/**
 * Decode day table TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 */
std::string RfnTouScheduleConfigurationCommand::decodeDayTable( const Bytes& value )
{
    validate( Condition( value.size() == 3, ClientErrors::InvalidData )
            << "Invalid day table data size - (" << value.size() << ", expecting 3-byte)" );

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

    std::string description = "Day Table :";

    for( int day_nbr = 0; day_nbr < 8; day_nbr++ )
    {
        const unsigned schedule_nbr = getValueFromBits_lEndian( value, day_nbr*3, 3 );

        validate( Condition( schedule_nbr <= 3, ClientErrors::InvalidData )
                << "Invalid day table schedule number - (" << schedule_nbr << ")" );

        const string schedule_name = SchedulePrefix + CtiNumStr(schedule_nbr + 1);

        description += string("\n ") + dayNames[day_nbr] + " - " + schedule_name;

        dayTable.push_back( schedule_name );
    }

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validate( Condition( _schedule_received->_dayTable.empty(), ClientErrors::InvalidData )
            << "Unexpected day table tlv has been already received" );

    _schedule_received->_dayTable = dayTable;

    return description;
}

/**
 * Decode switch times TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 * @param schedule_nbr schedule number <=> [0,3]
 */
std::string RfnTouScheduleConfigurationCommand::decodeScheduleSwitchTimes( const Bytes& value, const ScheduleNbr schedule_nbr )
{
    validate( Condition( value.size() == 10, ClientErrors::InvalidData )
            << "Invalid schedule switch times data size - (" << value.size() << ", expecting 10-byte)" );

    DailyTimes times;

    std::string description = "SCHEDULE_" + CtiNumStr((int)schedule_nbr+1) + " switch times :";

    times.push_back("00:00");

    unsigned switchTime = 0; // time in minutes from midnight

    for( int time_nbr = 1; time_nbr < 6; time_nbr++ )
    {
        const unsigned duration = getValueFromBits_bEndian( value, (time_nbr-1)*16, 16 );

        switchTime += duration;

        validate( Condition( switchTime <= 1440, ClientErrors::InvalidData)
                << "Invalid switch time - (" << duration << ")" );

        const unsigned hour   = switchTime / 60 % 24,
                       minute = switchTime % 60;

        const string time_str = CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2);

        description += "\n Switch time " + CtiNumStr(time_nbr) + " - " + time_str;

        times.push_back( time_str );
    }

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validate( Condition( ! mapFind( _schedule_received->_times, schedule_nbr ), ClientErrors::InvalidData )
            << "Unexpected switch Times tlv has been already received" );

    _schedule_received->_times[schedule_nbr] = times;

    return description;
}

/**
 * Decode rates TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 * @param schedule_nbr schedule number <=> [0,3]
 */
std::string RfnTouScheduleConfigurationCommand::decodeScheduleRates( const Bytes& value, const ScheduleNbr schedule_nbr )
{
    validate( Condition( value.size() == 3, ClientErrors::InvalidData )
            << "Invalid schedule rate data size - (" << value.size() << ", expecting 3-byte)" );

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

    std::string description = "SCHEDULE_" + CtiNumStr((int)schedule_nbr+1) + " rates :";

    for( int rate_nbr = 0; rate_nbr < 6; rate_nbr++ )
    {
        const unsigned char rate = getValueFromBits_lEndian( value, rate_nbr*3, 3 );
        boost::optional<string> rate_str = mapFind( rateItems, rate );

        validate( Condition( !! rate_str, ClientErrors::InvalidData )
                << "Invalid schedule rate - (" << rate << ")");

        description += string("\n ") + switchRates[rate_nbr] + " rate - " + *rate_str;

        rates.push_back( *rate_str );
    }

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validate( Condition( ! mapFind( _schedule_received->_rates, schedule_nbr ), ClientErrors::InvalidData )
            << "Unexpected switch rates tlv has been already received" );

    _schedule_received->_rates[schedule_nbr] = rates;

    return description;
}

/**
 * Decode Default TOU rate TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 */
std::string RfnTouScheduleConfigurationCommand::decodeDefaultTouRate( const Bytes& value )
{
    validate( Condition( value.size() == 1, ClientErrors::InvalidData )
            << "Invalid default rate data size - (" << value.size() << ", expecting 1-byte)");

    const unsigned char rate = value[0];
    boost::optional<string> rate_str = mapFind( rateItems, rate );

    validate( Condition( !! rate_str, ClientErrors::InvalidData )
            << "Invalid default rate - (" << rate << ")");

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validate( Condition( _schedule_received->_defaultRate.empty(), ClientErrors::InvalidData )
            << "Unexpected tlv - default rates has been already received" );

    _schedule_received->_defaultRate = *rate_str;

    return "Default TOU rate : " + *rate_str;
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
    boost::optional<ScheduleNbr> schedule_nbr = Cti::mapFind( scheduleResolver, schedule_name );

    validate( Condition( !! schedule_nbr, ClientErrors::BadParameter )
            << "Invalid schedule - (" << schedule_name << ")" );

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
        validate( Condition( holidays[holiday_nbr].isValid() && holidays[holiday_nbr] > CtiDate::now(), ClientErrors::BadParameter )
                << "Invalid holiday date " << (holiday_nbr + 1) );
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
std::string RfnTouHolidayConfigurationCommand::decodeTlv( const TypeLengthValue& tlv )
{
    validate( Condition( tlv.type == Type_Holiday, ClientErrors::InvalidData )
            << "Unexpected tlv - (type " << tlv.type << ")" );

    return decodeHoliday( tlv.value );
}

/**
 * Decode Holiday dates from a TOU configuration
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 */
std::string RfnTouHolidayConfigurationCommand::decodeHoliday( const Bytes& value )
{
    validate( Condition( value.size() == 12, ClientErrors::InvalidData )
            << "Invalid holiday data size - (" << value.size() << ", expecting 12-byte)" );

    Holidays holidays;

    std::string description = "Holidays :";

    for( int holiday_nbr = 0; holiday_nbr < 3; holiday_nbr++ )
    {
        const unsigned long date = getValueFromBits_bEndian( value, holiday_nbr*32, 32 );

        CtiTime holidayTime( date );

        description += "\n Date " + CtiNumStr( holiday_nbr + 1 ) + " - " + holidayTime.asString();

        holidays[holiday_nbr] = CtiDate( holidayTime );
    }

    validate( Condition( ! _holidays_received, ClientErrors::InvalidData )
            << "Unexpected tlv - holiday has been already received" );

    _holidays_received = holidays;

    return description;
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

boost::optional<RfnTouConfigurationCommand::TouState> RfnTouStateConfigurationCommand::getTouState() const
{
    if( _touState_to_send )
    {
        return _touState_to_send;
    }

    return _touState_received;
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
std::string RfnTouStateConfigurationCommand::decodeTlv( const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
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
std::string RfnTouSetHolidayActiveCommand::decodeTlv( const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
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
std::string RfnTouCancelHolidayActiveCommand::decodeTlv( const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU reset registers
//-----------------------------------------------------------------------------

RfnTouResetCommand::RfnTouResetCommand()
{
}

unsigned char RfnTouResetCommand::getOperation() const
{
    return Operation_ClearAccumulatorsAndSetCurrentReading;
}

RfnCommand::Bytes RfnTouResetCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

std::string RfnTouResetCommand::decodeTlv( const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU reset registers and set to current reading
//-----------------------------------------------------------------------------

RfnTouResetZeroCommand::RfnTouResetZeroCommand()
{
}

unsigned char RfnTouResetZeroCommand::getOperation() const
{
    return Operation_ClearAccumulators;
}

RfnCommand::Bytes RfnTouResetZeroCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

std::string RfnTouResetZeroCommand::decodeTlv( const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration critical peak set
//-----------------------------------------------------------------------------

RfnTouCriticalPeakCommand::RfnTouCriticalPeakCommand( const std::string & rate, const unsigned hour, const unsigned minute )
    :   _hour( hour ),
        _minute( minute )
{
    validate( Condition( _hour < 24, ClientErrors::BadParameter )
            << "Invalid hour (" << _hour << "), expecting hour < 24" );

    validate( Condition( _minute < 60, ClientErrors::BadParameter )
            << "Invalid minute (" << _minute << "), expecting minute < 60" );

    boost::optional<Rate>   rateLookup = Cti::mapFind( rateResolver, rate );

    validate( Condition( !! rateLookup, ClientErrors::BadParameter )
            << "Invalid rate - (" << rate << ")" );

    _rate = *rateLookup;
}

unsigned char RfnTouCriticalPeakCommand::getOperation() const
{
    return Operation_CriticalPeak;
}

void RfnTouCriticalPeakCommand::prepareCommandData( const CtiTime now )
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
std::string RfnTouCriticalPeakCommand::decodeTlv( const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
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
std::string RfnTouCancelCriticalPeakCommand::decodeTlv( const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ClientErrors::InvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

}
}
}

