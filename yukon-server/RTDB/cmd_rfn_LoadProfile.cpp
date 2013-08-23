#include "precompiled.h"

#include "cmd_rfn_LoadProfile.h"
#include "numstr.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>


namespace Cti        {
namespace Devices    {
namespace Commands   {


namespace   {

const std::map<unsigned char, std::string>  statusResolver = boost::assign::map_list_of
    ( 0, "Success")
    ( 1, "Failure");


const std::map<unsigned char, std::string>  stateResolver = boost::assign::map_list_of
    ( 0, "Disabled")
    ( 1, "Enabled");


void validateCondition( const bool condition,
                        const int error_code,
                        const std::string & error_message )
{
    if ( ! condition )
    {
        throw RfnCommand::CommandException( error_code, error_message );
    }
}


}


RfnLoadProfileCommand::RfnLoadProfileCommand( const Operation operation )
    : _operation( operation )
{

}


unsigned char RfnLoadProfileCommand::getCommandCode() const
{
    return CommandCode_Request;
}


unsigned char RfnLoadProfileCommand::getOperation() const
{
    return _operation;
}


RfnCommand::Bytes RfnLoadProfileCommand::getCommandData()
{
    std::vector< TypeLengthValue >  tlvs;

    populateTlvs( tlvs );

    return getBytesFromTlvs( tlvs );
}


void RfnLoadProfileCommand::populateTlvs( std::vector< TypeLengthValue > & tlvs )
{

}


RfnCommand::RfnResult RfnLoadProfileCommand::error( const CtiTime now,
                                                    const YukonError_t error_code )
{
    throw CommandException( error_code, GetErrorString( error_code ));
}


RfnCommand::RfnResult RfnLoadProfileCommand::decodeResponseHeader( const CtiTime now,
                                                                   const RfnResponse & response )
{
    RfnCommand::RfnResult  result;

    // We need at least 3 bytes

    validateCondition( response.size() >= 3,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    // Validate the first 3 bytes

    validateCondition( response[0] == CommandCode_Response,
                       ErrorInvalidData, "Invalid Response Command Code (" + CtiNumStr(response[0]).xhex(2) + ")" );

    validateCondition( response[1] == _operation,
                       ErrorInvalidData, "Invalid Operation Code (" + CtiNumStr(response[1]).xhex(2) + ")" );

    boost::optional<std::string> status = Cti::mapFind( statusResolver, response[2] );

    // invalid status byte -- not found in map

    validateCondition( status,
                       ErrorInvalidData, "Invalid Status (" + CtiNumStr(response[2]) + ")" );

    validateCondition( response[2] == 0,    // success
                       ErrorInvalidData, "Status: " + *status + " (" + CtiNumStr(response[2]) + ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[2]) + ")";

    return result;
}


////


RfnVoltageProfileConfigurationCommand::RfnVoltageProfileConfigurationCommand( ResultHandler & rh )
    :   RfnLoadProfileCommand( Operation_GetConfiguration ),
        _rh( rh ),
        _demandInterval( 0x00 ),
        _loadProfileInterval( 0x00 )
{

}


RfnVoltageProfileConfigurationCommand::RfnVoltageProfileConfigurationCommand( ResultHandler & rh,
                                                                              const unsigned demand_interval_seconds,
                                                                              const unsigned load_profile_interval_minutes )
    :   RfnLoadProfileCommand( Operation_SetConfiguration ),
        _rh( rh ),
        _demandInterval( 0x00 ),
        _loadProfileInterval( 0x00 )
{
    validateCondition( demand_interval_seconds >= (1 * SecondsPerInterval),
                       BADPARAM, "Invalid Voltage Demand Interval: (" + CtiNumStr(demand_interval_seconds) +
                                    ") underflow (minimum: " + CtiNumStr(1 * SecondsPerInterval) + ")" );

    validateCondition( demand_interval_seconds <= (255 * SecondsPerInterval),
                       BADPARAM, "Invalid Voltage Demand Interval: (" + CtiNumStr(demand_interval_seconds) +
                                    ") overflow (maximum: " + CtiNumStr(255 * SecondsPerInterval) + ")" );

    validateCondition( ! (demand_interval_seconds % SecondsPerInterval),
                       BADPARAM, "Invalid Voltage Demand Interval: (" + CtiNumStr(demand_interval_seconds) +
                                    ") not divisible by " + CtiNumStr(SecondsPerInterval) );

    validateCondition( load_profile_interval_minutes > 0,
                       BADPARAM, "Invalid Load Profile Demand Interval: (" + CtiNumStr(load_profile_interval_minutes) +
                                    ") underflow (minimum: 1)" );

    validateCondition( load_profile_interval_minutes <= 255,
                       BADPARAM, "Invalid Load Profile Demand Interval: (" + CtiNumStr(load_profile_interval_minutes) +
                                    ") overflow (maximum: 255)" );

    _demandInterval      = demand_interval_seconds / SecondsPerInterval;
    _loadProfileInterval = load_profile_interval_minutes;
}


void RfnVoltageProfileConfigurationCommand::populateTlvs( std::vector< TypeLengthValue > & tlvs )
{
    if ( _operation == Operation_SetConfiguration )
    {
        TypeLengthValue tlv(TlvType_VoltageProfileConfiguration);

        tlv.value.push_back( _demandInterval );
        tlv.value.push_back( _loadProfileInterval );

        tlvs.push_back( tlv );
    }
}


RfnCommand::RfnResult RfnVoltageProfileConfigurationCommand::decodeCommand( const CtiTime now,
                                                                     const RfnCommand::RfnResponse & response )
{
    RfnCommand::RfnResult  result = decodeResponseHeader( now, response );

    switch ( _operation )
    {
        case Operation_SetConfiguration:
        {
            validateCondition( response.size() == 4,
                               ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

            validateCondition( response[3] == 0,
                               ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );
            break;
        }
        case Operation_GetConfiguration:
        {
            validateCondition( response.size() == 8,
                               ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

            validateCondition( response[3] == 1,
                               ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );

            validateCondition( response[4] == 0x01,
                               ErrorInvalidData, "Invalid TLV type (" + CtiNumStr(response[4]) + ")" );

            validateCondition( response[5] == 2,
                           ErrorInvalidData, "Invalid TLV length (" + CtiNumStr(response[5]) + ")" );

            _demandInterval      = response[6];
            _loadProfileInterval = response[7];

            result.description += "\nVoltage Demand interval: " + CtiNumStr(getDemandIntervalSeconds()) + " seconds";
            result.description += "\nLoad Profile Demand interval: " + CtiNumStr(getLoadProfileIntervalMinutes()) + " minutes";

            break;
        }
        default:
        {
            throw RfnCommand::CommandException( ErrorInvalidData,
                                                "Missing decode for Operation (" + CtiNumStr(_operation).xhex(2) + ")" );
        }
    }

    return result;
}


unsigned RfnVoltageProfileConfigurationCommand::getDemandIntervalSeconds() const
{
    return SecondsPerInterval * _demandInterval;
}


unsigned RfnVoltageProfileConfigurationCommand::getLoadProfileIntervalMinutes() const
{
    return _loadProfileInterval;
}


////


RfnLoadProfileRecordingCommand::RfnLoadProfileRecordingCommand( ResultHandler & rh )
    :   RfnLoadProfileCommand( Operation_GetLoadProfileRecordingState ),
        _rh( rh )
{

}


RfnLoadProfileRecordingCommand::RfnLoadProfileRecordingCommand( ResultHandler & rh, const RecordingOption option )
    :   RfnLoadProfileCommand( option == EnableRecording
                               ? Operation_EnableLoadProfileRecording
                               : Operation_DisableLoadProfileRecording ),
        _rh( rh )
{

}


RfnCommand::RfnResult RfnLoadProfileRecordingCommand::decodeCommand( const CtiTime now,
                                                                     const RfnCommand::RfnResponse & response )
{
    RfnCommand::RfnResult  result = decodeResponseHeader( now, response );

    switch ( _operation )
    {
        case Operation_EnableLoadProfileRecording:
        case Operation_DisableLoadProfileRecording:
        {
            validateCondition( response.size() == 4,
                               ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

            validateCondition( response[3] == 0,
                               ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );
            break;
        }
        case Operation_GetLoadProfileRecordingState:
        {
            validateCondition( response.size() == 7,
                               ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

            validateCondition( response[3] == 1,
                               ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );

            validateCondition( response[4] == 0x02,
                               ErrorInvalidData, "Invalid TLV type (" + CtiNumStr(response[4]) + ")" );

            validateCondition( response[5] == 1,
                               ErrorInvalidData, "Invalid TLV length (" + CtiNumStr(response[5]) + ")" );

            boost::optional<std::string> state = Cti::mapFind( stateResolver, response[6] );

            validateCondition( state,
                               ErrorInvalidData, "Invalid State (" + CtiNumStr(response[6]) + ")" );

            _option = response[6] ? EnableRecording : DisableRecording;

            result.description += "\nCurrent State: " + *state + " (" + CtiNumStr(response[6]) + ")";

            break;
        }
        default:
        {
            throw RfnCommand::CommandException( ErrorInvalidData,
                                                "Missing decode for Operation (" + CtiNumStr(_operation).xhex(2) + ")" );
        }
    }

    return result;
}


RfnLoadProfileRecordingCommand::RecordingOption RfnLoadProfileRecordingCommand::getRecordingOption() const
{
    return _option;
}
////


RfnLoadProfileReadPointsCommand::RfnLoadProfileReadPointsCommand( const CtiTime &now,
                                                                  const CtiDate begin,
                                                                  const CtiDate end ) :
    RfnLoadProfileCommand( Operation_GetLoadProfilePoints ),
    _begin(begin),
    _end(end)
{
    validateCondition( _begin < _end,
                       BADPARAM, "End date must be before begin date (begin = " + _begin.asStringUSFormat() + ", end = " + _end.asStringUSFormat() + ")" );

    validateCondition( _end < now.date(),
                       BADPARAM, "End date must be before today (end = " + _begin.asStringUSFormat() + ", now = " + now.date().asStringUSFormat() + ")" );
}


void RfnLoadProfileReadPointsCommand::populateTlvs( std::vector< TypeLengthValue > & tlvs )
{
    TypeLengthValue tlv(TlvType_GetProfilePointsRequest);

    setBits_lEndian(tlv.value,  0, 32, CtiTime(_begin).seconds());
    setBits_lEndian(tlv.value, 32, 32, CtiTime(_end  ).seconds());

    tlvs.push_back( tlv );
}


RfnCommand::RfnResult RfnLoadProfileReadPointsCommand::decodeCommand( const CtiTime now,
                                                                      const RfnCommand::RfnResponse & response )
{
    RfnCommand::RfnResult result = decodeResponseHeader( now, response );

    validateCondition( response.size() >= 4,
                       ErrorInvalidData, "Response too small (" + CtiNumStr(response.size()) + " < 4)" );

    const unsigned tlv_count = response[3];



    return result;
}


}
}
}

