#include "precompiled.h"

#include <boost/assign/list_of.hpp>
#include "cmd_rfn_FocusLcdConfiguration.h"

namespace Cti {
namespace Devices {
namespace Commands {

namespace {

enum
{
    LcdConfiguration_CommandCode_Request  = 0x60,
};

const std::map<unsigned char, std::string> MetricIndexLookup = boost::assign::map_list_of
    (  0, "Delivered +kWh (6 digit)"       )
    (  1, "Delivered +kWh (5 digit)"       )
    (  2, "Delivered +kWh (4 digit)"       )
    (  3, "Delivered +kWh (4 x 10  digit)" )
    (  4, "Received +kWh (6 digit)"        )
    (  5, "Received +kWh (5 digit)"        )
    (  6, "Received +kWh (4 digit)"        )
    (  7, "Received +kWh (4 x 10  digit)"  )
    (  8, "Total kWh (6 digit)"            )
    (  9, "Total kWh (5 digit)"            )
    ( 10, "Total kWh (4 digit)"            )
    ( 11, "Total kWh (4 x 10  digit)"      )
    ( 12, "Net kWh (6 digit)"              )
    ( 13, "Net kWh (5 digit)"              )
    ( 14, "Net kWh (4 digit)"              )
    ( 15, "Net kWh (4 x 10  digit)"        )
    ( 16, "Diagnostic flags"               )
    ( 17, "All Segments"                   )
    ( 18, "Firmware version"               );


typedef std::map<unsigned char, std::string> IdCodeLookup_t;
typedef std::map<std::string, unsigned char> IdCodeReverseLookup_t;

IdCodeLookup_t initIdCodeLookup()
{
    IdCodeLookup_t m;

    for(int code = 0x30; code <= 0x39; code++)
    {
        m[code] = code;     // 0 – 9   30h - 39h (ASCII Equivalent)
    }

    m[0x3a] = "all segment";
    m[0x3b] = "*";          // Star (*)
    m[0x3c] = " ";          // Blank
    m[0x3d] = "=";          // '=' 3Dh (ASCII Equivalent)
    m[0x3e] = ">";          // '>' 3Dh (ASCII Equivalent)
    m[0x3f] = "+";          // '+' 3Fh
    m[0x40] = "-";          // '-' 40h

    for(int code = 0x41; code <= 0x5a; code++)
    {
        m[code] = code;     // A – Z   41h - 5Ah (ASCII Equivalent)
    }

    return m;
}

const IdCodeLookup_t IdCodeLookup = initIdCodeLookup();

IdCodeReverseLookup_t initIdCodeReverseLookup()
{
    IdCodeReverseLookup_t m;

    for each( const std::pair<unsigned char, std::string> & p in IdCodeLookup )
    {
        m[p.second] = p.first;
    }

    return m;
}

const IdCodeReverseLookup_t IdCodeReverseLookup = initIdCodeReverseLookup();

} // anonymous namespace


RfnFocusLcdConfigurationCommand::RfnFocusLcdConfigurationCommand( const DisplayMetrics_t &display_metrics ) :
    _display_metrics_to_send(display_metrics)
{
}

RfnCommand::RfnResult RfnFocusLcdConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponse &response)
{
    RfnResult result;

    // TODO: find response format

    return result;
}

//  throws CommandException
RfnCommand::RfnResult RfnFocusLcdConfigurationCommand::error(const CtiTime now, const YukonError_t error_code)
{
    //  This should probably be the default for all commands unless specified otherwise.
    throw CommandException(error_code, GetErrorString(error_code));
}

//  returns the command code
unsigned char RfnFocusLcdConfigurationCommand::getCommandCode() const
{
    return LcdConfiguration_CommandCode_Request;
}

//  returns the operation field of a request
unsigned char RfnFocusLcdConfigurationCommand::getOperation() const
{
    return 0x0; // TODO: find operation
}

//  returns the data portion a request
RfnCommand::Bytes RfnFocusLcdConfigurationCommand::getCommandData()
{
    Bytes data;

    if( ! _display_metrics_to_send )
    {
        data.push_back(0);
        return data;
    }

    data.push_back( _display_metrics_to_send->size() );

    for each( const DisplayMetric & m in *_display_metrics_to_send )
    {
        data.push_back( m.indexCode );
        data.push_back( m.idCode[0] );
        data.push_back( m.idCode[1] );
    }

    return data;
}


}
}
}
