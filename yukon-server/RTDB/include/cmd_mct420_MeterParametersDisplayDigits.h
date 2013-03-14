#pragma once

#include "cmd_mct420_MeterParameters.h"

#include <boost/optional.hpp>
#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct420MeterParametersDisplayDigitsCommand : public Mct420MeterParametersCommand
{
    boost::optional<std::string> _parseStr;
    boost::optional<unsigned char> _dynamicValue;

    unsigned char _displayDigits;

    enum DisplayDigits {
        Four_Digits = 0x20,
        Five_Digits = 0x00,
        Six_Digits  = 0x10,
    };

    static const std::map<std::string, DisplayDigits> _digit_mapping;

protected:

    virtual unsigned char getDisplayParametersByte();

public:

    // Write constructor
    Mct420MeterParametersDisplayDigitsCommand(const unsigned cycleTime, bool disconnectDisplayDisabled, boost::optional<unsigned> transformerRatio, boost::optional<std::string> parseStr, 
                                              boost::optional<unsigned char> dynamicValue);

    // Read constructor
    Mct420MeterParametersDisplayDigitsCommand();
};

}
}
}

