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


RfnCommand::Bytes RfnLoadProfileCommand::getData()
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
    char error_str[80];

    GetErrorString( error_code, error_str );
    throw CommandException( error_code, error_str );
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


RfnVoltageProfileConfigurationCommand::RfnVoltageProfileConfigurationCommand()
    :   RfnLoadProfileCommand( Operation_GetConfiguration ),
        _demandInterval( 0x00 ),
        _loadProfileInterval( 0x00 )
{

}


RfnVoltageProfileConfigurationCommand::RfnVoltageProfileConfigurationCommand( const unsigned demand_interval_seconds,
                                                                              const unsigned load_profile_interval_minutes )
    :   RfnLoadProfileCommand( Operation_SetConfiguration ),
        _demandInterval( 0x00 ),
        _loadProfileInterval( 0x00 )
{
    validateCondition( demand_interval_seconds > 0 && demand_interval_seconds <= (255 * 15) && ! (demand_interval_seconds % 15),
                       BADPARAM, "Invalid Voltage Demand Interval" );

    validateCondition( load_profile_interval_minutes > 0 && load_profile_interval_minutes <= 255,
                       BADPARAM, "Invalid Load Profile Demand Interval" );

    _demandInterval      = demand_interval_seconds / 15;
    _loadProfileInterval = load_profile_interval_minutes;
}


void RfnVoltageProfileConfigurationCommand::populateTlvs( std::vector< TypeLengthValue > & tlvs )
{
    if ( _operation == Operation_SetConfiguration )
    {
        TypeLengthValue  tlv;

        tlv.type = TlvType_VoltageProfileConfiguration;

        tlv.value.push_back( _demandInterval );
        tlv.value.push_back( _loadProfileInterval );

        tlvs.push_back( tlv );
    }
}


RfnCommand::RfnResult RfnVoltageProfileConfigurationCommand::decode( const CtiTime now,
                                                                     const RfnCommand::RfnResponse & response )
{
    RfnCommand::RfnResult  result = decodeResponseHeader( now, response );

    if ( _operation == Operation_SetConfiguration )
    {
        validateCondition( response.size() == 4,
                           ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

        validateCondition( response[3] == 0,
                           ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );
    }
    else    // if ( _operation == Operation_GetConfiguration )
    {
        validateCondition( response.size() == 8,
                           ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

        validateCondition( response[3] == 1,
                           ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );

        validateCondition( response[4] == 0x01,
                           ErrorInvalidData, "Invalid TLV type (" + CtiNumStr(response[4]) + ")" );

        validateCondition( response[5] == 2,
                           ErrorInvalidData, "Invalid TLV length (" + CtiNumStr(response[5]) + ")" );

        const unsigned demand_interval_seconds          = response[6] * 15;
        const unsigned load_profile_interval_minutes    = response[7];

        result.description += "\nVoltage Demand interval: " + CtiNumStr(demand_interval_seconds) + " seconds";
        result.description += "\nLoad Profile Demand interval: " + CtiNumStr(load_profile_interval_minutes) + " minutes";

        // now -- how to get these values back to the device...

    }

    return result;
}


////


RfnLoadProfileRecordingCommand::RfnLoadProfileRecordingCommand()
    :   RfnLoadProfileCommand( Operation_GetLoadProfileRecordingState )
{

}


RfnLoadProfileRecordingCommand::RfnLoadProfileRecordingCommand( const bool enable )
    :   RfnLoadProfileCommand( enable
                               ? Operation_EnableLoadProfileRecording
                               : Operation_DisableLoadProfileRecording )
{

}


RfnCommand::RfnResult RfnLoadProfileRecordingCommand::decode( const CtiTime now,
                                                              const RfnCommand::RfnResponse & response )
{
    RfnCommand::RfnResult  result = decodeResponseHeader( now, response );

    if ( _operation == Operation_GetLoadProfileRecordingState )
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

        result.description += "\nCurrent State: " + *state + " (" + CtiNumStr(response[6]) + ")";

        const unsigned char enable = response[6];

        // now -- how to get this value back to the device...

    }
    else    // if ( _operation == Operation_EnableLoadProfileRecording || _operation == Operation_DisableLoadProfileRecording )
    {
        validateCondition( response.size() == 4,
                           ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

        validateCondition( response[3] == 0,
                           ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );
    }

    return result;
}


}
}
}

