#include "precompiled.h"

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
 * reverse the map key and mapped values to create simple reverse lookup map, (each value must be unique)
 * @param input
 * @return
 */
template<class MapOut, class MapIn>
MapOut reverseMap( const MapIn & input )
{
    MapOut output;

    for each( const MapIn::value_type & p in input )
    {
        output[p.second] = p.first;
    }

    return output;
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
    {  0, "Delivered +kWh (6 digit)",       "deliveredKwh6x1",  "deliveredKwh",    "6x1"  },
    {  1, "Delivered +kWh (5 digit)",       "deliveredKwh5x1",  "deliveredKwh",    "5x1"  },
    {  2, "Delivered +kWh (4 digit)",       "deliveredKwh4x1",  "deliveredKwh",    "4x1"  },
    {  3, "Delivered +kWh (4 x 10  digit)", "deliveredKwh4x10", "deliveredKwh"     "4x10" },
    {  4, "Received +kWh (6 digit)",        "receivedKwh6x1",   "receivedKwh",     "6x1"  },
    {  5, "Received +kWh (5 digit)",        "receivedKwh5x1",   "receivedKwh",     "5x1"  },
    {  6, "Received +kWh (4 digit)",        "receivedKwh4x1",   "receivedKwh",     "4x1"  },
    {  7, "Received +kWh (4 x 10  digit)",  "receivedKwh4x10"   "receivedKwh"      "4x10" },
    {  8, "Total kWh (6 digit)",            "totalKwh6x1",      "totalKwh",        "6x1"  },
    {  9, "Total kWh (5 digit)",            "totalKwh5x1",      "totalKwh",        "5x1"  },
    { 10, "Total kWh (4 digit)",            "totalKwh4x1",      "totalKwh",        "4x1"  },
    { 11, "Total kWh (4 x 10  digit)",      "totalKwh4x10",     "totalKwh",        "4x10" },
    { 12, "Net kWh (6 digit)" ,             "netKwh6x1",        "netKwh",          "6x1"  },
    { 13, "Net kWh (5 digit)" ,             "netKwh5x1",        "netKwh",          "5x1"  },
    { 14, "Net kWh (4 digit)" ,             "netKwh4x1",        "netKwh",          "4x1"  },
    { 15, "Net kWh (4 x 10  digit)",        "netKwh4x10",       "netKwh",          "4x10" },
    { 16, "Diagnostic flags",               "diagnosticFlags",  "diagnosticFlags", ""     },
    { 17, "All Segments",                   "allSegments",      "allSegments",     ""     },
    { 18, "Firmware version",               "firmwareVersion",  "firmwareVersion", ""     }};

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

typedef map<unsigned char, char> AlphaDisplayMap;
typedef map<char, unsigned char> AlphaDisplayReverseMap;

AlphaDisplayMap initAlphaDisplayLookup()
{
    AlphaDisplayMap m;

    for(unsigned char code = 0x30; code <= 0x39; code++)
    {
        m[code] = code;     // 0 – 9   30h - 39h (ASCII Equivalent)
    }

    m[0x3a] = '$';          // all segments
    m[0x3b] = '*';          // Star (*)
    m[0x3c] = ' ';          // Blank
    m[0x3d] = '=';          // '=' 3Dh (ASCII Equivalent)
    m[0x3e] = '>';          // '>' 3Dh (ASCII Equivalent)
    m[0x3f] = '+';          // '+' 3Fh
    m[0x40] = '-';          // '-' 40h

    for(unsigned char code = 0x41; code <= 0x5a; code++)
    {
        m[code] = code;     // A – Z   41h - 5Ah (ASCII Equivalent)
    }

    return m;
}

const AlphaDisplayMap        alphaDisplayLookup        = initAlphaDisplayLookup();                                 // code to ascii
const AlphaDisplayReverseMap alphaDisplayReverseLookup = reverseMap<AlphaDisplayReverseMap>( alphaDisplayLookup ); // acsii to code

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

    boost::optional<const MetricItem*> metric_item;
    boost::optional<unsigned char>     alpha_code;

    for each( const DisplayItem & item in displayItems )
    {
        validateCondition( metric_item = mapFind( metricConfigNameLookup, item.metric ),
                           BADPARAM, "Invalid metric (" + item.metric + ")" );

        command_data.push_back( (*metric_item)->code );

        validateCondition( item.alpha.length() == 2,
                           BADPARAM, "Invalid alpha display length (" + CtiNumStr(item.alpha.length()) + ", expected 2)" );

        for(int alpha_nbr = 0; alpha_nbr < 2; alpha_nbr++)
        {
            validateCondition( alpha_code = mapFind( alphaDisplayReverseLookup, item.alpha[alpha_nbr] ),
                               BADPARAM, "Invalid alpha character (" + string(1,item.alpha[alpha_nbr]) + ")" );

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
RfnCommand::RfnResult RfnFocusLcdConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponse &response)
{
    RfnResult result;

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

    boost::optional<const MetricItem*> metric_item;
    boost::optional<char>              alpha_char;

    Bytes::const_iterator response_iter = response.begin()+3;

    for(int item_nbr = 0 ; item_nbr < displayItemNbr ; item_nbr++)
    {
        DisplayItem display_item;

        const unsigned char metric_code = *(response_iter++);

        validateCondition( metric_item = mapFind( metricCodeLookup, metric_code ),
                           ErrorInvalidData, "Invalid metric code (" + CtiNumStr(metric_code) + ")");

        display_item.metric = (*metric_item)->configName;

        for(int alpha_nbr = 0; alpha_nbr < 2; alpha_nbr++)
        {
            const unsigned char alpha_code = *(response_iter++);

            validateCondition( alpha_char = mapFind( alphaDisplayLookup, alpha_code ),
                               ErrorInvalidData, "Invalid alpha display code (" + CtiNumStr(alpha_code) + ")");

            display_item.alpha += *alpha_char;

        }

        _displayItemsReceived->push_back( display_item );

        result.description += "Display item " + CtiNumStr(item_nbr) + " :\n"
                           + " " + (*metric_item)->description + "\n"
                           + " \"" + display_item.alpha + "\"\n";
    }

    return result;
}

/**
 * throws CommandException
 * @param now
 * @param error_code
 * @return
 */
RfnCommand::RfnResult RfnFocusLcdConfigurationCommand::error(const CtiTime now, const YukonError_t error_code)
{
    //  This should probably be the default for all commands unless specified otherwise.
    throw CommandException(error_code, GetErrorString(error_code));
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
boost::optional<RfnFocusLcdConfigurationCommand::DisplayItemVector> RfnFocusLcdConfigurationCommand::getDisplayItemReceived() const
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
