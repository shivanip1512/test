#pragma once

#include "yukon.h"

#include <string>

namespace Cti::Messaging::Porter {

struct MeterProgramValidationRequestMsg 
{
    std::string meterProgramGuid;
};

struct MeterProgramValidationResponseMsg
{
    std::string meterProgramGuid;
    YukonError_t status;
};

}