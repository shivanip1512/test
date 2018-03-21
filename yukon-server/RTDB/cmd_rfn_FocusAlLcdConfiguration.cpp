#include "precompiled.h"

#include "std_helper.h"
#include "cmd_rfn_FocusAlLcdConfiguration.h"
#include "cmd_rfn_helper.h"

#include <boost/bimap.hpp>

using namespace std;

namespace Cti {
namespace Devices {
namespace Commands {

namespace {

enum
{
    FocusLcdConfig_CommandCode_Request  = 0x72,
    FocusLcdConfig_CommandCode_Response = 0x73,
};

enum
{
    FocusLcdConfig_Operation_Write      = 0x00,
    FocusLcdConfig_Operation_Read       = 0x01,
};

struct MetricDescription
{
    MetricDescription(RfnFocusAlLcdConfigurationCommand::Metrics metric_, std::string description_) : metric(metric_), description(description_) {}

    RfnFocusAlLcdConfigurationCommand::Metrics metric;
    std::string description;
};

typedef RfnFocusAlLcdConfigurationCommand Cmd;

const std::map<unsigned char, std::string> StatusItems
{
    { 0x0, "Success"},
    { 0x1, "Failure"}
};

const std::map<unsigned char, MetricDescription> MetricDescriptions 
{
    { 0x00, {Cmd::deliveredKwh6x1,  "Delivered kWh (6 digit)"        }},
    { 0x01, {Cmd::deliveredKwh5x1,  "Delivered kWh (5 digit)"        }},
    { 0x02, {Cmd::deliveredKwh4x1,  "Delivered kWh (4 digit)"        }},
    { 0x03, {Cmd::deliveredKwh4x10, "Delivered kWh (4 x 10  digit)"  }},
    { 0x04, {Cmd::reverseKwh6x1,    "Reverse kWh (6 digit)"          }},
    { 0x05, {Cmd::reverseKwh5x1,    "Reverse kWh (5 digit)"          }},
    { 0x06, {Cmd::reverseKwh4x1,    "Reverse kWh (4 digit)"          }},
    { 0x07, {Cmd::receivedKwh4x10,  "Reverse kWh (4 x 10  digit)"    }},
    { 0x08, {Cmd::totalKwh6x1,      "Total kWh (6 digit)"            }},
    { 0x09, {Cmd::totalKwh5x1,      "Total kWh (5 digit)"            }},
    { 0x0a, {Cmd::totalKwh4x1,      "Total kWh (4 digit)"            }},
    { 0x0b, {Cmd::totalKwh4x10,     "Total kWh (4 x 10  digit)"      }},
    { 0x0c, {Cmd::netKwh6x1,        "Net kWh (6 digit)"              }},
    { 0x0d, {Cmd::netKwh5x1,        "Net kWh (5 digit)"              }},
    { 0x0e, {Cmd::netKwh4x1,        "Net kWh (4 digit)"              }},
    { 0x0f, {Cmd::netKwh4x10,       "Net kWh (4 x 10  digit)"        }},
    { 0x10, {Cmd::diagnosticFlags,  "Diagnostic flags"               }},
    { 0x11, {Cmd::allSegments,      "All Segments"                   }},
    { 0x12, {Cmd::firmwareVersion,  "Firmware version"               }}
};

typedef boost::bimap<unsigned char, char> AlphaDisplayBimap;

AlphaDisplayBimap initAlphaDisplayBimap()
{
    AlphaDisplayBimap bm;

    typedef AlphaDisplayBimap::value_type bm_value_type;

    for(unsigned char code = 0x30; code <= 0x39; code++)
    {
        bm.insert(bm_value_type(code,code));    // 0 � 9   30h - 39h (ASCII Equivalent)
    }

    bm.insert(bm_value_type(0x3a,'$'));         // all segments
    bm.insert(bm_value_type(0x3b,'*'));         // Star (*)
    bm.insert(bm_value_type(0x3c,' '));         // Blank
    bm.insert(bm_value_type(0x3d,'='));         // '=' 3Dh (ASCII Equivalent)
    bm.insert(bm_value_type(0x3e,'>'));         // '>' 3Eh (ASCII Equivalent)
    bm.insert(bm_value_type(0x3f,'+'));         // '+' 3Fh
    bm.insert(bm_value_type(0x40,'-'));         // '-' 40h

    for(unsigned char code = 0x41; code <= 0x5a; code++)
    {
        bm.insert(bm_value_type(code,code));    // A � Z   41h - 5Ah (ASCII Equivalent)
    }

    return bm;
}

const AlphaDisplayBimap alphaDisplayBimap = initAlphaDisplayBimap();

boost::optional<char> convertCodeToAscii( unsigned char code )
{
    return bimapFind<char>( alphaDisplayBimap.left, code );
}

boost::optional<unsigned char> convertAsciiToCode( char ascii )
{
    return bimapFind<unsigned char>( alphaDisplayBimap.right, ascii );
}

} // anonymous namespace

unsigned char RfnFocusAlLcdConfigurationCommand::getCommandCode() const
{
    return FocusLcdConfig_CommandCode_Request;
}

unsigned char RfnFocusAlLcdConfigurationReadCommand::getOperation() const
{
    return FocusLcdConfig_Operation_Read;
}

RfnCommand::Bytes RfnFocusAlLcdConfigurationReadCommand::getCommandData()
{
    return Bytes();
}

RfnCommandResult RfnFocusAlLcdConfigurationReadCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    /*
     *  Length (Bytes)  Field Description
     *  1               Command Code
     *  1               # of display items (# of display items scrolling)
     *  1               time in seconds to display each item
     *  N/A             Items to display (repeated # display items times)
     *  1               Metric index code (see table below)
     *  2               Two bytes to be displayed as ID code on the display (see Alpha Display ID table below)
     *
     */

    validate(Condition( response.size() >= 2, ClientErrors::Unknown )
            << "Response too small - (" << response.size() << ", expecting >= 2)");

    const unsigned char commandCode     = response[0],
                        statusCode      = response[1];

    const boost::optional<string> statusResult = mapFind( StatusItems, statusCode );

    validate( Condition( !! statusResult, ClientErrors::InvalidData )
            << "Invalid status - (" << statusCode << ")");

    validate(Condition( statusCode == 0x00, ClientErrors::Unknown )
            << "Failure status - (" << statusCode << ")");

    validate( Condition( response.size() >= 4, ClientErrors::InvalidData )
            << "Response too small - (" << response.size() << ", expecting >= 4)" );

    const unsigned char displayItemNbr      = response[2],
                        displayItemDuration = response[3];

    // check command
    validate( Condition( commandCode == FocusLcdConfig_CommandCode_Response, ClientErrors::InvalidData )
            << "Invalid command - (" << commandCode << ", expecting " << FocusLcdConfig_CommandCode_Response << ")" );

    // update display item duration
    _displayItemDurationReceived = displayItemDuration;

    std::string description = "Display items received:";

    description += "\nLCD cycle time : " + CtiNumStr( displayItemDuration ) + " seconds";

    const unsigned responseSizeExp = 4 + displayItemNbr*3;

    validate( Condition( response.size() == responseSizeExp, ClientErrors::InvalidData )
            << "Invalid response size (" << response.size() << ", expecting " << responseSizeExp << ")" );

    _displayItemsReceived = MetricVector();

    if( ! displayItemNbr )
    {
        return description += "\nNo display metrics";
    }

    Bytes::const_iterator response_iter = response.begin() + 4;

    for(unsigned item = 0 ; item < displayItemNbr ; ++item)
    {
        const unsigned char metric_code = *(response_iter++);

        const boost::optional<MetricDescription> metric = mapFind( MetricDescriptions, metric_code );

        validate( Condition( !! metric, ClientErrors::InvalidData )
                << "Invalid metric code (" << metric_code << ")" );

        std::string alphamericId;

        for(int alpha_nbr = 0; alpha_nbr < 2; alpha_nbr++)
        {
            const unsigned char alpha_code = *(response_iter++);

            const boost::optional<char> alpha_char = convertCodeToAscii( alpha_code );

            validate( Condition( !! alpha_char, ClientErrors::InvalidData )
                    << "Invalid alpha display code (" << alpha_code << ")" );

            alphamericId += *alpha_char;
        }

        _displayItemsReceived->push_back( metric->metric );

        description += "\nDisplay metric " + CtiNumStr(item+1) + " [" + alphamericId + "] : " + metric->description;
    }

    return description;
}


/**
 * get the display items received, will contain empty vector is valid response is received with no display items
 * @return
 */
boost::optional<RfnFocusAlLcdConfigurationCommand::MetricVector> RfnFocusAlLcdConfigurationReadCommand::getDisplayItemsReceived() const
{
    return _displayItemsReceived;
}

boost::optional<unsigned char> RfnFocusAlLcdConfigurationReadCommand::getDisplayItemDurationReceived() const
{
    return _displayItemDurationReceived;
}



RfnFocusAlLcdConfigurationWriteCommand::RfnFocusAlLcdConfigurationWriteCommand( const MetricVector &metrics_, const unsigned char displayItemDuration_ ) :
    metrics(metrics_),
    displayItemDuration(displayItemDuration_)
{
}

unsigned char RfnFocusAlLcdConfigurationWriteCommand::getOperation() const
{
    return FocusLcdConfig_Operation_Write;
}

RfnCommand::Bytes RfnFocusAlLcdConfigurationWriteCommand::getCommandData()
{
    Bytes command_data;

    /*
     *  Length (Bytes)  Field Description
     *  1               Command Code - see note(1)
     *  1               Operation Code - see note(1)
     *  1               # of display items (# of display items scrolling)
     *  1               time in seconds to display each item
     *  N/A             Items to display (repeated # display items times)
     *  1               Metric index code (see table below)
     *  2               Two bytes to be displayed as ID code on the display (see Alpha Display ID table below)
     *
     *  note(1) : fields are managed by the RfnDevice class
     */

    command_data.push_back( metrics.size() );
    command_data.push_back( displayItemDuration );

    unsigned index = 0;

    for each( const Metrics metric in metrics )
    {
        command_data.push_back( metric );  //  enum values match the code values

        //  Apply a numeric index ID for the metric (01, 02, etc)
        index++;
        command_data.push_back( '0' + index / 10 );
        command_data.push_back( '0' + index % 10 );

        //  Future, non-digit ASCII ID code values will need to run through convertAsciiToCode:
        /*
        const boost::optional<unsigned char> alpha_code = convertAsciiToCode( alphameric[digit] );

        validate( Condition( alpha_code, BADPARAM )
                << "Invalid alphanumeric character ('" << alphameric[digit] << "' " << CtiNumStr(alphameric[digit]).xhex(2) << ")" );
        */
    }

    return command_data;
}

RfnCommandResult RfnFocusAlLcdConfigurationWriteCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    /*
     *  Length (Bytes)  Field Description
     *  1               Command Code
     *  1               Status code
     *
     */

    validate( Condition( response.size() == 2, ClientErrors::InvalidData )
            << "Response size unexpected - (" << response.size() << ", expecting 2)" );

    const unsigned char commandCode = response[0],
                        statusCode  = response[1];

    // check command
    validate( Condition( commandCode == FocusLcdConfig_CommandCode_Response, ClientErrors::InvalidData )
            << "Invalid command - (" << commandCode << ", expecting " << FocusLcdConfig_CommandCode_Response << ")" );

    const boost::optional<std::string> status = mapFind(StatusItems, statusCode);

    validate( Condition( !! status, ClientErrors::InvalidData )
            << "Invalid Status (" << statusCode << ")");

    validate( Condition( statusCode == 0x00, ClientErrors::Unknown )
            << "Failure Status (" << statusCode << ")");

    std::string description = "Status: " + *status + " (" + CtiNumStr(statusCode) + ")";

    description += "\nDisplay items sent:";

    description += "\nLCD cycle time : " + CtiNumStr(displayItemDuration) + " seconds";

    if( metrics.empty() )
    {
        return description += "\nNo display metrics";
    }

    unsigned item = 0;

    for each(Metrics m in metrics)
    {
        const boost::optional<MetricDescription> metric = mapFind( MetricDescriptions, m );

        validate( Condition( !! metric, ClientErrors::InvalidData )
                << "Invalid metric code (" << m << ")" );

        description += "\nDisplay metric " + std::to_string(++item) + " : " + metric->description;
    }

    return description;
}


}
}
}
