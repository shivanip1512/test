#include "precompiled.h"

#include "boost/assign/list_of.hpp"
#include "std_helper.h"
#include "cmd_rfn_TouConfiguration.h"

using namespace std;
using boost::assign::map_list_of;

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

typedef map<unsigned char, string> descriptionMapT;

const descriptionMapT statusItems = map_list_of
        ( 0x0, "Success" )
        ( 0x1, "Not Ready" )
        ( 0x2, "Busy" )
        ( 0x3, "Protocol Error" )
        ( 0x4, "Meter Error" )
        ( 0x5, "Illegal Request" )
        ( 0x6, "Aborted Command" )
        ( 0x7, "Timeout" );

const descriptionMapT rateItems = map_list_of
        ( 0x0, "A" )
        ( 0x1, "B" )
        ( 0x2, "C" )
        ( 0x3, "D" );

const map<unsigned char, descriptionMapT> additionalStatusItems = map_list_of
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
                ( 0x0, "DATA NOT READY"))
        ( 0x3, map_list_of
                ( 0x0, "DEVICE NOT PRESENT"));

const descriptionMapT touStateItems = map_list_of
        ( 0x0, "Disabled" )
        ( 0x1, "Enabled" );

const vector<CtiDeviceBase::PaoInfoKeys> dayTableKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_MondaySchedule    )
        ( CtiTableDynamicPaoInfo::Key_RFN_TuesdaySchedule   )
        ( CtiTableDynamicPaoInfo::Key_RFN_WednesdaySchedule )
        ( CtiTableDynamicPaoInfo::Key_RFN_ThursdaySchedule  )
        ( CtiTableDynamicPaoInfo::Key_RFN_FridaySchedule    )
        ( CtiTableDynamicPaoInfo::Key_RFN_SaturdaySchedule  )
        ( CtiTableDynamicPaoInfo::Key_RFN_SundaySchedule    )
        ( CtiTableDynamicPaoInfo::Key_RFN_HolidaySchedule   );

const vector<CtiDeviceBase::PaoInfoKeys> schedule1RatesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1MidnightRate )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate1        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate2        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate3        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate4        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Rate5        );

const vector<CtiDeviceBase::PaoInfoKeys> schedule2RatesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2MidnightRate )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate1        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate2        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate3        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate4        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Rate5        );

const vector<CtiDeviceBase::PaoInfoKeys> schedule3RatesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3MidnightRate )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate1        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate2        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate3        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate4        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Rate5        );

const vector<CtiDeviceBase::PaoInfoKeys> schedule4RatesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4MidnightRate )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate1        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate2        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate3        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate4        )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Rate5        );

const vector<CtiDeviceBase::PaoInfoKeys> schedule1TimesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time1 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time2 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time3 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time4 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule1Time5 );

const vector<CtiDeviceBase::PaoInfoKeys> schedule2TimesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time1 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time2 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time3 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time4 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule2Time5 );

const vector<CtiDeviceBase::PaoInfoKeys> schedule3TimesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time1 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time2 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time3 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time4 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule3Time5 );

const vector<CtiDeviceBase::PaoInfoKeys> schedule4TimesKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time1 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time2 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time3 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time4 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Schedule4Time5 );

const vector<CtiDeviceBase::PaoInfoKeys> holidaysKeys = boost::assign::list_of
        ( CtiTableDynamicPaoInfo::Key_RFN_Holiday1 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Holiday2 )
        ( CtiTableDynamicPaoInfo::Key_RFN_Holiday3 );

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

    boost::optional<descriptionMapT> acsMap = mapFind( additionalStatusItems, ascCode );

    validateCondition( acsMap,
                       ErrorInvalidData, "Invalid additional status - (" + CtiNumStr(ascCode) + ")");

    // decode acsq

    boost::optional<string> acsqDesc = mapFind( *acsMap, ascqCode );

    validateCondition( acsqDesc,
                       ErrorInvalidData, "Invalid additional status qualifier - (" + CtiNumStr(ascCode) + ")");

    // decode tou state

    boost::optional<string> touStateDesc = mapFind( touStateItems, touStateCode );

    validateCondition( touStateDesc,
                       ErrorInvalidData, "Invalid TOU state - (" + CtiNumStr(touStateCode) + ")");

    result.description += "Status : " + *statusDesc + "\n"
                       +  "Additional Status : " + *acsqDesc + "\n"
                       +  "TOU State : " + *touStateDesc + "\n";

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
 * get the pao information that was received
 * @return vector with copy of dynamic pao Info
 */
std::vector<CtiTableDynamicPaoInfo> RfnTouConfigurationCommand::getPaoInfo() const
{
    return _paoInfo;
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
    {
        TypeLengthValue tlv( Type_DayTable );
        tlv.value.resize(3, 0);

        for( int day_nbr = 0; day_nbr < 8; day_nbr++ )
        {
            setBits(tlv.value, day_nbr*3, 3, (*_schedule_to_send)._dayTable[day_nbr]);
        }

        tlvs.push_back(tlv);
    }

    // schedule 1 - 4 switch times
    for( int schedule_nbr = 0; schedule_nbr < 4; schedule_nbr++ )
    {
        TypeLengthValue tlv( Type_Schedule1_SwitchTimes + schedule_nbr );
        tlv.value.resize(10, 0);

        const dailyTimesT& times = *mapFind( _schedule_to_send->_times, (ScheduleE)schedule_nbr );

        for( int switchTime_nbr = 0; switchTime_nbr < 5; switchTime_nbr++ )
        {
            setBits(tlv.value, switchTime_nbr*16, 16, times[switchTime_nbr]);
        }

        tlvs.push_back(tlv);
    }

    // schedule 1 - 4 rates
    for( int schedule_nbr = 0; schedule_nbr < 4; schedule_nbr++ )
    {
        TypeLengthValue tlv( Type_Schedule1_Rates + schedule_nbr );
        tlv.value.resize(3, 0);

        const dailyRatesT& rates = *mapFind( _schedule_to_send->_rates, (ScheduleE)schedule_nbr );

        for( int rate_nbr = 0; rate_nbr < 6; rate_nbr++ )
        {
            setBits(tlv.value, rate_nbr*3, 3, rates[rate_nbr]);
        }

        tlvs.push_back(tlv);
    }

    // default tou rate
    {
        TypeLengthValue tlv( Type_DefaultTouRate );
        tlv.value.push_back((unsigned char)_schedule_to_send->_defaultRate);

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
            decodeScheduleSwitchTimes( result, tlv.value, (tlv.type - Type_Schedule1_SwitchTimes) );
            break;
        }
        case Type_Schedule1_Rates :
        case Type_Schedule2_Rates :
        case Type_Schedule3_Rates :
        case Type_Schedule4_Rates :
        {
            decodeScheduleRates( result, tlv.value, (tlv.type - Type_Schedule1_Rates) );
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

    result.description += "Day Table :\n";

    for( int day_nbr = 0; day_nbr < 8; day_nbr++ )
    {
        const unsigned schedule_nbr = getValueFromBitsLE( value, day_nbr*3, 3 );

        validateCondition( schedule_nbr <= 3,
                           ErrorInvalidData, "Invalid day table schedule number - (" + CtiNumStr(schedule_nbr) + ")");

        result.description += string(" ") + days[day_nbr] + " - schedule " + CtiNumStr(schedule_nbr + 1) + "\n";

        _paoInfo.push_back( CtiTableDynamicPaoInfo( -1, dayTableKeys[day_nbr] ));
        _paoInfo.back().setValue( schedule_nbr );
    }
}

/**
 * Decode switch times TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 * @param schedule_nbr schedule number <=> [0,3]
 */
void RfnTouScheduleConfigurationCommand::decodeScheduleSwitchTimes( RfnResult& result, const Bytes& value, const unsigned schedule_nbr )
{
    validateCondition( value.size() == 10,
                       ErrorInvalidData, "Invalid schedule switch times data size - (" + CtiNumStr(value.size()) + ", expecting 10-byte)" );

    const vector<CtiDeviceBase::PaoInfoKeys>& keys = schedule_nbr == Schedule1 ? schedule1TimesKeys :
                                                     schedule_nbr == Schedule2 ? schedule2TimesKeys :
                                                     schedule_nbr == Schedule3 ? schedule3TimesKeys :
                                                     schedule4TimesKeys;

    result.description += "Schedule " + CtiNumStr(schedule_nbr+1) + " switch times :\n";

    for( int switchTime_nbr = 0; switchTime_nbr < 5; switchTime_nbr++ )
    {
        const unsigned short switchTime = getValueFromBitsLE( value, switchTime_nbr*16, 16 );
        result.description += " Switch time " + CtiNumStr(switchTime_nbr + 1) + " - " + CtiNumStr(switchTime) + " minutes\n";

        _paoInfo.push_back( CtiTableDynamicPaoInfo( -1, keys[switchTime_nbr] ));
        _paoInfo.back().setValue( switchTime );
    }
}

/**
 * Decode rates TLV from a response
 * @param result append description to the result
 * @param value byte vector containing the tlv value
 * @param schedule_nbr schedule number <=> [0,3]
 */
void RfnTouScheduleConfigurationCommand::decodeScheduleRates( RfnResult& result, const Bytes& value, const unsigned schedule_nbr )
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

    const vector<CtiDeviceBase::PaoInfoKeys>& keys = schedule_nbr == Schedule1 ? schedule1RatesKeys :
                                                     schedule_nbr == Schedule2 ? schedule2RatesKeys :
                                                     schedule_nbr == Schedule3 ? schedule3RatesKeys :
                                                     schedule4RatesKeys;

    result.description += "Schedule " + CtiNumStr(schedule_nbr+1) + " rates :\n";

    for( int switch_nbr = 0; switch_nbr < 6; switch_nbr++ )
    {
        const unsigned char rate = getValueFromBitsLE( value, switch_nbr*3, 3 );

        boost::optional<string> rateDesc = mapFind( rateItems, rate );

        validateCondition( rateDesc,
                           ErrorInvalidData, "Invalid schedule rate - (" + CtiNumStr(rate) + ")");

        result.description += string(" ") + switchRates[switch_nbr] + " rate - " + *rateDesc + "\n";

        _paoInfo.push_back( CtiTableDynamicPaoInfo( -1, keys[switch_nbr] ));
        _paoInfo.back().setValue( rate );
    }
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

    const unsigned char rate         = value[0];
    boost::optional<string> rateDesc = mapFind( rateItems, rate );

    validateCondition( rateDesc,
                       ErrorInvalidData, "Invalid default rate - (" + CtiNumStr(rate) + ")");

    result.description += "Default TOU rate : " + *rateDesc + "\n";

    _paoInfo.push_back( CtiTableDynamicPaoInfo( -1, CtiTableDynamicPaoInfo::Key_RFN_DefaultTOURate ));
    _paoInfo.back().setValue( rate );
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
RfnTouHolidayConfigurationCommand::RfnTouHolidayConfigurationCommand( const holidaysT &holidays ) :
    _holidays_to_send(holidays)
{
}

/**
 * Get Operation code for TOU Holiday configuration
 * @return operation code
 */
unsigned char RfnTouHolidayConfigurationCommand::getOperation() const
{
    return _holidays_to_send ? Operation_SetHoliday : Operation_GetHoliday;
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
        setBits(tlv.value, holiday_nbr*32, 32, (*_holidays_to_send)[holiday_nbr]);
    }

    vector<TypeLengthValue> tlvs;
    tlvs.push_back(tlv);

    return getBytesFromTlvs( tlvs );
}

/**
 * decode tlv in the context of a holiday configuration command
 * @param result append description to the result
 * @param tlv
 */
void RfnTouHolidayConfigurationCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    validateCondition( tlv.type == Type_Holiday,
                       ErrorInvalidData, "Unexpected tlv type - (" + CtiNumStr(tlv.type) + ")");

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

    result.description += "Holidays :\n";

    for( int date_nbr = 0; date_nbr < 3; date_nbr++ )
    {
        const unsigned long date = getValueFromBitsLE( value, date_nbr*32, 32 );
        result.description += " Date " + CtiNumStr( date_nbr + 1 ) + " - " + CtiTime(date).asString() + "\n";

        _paoInfo.push_back( CtiTableDynamicPaoInfo( -1, holidaysKeys[date_nbr] ));
        _paoInfo.back().setValue( date );
    }
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
RfnTouEnableConfigurationCommand::RfnTouEnableConfigurationCommand( bool bEnable ) :
    _enableTou_to_send( bEnable )
{
}

/**
 * Get Operation code for TOU Enable configuration.
 * @return Operation_GetTouState if read_only, otherwize, Operation_EnableTou or Operation_DisableTou
 */
unsigned char RfnTouEnableConfigurationCommand::getOperation() const
{
    if( ! _enableTou_to_send )
    {
        return Operation_GetTouState;
    }

    return *_enableTou_to_send ? Operation_EnableTou : Operation_DisableTou;
}

/**
 * No data except 1 byte to 0 resquired
 * @return byte vector containing 1 byte set to zero
 */
RfnCommand::Bytes RfnTouEnableConfigurationCommand::getCommandData()
{
    return Bytes(1,0); // zero tlvs
}

/**
 * called if a tlv is to be decoded
 * since no tlv is expected, throws a command exception
 * @param result
 * @param tlv
 */
void RfnTouEnableConfigurationCommand::decodeTlv( RfnResult& result, const TypeLengthValue& tlv )
{
    throw RfnCommand::CommandException( ErrorInvalidData, "Unexpected tlv type - (" + CtiNumStr(tlv.type) + ")");
}


}
}
}
