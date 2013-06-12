#pragma once

#include "dlldefs.h"
#include <string>

#include "intsafe.h"  //  for INT, CHAR, etc

enum IM_EX_CTIBASE ErrorTypes
{
    ERRTYPENONE = 0,
    ERRTYPEUNKNOWN = 0,
    ERRTYPESYSTEM = 1,
    ERRTYPEPROTOCOL,
    ERRTYPECOMM,
};


IM_EX_CTIBASE ErrorTypes  GetErrorType  (int errorNumber);
IM_EX_CTIBASE std::string GetErrorString(int errorNumber);

