#include "precompiled.h"

#include "cmd_rfn_LoadProfile.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

#include <boost/optional.hpp>


namespace Cti::Devices::Commands {


namespace   { // anonymous namespace

const std::map<unsigned char, std::string>  loadProfileStatusResolver {
    { 0, "Success" },
    { 1, "Failure" }};


const std::map<unsigned char, std::string>  loadProfileStateResolver {
    { 0, "Disabled" },
    { 1, "Enabled"  }};

} // anonymous namespace


RfnLoadProfileCommand::LongTlvList RfnLoadProfileCommand::longTlvs {
    TlvType_VoltageProfileConfiguration,
    TlvType_LoadProfileState,
    TlvType_GetProfilePointsResponse,
    TlvType_PermanentLoadProfileRecordingState,
    TlvType_TemporaryLoadProfileRecordingState
};


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
    // We need at least 3 bytes

    validate( Condition( response.size() >= 3, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the first 3 bytes

    validate( Condition( response[0] == CommandCode_Response, ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    validate( Condition( response[1] == getOperation(), ClientErrors::InvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    boost::optional<std::string> status = mapFind( loadProfileStatusResolver, response[2] );

    // invalid status byte -- not found in map

    validate( Condition( !! status, ClientErrors::InvalidData )
            << "Invalid Status (" << response[2] << ")" );

    validate( Condition( response[2] == 0, ClientErrors::InvalidData ) // success
            << "Status: " << *status << " (" << response[2] << ")" );

    return "Status: " + *status + " (" + CtiNumStr(response[2]) + ")";
}


RfnLoadProfileCommand::TlvList RfnLoadProfileCommand::getTlvsFromPayload( const RfnResponsePayload & response )
{
    validate( Condition( response.size() >= 4, ClientErrors::InvalidData )
            << "Response too small (" << response.size() << " < 4)" );

    const RfnResponsePayload payload(response.begin() + 3, response.end());

    return getTlvsFromBytes(payload, longTlvs);
}


//
// Voltage Profile Configuration Base Class
//

RfnVoltageProfileConfigurationCommand::RfnVoltageProfileConfigurationCommand( const Operation operation )
    :   RfnLoadProfileCommand( operation )
{
}


//
// Voltage Profile Get Configuration
//

RfnVoltageProfileGetConfigurationCommand::RfnVoltageProfileGetConfigurationCommand()
    :   RfnVoltageProfileConfigurationCommand( Operation_GetConfiguration )
{
}


RfnCommandResult RfnVoltageProfileGetConfigurationCommand::decodeCommand( const CtiTime now,
                                                                          const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    const TypeLengthValue & tlv = tlvs[0];

    validate( Condition( tlv.type == TlvType_VoltageProfileConfiguration, ClientErrors::InvalidData )
            << "Invalid TLV type (" << tlv.type << " != " << TlvType_VoltageProfileConfiguration << ")" );

    validate( Condition( tlv.value.size() == 2, ClientErrors::InvalidData )
            << "Invalid TLV length (" << tlv.value.size() << ")" );

    _voltageAveragingInterval = tlv.value[0] * SecondsPerIncrement;
    _loadProfileInterval      = tlv.value[1];

    result.description += "\nVoltage Averaging interval: " + CtiNumStr(*getVoltageAveragingInterval()) + " seconds";
    result.description += "\nLoad Profile Demand interval: " + CtiNumStr(*getLoadProfileInterval()) + " minutes";

    return result;
}


boost::optional<unsigned> RfnVoltageProfileGetConfigurationCommand::getVoltageAveragingInterval() const
{
    return _voltageAveragingInterval;
}


boost::optional<unsigned> RfnVoltageProfileGetConfigurationCommand::getLoadProfileInterval() const
{
    return _loadProfileInterval;
}

std::string RfnVoltageProfileGetConfigurationCommand::getCommandName() const
{
    return "Voltage Profile Get Configuration Request";
}

//
// Voltage profile set configuration
//

RfnVoltageProfileSetConfigurationCommand::RfnVoltageProfileSetConfigurationCommand( const unsigned voltage_averaging_interval_seconds,
                                                                                    const unsigned load_profile_interval_minutes )
    :   RfnVoltageProfileConfigurationCommand( Operation_SetConfiguration ),
        voltageAveragingInterval( voltage_averaging_interval_seconds  ),
        loadProfileInterval( load_profile_interval_minutes )
{
    static const std::set<unsigned>     allowedAveragingIntervals_seconds
    {
        15, 30, 45, 60, 90, 120, 180, 300, 600, 900
    };

    validate( Condition( allowedAveragingIntervals_seconds.count( voltage_averaging_interval_seconds ), ClientErrors::BadParameter )
            << "Invalid Voltage Averaging Interval: (" << voltage_averaging_interval_seconds
            << ") invalid setting" );

    validate( Condition( load_profile_interval_minutes > 0, ClientErrors::BadParameter )
            << "Invalid Load Profile Demand Interval: (" << load_profile_interval_minutes
            << ") underflow (minimum: 1)" );

    validate( Condition( load_profile_interval_minutes <= 255, ClientErrors::BadParameter )
            << "Invalid Load Profile Demand Interval: (" << load_profile_interval_minutes
            << ") overflow (maximum: 255)" );
}


std::string RfnVoltageProfileSetConfigurationCommand::getCommandName() const
{
    return "Voltage Profile Set Configuration Request";
}

RfnLoadProfileCommand::TlvList RfnVoltageProfileSetConfigurationCommand::getTlvs()
{
    TypeLengthValue tlv = TypeLengthValue::makeLongTlv(TlvType_VoltageProfileConfiguration);

    tlv.value.push_back( voltageAveragingInterval / SecondsPerIncrement );
    tlv.value.push_back( loadProfileInterval );

    return { tlv };
}


RfnCommandResult RfnVoltageProfileSetConfigurationCommand::decodeCommand( const CtiTime now,
                                                                          const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 0, ClientErrors::InvalidData )
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
    :   RfnLoadProfileRecordingCommand( Operation_GetLoadProfileRecordingState ),
        _isPermanentEnabled( false ),
        _isTemporaryEnabled( false )
{
}


auto RfnLoadProfileGetRecordingCommand::getRecordingOption() const -> boost::optional<RecordingOption>
{
    return _option;
}


auto RfnLoadProfileGetRecordingCommand::getEndTime() const -> boost::optional<CtiTime>
{
    return _endTime;
}


bool RfnLoadProfileGetRecordingCommand::isPermanentEnabled() const
{
    return _isPermanentEnabled;
}


bool RfnLoadProfileGetRecordingCommand::isTemporaryEnabled() const
{
    return _isTemporaryEnabled;
}

std::string RfnLoadProfileGetRecordingCommand::getCommandName() const
{
    return "Load Profile Get Recording Request";
}


RfnCommandResult RfnLoadProfileGetRecordingCommand::decodeCommand(const CtiTime now,
                                                                   const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    const TypeLengthValue & tlv = tlvs[0];

    _option  = boost::none;
    _endTime = boost::none;

    _isPermanentEnabled = false;
    _isTemporaryEnabled = false;

    switch ( tlv.type )
    {
        case TlvType_PermanentLoadProfileRecordingState:
        {
            validate( Condition( tlv.value.size() == 0, ClientErrors::InvalidData )
                    << "Invalid TLV length (" << tlv.value.size() << ")" );

            _option = EnableRecording;

            _isPermanentEnabled = true;

            result.description += "\nCurrent State: Enabled (Permanent)";

            break;
        }

        case TlvType_TemporaryLoadProfileRecordingState:
        {
            validate( Condition( tlv.value.size() == 4, ClientErrors::InvalidData )
                    << "Invalid TLV length (" << tlv.value.size() << ")" );

            _option  = EnableRecording;
            _endTime = CtiTime( getValueFromBits_bEndian( tlv.value, 0, 32 ) );

            _isTemporaryEnabled = true;

            result.description += "\nCurrent State: Enabled (Temporary)";
            result.description += "\nEnd Time: " + _endTime->asString();

            break;
        }

        case TlvType_LoadProfileState:
        {
            validate( Condition( tlv.value.size() == 1, ClientErrors::InvalidData )
                    << "Invalid TLV length (" << tlv.value.size() << ")" );

            boost::optional<std::string> state = Cti::mapFind( loadProfileStateResolver, tlv.value[0] );

            validate( Condition( !! state, ClientErrors::InvalidData )
                    << "Invalid State (" << tlv.value[0] << ")" );

            _option = tlv.value[0] ? EnableRecording : DisableRecording;

            _isTemporaryEnabled = ( _option == EnableRecording );

            result.description += "\nCurrent State: " + *state + " (" + CtiNumStr(tlv.value[0]) + ")";

            break;
        }

        default:
        {
            validate( Condition( false, ClientErrors::InvalidData )
                    << "Invalid TLV type (" << tlv.type << ")" );
        }
    }

    return result;
}


//
// Load Profile Set Temporary Recording State
//

RfnLoadProfileSetTemporaryRecordingCommand::RfnLoadProfileSetTemporaryRecordingCommand( const RecordingOption option )
    :   RfnLoadProfileRecordingCommand( option == EnableRecording
                                                  ? Operation_EnableTemporaryLoadProfileRecording
                                                  : Operation_DisableLoadProfileRecording ),
        recordingOption(option)
{
}


RfnCommandResult RfnLoadProfileSetTemporaryRecordingCommand::decodeCommand( const CtiTime now,
                                                                            const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 0, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    return result;
}

std::string RfnLoadProfileSetTemporaryRecordingCommand::getCommandName() const
{
    return "Load Profile Set Temporary Recording Request";
}


//
// Load Profile Set Permanent Recording State
//

RfnLoadProfileSetPermanentRecordingCommand::RfnLoadProfileSetPermanentRecordingCommand( const RecordingOption option )
    :   RfnLoadProfileRecordingCommand( option == EnableRecording
                                                  ? Operation_EnablePermanentLoadProfileRecording
                                                  : Operation_DisableLoadProfileRecording ),
        recordingOption(option)
{
}


RfnCommandResult RfnLoadProfileSetPermanentRecordingCommand::decodeCommand( const CtiTime now,
                                                                            const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( tlvs.size() == 0, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvs.size() << ")" );

    return result;
}

std::string RfnLoadProfileSetPermanentRecordingCommand::getCommandName() const
{
    return "Load Profile Set Permanent Recording Request";
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

const std::map<unsigned, PointTypeDesc> pointTypeDescriptionMap {
    { Delta_8Bit,     PointTypeDesc( "8-bit delta",     1 ) },
    { Delta_16Bit,    PointTypeDesc( "16-bit delta",    2 ) },
    { Absolute_32Bit, PointTypeDesc( "32-bit absolute", 4 ) },
    { Absolute_16Bit, PointTypeDesc( "16-bit absolute", 2 ) }};

const std::map<unsigned, PointStatusDesc> pointStatusDescriptionMap {
    { Ok,      PointStatusDesc( "Ok",      NormalQuality  ) },
    { Long,    PointStatusDesc( "Long",    InvalidQuality ) },
    { Failure, PointStatusDesc( "Failure", InvalidQuality ) },
    { Timeout, PointStatusDesc( "Timeout", InvalidQuality ) }};

} // anonymous


RfnLoadProfileReadPointsCommand::RfnLoadProfileReadPointsCommand( const CtiTime now,
                                                                  const CtiTime begin,
                                                                  const CtiTime end )
    :   RfnLoadProfileCommand( Operation_GetLoadProfilePoints ),
        _begin(begin),
        _end(end),
        _uomModifier1(0),
        _uomModifier2(0)
{
    validate( Condition( _begin <= _end, ClientErrors::BadParameter )
            << "End time must be after begin time (begin = " << _begin << ", end = " << _end << ")" );

    validate( Condition( _end < now, ClientErrors::BadParameter )
            << "End time must be before now (end = " << _end << ", now = " << now << ")" );
}


RfnLoadProfileReadPointsCommand::TlvList RfnLoadProfileReadPointsCommand::getTlvs()
{
    TypeLengthValue tlv = TypeLengthValue::makeLongTlv(TlvType_GetProfilePointsRequest);

    setBits_bEndian(tlv.value,  0, 32, _begin.seconds());
    setBits_bEndian(tlv.value, 32, 32, _end.seconds());

    return { tlv };
}


RfnCommandResult RfnLoadProfileReadPointsCommand::decodeCommand( const CtiTime now,
                                                                      const RfnResponsePayload & response )
{
    RfnCommandResult result = decodeResponseHeader( now, response );

    const TlvList tlvs = getTlvsFromPayload( response );

    validate( Condition( ! tlvs.empty(), ClientErrors::InvalidData )
            << "No TLVs in payload" );

    const TypeLengthValue & tlv = tlvs[0];

    validate( Condition( tlv.type == TlvType_GetProfilePointsResponse, ClientErrors::InvalidData )
            << "Invalid TLV type received in response (" << tlv.type << " != " << TlvType_GetProfilePointsResponse << ")" );

    const Bytes & lpPointDescriptor = tlv.value;

    unsigned pos = 0;
    unsigned pointRecordCount = 0;

    pos += decodePointsReportHeader( result, lpPointDescriptor, pointRecordCount);

    for( int record_nbr=0 ; record_nbr < pointRecordCount; record_nbr++ )
    {
        pos += decodePointRecord( result, lpPointDescriptor, pos );
    }

    validate( Condition( pos == lpPointDescriptor.size(), ClientErrors::InvalidData )
            << "Response TLV does not match expected size (" << lpPointDescriptor.size() << ", expected " << pos << ")" );

    return result;
}

std::string RfnLoadProfileReadPointsCommand::getCommandName() const
{
    return "Load Profile Read Points Request";
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

    validate( Condition( lpPointDescriptor.size() >= 8, ClientErrors::InvalidData )
            << "Response TLV too small (" << lpPointDescriptor.size() << " < 8)" );

    // Channel number

    pos++;  // unused

    // Unit of measure

    const unsigned uom = lpPointDescriptor[pos++];

    validate( Condition( uom == Rfn::UnitOfMeasure::Volts, ClientErrors::InvalidData )
            << "Incorrect UOM returned (" << uom << " != " << Rfn::UnitOfMeasure::Volts << ")" );

    // Uom modifier 1

    _uomModifier1 = (unsigned short)getDataFromBytes( lpPointDescriptor, pos, 2 );
    pos += 2;

    validate( Condition( _uomModifier1.getExtensionBit(), ClientErrors::InvalidData )
            << "Incorrect number of UOM modifiers (1, expecting 2)" );

    // Uom modifier 2

    _uomModifier2 = (unsigned short)getDataFromBytes( lpPointDescriptor, pos, 2 );
    pos += 2;

    validate( Condition( ! _uomModifier2.getExtensionBit(), ClientErrors::InvalidData )
            << "Incorrect number of UOM modifiers (>2, expecting 2)" );

    // Profile interval

    _profileInterval = lpPointDescriptor[pos++];

    validate( Condition( _profileInterval, ClientErrors::InvalidData )
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

    validate( Condition( lpPointDescriptor.size() >= pos + 6, ClientErrors::InvalidData )
            << "Response TLV too small (" << lpPointDescriptor.size() << " < " << (pos + 6) << ")" );

    // Timestamp

    unsigned long timestamp = getDataFromBytes( lpPointDescriptor, pos, 4 );
    pos += 4;

    // Point type

    const unsigned pointType = lpPointDescriptor[pos++];

    boost::optional<PointTypeDesc> pointTypeDesc = mapFind( pointTypeDescriptionMap, pointType );

    validate( Condition( !! pointTypeDesc, ClientErrors::InvalidData )
            << "Invalid point type (" << pointType << ")" );

    const unsigned value_size = pointTypeDesc->_size;

    // Number of points in the record

    const unsigned pointCount = lpPointDescriptor[pos++];

    // Array of points

    for(int point_nbr=0; point_nbr < pointCount; point_nbr++)
    {
        validate( Condition( lpPointDescriptor.size() >= pos + value_size + 1, ClientErrors::InvalidData )
                << "Response TLV too small (" << lpPointDescriptor.size() << " < " << (pos + value_size + 1) << ")" );

        // Point value

        const double raw_value = (double)getDataFromBytes( lpPointDescriptor, pos, value_size );
        const double value = adjustByScalingFactor( raw_value, _uomModifier2.getScalingFactor() );
        pos += value_size;

        // Point status

        const unsigned status = lpPointDescriptor[pos++];

        boost::optional<PointStatusDesc> pointStatusDesc = mapFind(pointStatusDescriptionMap, status);

        validate( Condition( !! pointStatusDesc, ClientErrors::InvalidData )
                << "Invalid point status (" << status << ")" );

        point_data point;

        point.value       = value ;
        point.quality     = pointStatusDesc->_quality;
        point.description = "";
        point.tags        = TAG_POINT_LOAD_PROFILE_DATA;

        point.type        = AnalogPointType;
        point.offset      = PointOffset_Analog;
        point.name        = "Voltage";
        point.time        = CtiTime( timestamp );

        result.points.push_back( point );

        timestamp += (_profileInterval * SecondsPerMinute);
    }

    return (pos - offset); // return the size of the point record
}


}
