#pragma once

#include "yukon.h"
#include "dllbase.h"

#include <string>


namespace Cti       {
namespace Config    {

class IM_EX_CONFIG RegulatorStrings
{
public:
    static const std::string voltageControlMode;
    static const std::string voltageChangePerTap;
    static const std::string heartbeatPeriod;
    static const std::string heartbeatValue;
    static const std::string heartbeatMode;
};

}
}

