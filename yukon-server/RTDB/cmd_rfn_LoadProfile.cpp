#include "precompiled.h"

#include "cmd_rfn_LoadProfile.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

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

} // anonymous namespace


RfnLoadProfileCommand::LongTlvList RfnLoadProfileCommand::longTlvs = boost::assign::list_of
    (TlvType_LoadProfileState)
    (TlvType_GetProfilePointsResponse);


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


RfnCommandResult RfnLoadProfileCommand::decodeResponseHeader( const CtiTime now,
                                                              const RfnResponsePayload & response )
{
    RfnCommandResult result;

    // We need at least 3 bytes

    validate( Condition( response.size() >= 3, ErrorInvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the first 3 bytes

    validate( Condition( response[0] == CommandCode_Response, ErrorInvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    validate( Condition( response[1] == _operation, ErrorInvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    boost::optional<std::string> status = Cti::mapFind( statusResolver, response[2] );

    // invalid status byte -- not found in map

    validate( Condition( status, ErrorInvalidData )
            << "Invalid Status (" << response[2] << ")" );

    validate( Condition( response[2] == 0, ErrorInvalidData ) // success
            << "Status: " << *status << " (" << response[2] << ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[2]) + ")";

    return result;
}


RfnLoadProfileCommand::TlvList RfnLoadProfileCommand::getTlvsFromPayload( const RfnResponsePayload & response )
{
    validate( Condition( response.size() >= 4, ErrorInvalidData )
            << "Response too small (" << response.size() << " < 4)" );

    const RfnResponsePayload payload(response.begin() + 3, response.end());

    return getTlvsFromBytes(payload, longTlvs);
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


void RfnVoltageProfileGetConfigurationCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}


RfnCommandResult RfnVoltageProfileGetConfigurationCommand::decodeCommand( const CtiTime now,
                                                                          const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 1, ErrorInvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    const TypeLengthValue & tlv = tlvs[0];

    validate( Condition( tlv.type == TlvType_VoltageProfileConfiguration, ErrorInvalidData )
            << "Invalid TLV type (" << tlv.type << " != " << TlvType_VoltageProfileConfiguration << ")" );

    validate( Condition( tlv.value.size() == 2, ErrorInvalidData )
            << "Invalid TLV length (" << tlv.value.size() << ")" );

    _demandInterval      = tlv.value[0];
    _loadProfileInterval = tlv.value[1];

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
        demandInterval( demand_interval_seconds / SecondsPerInterval ),
        loadProfileInterval( load_profile_interval_minutes )
{
    validate( Condition( demand_interval_seconds >= (1 * SecondsPerInterval), BADPARAM )
            << "Invalid Voltage Demand Interval: (" << demand_interval_seconds
            << ") underflow (minimum: " << (1 * SecondsPerInterval) << ")" );

    validate( Condition( demand_interval_seconds <= (255 * SecondsPerInterval), BADPARAM )
            << "Invalid Voltage Demand Interval: (" << demand_interval_seconds
            << ") overflow (maximum: " << (255 * SecondsPerInterval) << ")" );

    validate( Condition( ! (demand_interval_seconds % SecondsPerInterval), BADPARAM )
            << "Invalid Voltage Demand Interval: (" << demand_interval_seconds
            << ") not divisible by " << SecondsPerInterval );

    validate( Condition( load_profile_interval_minutes > 0, BADPARAM )
            << "Invalid Load Profile Demand Interval: (" << load_profile_interval_minutes
            << ") underflow (minimum: 1)" );

    validate( Condition( load_profile_interval_minutes <= 255, BADPARAM )
            << "Invalid Load Profile Demand Interval: (" << load_profile_interval_minutes
            << ") overflow (maximum: 255)" );
}


RfnLoadProfileCommand::TlvList RfnVoltageProfileSetConfigurationCommand::getTlvs()
{
        TypeLengthValue tlv(TlvType_VoltageProfileConfiguration);

        tlv.value.push_back( demandInterval );
        tlv.value.push_back( loadProfileInterval );

        return boost::assign::list_of(tlv);
}


RfnCommandResult RfnVoltageProfileSetConfigurationCommand::decodeCommand( const CtiTime now,
                                                                          const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 0, ErrorInvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

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


void RfnLoadProfileGetRecordingCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}


RfnCommandResult RfnLoadProfileGetRecordingCommand::decodeCommand( const CtiTime now,
                                                                   const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 1, ErrorInvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    const TypeLengthValue & tlv = tlvs[0];

    validate( Condition( tlv.type == TlvType_LoadProfileState, ErrorInvalidData )
            << "Invalid TLV type (" << tlv.type << " != " << TlvType_LoadProfileState << ")" );

    validate( Condition( tlv.value.size() == 1, ErrorInvalidData )
            << "Invalid TLV length (" << tlv.value.size() << ")" );

    boost::optional<std::string> state = Cti::mapFind( stateResolver, tlv.value[0] );

    validate( Condition( state, ErrorInvalidData )
            << "Invalid State (" << tlv.value[0] << ")" );

    _option = tlv.value[0] ? EnableRecording : DisableRecording;


    result.description += "\nCurrent State: " + *state + " (" + CtiNumStr(tlv.value[0]) + ")";

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

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 0, ErrorInvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

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
    validate( Condition( _begin < _end, BADPARAM )
            << "End date must be before begin date (begin = " << _begin.asStringUSFormat() << ", end = " << _end.asStringUSFormat() << ")" );

    validate( Condition( _end < now.date(), BADPARAM )
            << "End date must be before today (end = " << _begin.asStringUSFormat() << ", now = " << now.date().asStringUSFormat() << ")" );
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

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( ! tlvs.empty(), ErrorInvalidData )
            << "No TLVs in payload" );

    const TypeLengthValue & tlv = tlvs[0];

    validate( Condition( tlv.type == TlvType_GetProfilePointsResponse, ErrorInvalidData )
            << "Invalid TLV type received in response (" << tlv.type << " != " << TlvType_GetProfilePointsResponse << ")" );

    const Bytes & lpPointDescriptor = tlv.value;

    unsigned pos = 0;
    unsigned pointRecordCount = 0;

    pos += decodePointsReportHeader( result, lpPointDescriptor, pointRecordCount);

    for( int record_nbr=0 ; record_nbr < pointRecordCount; record_nbr++ )
    {
        pos += decodePointRecord( result, lpPointDescriptor, pos );
    }

    validate( Condition( pos == lpPointDescriptor.size(), ErrorInvalidData )
            << "Response TLV does not match expected size (" << lpPointDescriptor.size() << ", expected " << pos << ")" );

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

    validate( Condition( lpPointDescriptor.size() >= 8, ErrorInvalidData )
            << "Response TLV too small (" << lpPointDescriptor.size() << " < 8)" );

    // Channel number

    pos++;  // unused

    // Unit of measure

    const unsigned uom = lpPointDescriptor[pos++];

    validate( Condition( uom == Uom_Volts, ErrorInvalidData )
            << "Incorrect UOM returned (" << uom << " != " << Uom_Volts << ")" );

    // Uom modifier 1

    _uomModifier1 = (unsigned short)getDataFromBytes( lpPointDescriptor, pos, 2 );
    pos += 2;

    validate( Condition( _uomModifier1.getExtensionBit(), ErrorInvalidData )
            << "Incorrect number of UOM modifiers (1, expecting 2)" );

    // Uom modifier 2

    _uomModifier2 = (unsigned short)getDataFromBytes( lpPointDescriptor, pos, 2 );
    pos += 2;

    validate( Condition( ! _uomModifier2.getExtensionBit(), ErrorInvalidData )
            << "Incorrect number of UOM modifiers (>2, expecting 2)" );

    // Profile interval

    _profileInterval = lpPointDescriptor[pos++];

    validate( Condition( _profileInterval, ErrorInvalidData )
            << "Zero-length profile interval returned" );

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
     *                 0 � 8 bit delta
     *                 1 � 16 bit delta
     *                 2 � 32 bit absolute
     *                 3 � 16 bit absolute
     *
     * Byte[5]       # of points in this record
     * Byte[6..N]    Array of point values status points
     *               (1, 2 or 4 byte)  Point value
     *               (1 byte)Point Status
     *                 0 � OK
     *                 1 � Long
     *                 2 � Failure
     *                 3 - Timeout
     */

    unsigned pos = offset;

    validate( Condition( lpPointDescriptor.size() >= pos + 6, ErrorInvalidData )
            << "Response TLV too small (" << lpPointDescriptor.size() << " < " << (pos + 6) << ")" );

    // Timestamp

    unsigned long timestamp = getDataFromBytes( lpPointDescriptor, pos, 4 );
    pos += 4;

    // Point type

    const unsigned pointType = lpPointDescriptor[pos++];

    boost::optional<PointTypeDesc> pointTypeDesc = mapFind( pointTypeDescriptionMap, pointType );

    validate( Condition( pointTypeDesc, ErrorInvalidData )
            << "Invalid point type (" << pointType << ")" );

    const unsigned value_size = pointTypeDesc->_size;

    // Number of points in the record

    const unsigned pointCount = lpPointDescriptor[pos++];

    // Array of points

    for(int point_nbr=0; point_nbr < pointCount; point_nbr++)
    {
        validate( Condition( lpPointDescriptor.size() >= pos + value_size + 1, ErrorInvalidData )
                << "Response TLV too small (" << lpPointDescriptor.size() << " < " << (pos + value_size + 1) << ")" );

        // Point value

        const double value = (double)getDataFromBytes( lpPointDescriptor, pos, value_size ) * _uomModifier2.getScalingFactor();
        pos += value_size;

        // Point status

        const unsigned status = lpPointDescriptor[pos++];

        boost::optional<PointStatusDesc> pointStatusDesc = mapFind(pointStatusDescriptionMap, status);

        validate( Condition( pointStatusDesc, ErrorInvalidData )
                << "Invalid point status (" << status << ")" );

        point_data point;

        point.value       = value ;
        point.quality     = pointStatusDesc->_quality;
        point.freeze_bit  = false;
        point.description = "";

        point.type        = AnalogPointType;
        point.offset      = PointOffset_Analog;
        point.name        = "Voltage";
        point.time        = CtiTime( timestamp );

        result.points.push_back( point );

        timestamp += (_profileInterval * 60);
    }

    return (pos - offset); // return the size of the point record
}


}
}
}
