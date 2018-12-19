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
        {  13,  TouRates,  "Watts, peak"                           },

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
        {  38,  TouRates,  "Var Q1"                                },
        {  39,  TouRates,  "Var Q2"                                },
        {  40,  TouRates,  "Var Q3"                                },

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

        {  78,  TouRates,  "Average Delivered Power Factor"        },
        {  79,  TouRates,  "Average Received Power Factor"         },
        {  80,  TouRates,  "Power Factor"                          },
        {  81,  TouRates,  "Average Power Factor (Quadrants 1 2 4)"},
        {  82,  TouRates,  "Average Power Factor (Quadrants 2 3 4)"},
        {  83,  TouRates,  "Average Power Factor"                  },

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

        { 128,  TouRates,  "Current Neutral to Ground"             },

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
        { 160,  TouRates,  "PF degree Phase B"                     },
        { 161,  TouRates,  "PF degree Phase C"                     },
        { 162,  TouRates,  "PF Phase A"                            },
        { 163,  TouRates,  "PF Phase B"                            },
        { 164,  TouRates,  "PF Phase C"                            },
        
        { 180,  TouRates,  "Peak kVAr (Quadrants 1 4)"             },
        { 181,  TouRates,  "Peak kVAr (Quadrants 2 3)"             },

        { 184,  TouRates,  "Sum Peak kVAr"                         },

        { 200,  TouRates,  "Watts"                                 },
        { 201,  TouRates,  "Var"                                   },
        { 202,  TouRates,  "VA"                                    },

        { 210,  TouRates,  "Sum Peak kVA"                          },

        { 240,  TouRates,  "Peak Demand Daily"                     },

        { 256,  TouRates,  "Time in Seconds"                       },
        { 257,  None,      "Temperature in Centigrade"             },

        { 330,  None,      "Sum Watts" },
        { 331,  None,      "Net Watts" },

        { 340,  None,      "kVAr (Quadrants 1 3)" },
        { 341,  None,      "kVAr (Quadrants 2 4)" },
        { 342,  None,      "kVAr (Quadrants 1 4)" },
        { 343,  None,      "kVAr (Quadrants 2 3)" },
        { 344,  None,      "Sum Vars" },
        { 345,  None,      "Net Vars" },

        { 350,  None,      "kVA (Quadrants 1 2)" },
        { 351,  None,      "kVA (Quadrants 3 4)" },
        { 352,  None,      "kVA (Quadrants 1 3)" },
        { 353,  None,      "kVA (Quadrants 2 4)" },
        { 354,  None,      "Sum VA" },
        { 355,  None,      "Net VA" }};

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
        return "Unknown";
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
            fundHarmonic         ( getBitsField <13, 2> (val) ),
            primarySecondary     ( getBitsField <11, 2> (val) ),
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

        validate( Condition( !! desc, ClientErrors::InvalidData )
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

std::string RfnChannelConfigurationCommand::decodeHeader( const Bytes &response )
{
    // We need at least 4 bytes for Command code, operation, status and the number of TLVs
    validate( Condition( response.size() >= 4, ClientErrors::InvalidData )
            << "Invalid Response Length (" << response.size() << ")" );

    validate( Condition( response[0] == getResponseCommandCode(), ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    validate( Condition( response[1] == getOperation(), ClientErrors::InvalidData )
            << "Invalid Operation Code (" << CtiNumStr(response[1]).xhex(2) << ")" );

    boost::optional<std::string> status = mapFind( responseStatusResolver, response[2] );

    // invalid status byte -- not found in map
    validate( Condition( !! status, ClientErrors::InvalidData )
            << "Invalid Status (" << response[2] << ")" );

    // make sure status is success
    validate( Condition( response[2] == 0, ClientErrors::InvalidData )
            << "Status: " << *status << " (" << response[2] << ")" );

    return "Status: " + *status + " (" + std::to_string(response[2]) + ")";
}

std::string RfnChannelConfigurationCommand::decodeMetricsIds( const Bytes &response )
{
    validate( Condition( response.size() >= 1, ClientErrors::InvalidData )
            << "Number of bytes for list of metric IDs received 0, expected >= 1" );

    unsigned offset = 0;

    const unsigned totalMetricsIds = response[offset++];
    const unsigned expectedSize = 1 + (totalMetricsIds * 2);

    validate( Condition( expectedSize == response.size(), ClientErrors::InvalidData )
            << "Number of bytes for list of metric IDs received " << response.size() << ", expected " << expectedSize );

    std::string description = "Metric(s) list:\n";

    if( offset == expectedSize )
    {
        return description += "none\n";
    }

    while( offset < expectedSize )
    {
        const unsigned metricId = getValueFromBytes_bEndian( response, offset, 2 );
        offset += 2;

        description += getMetricDescription(metricId) + " (" + std::to_string(metricId) + ")\n";
    }

    return description;
}


bool isValidRecordingMetric( const unsigned metricId )
{
    //  Metric ID 0 is a the catch-all "unrecognized" metric ID.
    //  Metric IDs 256, 1256, 2256, 3256, and 4256 are time metrics that are not actual recording channels.
    //    They are being mistakenly returned due to firmware bug RFNFIVE-353.
    return metricId && ((metricId % 1000) != 256);
}


std::string RfnChannelConfigurationCommand::decodeChannelDescriptors( const Bytes &response )
{
    validate( Condition( response.size() >= 1, ClientErrors::InvalidData )
            << "Number of bytes for channel descriptors received 0, expected >= 1" );

    unsigned offset = 0;

    const unsigned totalChannelDescriptors = response[offset++];
    const unsigned expectedSize = 1 + (totalChannelDescriptors * 4);

    validate( Condition( expectedSize == response.size(), ClientErrors::InvalidData )
            << "Number of bytes for channel descriptors received " << response.size() << ", expected " << expectedSize );

    std::string description = "Metric(s) descriptors:\n";

    if( offset == expectedSize )
    {
        return description += "none\n";
    }

    unsigned coincidentValue = -1; // start with -1 in case the first metric has a non-zero coincident value

    while( offset < expectedSize )
    {
        const unsigned metricId = getValueFromBytes_bEndian( response, offset, 2 );
        offset += 2;

        std::string metricDescription = getMetricDescription(metricId);

        description += metricDescription + " (" + std::to_string(metricId) + "): ";

        const unsigned metricQualifier = getValueFromBytes_bEndian( response, offset, 2 );
        offset += 2;

        const MetricQualifierFields metricQFields( metricQualifier );

        validate( Condition( ! metricQFields.extensionBit, ClientErrors::InvalidData )
                << "Metric qualifier expected extension bit to be zero" );

        if( metricQFields.coincidentValue == 0 )
        {
            if( isValidRecordingMetric(metricId) )
            {
                //  Duplicates are allowed - it's possible to receive multiple voltage
                //    metrics with different qualifiers, for example.
                //  That particular case is due to a marketing requirement to report both
                //    the meter's voltage and the Metrology library's voltage.
                _metricsReceived.insert(metricId);
            }

            coincidentValue = 0;
        }
        else
        {
            const unsigned coincidentValueExp = (coincidentValue < 7) ? ++coincidentValue : 0;

            if( metricQFields.coincidentValue != coincidentValueExp )
            {
                CTILOG_ERROR(dout, "Received unexpected coincident value: " << metricQFields.coincidentValue 
                    << " (expected: " << coincidentValueExp << "), " << metricDescription << " (" << metricId << ")" );
            }

            //  Ignore the coincidents - we don't record them in DynamicPaoInfo or report them to the user
        }

        description += metricQFields.resolve() + "\n";
    }

    return description;
}

//----------------------------------------------------------------------------
// Class RfnChannelSelectionCommand
//----------------------------------------------------------------------------

const RfnChannelSelectionCommand::LongTlvList RfnChannelSelectionCommand::longTlvs = boost::assign::list_of
        (TlvType_ChannelSelection_Configuration)
        (TlvType_ChannelSelection_ActiveChannels);

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
    auto description = decodeHeader( response );
    return description + "\n" + decodeTlvs( getTlvsFromBytes( Bytes( response.begin() + 3, response.end()), longTlvs ), getExpectedTlvType() );
}

std::string RfnChannelSelectionCommand::getCommandName()
{
    return "Channel Selection Request";
}

std::string RfnChannelSelectionCommand::decodeTlvs( const TlvList& tlvs, const unsigned char tlvTypeExpected )
{
    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Unexpected TLV count (" << tlvs.size() << "), expected (1)" );

    const TypeLengthValue & tlv = tlvs[0];

    validate( Condition( tlv.type == tlvTypeExpected, ClientErrors::InvalidData )
             << "Unexpected TLV of type (" << tlv.type << "), expected (" << (unsigned)tlvTypeExpected << ")" );

    switch( tlv.type )
    {
        case TlvType_ChannelSelection_Configuration :
        {
            return "Channel Selection Configuration:\n" 
                + decodeMetricsIds( tlv.value );
        }
        case TlvType_ChannelSelection_ActiveChannels :
        {
            return "Channel Registration Full Description:\n" 
                + decodeChannelDescriptors( tlv.value );
        }
        default:
        {
            return "Unknown TLV type (" + std::to_string(tlv.type) + ")";
        }
    }
}

//----------------------------------------------------------------------------
// Class RfnSetChannelSelectionCommand
//----------------------------------------------------------------------------

RfnSetChannelSelectionCommand::RfnSetChannelSelectionCommand( const MetricIds& metrics )
{
    const unsigned maxMetrics = 80;

    validate( Condition( metrics.size() <= maxMetrics, ClientErrors::BadParameter )
            << "Number of metrics " << metrics.size() << ", expected <= " << maxMetrics );

    _setChannelSelectionTlvPayload.push_back( metrics.size() );

    for each( unsigned metricId in metrics )
    {
        validate( Condition( metricIdResolver.count(metricId), ClientErrors::BadParameter )
                << "Invalid metric id (" << metricId << ")" );

        insertValue_bEndian<2> ( _setChannelSelectionTlvPayload, metricId );
    }
}

RfnChannelConfigurationCommand::TlvList RfnSetChannelSelectionCommand::getTlvsToSend() const
{
    return boost::assign::list_of
            (TypeLengthValue::makeLongTlv( TlvType_ChannelSelection_Configuration, _setChannelSelectionTlvPayload ));
}

unsigned char RfnSetChannelSelectionCommand::getOperation() const
{
    return Operation_SetChannelSelectionConfiguration;
}

unsigned char RfnSetChannelSelectionCommand::getExpectedTlvType() const
{
    return TlvType_ChannelSelection_ActiveChannels;
}

//----------------------------------------------------------------------------
// Class RfnGetChannelSelectionCommand
//----------------------------------------------------------------------------

unsigned char RfnGetChannelSelectionCommand::getOperation() const
{
    return Operation_GetChannelSelectionConfiguration;
}

unsigned char RfnGetChannelSelectionCommand::getExpectedTlvType() const
{
    return TlvType_ChannelSelection_Configuration;
}

//----------------------------------------------------------------------------
// Class RfnGetChannelSelectionFullDescriptionCommand
//----------------------------------------------------------------------------

unsigned char RfnGetChannelSelectionFullDescriptionCommand::getOperation() const
{
    return Operation_GetChannelSelectionActiveChannels;
}

unsigned char RfnGetChannelSelectionFullDescriptionCommand::getExpectedTlvType() const
{
    return TlvType_ChannelSelection_ActiveChannels;
}

//----------------------------------------------------------------------------
// Class RfnChannelIntervalRecordingCommand
//----------------------------------------------------------------------------

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
    std::string description = decodeHeader( response );

    TlvList tlvs = getTlvsFromBytes( Bytes( response.begin() + 3, response.end()) );

    validate( Condition( tlvs.size() == 1, ClientErrors::InvalidData )
            << "Unexpected TLV count (" << tlvs.size() << "), expected (1)" );

    return description + "\n" + decodeTlv( tlvs[0] );
}

std::string RfnChannelIntervalRecordingCommand::getCommandName()
{
    return "Channel Interval Recording Request";
}

namespace RfnChannelIntervalRecording {

//----------------------------------------------------------------------------
// Class RfnSetChannelIntervalRecordingCommand
//----------------------------------------------------------------------------

SetConfigurationCommand::SetConfigurationCommand( const MetricIds& metrics,
                                                  unsigned intervalRecordingSeconds,
                                                  unsigned intervalReportingSeconds )
    :   _intervalRecordingSeconds(intervalRecordingSeconds),
        _intervalReportingSeconds(intervalReportingSeconds)
{
    insertValue_bEndian<4> ( _setIntervalRecordingTlvPayload, _intervalRecordingSeconds );
    insertValue_bEndian<4> ( _setIntervalRecordingTlvPayload, _intervalReportingSeconds );

    const unsigned maxMetrics = 15;

    validate( Condition( metrics.size() <= maxMetrics, ClientErrors::BadParameter )
            << "Number of metrics " << metrics.size() << ", expected <= " << maxMetrics );

    _setIntervalRecordingTlvPayload.push_back( metrics.size() );

    for each( unsigned metricId in metrics )
    {
        validate( Condition( metricIdResolver.count(metricId), ClientErrors::BadParameter )
                << "Invalid metric id (" << metricId << ")" );

        insertValue_bEndian<2> ( _setIntervalRecordingTlvPayload, metricId );
    }
}

RfnChannelConfigurationCommand::TlvList SetConfigurationCommand::getTlvsToSend() const
{
    return boost::assign::list_of
            (TypeLengthValue( TlvType_ChannelIntervalRecording_Configuration, _setIntervalRecordingTlvPayload ));
}

unsigned char SetConfigurationCommand::getOperation() const
{
    return Operation_SetChannelIntervalRecordingConfiguration;
}

std::string SetConfigurationCommand::decodeTlv( const TypeLengthValue& tlv )
{
    validate( Condition( tlv.type == TlvType_ChannelIntervalRecording_ActiveChannels, ClientErrors::InvalidData )
             << "Unexpected TLV of type (" << tlv.type << "), expected (" << (unsigned)TlvType_ChannelIntervalRecording_ActiveChannels << ")" );

    return "Channel Interval Recording Full Description:\n" + decodeChannelDescriptors( tlv.value );
}

unsigned SetConfigurationCommand::getIntervalRecordingSeconds() const
{
    return _intervalRecordingSeconds;
}

unsigned SetConfigurationCommand::getIntervalReportingSeconds() const
{
    return _intervalReportingSeconds;
}

auto SetConfigurationCommand::getIntervalMetrics() const -> MetricIds
{
    return getMetricsReceived();
}

//----------------------------------------------------------------------------
// Class RfnChannelIntervalRecording::GetConfigurationCommand
//----------------------------------------------------------------------------

unsigned char GetConfigurationCommand::getOperation() const
{
    return Operation_GetChannelIntervalRecordingConfiguration;
}

std::string GetConfigurationCommand::decodeTlv( const TypeLengthValue& tlv )
{
    validate( Condition( tlv.type == TlvType_ChannelIntervalRecording_Configuration, ClientErrors::InvalidData )
             << "Unexpected TLV of type (" << tlv.type << "), expected (" << (unsigned)TlvType_ChannelIntervalRecording_Configuration << ")" );

    return "Channel Interval Recording Configuration:\n" + decodeChannelIntervalRecording( tlv.value );
}

std::string GetConfigurationCommand::decodeChannelIntervalRecording( const Bytes &response )
{
    validate( Condition( response.size() >= 9, ClientErrors::InvalidData )
            << "Number of bytes for interval recording received " << response.size() << ", expected >= 9" );

    unsigned offset = 0;

    _intervalRecordingSecondsReceived = getValueFromBytes_bEndian( response, offset, 4 );
    offset += 4;

    _intervalReportingSecondsReceived = getValueFromBytes_bEndian( response, offset, 4 );
    offset += 4;

    return "Interval Recording: " + std::to_string(_intervalRecordingSecondsReceived) + " seconds\n" +
           "Interval Reporting: " + std::to_string(_intervalReportingSecondsReceived) + " seconds\n" +
           decodeMetricsIds( Bytes(response.begin() + 8 , response.end()) );
}

unsigned GetConfigurationCommand::getIntervalRecordingSecondsReceived() const
{
    return _intervalRecordingSecondsReceived;
}

unsigned GetConfigurationCommand::getIntervalReportingSecondsReceived() const
{
    return _intervalReportingSecondsReceived;
}

//----------------------------------------------------------------------------
// Class RfnChannelIntervalRecording::GetActiveConfigurationCommand
//----------------------------------------------------------------------------

unsigned char GetActiveConfigurationCommand::getOperation() const
{
    return Operation_GetChannelIntervalRecordingActiveConfiguration;
}

std::string GetActiveConfigurationCommand::decodeTlv( const TypeLengthValue& tlv )
{
    validate( Condition( tlv.type == TlvType_ChannelIntervalRecording_ActiveConfiguration, ClientErrors::InvalidData )
             << "Unexpected TLV of type (" << tlv.type << "), expected (" << (unsigned)TlvType_ChannelIntervalRecording_ActiveConfiguration << ")" );

    return "Channel Interval Recording Active Configuration:\n" + decodeActiveConfiguration( tlv.value );
}

std::string GetActiveConfigurationCommand::decodeActiveConfiguration( const Bytes &response )
{
    validate( Condition( response.size() >= 9, ClientErrors::InvalidData )
            << "Number of bytes for interval recording received " << response.size() << ", expected >= 9" );

    unsigned offset = 0;

    _intervalRecordingSecondsReceived = getValueFromBytes_bEndian( response, offset, 4 );
    offset += 4;

    _intervalReportingSecondsReceived = getValueFromBytes_bEndian( response, offset, 4 );
    offset += 4;

    return "Interval Recording: " + std::to_string(_intervalRecordingSecondsReceived) + " seconds\n" +
           "Interval Reporting: " + std::to_string(_intervalReportingSecondsReceived) + " seconds\n" +
           decodeChannelDescriptors( Bytes(response.begin() + 8, response.end()) );
}

unsigned GetActiveConfigurationCommand::getIntervalRecordingSeconds() const
{
    return _intervalRecordingSecondsReceived;
}

unsigned GetActiveConfigurationCommand::getIntervalReportingSeconds() const
{
    return _intervalReportingSecondsReceived;
}

auto GetActiveConfigurationCommand::getIntervalMetrics() const -> MetricIds
{
    return getMetricsReceived();
}

} // RfnChannelIntervalRecording

} // Commands
} // Devices
} // Cti
