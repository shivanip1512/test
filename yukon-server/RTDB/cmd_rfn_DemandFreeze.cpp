
#include "precompiled.h"

#include "cmd_rfn_DemandFreeze.h"
#include "numstr.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>
#include <numeric>
#include <iterator>


namespace Cti        {
namespace Devices    {
namespace Commands   {


namespace   {

const std::map<unsigned char, std::string>  statusResolver = boost::assign::map_list_of
    ( 0x00, "Success" )
    ( 0x01, "Not Ready" )
    ( 0x02, "Busy" )
    ( 0x03, "Protocol Error" )
    ( 0x04, "Meter Error" )
    ( 0x05, "Illegal Request" )
    ( 0x06, "Aborted Command" )
    ( 0x07, "Timeout" );


const std::map< std::pair<unsigned char, unsigned char>, std::string>  ascAscqResolver = boost::assign::map_list_of
    ( std::make_pair( 0x00, 0x00 ), "NO ADDITIONAL STATUS" )
    ( std::make_pair( 0x00, 0x01 ), "REJECTED, SERVICE NOT SUPPORTED" )
    ( std::make_pair( 0x00, 0x02 ), "REJECTED, INVALID FIELD IN COMMAND" )
    ( std::make_pair( 0x00, 0x03 ), "REJECTED, INAPPROPRIATE ACTION REQUESTED" )
    ( std::make_pair( 0x00, 0x04 ), "REJECTED, LOAD VOLTAGE HIGHER THAN THRESHOLD" )
    ( std::make_pair( 0x00, 0x05 ), "REJECTED, SWITCH IS OPEN" )
    ( std::make_pair( 0x00, 0x06 ), "REJECTED, TEST MODE ENABLED" )
    ( std::make_pair( 0x00, 0x07 ), "REJECTED, SERVICE DISCONNECT BUTTON PRESSED BUT METER NOT ARMED" )
    ( std::make_pair( 0x00, 0x08 ), "REJECTED, SERVICE DISCONNECT NOT ENABLED" )
    ( std::make_pair( 0x00, 0x09 ), "REJECTED, SERVICE DISCONNECT IS CURRENTLY CHARGING" )
    ( std::make_pair( 0x00, 0x0a ), "REJECTED, SERVICE DISCONNECT IN OPERATION" )
    ( std::make_pair( 0x01, 0x00 ), "ACCESS DENIED, INSUFFICIENT SECURITY CLEARANCE" )
    ( std::make_pair( 0x01, 0x01 ), "ACCESS DENIED, DATA LOCKED" )
    ( std::make_pair( 0x01, 0x02 ), "ACCESS DENIED, INVALID SERVICE SEQUENCE STATE" )
    ( std::make_pair( 0x01, 0x03 ), "ACCESS DENIED, RENEGOTIATE REQUEST" )
    ( std::make_pair( 0x02, 0x00 ), "DATA NOT READY" )
    ( std::make_pair( 0x03, 0x00 ), "DEVICE BUSY" );


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


RfnDemandFreezeCommand::RfnDemandFreezeCommand( const Operation operation )
    : _operation( operation )
{

}


unsigned char RfnDemandFreezeCommand::getCommandCode() const
{
    return CommandCode_Request;
}


unsigned char RfnDemandFreezeCommand::getOperation() const
{
    return _operation;
}


RfnCommand::Bytes RfnDemandFreezeCommand::getCommandData()
{
    RfnCommand::Bytes   data;

    return data;
}


RfnCommandResult RfnDemandFreezeCommand::error( const CtiTime now,
                                                     const YukonError_t error_code )
{
    throw CommandException( error_code, GetErrorString( error_code ));
}


RfnCommandResult RfnDemandFreezeCommand::decodeResponseHeader( const CtiTime now,
                                                                    const RfnResponsePayload & response )
{
    RfnCommandResult  result;

    // We need at least 4 bytes

    validateCondition( response.size() >= 4,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    // Validate the first 4 bytes

    validateCondition( response[0] == CommandCode_Response,
                       ErrorInvalidData, "Invalid Response Command Code (" + CtiNumStr(response[0]).xhex(2) + ")" );

    // validate status

    boost::optional<std::string> status = Cti::mapFind( statusResolver, response[1] );

    // if not found in map, then status == Reserved

    result.description += "Status: " + ( status ? *status : "Reserved" ) + " (" + CtiNumStr(response[1]).xhex(2) + ")";

    // validate additional status

    boost::optional<std::string> additionalStatus = Cti::mapFind( ascAscqResolver, std::make_pair( response[2], response[3] ) );

    validateCondition( additionalStatus,
                       ErrorInvalidData, "Invalid Additional Status (ASC: " + CtiNumStr(response[2]).xhex(2) + ", ASCQ: " +  CtiNumStr(response[3]).xhex(2) + ")" );

    result.description += "\nAdditional Status: " + *additionalStatus  + " (ASC: " + CtiNumStr(response[2]).xhex(2) + ", ASCQ: " +  CtiNumStr(response[3]).xhex(2) + ")";

    // check for errors ( status or additional status != 0 )

    validateCondition( response[1] == 0x00 && response[2] == 0x00 && response[3] == 0x00,
                       ErrorInvalidData, result.description );

    return result;
}


////


RfnDemandFreezeConfigurationCommand::RfnDemandFreezeConfigurationCommand( const unsigned char day_of_freeze )
    :   RfnDemandFreezeCommand( Operation_SetDayOfDemandFreeze ),
        _freezeDay( 0x00 )
{
    /*
        0       : freeze disabled
        1 - 31  : day number of freeze
        32 +    : last day of month
    */

    _freezeDay = day_of_freeze;
}


RfnCommand::Bytes RfnDemandFreezeConfigurationCommand::getCommandData()
{
    RfnCommand::Bytes   data;

    data.push_back( _freezeDay );

    return data;
}


RfnCommandResult RfnDemandFreezeConfigurationCommand::decodeCommand( const CtiTime now,
                                                                   const RfnCommand::RfnResponsePayload & response )
{
    RfnCommandResult  result = decodeResponseHeader( now, response );

    validateCondition( response.size() == 5,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    validateCondition( response[4] == 0,
                       ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[4]) + ")" );

    return result;
}


////


RfnImmediateDemandFreezeCommand::RfnImmediateDemandFreezeCommand()
    :   RfnDemandFreezeCommand( Operation_ImmediateDemandFreeze )
{

}


RfnCommandResult RfnImmediateDemandFreezeCommand::decodeCommand( const CtiTime now,
                                                               const RfnCommand::RfnResponsePayload & response )
{
    RfnCommandResult  result = decodeResponseHeader( now, response );

    validateCondition( response.size() == 5,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    validateCondition( response[4] == 0,
                       ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[4]) + ")" );

    return result;
}


////


RfnGetDemandFreezeInfoCommand::RfnGetDemandFreezeInfoCommand()
    :   RfnDemandFreezeCommand( Operation_GetDemandFreezeInfo )
{
}


void RfnGetDemandFreezeInfoCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}


RfnGetDemandFreezeInfoCommand::DemandFreezeData RfnGetDemandFreezeInfoCommand::getDemandFreezeData() const
{
    return _freezeData;
}


RfnCommandResult RfnGetDemandFreezeInfoCommand::decodeCommand( const CtiTime now,
                                                                    const RfnCommand::RfnResponsePayload & response )
{
    struct Accumulator
    {
        unsigned int operator()( unsigned int acc, unsigned char val )
        {
            return ( acc << 8 ) + val;
        }
    };

    RfnCommandResult  result = decodeResponseHeader( now, response );

    validateCondition( response.size() >= 5,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    unsigned tlvCount   = 0;
    unsigned totalBytes = 5;

    for ( RfnCommand::RfnResponsePayload::const_iterator current = response.begin() + 5;
          current <= response.end() - 2;
          ++tlvCount )
    {
        unsigned char tlvType   = *current++;
        unsigned char tlvLength = *current++;

        totalBytes += 2;

        validateCondition( totalBytes + tlvLength <= response.size(),
                           ErrorInvalidData, "Invalid TLV length (" + CtiNumStr(std::distance(current, response.end())) + ") expected " + CtiNumStr(tlvLength) );

        unsigned int  tlvValue  = std::accumulate( current, current + tlvLength, 0u, Accumulator() );

        std::advance( current, tlvLength );
        totalBytes += tlvLength;

        switch ( tlvType )
        {
            case TlvType_ScheduledDayOfDemandFreeze:
            {
                _freezeData.dayOfFreeze = tlvValue;
                break;
            }
            case TlvType_TimeLastDemandFreezeOccured:
            {
                _freezeData.lastFreezeTime = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandTotal:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Base ].rate = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandTime:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Base ].timestamp = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateA:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_A ].rate = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateATime:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_A ].timestamp = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateB:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_B ].rate = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateBTime:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_B ].timestamp = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateC:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_C ].rate = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateCTime:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_C ].timestamp = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateD:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_D ].rate = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateDTime:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_D ].timestamp = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateE:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_E ].rate = tlvValue;
                break;
            }
            case TlvType_FrozenPeakDemandRateETime:
            {
                 _freezeData.demandInfo[ DemandFreezeData::DemandRates_Rate_E ].timestamp = tlvValue;
                break;
            }
            default:
            {
                throw RfnCommand::CommandException( ErrorInvalidData,
                                                    "Missing decode for TLV type (" + CtiNumStr(tlvType).xhex(2) + ")" );
            }
        }
    }

    validateCondition( response[4] == tlvCount,
                       ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(tlvCount) + ") expected " + CtiNumStr(response[4]) );

    return result;
}


}
}
}

