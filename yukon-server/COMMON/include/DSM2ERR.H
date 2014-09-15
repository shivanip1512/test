#pragma once

#include "dlldefs.h"
#include "yukon.h"

#include <string>

enum IM_EX_CTIBASE ErrorTypes
{
    ERRTYPENONE = 0,
    ERRTYPEUNKNOWN = 0,
    ERRTYPESYSTEM = 1,
    ERRTYPEPROTOCOL,
    ERRTYPECOMM,
};


IM_EX_CTIBASE ErrorTypes  GetErrorType  (YukonError_t errorNumber);
IM_EX_CTIBASE std::string GetErrorString(YukonError_t errorNumber);

