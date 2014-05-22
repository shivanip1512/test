#include "precompiled.h"

#include "cmd_rfn_ChannelConfiguration.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>

namespace Cti      {
namespace Devices  {
namespace Commands {

namespace { // anonymous

template <typename T,unsigned Size>
unsigned arraySize(const T (&arr)[Size]) { return Size; }

enum TouRatesOptions
{
    None,
    TouRates
};

struct MetricInfo
{
    const unsigned        id;
    const TouRatesOptions touRatesOption;
    const std::string     description;
};

const MetricInfo metricInfos[] = {
        {   1,  TouRates,  "Watt hour delivered"                   },
        {   2,  TouRates,  "Watt hour received"                    },
        {   3,  TouRates,  "Watt hour total/sum"                   },
        {   4,  TouRates,  "Watt hour net"                         },
        {   5,  TouRates,  "Watts delivered, current demand"       },
        {   6,  TouRates,  "Watts received, current demand"        },
        {   7,  TouRates,  "Watts delivered, peak demand"          },
        {   8,  TouRates,  "Watts received, peak demand"           },
        {   9,  TouRates,  "Watts delivered, peak demand (Frozen)" },
        {  10,  TouRates,  "Watts received, peak demand (Frozen)"  },
        {  11,  TouRates,  "Watt hour delivered (Frozen)"          },
        {  12,  TouRates,  "Watt hour received (Frozen)"           },

        {  21,  TouRates,  "Var hour delivered"                    },
        {  22,  TouRates,  "Var hour received"                     },
        {  23,  TouRates,  "Var hour total/sum"                    },
        {  24,  TouRates,  "Var hour net"                          },
        {  25,  TouRates,  "Var hour Q1"                           },
        {  26,  TouRates,  "Var hour Q2"                           },
        {  27,  TouRates,  "Var hour Q3"                           },
        {  28,  TouRates,  "Var hour Q4"                           },
        {  29,  TouRates,  "Var hour Q1 + Q4"                      },
        {  30,  TouRates,  "Var hour Q2 + Q3"                      },
        {  31,  TouRates,  "Var hour Q1 - Q4"                      },
        {  32,  TouRates,  "Var delivered, current demand"         },
        {  33,  TouRates,  "Var received, current demand"          },
        {  34,  TouRates,  "Var delivered, peak demand"            },
        {  35,  TouRates,  "Var received, peak demand"             },
        {  36,  TouRates,  "Var delivered, peak demand coincident" },
        {  37,  TouRates,  "Var received, peak demand coincident"  },

        {  41,  TouRates,  "VA hour delivered"                     },
        {  42,  TouRates,  "VA hour received"                      },
        {  43,  TouRates,  "VA hour total/sum"                     },
        {  44,  TouRates,  "VA hour net"                           },
        {  45,  TouRates,  "VA hour Q1"                            },
        {  46,  TouRates,  "VA hour Q2"                            },
        {  47,  TouRates,  "VA hour Q3"                            },
        {  48,  TouRates,  "VA hour Q4"                            },
        {  49,  TouRates,  "VA delivered, current demand"          },
        {  50,  TouRates,  "VA received, current demand"           },
        {  51,  TouRates,  "VA received, peak demand"              },
        {  52,  TouRates,  "VA delivered, peak demand"             },

        {  61,  TouRates,  "Q hour delivered"                      },
        {  62,  TouRates,  "Q hour received"                       },
        {  63,  TouRates,  "Q hour total/sum"                      },
        {  64,  TouRates,  "Q hour net"                            },
        {  65,  TouRates,  "Q delivered, current demand"           },
        {  66,  TouRates,  "Q received, current demand"            },
        {  67,  TouRates,  "Q delivered, peak demand"              },
        {  68,  TouRates,  "Q received, peak demand"               },
        {  69,  TouRates,  "Q delivered, peak demand coincident"   },
        {  70,  TouRates,  "Q received, peak demand coincident"    },

        {  81,  TouRates,  "Power Factor kWh(del)/kVar(del)"       },
        {  82,  TouRates,  "Power Factor kWh(rec)/kVar(rec)"       },
        {  83,  TouRates,  "Power Factor kWh(total)/kVar(total)"   },

        { 100,  TouRates,  "Voltage Phase A"                       },
        { 101,  TouRates,  "Voltage Phase B"                       },
        { 102,  TouRates,  "Voltage Phase C"                       },
        { 103,  TouRates,  "Current Phase A"                       },
        { 104,  TouRates,  "Current Phase B"                       },
        { 105,  TouRates,  "Current Phase C"                       },
        { 106,  TouRates,  "Voltage Angle Phase A"                 },
        { 107,  TouRates,  "Voltage Angle Phase B"                 },
        { 108,  TouRates,  "Voltage Angle Phase C"                 },
        { 109,  TouRates,  "Current Angle Phase A"                 },
        { 110,  TouRates,  "Current Angle Phase B"                 },
        { 111,  TouRates,  "Current Angle Phase C"                 },
        { 112,  TouRates,  "Voltage Min"                           },
        { 113,  TouRates,  "Voltage Average"                       },
        { 114,  TouRates,  "Voltage Max"                           },
        { 115,  TouRates,  "Voltage"                               },
        { 116,  TouRates,  "Voltage Min Phase A"                   },
        { 117,  TouRates,  "Voltage Min Phase B"                   },
        { 118,  TouRates,  "Voltage Min Phase C"                   },
        { 119,  TouRates,  "Voltage Average Phase A"               },
        { 120,  TouRates,  "Voltage Average Phase B"               },
        { 121,  TouRates,  "Voltage Average Phase C"               },
        { 122,  TouRates,  "Voltage Max Phase A"                   },
        { 123,  TouRates,  "Voltage Max Phase B"                   },
        { 124,  TouRates,  "Voltage Max Phase C"                   },

        { 150,  TouRates,  "Watts Phase A"                         },
        { 151,  TouRates,  "Watts Phase B"                         },
        { 152,  TouRates,  "Watts Phase C"                         },
        { 153,  TouRates,  "Var Phase A"                           },
        { 154,  TouRates,  "Var Phase B"                           },
        { 155,  TouRates,  "Var Phase C"                           },
        { 156,  TouRates,  "VA Phase A"                            },
        { 157,  TouRates,  "VA Phase B"                            },
        { 158,  TouRates,  "VA Phase C"                            },
        { 159,  TouRates,  "PF degree Phase A"                     },
        { 160,  TouRates,  "PF degree Phase A"                     },
        { 161,  TouRates,  "PF degree Phase C"                     },
        { 162,  TouRates,  "PF Phase A"                            },
        { 163,  TouRates,  "PF Phase B"                            },
        { 164,  TouRates,  "PF Phase C"                            },

        { 256,  None,      "Time in Seconds"                       },
        { 257,  None,      "Temperature in Centigrade"             }};

struct MetricItem
{
    const MetricInfo* info;
    boost::optional<char> touRate;
};

std::map<unsigned, MetricItem> initializeMetricIdResolver()
{
    std::map<unsigned, MetricItem> m;

    const unsigned totalMetricInfos  = arraySize(metricInfos);
    const unsigned touRatesOffset    = 1000;
    const std::vector<char> touRates = boost::assign::list_of('A')('B')('C')('D');

    for(int nbr = 0; nbr < totalMetricInfos; nbr++)
    {
        const MetricInfo &info = metricInfos[nbr];

        const MetricItem item = { &info, boost::none };
        m[info.id] = item;

        if( info.touRatesOption == TouRates )
        {
            unsigned id = info.id;
            for each( char rate in touRates)
            {
                const MetricItem itemTou = { &info, rate };
                id += touRatesOffset;
                m[id] = itemTou;
            }
        }
    }

    return m;
}

const std::map<unsigned, MetricItem> metricIdResolver = initializeMetricIdResolver();

/**
 * find the metric description from the metric Id provided
 * @param metricId
 * @return the description of the metric if it exist, an empty string otherwise
 */
std::string getMetricDescription( unsigned metricId )
{
    const boost::optional<MetricItem> item = mapFind(metricIdResolver, metricId);

    if( ! item )
    {
        return std::string();
    }

    return item->touRate
            ? item->info->description + ", Rate " + *(item->touRate)
            : item->info->description;
}

const std::map<unsigned, std::string> responseStatusResolver = boost::assign::map_list_of
        ( 0, "Success" )
        ( 1, "Failure" );

const std::map<unsigned, std::string> metricQualifierResolver_FundHarmonic = boost::assign::map_list_of
        ( 0, ""/*"not specified"*/ )
        ( 1, "Fundamental" )
        ( 2, "Harmonic" );

const std::map<unsigned, std::string> metricQualifierResolver_PrimarySecondary = boost::assign::map_list_of
        ( 0, ""/*"not specified"*/ )
        ( 1, "Primary" )
        ( 2, "Secondary" );

const std::map<unsigned, std::string> metricQualifierResolver_Segmentation = boost::assign::map_list_of
        ( 0, ""/*"not specified"*/ )
        ( 1, "A to B" )
        ( 2, "B to C" )
        ( 3, "C to A" )
        ( 4, "Neutral to Ground" )
        ( 5, "A to Neutral" )
        ( 6, "B to Neutral" )
        ( 7, "C to Neutral" );

const std::map<unsigned, std::string> metricQualifierResolver_ContinuousCumulative = boost::assign::map_list_of
        ( 0, ""/*"not specified"*/ )
        ( 1, "Continuous Cumulative" );

const std::map<unsigned, std::string> metricQualifierResolver_Cumulative = boost::assign::map_list_of
        ( 0, ""/*"not specified"*/ )
        ( 1, "Cumulative" );

const std::map<unsigned, std::string> metricQualifierResolver_CoincidentValue = boost::assign::map_list_of
        ( 0, ""/*"not specified"*/ )
        ( 1, "Coincident Value 1" )
        ( 2, "Coincident Value 2" )
        ( 3, "Coincident Value 3" )
        ( 4, "Coincident Value 4" )
        ( 5, "Coincident Value 5" )
        ( 6, "Coincident Value 6" )
        ( 7, "Coincident Value 7" );

const std::map<unsigned, std::string> metricQualifierResolver_ScalingFactor = boost::assign::map_list_of
        ( 3, "10e9 (giga)" )    //  3 - 011
        ( 2, "10e6 (mega)" )    //  2 - 010
        ( 1, "10e3 (kilo)" )    //  1 - 001
        ( 0, "1" )              //  0 - 000
        ( 7, "10e-3 (milli)" )  // -1 - 111
        ( 6, "10e-6 (micro)" )  // -2 - 110
        ( 5, "10e-1 (deci)" )   // -3 - 101
        ( 4, "OVERFLOW" );      // -4 - 100

unsigned getValueFromBytes_bEndian( const DeviceCommand::Bytes &data, unsigned offset, unsigned len )
{
    assert( len > 0 && len <= 4 && offset + len <= data.size() );

    DeviceCommand::Bytes::const_iterator
        itr     = data.begin() + offset,
        itr_end = itr + len;

    unsigned val = *(itr++);
    for( ; itr != itr_end; ++itr)
    {
        val <<= 8;
        val |= *itr;
    }

    return val;
}

template <unsigned byteNbr>
void insertValue_bEndian( DeviceCommand::Bytes &data, unsigned val );

template <>
void insertValue_bEndian<2>( DeviceCommand::Bytes &data, unsigned val )
{
    data.push_back( (val >> 8) & 0xff );
    data.push_back( (val >> 0) & 0xff );
}

template <>
void insertValue_bEndian<4>( DeviceCommand::Bytes &data, unsigned val )
{
    data.push_back( (val >> 24) & 0xff );
    data.push_back( (val >> 16) & 0xff );
    data.push_back( (val >>  8) & 0xff );
    data.push_back( (val >>  0) & 0xff );
}

std::ostream& operator<<( std::ostream& out, const std::set<unsigned char> &values )
{
    std::set<unsigned char>::const_iterator itr = values.begin();

    if( itr != values.end() )
    {
        out << (unsigned)*(itr++);
        while( itr != values.end() )
        {
            out << ", " << (unsigned)*(itr++);
        }
    }

    return out;
}

template <unsigned offset, unsigned length>
unsigned getBitsField( unsigned val )
{
    const unsigned mask = ~(~0u << length);
    return (val >> offset) & mask;
}

struct MetricQualifierFields
{
    unsigned extensionBit, primarySecondary, fundHarmonic, segmentation, continuousCumulative, cumulative, coincidentValue, scalingFactor;

    MetricQualifierFields( unsigned val ) :
            extensionBit         ( getBitsField <15, 1> (val) ),
            primarySecondary     ( getBitsField <13, 2> (val) ),
            fundHarmonic         ( getBitsField <11, 2> (val) ),
            segmentation         ( getBitsField < 8, 3> (val) ),
            continuousCumulative ( getBitsField < 7, 1> (val) ),
            cumulative           ( getBitsField < 6, 1> (val) ),
            coincidentValue      ( getBitsField < 3, 3> (val) ),
            scalingFactor        ( getBitsField < 0, 3> (val) )
    {}

    std::string resolve() const
    {
        std::vector<std::string> descriptions;

        descriptions.push_back( resolveField( fundHarmonic,         "Fund/Harmonic",         metricQualifierResolver_FundHarmonic,          false ));
        descriptions.push_back( resolveField( primarySecondary,     "Primary/Secondary",     metricQualifierResolver_PrimarySecondary,      false ));
        descriptions.push_back( resolveField( segmentation,         "Segmentation",          metricQualifierResolver_Segmentation,          true  ));
        descriptions.push_back( resolveField( continuousCumulative, "Continuous Cumulative", metricQualifierResolver_ContinuousCumulative,  false ));
        descriptions.push_back( resolveField( cumulative,           "Cumulative",            metricQualifierResolver_Cumulative,            false ));
        descriptions.push_back( resolveField( coincidentValue,      "Coincident Value",      metricQualifierResolver_CoincidentValue,       false ));
        descriptions.push_back( resolveField( scalingFactor,        "Scaling Factor",        metricQualifierResolver_ScalingFactor,         true  ));

        std::string result;
        for each( const std::string &desc in descriptions )
        {
            if( ! desc.empty() )
            {
                result += result.empty() ? desc : (", " + desc);
            }
        }

        if( result.empty() )
        {
            result = "not specified";
        }

        return result;
    }

    std::string resolveField( unsigned val, const std::string &name, const std::map<unsigned, std::string> &resolverMap, bool showName ) const
    {
        boost::optional<std::string> desc = mapFind( resolverMap, val );

        validate( Condition( desc, ErrorInvalidData )
                << "Invalid metric qualifier value for \"" << name << "\" ("<< val << ")" );

        if( *desc == "" )
        {
            return ""; // return an empty string if not specified
        }

        return showName ? (name + ": " + *desc) : *desc;
    }
};

} // anonymous

//----------------------------------------------------------------------------
// Class RfnChannelConfigurationCommand
//----------------------------------------------------------------------------

RfnChannelConfigurationCommand::MetricIds RfnChannelConfigurationCommand::getMetricsReceived() const
{
    return _metricsReceived;
}

RfnChannelConfigurationCommand::TlvList RfnChannelConfigurationCommand::getTlvsToSend() const
{
    return TlvList();
}

RfnCommand::Bytes RfnChannelConfigurationCommand::getCommandData()
{
    return getBytesFromTlvs( getTlvsToSend() );
}

void RfnChannelConfigurationCommand::decodeHeader( const Bytes &response, RfnCommandResult &result )
{
    // We need at least 4 bytes for Command code, operation, status and the number of TLVs
    validate( Condition( response.size() >= 4, ErrorInvalidData )
            << "Invalid Response Length (" << response.size() << ")" );

    validate( Condition( response[0] == getResponseCommandCode(), ErrorInvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    validate( Condition( response[1] == getOperation(), ErrorInvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    boost::optional<std::string> status = mapFind( responseStatusResolver, response[2] );

    // invalid status byte -- not found in map
    validate( Condition( status, ErrorInvalidData )
            << "Invalid Status (" << response[2] << ")" );

    // make sure status is success
    validate( Condition( response[2] == 0, ErrorInvalidData )
            << "Status: " << *status << " (" << response[2] << ")" );

    result.description += "Status: " + *status + " (" + CtiNumStr(response[2]) + ")\n";
}

void RfnChannelConfigurationCommand::decodeMetricsIds( const Bytes &response, RfnCommandResult &result )
{
    validate( Condition( response.size() >= 1, ErrorInvalidData )
            << "Number of bytes for list of metric IDs received 0, expected >= 1" );

    unsigned offset = 0;

    const unsigned totalMetricsIds = response[offset++];
    const unsigned expectedSize = 1 + (totalMetricsIds * 2);

    validate( Condition( expectedSize == response.size(), ErrorInvalidData )
            << "Number of bytes for list of metric IDs received " << response.size() << ", expected " << expectedSize );

    result.description += "Metric(s) list:\n";

    if( offset == expectedSize )
    {
        result.description += "none\n";
        return;
    }

    while( offset < expectedSize )
    {
        const unsigned metricId = getValueFromBytes_bEndian( response, offset, 2 );
        offset += 2;

        std::string metricDescription = getMetricDescription(metricId);

        validate( Condition( ! metricDescription.empty(), ErrorInvalidData )
                << "Received unknown metric id (" << metricId << ")" );

        result.description += metricDescription + " (" + CtiNumStr(metricId) + ")\n";

        const bool isNewInsert = _metricsReceived.insert(metricId).second;
        validate( Condition( isNewInsert, ErrorInvalidData )
                << "Received unexpected duplicated metric: " << metricDescription << " (" << metricId << ")" );
    }
}


void RfnChannelConfigurationCommand::decodeChannelDescriptors( const Bytes &response, RfnCommandResult &result )
{
    validate( Condition( response.size() >= 1, ErrorInvalidData )
            << "Number of bytes for channel descriptors received 0, expected >= 1" );

    unsigned offset = 0;

    const unsigned totalChannelDescriptors = response[offset++];
    const unsigned expectedSize = 1 + (totalChannelDescriptors * 4);

    validate( Condition( expectedSize == response.size(), ErrorInvalidData )
            << "Number of bytes for channel descriptors received " << response.size() << ", expected " << expectedSize );

    result.description += "Metric(s) descriptors:\n";

    if( offset == expectedSize )
    {
        result.description += "none\n";
        return;
    }

    std::set<unsigned> metricsIdsReceived, metricsIdsCoincidentReceived;

    unsigned coincidentValue = -1; // start with -1 in case the first metric as a none-zero coincident value

    while( offset < expectedSize )
    {
        const unsigned metricId = getValueFromBytes_bEndian( response, offset, 2 );
        offset += 2;

        std::string metricDescription = getMetricDescription(metricId);

        validate( Condition( ! metricDescription.empty(), ErrorInvalidData )
                << "Received unknown metric id (" << metricId << ")" );

        result.description += metricDescription + " (" + CtiNumStr(metricId) + "): ";

        const unsigned metricQualifier = getValueFromBytes_bEndian( response, offset, 2 );
        offset += 2;

        const MetricQualifierFields metricQFields( metricQualifier );

        validate( Condition( ! metricQFields.extensionBit, ErrorInvalidData )
                << "Metric qualifier expected extension bit to be zero" );

        if( metricQFields.coincidentValue == 0 )
        {
            // check for duplicated metricId
            const bool isNewInsert = metricsIdsReceived.insert(metricId).second;
            validate( Condition( isNewInsert, ErrorInvalidData )
                    << "Received unexpected duplicated metric: " << metricDescription << " (" << metricId << ")" );

            coincidentValue = 0;
            metricsIdsCoincidentReceived.clear();
        }
        else
        {
            const unsigned coincidentValueExp = (coincidentValue < 7) ? ++coincidentValue : 0;

            validate( Condition( metricQFields.coincidentValue == coincidentValueExp, ErrorInvalidData )
                    << "Received unexpected coincident value: " << metricQFields.coincidentValue << " (expected: " << coincidentValueExp << "), "
                    << metricDescription << " (" << metricId << ")" );

            // check for duplicated coincident metricId
            const bool isNewInsert = metricsIdsCoincidentReceived.insert(metricId).second;
            validate( Condition( isNewInsert, ErrorInvalidData )
                    << "Received unexpected duplicated coincident metric: " << metricDescription << " (" << metricId << ")"
                    << ", coincident value: " << coincidentValue );
        }

        result.description += metricQFields.resolve() + "\n";
    }
}

//----------------------------------------------------------------------------
// Class RfnChannelSelectionCommand
//----------------------------------------------------------------------------

const RfnChannelSelectionCommand::LongTlvList RfnChannelSelectionCommand::longTlvs = boost::assign::list_of
        (TlvType_ChannelSelectionConfiguration)
        (TlvType_ChannelSelectionFullDescription);

void RfnChannelSelectionCommand::invokeResultHandler( RfnCommand::ResultHandler &rh ) const
{
    rh.handleCommandResult( *this );
}

unsigned char RfnChannelSelectionCommand::getCommandCode() const
{
    return CommandCode_Request;
}

unsigned char RfnChannelSelectionCommand::getResponseCommandCode() const
{
    return CommandCode_Response;
}

RfnCommandResult RfnChannelSelectionCommand::decodeCommand( const CtiTime now,
                                                            const RfnResponsePayload & response )
{
    RfnCommandResult result;

    decodeHeader( response, result );

    decodeTlvs( getTlvsFromBytes( Bytes( response.begin() + 3, response.end()), longTlvs ), result );

    return result;
}

void RfnChannelSelectionCommand::decodeTlvs( const TlvList& tlvs, RfnCommandResult &result )
{
    std::set<unsigned char> tlvs_received;

    const std::set<unsigned char> tlvs_expected = (getOperation() == Operation_GetChannelSelectionConfiguration) ?
            boost::assign::list_of
                (static_cast<unsigned char>(TlvType_ChannelSelectionConfiguration)) :
            boost::assign::list_of
                (static_cast<unsigned char>(TlvType_ChannelSelectionConfiguration))
                (static_cast<unsigned char>(TlvType_ChannelSelectionFullDescription));

    for each( const TypeLengthValue& tlv in tlvs )
    {
         validate( Condition( tlvs_expected.count(tlv.type), ErrorInvalidData )
                 << "Unexpected TLV of type (" << tlv.type << ")" );

         const bool isNewInsert = tlvs_received.insert(tlv.type).second;
         validate( Condition( isNewInsert, ErrorInvalidData )
                 << "Unexpected duplicated TLV of type (" << tlv.type << ")" );

         switch( tlv.type )
         {
         case TlvType_ChannelSelectionConfiguration :
             {
                 result.description += "Channel Selection Configuration:\n";
                 decodeMetricsIds( tlv.value, result );
                 break;
             }
         case TlvType_ChannelSelectionFullDescription :
             {
                 result.description += "Channel Registration Full Description:\n";
                 decodeChannelDescriptors( tlv.value, result );
                 break;
             }
         }
    }

    validate( Condition( tlvs_received == tlvs_expected, ErrorInvalidData )
            << "Received TLV of type(s): (" << tlvs_received << "), expected: (" << tlvs_expected << ")" );
}

//----------------------------------------------------------------------------
// Class RfnSetChannelSelectionCommand
//----------------------------------------------------------------------------

RfnSetChannelSelectionCommand::RfnSetChannelSelectionCommand( const MetricIds& metrics )
{
    const unsigned maxMetrics = 80;

    validate( Condition( metrics.size() <= maxMetrics, BADPARAM )
            << "Number of metrics " << metrics.size() << ", expected <= " << maxMetrics );

    _setChannelSelectionTlvPayload.push_back( metrics.size() );

    for each( unsigned metricId in metrics )
    {
        validate( Condition( metricIdResolver.count(metricId), BADPARAM )
                << "Invalid metric id (" << metricId << ")" );

        insertValue_bEndian<2> ( _setChannelSelectionTlvPayload, metricId );
    }
}

RfnChannelConfigurationCommand::TlvList RfnSetChannelSelectionCommand::getTlvsToSend() const
{
    return boost::assign::list_of
            (TypeLengthValue::makeLongTlv( TlvType_ChannelSelectionConfiguration, _setChannelSelectionTlvPayload ));
}

unsigned char RfnSetChannelSelectionCommand::getOperation() const
{
    return Operation_SetChannelSelectionConfiguration;
}

//----------------------------------------------------------------------------
// Class RfnGetChannelSelectionCommand
//----------------------------------------------------------------------------

unsigned char RfnGetChannelSelectionCommand::getOperation() const
{
    return Operation_GetChannelSelectionConfiguration;
}

//----------------------------------------------------------------------------
// Class RfnGetChannelSelectionFullDescriptionCommand
//----------------------------------------------------------------------------

unsigned char RfnGetChannelSelectionFullDescriptionCommand::getOperation() const
{
    return Operation_GetChannelSelectionFullDescription;
}

//----------------------------------------------------------------------------
// Class RfnChannelIntervalRecordingCommand
//----------------------------------------------------------------------------

void RfnChannelIntervalRecordingCommand::invokeResultHandler( RfnCommand::ResultHandler &rh ) const
{
    rh.handleCommandResult( *this );
}

unsigned char RfnChannelIntervalRecordingCommand::getCommandCode() const
{
    return CommandCode_Request;
}

unsigned char RfnChannelIntervalRecordingCommand::getResponseCommandCode() const
{
    return CommandCode_Response;
}

RfnCommandResult RfnChannelIntervalRecordingCommand::decodeCommand( const CtiTime now,
                                                                    const RfnResponsePayload & response )
{
    RfnCommandResult result;

    decodeHeader( response, result );

    decodeTlvs( getTlvsFromBytes( Bytes( response.begin() + 3, response.end()) ), result );

    return result;
}

void RfnChannelIntervalRecordingCommand::decodeTlvs( const TlvList& tlvs, RfnCommandResult &result )
{
    std::set<unsigned char> tlvs_received;

    const std::set<unsigned char> tlvs_expected = boost::assign::list_of
            (static_cast<unsigned char>(TlvType_ChannelIntervalRecordingConfiguration))
            (static_cast<unsigned char>(TlvType_ChannelIntervalRecordingFullDescription));

    for each( const TypeLengthValue& tlv in tlvs )
    {
         validate( Condition( tlvs_expected.count(tlv.type), ErrorInvalidData )
                 << "Unexpected TLV of type (" << tlv.type << ")" );

         const bool isNewInsert = tlvs_received.insert(tlv.type).second;
         validate( Condition( isNewInsert, ErrorInvalidData )
                 << "Unexpected duplicated TLV of type (" << tlv.type << ")" );

         switch( tlv.type )
         {
         case TlvType_ChannelIntervalRecordingConfiguration :
             {
                 result.description += "Channel Interval Recording Configuration:\n";
                 decodeChannelIntervalRecording( tlv.value, result );
                 break;
             }
         case TlvType_ChannelIntervalRecordingFullDescription :
             {
                 result.description += "Channel Interval Recoding Full Description:\n";
                 decodeChannelDescriptors( tlv.value, result );
                 break;
             }
         }
    }

    validate( Condition( tlvs_received == tlvs_expected, ErrorInvalidData )
            << "Received TLV of type(s): (" << tlvs_received << "), expected: (" << tlvs_expected << ")" );
}

void RfnChannelIntervalRecordingCommand::decodeChannelIntervalRecording( const Bytes &response, RfnCommandResult &result )
{
    validate( Condition( response.size() >= 9, ErrorInvalidData )
            << "Number of bytes for interval recording received " << response.size() << ", expected >= 9" );

    unsigned offset = 0;

    _intervalRecordingSecondsReceived = getValueFromBytes_bEndian( response, offset, 4 );
    offset += 4;

    _intervalReportingSecondsReceived = getValueFromBytes_bEndian( response, offset, 4 );
    offset += 4;

    result.description += "Interval Recording: " + CtiNumStr(_intervalRecordingSecondsReceived) + " seconds\n" +
                          "Interval Reporting: " + CtiNumStr(_intervalReportingSecondsReceived) + " seconds\n";

    decodeMetricsIds( Bytes(response.begin() + 8 , response.end()), result );
}

unsigned RfnChannelIntervalRecordingCommand::getIntervalRecordingSecondsReceived() const
{
    return _intervalRecordingSecondsReceived;
}

unsigned RfnChannelIntervalRecordingCommand::getIntervalReportingSecondsReceived() const
{
    return _intervalReportingSecondsReceived;
}

//----------------------------------------------------------------------------
// Class RfnSetChannelIntervalRecordingCommand
//----------------------------------------------------------------------------

RfnSetChannelIntervalRecordingCommand::RfnSetChannelIntervalRecordingCommand( const MetricIds& metrics,
                                                                              unsigned intervalRecordingSeconds,
                                                                              unsigned intervalReportingSeconds )
{
    insertValue_bEndian<4> ( _setIntervalRecordingTlvPayload, intervalRecordingSeconds );
    insertValue_bEndian<4> ( _setIntervalRecordingTlvPayload, intervalReportingSeconds );

    const unsigned maxMetrics = 15;

    validate( Condition( metrics.size() <= maxMetrics, BADPARAM )
            << "Number of metrics " << metrics.size() << ", expected <= " << maxMetrics );

    _setIntervalRecordingTlvPayload.push_back( metrics.size() );

    for each( unsigned metricId in metrics )
    {
        validate( Condition( metricIdResolver.count(metricId), BADPARAM )
                << "Invalid metric id (" << metricId << ")" );

        insertValue_bEndian<2> ( _setIntervalRecordingTlvPayload, metricId );
    }
}

RfnChannelConfigurationCommand::TlvList RfnSetChannelIntervalRecordingCommand::getTlvsToSend() const
{
    return boost::assign::list_of
            (TypeLengthValue( TlvType_ChannelIntervalRecordingConfiguration, _setIntervalRecordingTlvPayload ));
}

unsigned char RfnSetChannelIntervalRecordingCommand::getOperation() const
{
    return Operation_SetChannelIntervalRecordingConfiguration;
}

//----------------------------------------------------------------------------
// Class RfnGetChannelIntervalRecordingCommand
//----------------------------------------------------------------------------

unsigned char RfnGetChannelIntervalRecordingCommand::getOperation() const
{
    return Operation_GetChannelIntervalRecordingConfiguration;
}

} // Commands
} // Devices
} // Cti
