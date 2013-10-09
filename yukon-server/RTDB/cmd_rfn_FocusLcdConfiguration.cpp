#include "precompiled.h"

#include <boost/bimap.hpp>

#include "std_helper.h"
#include "cmd_rfn_FocusLcdConfiguration.h"


using namespace std;

namespace Cti {
namespace Devices {
namespace Commands {

namespace {

void validateCondition( const bool condition, const int error_code, const string & error_message )
{
    if ( ! condition )
    {
        throw RfnCommand::CommandException( error_code, error_message );
    }
}

/**
 * this method is somewhat similar to sizeof(arr)/sizeof(arr[0]),
 * but will return a compile error is the array is dynamic...
 * @param arr
 */
template <typename T,unsigned Size>
unsigned arraySize(const T (&arr)[Size]) { return Size; }

enum
{
    FocusLcdConfig_CommandCode_Request  = 0x60,
    FocusLcdConfig_CommandCode_Response = 0x61,
};

enum
{
    FocusLcdConfig_Operation_Write      = 0x00,
    FocusLcdConfig_Operation_Read       = 0x01,
};

struct MetricItem
{
    const unsigned char code;

    const string description,
                 configName,
                 configName_NoResolution,
                 resolution;
};

// this array is use to populate lookup maps
const MetricItem metricItems[] = {
    { 0x00, "Delivered +kWh (6 digit)",       "deliveredKwh6x1",  "deliveredKwh",    "6x1"  },
    { 0x01, "Delivered +kWh (5 digit)",       "deliveredKwh5x1",  "deliveredKwh",    "5x1"  },
    { 0x02, "Delivered +kWh (4 digit)",       "deliveredKwh4x1",  "deliveredKwh",    "4x1"  },
    { 0x03, "Delivered +kWh (4 x 10  digit)", "deliveredKwh4x10", "deliveredKwh"     "4x10" },
    { 0x04, "Received +kWh (6 digit)",        "receivedKwh6x1",   "receivedKwh",     "6x1"  },
    { 0x05, "Received +kWh (5 digit)",        "receivedKwh5x1",   "receivedKwh",     "5x1"  },
    { 0x06, "Received +kWh (4 digit)",        "receivedKwh4x1",   "receivedKwh",     "4x1"  },
    { 0x07, "Received +kWh (4 x 10  digit)",  "receivedKwh4x10"   "receivedKwh"      "4x10" },
    { 0x08, "Total kWh (6 digit)",            "totalKwh6x1",      "totalKwh",        "6x1"  },
    { 0x09, "Total kWh (5 digit)",            "totalKwh5x1",      "totalKwh",        "5x1"  },
    { 0x0a, "Total kWh (4 digit)",            "totalKwh4x1",      "totalKwh",        "4x1"  },
    { 0x0b, "Total kWh (4 x 10  digit)",      "totalKwh4x10",     "totalKwh",        "4x10" },
    { 0x0c, "Net kWh (6 digit)" ,             "netKwh6x1",        "netKwh",          "6x1"  },
    { 0x0d, "Net kWh (5 digit)" ,             "netKwh5x1",        "netKwh",          "5x1"  },
    { 0x0e, "Net kWh (4 digit)" ,             "netKwh4x1",        "netKwh",          "4x1"  },
    { 0x0f, "Net kWh (4 x 10  digit)",        "netKwh4x10",       "netKwh",          "4x10" },
    { 0x10, "Diagnostic flags",               "diagnosticFlags",  "diagnosticFlags", ""     },
    { 0x11, "All Segments",                   "allSegments",      "allSegments",     ""     },
    { 0x12, "Firmware version",               "firmwareVersion",  "firmwareVersion", ""     }};

const unsigned totalMetricItems = arraySize( metricItems );

typedef map<unsigned char, const MetricItem*> MetricCodeMap;        // code to metric item
typedef map<string, const MetricItem*>        MetricConfigNameMap;  // config name to metric item

MetricCodeMap initMetricCodeLookup()
{
    MetricCodeMap m;

    for(int item_nbr = 0 ; item_nbr < totalMetricItems; item_nbr++)
    {
        const MetricItem &item = metricItems[item_nbr];
        m[item.code] = &item;
    }

    return m;
}

MetricConfigNameMap initMetricConfigNameLookup()
{
    MetricConfigNameMap m;

    for(int item_nbr = 0 ; item_nbr < totalMetricItems; item_nbr++)
    {
        const MetricItem &item = metricItems[item_nbr];
        m[item.configName] = &item;
    }

    return m;
}

const MetricCodeMap       metricCodeLookup       = initMetricCodeLookup();
const MetricConfigNameMap metricConfigNameLookup = initMetricConfigNameLookup();

typedef boost::bimap<unsigned char, char> AlphaDisplayBimap;

AlphaDisplayBimap initAlphaDisplayBimap()
{
    AlphaDisplayBimap bm;

    typedef AlphaDisplayBimap::value_type bm_value_type;

    for(unsigned char code = 0x30; code <= 0x39; code++)
    {
        bm.insert(bm_value_type(code,code));    // 0 – 9   30h - 39h (ASCII Equivalent)
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
        bm.insert(bm_value_type(code,code));    // A – Z   41h - 5Ah (ASCII Equivalent)
    }

    return bm;
}

const AlphaDisplayBimap alphaDisplayBimap = initAlphaDisplayBimap();

template<typename MappedType, class MapViewType, typename KeyType>
boost::optional<MappedType> bimapFind( MapViewType mapView, KeyType key )
{
    MapViewType::const_iterator itr = mapView.find(key);

    if( itr == mapView.end() )
    {
        return boost::none;
    }

    return itr->second;
}

boost::optional<char> convertCodeToAscii( unsigned char code )
{
    return bimapFind<char>( alphaDisplayBimap.left, code );
}

boost::optional<unsigned char> convertAsciiToCode( char ascii )
{
    return bimapFind<unsigned char>( alphaDisplayBimap.right, ascii );
}

} // anonymous namespace

/**
 * Constructor read
 */
RfnFocusLcdConfigurationCommand::RfnFocusLcdConfigurationCommand()
{
}

/**
 * Constructor write
 * @param displayItems
 * @param displayItemDuration
 */
RfnFocusLcdConfigurationCommand::RfnFocusLcdConfigurationCommand( const DisplayItemVector & displayItems, unsigned char displayItemDuration ) :
    _commandDataToSend( createCommandData( displayItems, displayItemDuration ))
{
}

/**
 * called by the write constructor to verify and construct data to send
 * @param displayItems
 * @param displayItemDuration
 * @return
 */
RfnCommand::Bytes RfnFocusLcdConfigurationCommand::createCommandData( const DisplayItemVector & displayItems, unsigned char displayItemDuration )
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

    command_data.push_back( displayItems.size() );
    command_data.push_back( displayItemDuration );

    boost::optional<const MetricItem*> metric;
    boost::optional<unsigned char>     alpha_code;

    for each( const DisplayItem & display_item in displayItems )
    {
        validateCondition( metric = mapFind( metricConfigNameLookup, display_item.metric ),
                           BADPARAM, "Invalid metric (" + display_item.metric + ")" );

        command_data.push_back( (*metric)->code );

        validateCondition( display_item.alphamericId.length() == 2,
                           BADPARAM, "Invalid alpha display length (" + CtiNumStr(display_item.alphamericId.length()) + ", expected 2)" );

        for each( char alpha_char in display_item.alphamericId )
        {
            validateCondition( alpha_code = convertAsciiToCode( alpha_char ),
                               BADPARAM, "Invalid alphanumeric character ('" + string(1,alpha_char) + "' " + CtiNumStr(alpha_char).xhex(2) + ")" );

            command_data.push_back( *alpha_code );
        }
    }

    return command_data;
}

/**
 * Decode the response, check for errors (invalid data) and save the result
 * @param now
 * @param response
 * @return
 */
RfnCommandResult RfnFocusLcdConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    RfnCommandResult result;

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

    validateCondition( response.size() >= 3,
                       ErrorInvalidData, "Response too small - (" + CtiNumStr(response.size()) + ", expecting >= 3-byte)");

    const unsigned char commandCode         = response[0],
                        displayItemNbr      = response[1],
                        displayItemDuration = response[2];

    // check command
    validateCondition( commandCode == FocusLcdConfig_CommandCode_Response,
                       ErrorInvalidData, "Invalid command - (" + CtiNumStr(commandCode) + ", expecting " + CtiNumStr((int)FocusLcdConfig_CommandCode_Response) + ")");

    // update display item duration
    _displayItemDurationReceived = displayItemDuration;

    result.description += "Display item duration : " + CtiNumStr(displayItemDuration) + " seconds\n";

    const unsigned responseSizeExp = 3 + displayItemNbr*3;

    validateCondition( response.size() == responseSizeExp,
                       ErrorInvalidData, "Invalid response size ("+ CtiNumStr(response.size()) + "-byte, expecting " + CtiNumStr(responseSizeExp) + "-byte)");

    _displayItemsReceived = DisplayItemVector();

    if( ! displayItemNbr )
    {
        result.description += "No display metrics";

        return result;
    }

    boost::optional<const MetricItem*> metric;
    boost::optional<char>              alpha_char;

    Bytes::const_iterator response_iter = response.begin()+3;

    for(int item_nbr = 0 ; item_nbr < displayItemNbr ; item_nbr++)
    {
        DisplayItem display_item;

        const unsigned char metric_code = *(response_iter++);

        validateCondition( metric = mapFind( metricCodeLookup, metric_code ),
                           ErrorInvalidData, "Invalid metric code (" + CtiNumStr(metric_code) + ")");

        display_item.metric = (*metric)->configName;

        for(int alpha_nbr = 0; alpha_nbr < 2; alpha_nbr++)
        {
            const unsigned char alpha_code = *(response_iter++);

            validateCondition( alpha_char = convertCodeToAscii( alpha_code ),
                               ErrorInvalidData, "Invalid alpha display code (" + CtiNumStr(alpha_code) + ")");

            display_item.alphamericId += *alpha_char;
        }

        _displayItemsReceived->push_back( display_item );

        result.description += "Display metric " + CtiNumStr(item_nbr+1) + " : " + (*metric)->description + "\n" +
                              "- ID code : \"" + display_item.alphamericId + "\"\n";
    }

    return result;
}

/**
 * returns the request command code
 * @return
 */
unsigned char RfnFocusLcdConfigurationCommand::getCommandCode() const
{
    return FocusLcdConfig_CommandCode_Request;
}

/**
 * returns the operation field of a request
 * @return
 */
unsigned char RfnFocusLcdConfigurationCommand::getOperation() const
{
    return _commandDataToSend ? FocusLcdConfig_Operation_Write : FocusLcdConfig_Operation_Read;
}

/**
 * returns the data portion a request
 * @return
 */
RfnCommand::Bytes RfnFocusLcdConfigurationCommand::getCommandData()
{
    if( ! _commandDataToSend )
    {
        return Bytes();
    }

    return *_commandDataToSend;
}

/**
 * get the display items received, will contain empty vector is valid response is received with no display items
 * @return
 */
boost::optional<RfnFocusLcdConfigurationCommand::DisplayItemVector> RfnFocusLcdConfigurationCommand::getDisplayItemsReceived() const
{
    return _displayItemsReceived;
}

/**
 * get the display item duration received
 * @return
 */
boost::optional<unsigned char> RfnFocusLcdConfigurationCommand::getDisplayItemDurationReceived() const
{
    return _displayItemDurationReceived;
}


}
}
}
