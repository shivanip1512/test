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
    _schedule_to_send(schedule)
{
}

/**
 * Get TOU config schedule operation code
 * @return TOU config schedule operation code
 */
unsigned char RfnTouScheduleConfigurationCommand::getOperation() const
{
    return _schedule_to_send ? Operation_SetTouSchedule : Operation_GetTouSchedule;
}

/**
 * Parse schedule config and get byte vector
 * @return byte vector that contains the data
 */
RfnCommand::Bytes RfnTouScheduleConfigurationCommand::getCommandData()
{
    if( ! _schedule_to_send )
    {
        return Bytes(1,0); // zero tlvs
    }

    vector<TypeLengthValue> tlvs;

    // day table
    if( _schedule_to_send->_dayTable )
    {
        TypeLengthValue tlv( Type_DayTable );
        tlv.value.resize(3, 0);

        for( int day_nbr = 0; day_nbr < 8; day_nbr++ )
        {
            setBits_lEndian(tlv.value, day_nbr*3, 3, (*_schedule_to_send->_dayTable)[day_nbr]);
        }

        tlvs.push_back(tlv);
    }

    // schedule 1 - 4 switch times
    for( int schedule_nbr = 0; schedule_nbr < 4; schedule_nbr++ )
    {
        boost::optional<DailyTimes> times = *mapFind( _schedule_to_send->_times, (ScheduleNbr)schedule_nbr );

        if( times )
        {
            TypeLengthValue tlv( Type_Schedule1_SwitchTimes + schedule_nbr );
            tlv.value.resize(10, 0);

            for( int time_nbr = 0; time_nbr < 5; time_nbr++ )
            {
                setBits_bEndian(tlv.value, time_nbr*16, 16, (*times)[time_nbr]);
            }

            tlvs.push_back(tlv);
        }
    }

    // schedule 1 - 4 rates
    for( int schedule_nbr = 0; schedule_nbr < 4; schedule_nbr++ )
    {
        boost::optional<DailyRates> rates = *mapFind( _schedule_to_send->_rates, (ScheduleNbr)schedule_nbr );

        if( rates )
        {
            TypeLengthValue tlv( Type_Schedule1_Rates + schedule_nbr );
            tlv.value.resize(3, 0);

            for( int rate_nbr = 0; rate_nbr < 6; rate_nbr++ )
            {
                setBits_lEndian(tlv.value, rate_nbr*3, 3, (*rates)[rate_nbr]);
            }

            tlvs.push_back(tlv);
        }
    }

    // default tou rate
    if( _schedule_to_send->_defaultRate )
    {
        TypeLengthValue tlv( Type_DefaultTouRate );
        tlv.value.push_back( (unsigned char)*_schedule_to_send->_defaultRate );

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

    const char *days[] =
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

        result.description += string(" ") + days[day_nbr] + " - schedule " + CtiNumStr(schedule_nbr + 1) + "\n";

        dayTable[day_nbr] = (ScheduleNbr)schedule_nbr;
    }

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validateCondition( ! _schedule_received->_dayTable,
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

    for( int time_nbr = 0; time_nbr < 5; time_nbr++ )
    {
        const unsigned short switchTime = getValueFromBits_bEndian( value, time_nbr*16, 16 );

        result.description += " Switch time " + CtiNumStr(time_nbr + 1) + " - " + CtiNumStr(switchTime) + " minutes\n";

        times[time_nbr] = switchTime;
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
        boost::optional<string> rateDesc = mapFind( rateItems, rate );

        validateCondition( rateDesc,
                           ErrorInvalidData, "Invalid schedule rate - (" + CtiNumStr(rate) + ")");

        result.description += string(" ") + switchRates[rate_nbr] + " rate - " + *rateDesc + "\n";

        rates[rate_nbr] = (Rate)rate;
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
    boost::optional<string> rateDesc = mapFind( rateItems, rate );

    validateCondition( rateDesc,
                       ErrorInvalidData, "Invalid default rate - (" + CtiNumStr(rate) + ")");

    result.description += "Default TOU rate : " + *rateDesc + "\n";

    if( ! _schedule_received )
    {
        _schedule_received = Schedule();
    }

    validateCondition( ! _schedule_received->_defaultRate,
                       ErrorInvalidData, "Unexpected tlv - default rates has been already received" );

    _schedule_received->_defaultRate = (Rate)rate;
}

/**
 * get the tou schedule that has been decoded
 * @return the tou schedule
 */
boost::optional<RfnTouScheduleConfigurationCommand::Schedule> RfnTouScheduleConfigurationCommand::getTouScheduleReceived() const
{
    return _schedule_received;
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
        return Bytes(1,0); // zero tlvs
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
RfnTouEnableConfigurationCommand::RfnTouEnableConfigurationCommand()
{
}

/**
 * Constructor
 * @param bEnable TOU enable
 * @param readOnly set to true to read back
 */
RfnTouEnableConfigurationCommand::RfnTouEnableConfigurationCommand( TouState touState_to_send  ) :
    _touState_to_send( touState_to_send )
{
}

/**
 * Get Operation code for TOU Enable configuration.
 * @return Operation_GetTouState if read_only, otherwize, Operation_EnableTou or Operation_DisableTou
 */
unsigned char RfnTouEnableConfigurationCommand::getOperation() const
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
RfnCommand::Bytes RfnTouEnableConfigurationCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

/**
 * Called if a tlv is to be decoded
 * since no tlv is expected, throws a command exception
 * @param result
 * @param tlv
 */
void RfnTouEnableConfigurationCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration command set/cancel holiday active
//-----------------------------------------------------------------------------

/**
 * Constructor of holiday active set/cancel command
 * @param holidayActive_to_send
 */
RfnTouHolidayActiveConfigurationCommand::RfnTouHolidayActiveConfigurationCommand( HolidayActive holidayActive_to_send ) :
    _holidayActive_to_send( holidayActive_to_send )
{
}

/**
 * Get the operation code to execute (set or cancel)
 * @return the operation code
 */
unsigned char RfnTouHolidayActiveConfigurationCommand::getOperation() const
{
    return (_holidayActive_to_send == SetHolidayActive) ? Operation_SetHolidayActive :
                                                          Operation_CancelHolidayActive;
}

/**
 * Return 0 tlv as payload data
 * @return list of byte containing zero tlvs
 */
RfnCommand::Bytes RfnTouHolidayActiveConfigurationCommand::getCommandData()
{
    return list_of(0); // zero tlvs
}

/**
 * Called if a tlv is decoded - throw since no tlv are expected
 * @param result
 * @param tlv
 */
void RfnTouHolidayActiveConfigurationCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv - (type " + CtiNumStr(tlv.type) + ")");
}

//-----------------------------------------------------------------------------
//  RFN TOU configuration critical peak set
//-----------------------------------------------------------------------------

RfnTouCriticalPeakCommand::RfnTouCriticalPeakCommand( const Rate rate, const CtiTime & utcExpireTime )
    :   _rate( rate ),
        _utcExpireTime( utcExpireTime )
{

}

unsigned char RfnTouCriticalPeakCommand::getOperation() const
{
    return Operation_CriticalPeak;
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

