#include "precompiled.h"

#include "cmd_mct420_MeterParametersDisplayDigits.h"

namespace Cti {
namespace Devices {
namespace Commands {

const std::map<std::string, Mct420MeterParametersDisplayDigitsCommand::DisplayDigits> Mct420MeterParametersDisplayDigitsCommand::_digit_mapping = boost::assign::map_list_of
    ("4x1", Four_Digits)
    ("5x1", Five_Digits)
    ("6x1", Six_Digits);

Mct420MeterParametersDisplayDigitsCommand::Mct420MeterParametersDisplayDigitsCommand(const unsigned cycleTime, bool disconnectDisplayDisabled, boost::optional<unsigned> transformerRatio,
                                                                                     boost::optional<std::string> parseStr, boost::optional<unsigned char> dynamicValue) :
    Mct420MeterParametersCommand(cycleTime, disconnectDisplayDisabled, transformerRatio),
    _parseStr(parseStr),
    _dynamicValue(dynamicValue)
{
    if( _parseStr )
    {
        std::map<std::string, DisplayDigits>::const_iterator itr = _digit_mapping.find(*_parseStr);

        if( itr == _digit_mapping.end() )
        {
            // Not a valid value!
            throw CommandException(ClientErrors::BadParameter, "Invalid LCD display digit configuration specified (" + *_parseStr + ")");
        }

        _displayDigits = itr->second;
    }
    else if( _dynamicValue )
    {
        // Use this, regardless of what it was.
        _displayDigits = *_dynamicValue;
    }
    else
    {
        // We got nothing.
       throw CommandException(ClientErrors::BadParameter, "No LCD display digit configuration specified.");
    }
}

Mct420MeterParametersDisplayDigitsCommand::Mct420MeterParametersDisplayDigitsCommand() :
    Mct420MeterParametersCommand(),
    _parseStr(boost::none),
    _dynamicValue(boost::none)
{
}

unsigned char Mct420MeterParametersDisplayDigitsCommand::getDisplayParametersByte()
{
    unsigned char displayParameters = Mct420MeterParametersCommand::getDisplayParametersByte();

    displayParameters |= _displayDigits;

    return displayParameters;
}

}
}
}
