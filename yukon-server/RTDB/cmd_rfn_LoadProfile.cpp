#include "precompiled.h"

#include "cmd_rfn_LoadProfile.h"
#include "numstr.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>


namespace Cti        {
namespace Devices    {
namespace Commands   {


namespace   { // anonymous namespace

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
    if( ! condition )
    {
        throw RfnCommand::CommandException( error_code, error_message );
    }
}

} // anonymous namespace


//
// Load Profile Command Base Class
//

RfnLoadProfileCommand::RfnLoadProfileCommand( const Operation operation )
    :   _operation( operation )
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
    return getBytesFromTlvs( getTlvs() );
}


RfnLoadProfileCommand::TlvList RfnLoadProfileCommand::getTlvs()
{
    return TlvList();
}


RfnCommandResult RfnLoadProfileCommand::error( const CtiTime now,
                                               const YukonError_t error_code )
{
    throw CommandException( error_code, GetErrorString( error_code ));
}


RfnCommandResult RfnLoadProfileCommand::decodeResponseHeader( const CtiTime now,
                                                              const RfnResponsePayload & response )
{
    RfnCommandResult result;

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


//
// Voltage Profile Configuration Base Class
//

RfnVoltageProfileConfigurationCommand::RfnVoltageProfileConfigurationCommand( const Operation op )
    :   RfnLoadProfileCommand(op)
{
}


//
// Voltage Profile Get Configuration
//

RfnVoltageProfileGetConfigurationCommand::RfnVoltageProfileGetConfigurationCommand()
    :   RfnVoltageProfileConfigurationCommand( Operation_GetConfiguration ),
        _demandInterval( 0x00 ),
        _loadProfileInterval( 0x00 )
{
}


RfnCommandResult RfnVoltageProfileGetConfigurationCommand::decodeCommand( const CtiTime now,
                                                                          const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

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

    return result;
}


unsigned RfnVoltageProfileGetConfigurationCommand::getDemandIntervalSeconds() const
{
    return SecondsPerInterval * _demandInterval;
}


unsigned RfnVoltageProfileGetConfigurationCommand::getLoadProfileIntervalMinutes() const
{
    return _loadProfileInterval;
}


//
// Voltage profile set configuration
//

RfnVoltageProfileSetConfigurationCommand::RfnVoltageProfileSetConfigurationCommand( const unsigned demand_interval_seconds,
                                                                              const unsigned load_profile_interval_minutes )
    :   RfnVoltageProfileConfigurationCommand( Operation_SetConfiguration ),
        _demandInterval( demand_interval_seconds / SecondsPerInterval ),
        _loadProfileInterval( load_profile_interval_minutes )
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
}

RfnLoadProfileCommand::TlvList RfnVoltageProfileSetConfigurationCommand::getTlvs()
{
        TypeLengthValue tlv(TlvType_VoltageProfileConfiguration);

        tlv.value.push_back( _demandInterval );
        tlv.value.push_back( _loadProfileInterval );

        return boost::assign::list_of(tlv);
}


RfnCommandResult RfnVoltageProfileSetConfigurationCommand::decodeCommand( const CtiTime now,
                                                                          const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

            validateCondition( response.size() == 4,
                               ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

            validateCondition( response[3] == 0,
                               ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );

    return result;
}


//
// Recording State Base Class
//

RfnLoadProfileRecordingCommand::RfnLoadProfileRecordingCommand( const Operation operation )
    :   RfnLoadProfileCommand( operation )
{
}


//
// Load Profile Get Recording State
//

RfnLoadProfileGetRecordingCommand::RfnLoadProfileGetRecordingCommand()
    :   RfnLoadProfileRecordingCommand( Operation_GetLoadProfileRecordingState )
{
}


RfnCommandResult RfnLoadProfileGetRecordingCommand::decodeCommand( const CtiTime now,
                                                                   const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

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

    return result;
}


RfnLoadProfileRecordingCommand::RecordingOption RfnLoadProfileGetRecordingCommand::getRecordingOption() const
{
    return _option;
}


//
// Load Profile Set Recording State
//

RfnLoadProfileSetRecordingCommand::RfnLoadProfileSetRecordingCommand( const RecordingOption option )
    :   RfnLoadProfileRecordingCommand( option == EnableRecording
                                                  ? Operation_EnableLoadProfileRecording
                                                  : Operation_DisableLoadProfileRecording )
{
}


RfnCommandResult RfnLoadProfileSetRecordingCommand::decodeCommand( const CtiTime now,
                                                                   const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    validateCondition( response.size() == 4,
                       ErrorInvalidData, "Invalid Response length (" + CtiNumStr(response.size()) + ")" );

    validateCondition( response[3] == 0,
                       ErrorInvalidData, "Invalid TLV count (" + CtiNumStr(response[3]) + ")" );

    return result;
}


//
// Load Profile Read Points
//

namespace { // anonymous

unsigned getDataFromBytes( const RfnCommand::Bytes & data, const unsigned offset, const unsigned size )
{
    unsigned const end = offset + size;
    unsigned       val = data[offset];

    for( unsigned pos = offset+1; pos < end; pos++ )
    {
        val = (val << 8) | data[pos];
        }

    return val;
}

enum PointOffset
{
    PointOffset_Analog = 214
};

enum PointStatus
{
    Ok = 0,
    Long,
    Failure,
    Timeout
};

enum PointType
{
    Delta_8Bit = 0,
    Delta_16Bit,
    Absolute_32Bit,
    Absolute_16Bit
};

struct PointTypeDesc
{
    std::string _description;
    unsigned    _size;

    PointTypeDesc( const std::string & description, const unsigned size )
        :   _description(description),
            _size(size)
    {
    }
};

struct PointStatusDesc
{
    std::string    _description;
    PointQuality_t _quality;

    PointStatusDesc( const std::string & description, PointQuality_t quality )
        :   _description(description),
            _quality(quality)
        {
        }
};

const std::map<unsigned, PointTypeDesc> pointTypeDescriptionMap = boost::assign::map_list_of
    ( Delta_8Bit,     PointTypeDesc( "8-bit delta",     1 ))
    ( Delta_16Bit,    PointTypeDesc( "16-bit delta",    2 ))
    ( Absolute_32Bit, PointTypeDesc( "32-bit absolute", 4 ))
    ( Absolute_16Bit, PointTypeDesc( "16-bit absolute", 2 ));

const std::map<unsigned, PointStatusDesc> pointStatusDescriptionMap = boost::assign::map_list_of
    ( Ok,      PointStatusDesc( "Ok",      NormalQuality  ))
    ( Long,    PointStatusDesc( "Long",    InvalidQuality ))
    ( Failure, PointStatusDesc( "Failure", InvalidQuality ))
    ( Timeout, PointStatusDesc( "Timeout", InvalidQuality ));

} // anonymous


RfnLoadProfileReadPointsCommand::RfnLoadProfileReadPointsCommand( const CtiTime &now,
                                                                  const CtiDate begin,
                                                                  const CtiDate end )
    :   RfnLoadProfileCommand( Operation_GetLoadProfilePoints ),
    _begin(begin),
        _end(end),
        _uomModifier1(0),
        _uomModifier2(0)
{
    validateCondition( _begin < _end,
                       BADPARAM, "End date must be before begin date (begin = " + _begin.asStringUSFormat() + ", end = " + _end.asStringUSFormat() + ")" );

    validateCondition( _end < now.date(),
                       BADPARAM, "End date must be before today (end = " + _begin.asStringUSFormat() + ", now = " + now.date().asStringUSFormat() + ")" );
}


RfnLoadProfileReadPointsCommand::TlvList RfnLoadProfileReadPointsCommand::getTlvs()
{
    TypeLengthValue tlv(TlvType_GetProfilePointsRequest);

    setBits_bEndian(tlv.value,  0, 32, CtiTime(_begin).seconds());
    setBits_bEndian(tlv.value, 32, 32, CtiTime(_end  ).seconds());

    return boost::assign::list_of(tlv);
}


RfnCommandResult RfnLoadProfileReadPointsCommand::decodeCommand( const CtiTime now,
                                                                      const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    validateCondition( response.size() >= 4,
                       ErrorInvalidData, "Response too small (" + CtiNumStr(response.size()) + " < 4)" );

    const RfnResponsePayload payload(response.begin() + 3, response.end());

    std::vector<TypeLengthValue> tlvs = getTlvsFromBytes(payload);

    validateCondition( ! tlvs.empty(),
                       ErrorInvalidData, "No TLVs in payload" );

    const TypeLengthValue & tlv = tlvs[0];

    validateCondition( tlv.type == TlvType_GetProfilePointsResponse,
                       ErrorInvalidData, "Invalid TLV type received in response (" + CtiNumStr(tlv.type) + " != " + CtiNumStr(TlvType_GetProfilePointsResponse) + ")" );

    const Bytes & lpPointDescriptor = tlv.value;

    unsigned pos = 0;
    unsigned pointRecordCount = 0;

    pos += decodePointsReportHeader( result, lpPointDescriptor, pointRecordCount);

    for( int record_nbr=0 ; record_nbr< pointRecordCount; record_nbr++ )
    {
        pos += decodePointRecord( result, lpPointDescriptor, pos );
    }

    validateCondition( pos == lpPointDescriptor.size(),
                       ErrorInvalidData, "Response TLV does not match expected size (" + CtiNumStr(lpPointDescriptor.size()) + ", expected " + CtiNumStr(pos) + ")" );

    return result;
}


unsigned RfnLoadProfileReadPointsCommand::decodePointsReportHeader( RfnCommandResult & result,
                                                                    const Bytes & lpPointDescriptor,
                                                                    unsigned & pointRecordCount )
{
    /*
     * Load Profile Data Points report header
     *
     * Byte(1)      Channel Number
     * Byte(1)      Unit of measure
     * Byte(4 or 6) UOM modifiers (2 or 3 modifiers) ? is this a mistake : assuming Byte(4) UOM modifiers (2 modifiers)
     * Byte(1)      Profile interval (minutes)
     * Byte(1)      Number of profile point records
     */

    unsigned pos = 0;

    validateCondition( lpPointDescriptor.size() >= 8,
                       ErrorInvalidData, "Response TLV too small (" + CtiNumStr(lpPointDescriptor.size()) + " < 8)" );

    // Channel number

    pos++;  // unused

    // Unit of measure

    const unsigned uom = lpPointDescriptor[pos++];

    validateCondition( uom == Uom_Volts,
                       ErrorInvalidData, "Incorrect UOM returned (" + CtiNumStr(uom) + " != " + CtiNumStr(Uom_Volts) + ")" );

    // Uom modifier 1

    _uomModifier1 = (unsigned short)getDataFromBytes( lpPointDescriptor, pos, 2 );
    pos += 2;

    validateCondition( _uomModifier1.getExtensionBit(),
                       ErrorInvalidData, "Incorrect number of UOM modifiers (1, expecting 2)");

    // Uom modifier 2

    _uomModifier2 = (unsigned short)getDataFromBytes( lpPointDescriptor, pos, 2 );
    pos += 2;

    validateCondition( ! _uomModifier2.getExtensionBit(),
                       ErrorInvalidData, "Incorrect number of UOM modifiers (>2, expecting 2)");

    // Profile interval

    _profileInterval = lpPointDescriptor[pos++];

    validateCondition( _profileInterval,
                       ErrorInvalidData, "Zero-length profile interval returned" );

    // Number of profile point records

    pointRecordCount = lpPointDescriptor[pos++];

    return pos; // return the size of the point report header
}


unsigned RfnLoadProfileReadPointsCommand::decodePointRecord( RfnCommandResult & result,
                                                             const Bytes & lpPointDescriptor,
                                                             const unsigned offset )
{
    /*
     * Load Profile  Point Record
     *
     * Byte[0..3]    Timestamp of first point
     * Byte[4]       Point type
     *                 0 – 8 bit delta
     *                 1 – 16 bit delta
     *                 2 – 32 bit absolute
     *                 3 – 16 bit absolute
     *
     * Byte[5]       # of points in this record
     * Byte[6..N]    Array of point values status points
     *               (1, 2 or 4 byte)  Point value
     *               (1 byte)Point Status
     *                 0 – OK
     *                 1 – Long
     *                 2 – Failure
     *                 3 - Timeout
     */

    unsigned pos = offset;

        validateCondition( lpPointDescriptor.size() >= pos + 6,
                           ErrorInvalidData, "Response TLV too small (" + CtiNumStr(lpPointDescriptor.size()) + " < " + CtiNumStr(pos + 6) + ")" );

    // Timestamp

    unsigned long timestamp = getDataFromBytes( lpPointDescriptor, pos, 4 );
    pos += 4;

    // Point type

    const unsigned pointType = lpPointDescriptor[pos++];

    boost::optional<PointTypeDesc> pointTypeDesc = mapFind( pointTypeDescriptionMap, pointType );

    validateCondition( pointTypeDesc,
                       ErrorInvalidData, "Invalid point type (" + CtiNumStr(pointType) + ")" );

    const unsigned value_size = pointTypeDesc->_size;

    // Number of points in the record

    const unsigned pointCount = lpPointDescriptor[pos++];

    // Array of points

    for(int point_nbr=0; point_nbr < pointCount; point_nbr++)
            {
        validateCondition( lpPointDescriptor.size() >= pos + value_size + 1,
                           ErrorInvalidData, "Response TLV too small (" + CtiNumStr(lpPointDescriptor.size()) + " < " + CtiNumStr(pos + value_size + 1) + ")" );

        // Point value

        double value = getDataFromBytes( lpPointDescriptor, pos, value_size );
        pos += value_size;

        // Point status

        const unsigned status = lpPointDescriptor[pos++];

        boost::optional<PointStatusDesc> pointStatusDesc = mapFind(pointStatusDescriptionMap, status);

        validateCondition( pointStatusDesc,
                           ErrorInvalidData, "Invalid point status (" + CtiNumStr(status) + ")" );

        point_data point;

        point.value       = value;
        point.quality     = pointStatusDesc->_quality;
        point.freeze_bit  = false;
        point.description = "";

        point.type        = AnalogPointType;
        point.offset      = PointOffset_Analog;
        point.name        = "";
        point.time        = CtiTime( timestamp );

        result.points.push_back( point );

        timestamp += _profileInterval;
        }

    return (pos - offset); // return the size of the point record
}


}
}
}
